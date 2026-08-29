/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockChest
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagInt
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 */
package melonslise.locks.common.capability;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ILockableWorldGenHandler;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.Cuboid6i;
import melonslise.locks.common.util.Lock;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import melonslise.locks.common.util.Orientation;
import melonslise.locks.mixin.TileEntityLockableLootAccessor;
import net.minecraft.block.BlockChest;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class LockableWorldGenHandler
implements ILockableWorldGenHandler {
    public static final ResourceLocation ID = new ResourceLocation("locks", "lockable_worldgen_handler");
    public static final int NONE = 0;
    public static final int SHOULD_GENERATE = 1;
    public static final int FINISHED_GENERATING = 2;
    public final Chunk chunk;
    public AtomicInteger phase = new AtomicInteger();

    public LockableWorldGenHandler(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void setChunkShouldGenerateChests() {
        if (this.phase.get() != 2) {
            this.phase.set(1);
        }
    }

    @Override
    public void tryGeneratingLocks() {
        if (this.phase.get() == 1) {
            World world = this.chunk.getWorld();
            Random rand = world.rand;
            ILockableHandler lockables = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
            for (Map.Entry entry : this.chunk.getTileEntityMap().entrySet()) {
                BlockPos pos = (BlockPos)entry.getKey();
                if (!(entry.getValue() instanceof TileEntityChest) || !(world.getBlockState(pos).getBlock() instanceof BlockChest) || lockables.getInChunk(pos).values().stream().anyMatch(lockable1 -> lockable1.box.intersects(pos))) continue;
                TileEntityChest te = (TileEntityChest)entry.getValue();
                boolean isForced = false;
                boolean isEmpty = true;
                if (te.getLootTable() == null) {
                    NonNullList<ItemStack> stacks = ((TileEntityLockableLootAccessor)te).getItems();
                    for (ItemStack stack : stacks) {
                        if (stack.isEmpty()) continue;
                        if (LocksConfig.isItemAlwaysLocked(stack)) {
                            isForced = true;
                            break;
                        }
                        if (!isEmpty || LocksConfig.isItemSkipped(stack)) continue;
                        isEmpty = false;
                    }
                } else {
                    isForced = false;
                    isEmpty = false;
                }
                if (isEmpty && LocksConfig.COMMON.skipGenerationEmptyChests || !isForced && !LocksConfig.canGen(rand)) continue;
                BlockPos adjPos = LocksUtil.getAdjacentChest((TileEntityChest)entry.getValue());
                ItemStack stack = LocksConfig.getRandomLock(rand);
                lockables.add(new Lockable(new Cuboid6i(pos, adjPos == null ? pos : adjPos), Lock.from(stack), Orientation.fromDirection((EnumFacing)world.getBlockState(pos).getValue((IProperty)BlockChest.FACING), EnumFacing.NORTH), stack, world));
            }
            this.phase.set(2);
            this.chunk.markDirty();
        }
    }

    public NBTTagInt serializeNBT() {
        return new NBTTagInt(this.phase.get());
    }

    public void deserializeNBT(NBTTagInt nbt) {
        this.phase.set(nbt.getInt());
    }
}

