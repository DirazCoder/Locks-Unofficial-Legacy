/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  net.minecraft.nbt.NBTTagInt
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.chunk.EmptyChunk
 */
package melonslise.locks.common.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ILockableStorage;
import melonslise.locks.common.capability.LockableStorage;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.network.toclient.AddLockablePacket;
import melonslise.locks.common.network.toclient.RemoveLockablePacket;
import melonslise.locks.common.network.toclient.UpdateLockablePacket;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;

public class LockableHandler
implements ILockableHandler {
    public static final ResourceLocation ID = new ResourceLocation("locks", "lockable_handler");
    public final World world;
    public AtomicInteger lastId = new AtomicInteger();
    public Int2ObjectMap<Lockable> lockables = Int2ObjectMaps.synchronize(new Int2ObjectLinkedOpenHashMap<Lockable>());
    public Int2ObjectMap<Lockable> emptyLockables = new Int2ObjectLinkedOpenHashMap();

    public LockableHandler(World world) {
        this.world = world;
    }

    @Override
    public int nextId() {
        return this.lastId.incrementAndGet();
    }

    @Override
    public Int2ObjectMap<Lockable> getLoaded() {
        return this.lockables;
    }

    @Override
    public Int2ObjectMap<Lockable> getInChunk(BlockPos pos) {
        // Same class of bug as LocksUtil.intersecting: hasChunkAt(pos) can
        // be true while the chunk's LOCKABLE_STORAGE capability isn't
        // attached yet (e.g. during early block-class construction before
        // real world/chunk state exists), so getCapability(...) returns
        // null and the blind cast + .get() would NPE. See KNOWN_ISSUES.md.
        if (!LocksUtil.hasChunkAt(this.world, pos)) {
            return this.emptyLockables;
        }
        ILockableStorage storage = (ILockableStorage) this.world.getChunkFromBlockCoords(pos).getCapability(LocksCapabilities.LOCKABLE_STORAGE, null);
        if (storage == null) {
            return this.emptyLockables;
        }
        return storage.get();
    }

    @Override
    public boolean add(Lockable lkb) {
        int a;
        if (lkb.box.volume() > LocksConfig.SERVER.maxLockableVolume) {
            return false;
        }
        boolean emptyCollision = false;
        List<ILockableStorage> sts = lkb.box.containedChunksTo((x, z) -> {
            if (!LocksUtil.hasChunk(this.world, x, z)) {
                return null;
            }
            ILockableStorage st = (ILockableStorage)this.world.getChunkFromChunkCoords(x, z).getCapability(LocksCapabilities.LOCKABLE_STORAGE, null);
            return st.get().values().stream().anyMatch(lkb1 -> lkb1.box.intersects(lkb.box)) ? null : st;
        }, true);
        if (sts == null) {
            return false;
        }
        if (this.world.isRemote) {
            for (a = 0; a < sts.size(); ++a) {
                ILockableStorage st = sts.get(a);
                if (!(st instanceof LockableStorage) || !(((LockableStorage)st).chunk instanceof EmptyChunk)) continue;
                return false;
            }
        }
        for (a = 0; a < sts.size(); ++a) {
            sts.get(a).add(lkb);
        }
        this.lockables.put(lkb.networkID, lkb);
        lkb.addObserver(this);
        if (this.world.isRemote) {
            lkb.shake(10);
        } else {
            LocksUtil.sendToTrackingPlayers(lkb.box, new AddLockablePacket(lkb), this.world);
        }
        return true;
    }

    @Override
    public boolean remove(int id) {
        Lockable lkb = this.lockables.get(id);
        if (lkb == this.lockables.defaultReturnValue()) {
            return false;
        }
        List<Chunk> chs = lkb.box.containedChunksTo((x, z) -> LocksUtil.hasChunk(this.world, x, z) ? this.world.getChunkFromChunkCoords(x, z) : null, true);
        for (int a = 0; a < chs.size(); ++a) {
            ((ILockableStorage)chs.get(a).getCapability(LocksCapabilities.LOCKABLE_STORAGE, null)).remove(id);
        }
        this.lockables.remove(id);
        lkb.deleteObserver(this);
        if (this.world.isRemote) {
            return true;
        }
        LocksUtil.sendToTrackingPlayers(lkb.box, new RemoveLockablePacket(id), this.world);
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (this.world.isRemote || !(o instanceof Lockable)) {
            return;
        }
        Lockable lockable = (Lockable)o;
        LocksUtil.sendToTrackingPlayers(lockable.box, new UpdateLockablePacket(lockable.networkID, lockable.lock.isLocked()), this.world);
    }

    public NBTTagInt serializeNBT() {
        return new NBTTagInt(this.lastId.get());
    }

    public void deserializeNBT(NBTTagInt nbt) {
        this.lastId.set(nbt.getInt());
    }
}

