/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package melonslise.locks.common.network.toclient;

import io.netty.buffer.ByteBuf;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.Lockable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateLockablePacket
implements IMessage {
    private int networkID;
    private boolean locked;

    public UpdateLockablePacket() {
    }

    public UpdateLockablePacket(int networkID, boolean locked) {
        this.networkID = networkID;
        this.locked = locked;
    }

    public void fromBytes(ByteBuf buf) {
        this.networkID = buf.readInt();
        this.locked = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.networkID);
        buf.writeBoolean(this.locked);
    }

    public static class Handler
    implements IMessageHandler<UpdateLockablePacket, IMessage> {
        public IMessage onMessage(final UpdateLockablePacket pkt, MessageContext ctx) {
            final Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    ((Lockable)((ILockableHandler)mc.world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null)).getLoaded().get((int)((UpdateLockablePacket)pkt).networkID)).lock.setLocked(pkt.locked);
                }
            });
            return null;
        }
    }
}

