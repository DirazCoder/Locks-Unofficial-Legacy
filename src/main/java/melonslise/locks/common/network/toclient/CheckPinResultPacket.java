/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.inventory.Container
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package melonslise.locks.common.network.toclient;

import io.netty.buffer.ByteBuf;
import melonslise.locks.common.container.LockPickingContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CheckPinResultPacket
implements IMessage {
    private boolean correct;
    private boolean reset;

    public CheckPinResultPacket() {
    }

    public CheckPinResultPacket(boolean correct, boolean reset) {
        this.correct = correct;
        this.reset = reset;
    }

    public void fromBytes(ByteBuf buf) {
        this.correct = buf.readBoolean();
        this.reset = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.correct);
        buf.writeBoolean(this.reset);
    }

    public static class Handler
    implements IMessageHandler<CheckPinResultPacket, IMessage> {
        public IMessage onMessage(final CheckPinResultPacket pkt, MessageContext ctx) {
            final Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    Container container = mc.player.openContainer;
                    if (container instanceof LockPickingContainer) {
                        ((LockPickingContainer)container).handlePin(pkt.correct, pkt.reset);
                    }
                }
            });
            return null;
        }
    }
}

