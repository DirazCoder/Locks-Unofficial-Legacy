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
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveLockablePacket
implements IMessage {
    private int networkID;

    public RemoveLockablePacket() {
    }

    public RemoveLockablePacket(int networkID) {
        this.networkID = networkID;
    }

    public void fromBytes(ByteBuf buf) {
        this.networkID = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.networkID);
    }

    public static class Handler
    implements IMessageHandler<RemoveLockablePacket, IMessage> {
        public IMessage onMessage(final RemoveLockablePacket pkt, MessageContext ctx) {
            final Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    ((ILockableHandler)mc.world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null)).remove(pkt.networkID);
                }
            });
            return null;
        }
    }
}

