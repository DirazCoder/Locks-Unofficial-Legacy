/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.chunk.Chunk
 */
package melonslise.locks.common.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import melonslise.locks.Locks;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ILockableStorage;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;

public class LockableStorage
implements ILockableStorage {
    public static final ResourceLocation ID = new ResourceLocation("locks", "lockable_storage");
    public final Chunk chunk;
    public Int2ObjectMap<Lockable> lockables = new Int2ObjectLinkedOpenHashMap<Lockable>();

    public LockableStorage(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public Int2ObjectMap<Lockable> get() {
        return this.lockables;
    }

    @Override
    public void add(Lockable lkb) {
        this.lockables.put(lkb.networkID, lkb);
        this.chunk.markDirty();
    }

    @Override
    public void remove(int id) {
        this.lockables.remove(id);
        this.chunk.markDirty();
    }

    public NBTTagList serializeNBT() {
        NBTTagList list = new NBTTagList();
        for (Lockable lkb : this.lockables.values()) {
            list.appendTag((NBTBase)LocksUtil.writeLockableToNBT(lkb));
        }
        return list;
    }

    public void deserializeNBT(NBTTagList nbt) {
        ILockableHandler handler = (ILockableHandler)this.chunk.getWorld().getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
        Int2ObjectMap<Lockable> loadedLockables = handler.getLoaded();
        for (int a = 0; a < nbt.tagCount(); ++a) {
            NBTTagCompound nbt1 = nbt.getCompoundTagAt(a);
            Lockable lkb = (Lockable)loadedLockables.get(nbt1.getInteger("id"));
            if (lkb == loadedLockables.defaultReturnValue()) {
                lkb = LocksUtil.readLockableFromNBT(nbt1);
                if (Locks.debug) {
                    Locks.logger.debug("Storage deserializing with id: " + lkb.networkID + " ::: " + lkb.toString());
                }
            }
            this.lockables.put(lkb.networkID, lkb);
        }
    }
}

