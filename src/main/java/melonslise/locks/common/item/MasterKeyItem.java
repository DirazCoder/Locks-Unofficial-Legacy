/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package melonslise.locks.common.item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import melonslise.locks.Locks;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksSoundEvents;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class MasterKeyItem
extends Item {
    public MasterKeyItem() {
        this.setMaxStackSize(1);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
        List<Lockable> matching;
        if (Locks.debug && player.isSneaking()) {
            String message;
            if (world.isRemote) {
                for (Map.Entry entry : ((ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null)).getLoaded().entrySet()) {
                    message = "C:" + entry.getKey() + ":" + ((Lockable)entry.getValue()).toString();
                    player.sendMessage((ITextComponent)new TextComponentString(message));
                }
            } else {
                for (Map.Entry entry : ((ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null)).getLoaded().entrySet()) {
                    message = "S:" + entry.getKey() + ":" + ((Lockable)entry.getValue()).toString();
                    player.sendMessage((ITextComponent)new TextComponentString(message));
                }
            }
        }
        if ((matching = LocksUtil.intersecting(world, pos).collect(Collectors.toList())).isEmpty()) {
            return EnumActionResult.PASS;
        }
        world.playSound(player, pos, LocksSoundEvents.LOCK_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        for (Lockable lockable : matching) {
            lockable.lock.setLocked(!lockable.lock.isLocked());
        }
        return EnumActionResult.SUCCESS;
    }
}

