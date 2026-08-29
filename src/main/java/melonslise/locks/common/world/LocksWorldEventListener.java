/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IWorldEventListener
 *  net.minecraft.world.World
 */
package melonslise.locks.common.world;

import java.util.stream.Collectors;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public final class LocksWorldEventListener
implements IWorldEventListener {
    public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        if (world.isRemote || oldState.getBlock() == newState.getBlock()) {
            return;
        }
        ILockableHandler lockables = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
        LocksUtil.intersecting(lockables, pos).collect(Collectors.toList()).forEach(lockable -> {
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 0.8f, 0.8f + world.rand.nextFloat() * 0.4f);
            world.spawnEntity((Entity)new EntityItem(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, lockable.stack));
            lockables.remove(lockable.networkID);
        });
    }

    public void notifyLightSet(BlockPos pos) {
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    }

    public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent sound, SoundCategory cat, double x, double y, double z, float vol, float pitch) {
    }

    public void playRecord(SoundEvent sound, BlockPos pos) {
    }

    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
    }

    public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
    }

    public void onEntityAdded(Entity entity) {
    }

    public void onEntityRemoved(Entity entity) {
    }

    public void broadcastSound(int soundID, BlockPos pos, int data) {
    }

    public void playEvent(EntityPlayer player, int type, BlockPos pos, int data) {
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
    }
}

