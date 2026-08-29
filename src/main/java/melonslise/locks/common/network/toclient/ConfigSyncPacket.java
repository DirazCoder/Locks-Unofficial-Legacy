/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.ByteBufUtils
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package melonslise.locks.common.network.toclient;

import io.netty.buffer.ByteBuf;
import melonslise.locks.common.config.LocksConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ConfigSyncPacket
implements IMessage {
    private int maxLockableVolume;
    private String[] lockableBlocks;
    private boolean allowRemovingLocks;
    private boolean protectLockables;
    private boolean easyLock;

    public ConfigSyncPacket() {
    }

    public ConfigSyncPacket(LocksConfig.Server cfg) {
        this.maxLockableVolume = cfg.maxLockableVolume;
        this.lockableBlocks = cfg.lockableBlocks;
        this.allowRemovingLocks = cfg.allowRemovingLocks;
        this.protectLockables = cfg.protectLockables;
        this.easyLock = cfg.easyLock;
    }

    public void fromBytes(ByteBuf buf) {
        this.maxLockableVolume = buf.readInt();
        this.lockableBlocks = new String[buf.readByte()];
        for (int a = 0; a < this.lockableBlocks.length; ++a) {
            this.lockableBlocks[a] = ByteBufUtils.readUTF8String((ByteBuf)buf);
        }
        this.allowRemovingLocks = buf.readBoolean();
        this.protectLockables = buf.readBoolean();
        this.easyLock = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.maxLockableVolume);
        buf.writeByte(this.lockableBlocks.length);
        for (String str : this.lockableBlocks) {
            ByteBufUtils.writeUTF8String((ByteBuf)buf, (String)str);
        }
        buf.writeBoolean(this.allowRemovingLocks);
        buf.writeBoolean(this.protectLockables);
        buf.writeBoolean(this.easyLock);
    }

    public static class Handler
    implements IMessageHandler<ConfigSyncPacket, IMessage> {
        public IMessage onMessage(final ConfigSyncPacket pkt, MessageContext ctx) {
            final Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    LocksConfig.Server cfg = LocksConfig.getServer((World)mc.world);
                    cfg.maxLockableVolume = pkt.maxLockableVolume;
                    cfg.lockableBlocks = pkt.lockableBlocks;
                    cfg.allowRemovingLocks = pkt.allowRemovingLocks;
                    cfg.protectLockables = pkt.protectLockables;
                    cfg.easyLock = pkt.easyLock;
                    cfg.init();
                }
            });
            return null;
        }
    }
}

