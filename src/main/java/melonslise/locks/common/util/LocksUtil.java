/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.util.internal.ThreadLocalRandom
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.management.PlayerChunkMap
 *  net.minecraft.server.management.PlayerChunkMapEntry
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package melonslise.locks.common.util;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Stream;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksNetworks;
import melonslise.locks.common.util.AttachFace;
import melonslise.locks.common.util.Cuboid6i;
import melonslise.locks.common.util.Lock;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksPredicates;
import melonslise.locks.common.util.Orientation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class LocksUtil {
    public static final String KEY_X1 = "x1";
    public static final String KEY_Y1 = "y1";
    public static final String KEY_Z1 = "z1";
    public static final String KEY_X2 = "x2";
    public static final String KEY_Y2 = "y2";
    public static final String KEY_Z2 = "z2";
    public static final String KEY_ID = "id";
    public static final String KEY_LENGTH = "length";
    public static final String KEY_OLD_CODE = "code";
    public static final String KEY_OLD_COMBINATION = "combination";
    public static final String KEY_LOCKED = "locked";
    public static final String KEY_BOX = "box";
    public static final String KEY_LOCK = "lock";
    public static final String KEY_ORIENTATION = "orientation";
    public static final String KEY_OLD_SIDE = "side";
    public static final String KEY_STACK = "Stack";

    private LocksUtil() {
    }

    public static void shuffle(byte[] array, Random rng) {
        for (int a = array.length - 1; a > 0; --a) {
            int index = rng.nextInt(a + 1);
            byte temp = array[index];
            array[index] = array[a];
            array[a] = temp;
        }
    }

    public static BlockPos getAdjacentChest(TileEntityChest te) {
        BlockPos pos = null;
        te.checkForAdjacentChests();
        if (te.adjacentChestXNeg != null) {
            pos = te.adjacentChestXNeg.getPos();
        } else if (te.adjacentChestXPos != null) {
            pos = te.adjacentChestXPos.getPos();
        } else if (te.adjacentChestZNeg != null) {
            pos = te.adjacentChestZNeg.getPos();
        } else if (te.adjacentChestZPos != null) {
            pos = te.adjacentChestZPos.getPos();
        }
        return pos;
    }

    public static AttachFace faceFromDir(EnumFacing dir) {
        return dir == EnumFacing.UP ? AttachFace.CEILING : (dir == EnumFacing.DOWN ? AttachFace.FLOOR : AttachFace.WALL);
    }

    public static Stream<Lockable> intersecting(World world, BlockPos pos) {
        // Guard against a null World: some mods (e.g. Vanilla Builders
        // Extension's vbe_BlockStairs constructor) call block methods like
        // getExplosionResistance with a null World during construction,
        // before any real world/capability state exists. Also guard the
        // capability lookup itself, since it can legitimately return null.
        // See KNOWN_ISSUES.md for the crash this fixes.
        if (world == null) {
            return Stream.empty();
        }
        ILockableHandler handler = world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
        if (handler == null) {
            return Stream.empty();
        }
        return LocksUtil.intersecting(handler, pos);
    }

    public static Stream<Lockable> intersecting(ILockableHandler handler, BlockPos pos) {
        return handler.getInChunk(pos).values().stream().filter(lkb -> lkb.box.intersects(pos));
    }

    public static boolean locked(World world, BlockPos pos) {
        return LocksUtil.intersecting(world, pos).anyMatch(LocksPredicates.LOCKED);
    }

    public static NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public static boolean hasKey(ItemStack stack, String key) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(key);
    }

    public static Cuboid6i readBoxFromNBT(NBTTagCompound nbt) {
        return new Cuboid6i(nbt.getInteger(KEY_X1), nbt.getInteger(KEY_Y1), nbt.getInteger(KEY_Z1), nbt.getInteger(KEY_X2), nbt.getInteger(KEY_Y2), nbt.getInteger(KEY_Z2));
    }

    public static NBTTagCompound writeBoxToNBT(Cuboid6i box) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(KEY_X1, box.x1);
        nbt.setInteger(KEY_Y1, box.y1);
        nbt.setInteger(KEY_Z1, box.z1);
        nbt.setInteger(KEY_X2, box.x2);
        nbt.setInteger(KEY_Y2, box.y2);
        nbt.setInteger(KEY_Z2, box.z2);
        return nbt;
    }

    public static Lock readLockFromNBT(NBTTagCompound nbt) {
        int id;
        int n = id = nbt.hasUniqueId(KEY_ID) ? ThreadLocalRandom.current().nextInt() : nbt.getInteger(KEY_ID);
        int length = nbt.hasKey(KEY_LENGTH) ? nbt.getByte(KEY_LENGTH) : (nbt.hasKey(KEY_OLD_CODE) ? nbt.getByteArray(KEY_OLD_CODE).length : nbt.getByteArray(KEY_OLD_COMBINATION).length);
        return new Lock(id, length, nbt.getBoolean(KEY_LOCKED));
    }

    public static NBTTagCompound writeLockToNBT(Lock lock) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(KEY_ID, lock.id);
        nbt.setByte(KEY_LENGTH, (byte)lock.combination.length);
        nbt.setBoolean(KEY_LOCKED, lock.locked);
        return nbt;
    }

    public static Lockable readLockableFromNBT(NBTTagCompound nbt) {
        Orientation orient = nbt.hasKey(KEY_ORIENTATION) ? Orientation.values()[nbt.getByte(KEY_ORIENTATION)] : Orientation.fromDirection(EnumFacing.getFront((int)nbt.getByte(KEY_OLD_SIDE)), EnumFacing.NORTH);
        return new Lockable(LocksUtil.readBoxFromNBT(nbt.getCompoundTag(KEY_BOX)), LocksUtil.readLockFromNBT(nbt.getCompoundTag(KEY_LOCK)), orient, new ItemStack(nbt.getCompoundTag(KEY_STACK)), nbt.getInteger(KEY_ID));
    }

    public static NBTTagCompound writeLockableToNBT(Lockable lockable) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(KEY_BOX, (NBTBase)LocksUtil.writeBoxToNBT(lockable.box));
        nbt.setTag(KEY_LOCK, (NBTBase)LocksUtil.writeLockToNBT(lockable.lock));
        nbt.setByte(KEY_ORIENTATION, (byte)lockable.orient.ordinal());
        nbt.setTag(KEY_STACK, (NBTBase)lockable.stack.serializeNBT());
        nbt.setInteger(KEY_ID, lockable.networkID);
        return nbt;
    }

    public static <T extends Enum<T>> T readEnumFromBuffer(ByteBuf buffer, Class<T> c) {
        return (T)((Enum[])c.getEnumConstants())[buffer.readByte()];
    }

    public static void writeEnumToBuffer(ByteBuf buffer, Enum<?> value) {
        buffer.writeByte((int)((byte)value.ordinal()));
    }

    public static Cuboid6i readBoxFromBuffer(ByteBuf buf) {
        return new Cuboid6i(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void writeBoxToBuffer(ByteBuf buf, Cuboid6i box) {
        buf.writeInt(box.x1);
        buf.writeInt(box.y1);
        buf.writeInt(box.z1);
        buf.writeInt(box.x2);
        buf.writeInt(box.y2);
        buf.writeInt(box.z2);
    }

    public static Lock readLockFromBuffer(ByteBuf buf) {
        return new Lock(buf.readInt(), buf.readByte(), buf.readBoolean());
    }

    public static void writeLockToBuffer(ByteBuf buf, Lock lock) {
        buf.writeInt(lock.id);
        buf.writeByte(lock.getLength());
        buf.writeBoolean(lock.isLocked());
    }

    public static ItemStack readItemStackFromBuffer(ByteBuf buf) {
        try {
            return new PacketBuffer(buf).readItemStack();
        }
        catch (IOException e) {
            e.printStackTrace();
            return ItemStack.EMPTY.copy();
        }
    }

    public static void writeItemStackToBuffer(ByteBuf buf, ItemStack stack) {
        new PacketBuffer(buf).writeItemStack(stack);
    }

    public static Lockable readLockableFromBuffer(ByteBuf buf) {
        return new Lockable(LocksUtil.readBoxFromBuffer(buf), LocksUtil.readLockFromBuffer(buf), LocksUtil.readEnumFromBuffer(buf, Orientation.class), LocksUtil.readItemStackFromBuffer(buf), buf.readInt());
    }

    public static void writeLockableToBuffer(ByteBuf buf, Lockable lockable) {
        LocksUtil.writeBoxToBuffer(buf, lockable.box);
        LocksUtil.writeLockToBuffer(buf, lockable.lock);
        LocksUtil.writeEnumToBuffer(buf, lockable.orient);
        LocksUtil.writeItemStackToBuffer(buf, lockable.stack);
        buf.writeInt(lockable.networkID);
    }

    public static AxisAlignedBB rotateY(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minZ, bb.minY, bb.minX, bb.maxZ, bb.maxY, bb.maxX);
    }

    public static AxisAlignedBB rotateX(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX, bb.minZ, bb.minY, bb.maxX, bb.maxZ, bb.maxY);
    }

    public static boolean intersectsInclusive(AxisAlignedBB box1, AxisAlignedBB box2) {
        return box1.minX <= box2.maxX && box1.maxX >= box2.minX && box1.minY <= box2.maxY && box1.maxY >= box2.minY && box1.minZ <= box2.maxZ && box1.maxZ >= box2.minZ;
    }

    public static Vec3d getAABBSideCenter(AxisAlignedBB box, EnumFacing side) {
        switch (side) {
            case DOWN: {
                return new Vec3d((box.minX + box.maxX) / 2.0, box.minY, (box.minZ + box.maxZ) / 2.0);
            }
            case UP: {
                return new Vec3d((box.minX + box.maxX) / 2.0, box.maxY, (box.minZ + box.maxZ) / 2.0);
            }
            case NORTH: {
                return new Vec3d((box.minX + box.maxX) / 2.0, (box.minY + box.maxY) / 2.0, box.minZ);
            }
            case SOUTH: {
                return new Vec3d((box.minX + box.maxX) / 2.0, (box.minY + box.maxY) / 2.0, box.maxZ);
            }
            case WEST: {
                return new Vec3d(box.minX, (box.minY + box.maxY) / 2.0, (box.minZ + box.maxZ) / 2.0);
            }
            case EAST: {
                return new Vec3d(box.maxX, (box.minY + box.maxY) / 2.0, (box.minZ + box.maxZ) / 2.0);
            }
        }
        return null;
    }

    public static boolean chance(Random rng, double ch) {
        return ch == 1.0 || ch != 0.0 && rng.nextDouble() <= ch;
    }

    public static void sendToTrackingPlayers(Cuboid6i bounds, IMessage message, World world) {
        if (world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer)world;
            PlayerChunkMap playerchunkmap = worldServer.getPlayerChunkMap();
            HashSet<EntityPlayerMP> playerSet = new HashSet<EntityPlayerMP>();
            bounds.containedChunksTo((x, z) -> {
                PlayerChunkMapEntry entry = playerchunkmap.getEntry(x, z);
                if (entry != null) {
                    playerSet.addAll(entry.getWatchingPlayers());
                }
                return null;
            }, false).clear();
            playerSet.stream().forEach(player -> LocksNetworks.MAIN.sendTo(message, player));
            playerSet.clear();
        }
    }

    public static long getOverworldSeed() {
        World world;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null && (world = server.getEntityWorld()) != null) {
            return world.getSeed();
        }
        return 1L;
    }

    public static boolean hasChunkAt(World world, BlockPos pos) {
        return world.isBlockLoaded(pos);
    }

    public static boolean hasChunk(World world, int xx, int zz) {
        return LocksUtil.hasChunkAt(world, new BlockPos((xx << 4) + 8, 64, (zz << 4) + 8));
    }
}

