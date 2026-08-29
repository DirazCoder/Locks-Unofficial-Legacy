/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.Container
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package melonslise.locks.common.network.toserver;

import io.netty.buffer.ByteBuf;
import melonslise.locks.common.container.LockPickingContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CheckPinPacket
implements IMessage {
    private byte pin;

    public CheckPinPacket() {
    }

    public CheckPinPacket(byte pin) {
        this.pin = pin;
    }

    public void fromBytes(ByteBuf buf) {
        this.pin = buf.readByte();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeByte((int)this.pin);
    }

    public static class Handler
    implements IMessageHandler<CheckPinPacket, IMessage> {
        public IMessage onMessage(CheckPinPacket pkt, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServer().addScheduledTask(() -> {
                Container container = player.openContainer;
                if (container instanceof LockPickingContainer) {
                    ((LockPickingContainer)container).checkPin(pkt.pin);
                }
            });
            return null;
        }
    }
}

