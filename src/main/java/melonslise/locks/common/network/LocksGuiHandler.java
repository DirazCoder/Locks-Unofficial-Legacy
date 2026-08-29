/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.IGuiHandler
 */
package melonslise.locks.common.network;

import melonslise.locks.client.gui.KeyRingGui;
import melonslise.locks.client.gui.LockPickingGui;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.container.KeyRingContainer;
import melonslise.locks.common.container.LockPickingContainer;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.Lockable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class LocksGuiHandler
implements IGuiHandler {
    public static final int KEY_RING_ID = 0;
    public static final int LOCK_PICKING_ID = 1;

    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0: {
                return new KeyRingContainer(player, player.getHeldItem(x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
            }
            case 1: {
                return new LockPickingContainer(player, y == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND, (Lockable)((ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null)).getLoaded().get(x));
            }
        }
        return null;
    }

    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0: {
                return new KeyRingGui((KeyRingContainer)((Object)this.getServerGuiElement(id, player, world, x, y, z)));
            }
            case 1: {
                return new LockPickingGui((LockPickingContainer)((Object)this.getServerGuiElement(id, player, world, x, y, z)));
            }
        }
        return null;
    }
}

