/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.culling.ClippingHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.util.AttachFace;
import melonslise.locks.common.util.Cuboid6i;
import melonslise.locks.common.util.Lock;
import melonslise.locks.common.util.LocksUtil;
import melonslise.locks.common.util.Orientation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Lockable
extends Observable
implements Observer {
    public final Cuboid6i box;
    public final Lock lock;
    public final Orientation orient;
    public final int networkID;
    public final ItemStack stack;
    public Map<List<IBlockState>, State> cache = new HashMap<List<IBlockState>, State>();
    public int prevShakeTicks;
    public int shakeTicks;
    public int maxShakeTicks;

    public Lockable(Cuboid6i box, Lock lock, Orientation orient, ItemStack stack, World world) {
        this(box, lock, orient, stack, ((ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null)).nextId());
    }

    public Lockable(Cuboid6i box, Lock lock, Orientation orient, ItemStack stack, int networkID) {
        this.box = box;
        this.lock = lock;
        lock.addObserver(this);
        this.orient = orient;
        this.stack = stack;
        this.networkID = networkID;
    }

    @Override
    public void update(Observable lock, Object data) {
        this.setChanged();
        this.notifyObservers();
        LockItem.setOpen(this.stack, !this.lock.locked);
    }

    public void tick() {
        this.prevShakeTicks = this.shakeTicks;
        if (this.shakeTicks > 0) {
            --this.shakeTicks;
        }
    }

    public void shake(int ticks) {
        this.prevShakeTicks = this.maxShakeTicks = ticks;
        this.shakeTicks = this.maxShakeTicks;
    }

    public State getLockState(World world) {
        Vec3d vec3d;
        ArrayList<IBlockState> states = new ArrayList<IBlockState>(this.box.volume());
        for (BlockPos blockPos : this.box.getContainedBlockPositions()) {
            if (!world.isBlockLoaded(blockPos)) {
                return null;
            }
            states.add(world.getBlockState(blockPos));
        }
        State state = this.cache.get(states);
        if (state != null) {
            return state;
        }
        ArrayList<AxisAlignedBB> arrayList = new ArrayList<AxisAlignedBB>(4);
        for (BlockPos blockPos : this.box.getContainedBlockPositions()) {
            AxisAlignedBB box = world.getBlockState(blockPos).getCollisionBoundingBox((IBlockAccess)world, blockPos);
            if (box == null || box == Block.NULL_AABB) continue;
            AxisAlignedBB union = box = box.offset(blockPos);
            Iterator iterator = arrayList.iterator();
            while (iterator.hasNext()) {
                AxisAlignedBB box1 = (AxisAlignedBB)iterator.next();
                if (!LocksUtil.intersectsInclusive(union, box1)) continue;
                union = union.union(box1);
                iterator.remove();
            }
            arrayList.add(union);
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        EnumFacing side = this.orient.getCuboidFace();
        Vec3d point = vec3d = this.box.getSideCenter(side);
        double min = -1.0;
        for (AxisAlignedBB box : arrayList) {
            for (EnumFacing side1 : EnumFacing.values()) {
                Vec3d point1 = LocksUtil.getAABBSideCenter(box, side1).add(new Vec3d(side1.getDirectionVec()).scale(0.05));
                double dist = vec3d.squareDistanceTo(point1);
                if (min != -1.0 && dist >= min) continue;
                point = point1;
                min = dist;
                side = side1;
            }
        }
        state = new State(point, Orientation.fromDirection(side, this.orient.dir));
        this.cache.put(states, state);
        return state;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Lockable)) {
            return false;
        }
        Lockable lockable = (Lockable)object;
        return this.networkID == lockable.networkID && (this.box == null && lockable.box == null || this.box.equals(lockable.box)) && (this.lock == null && lockable.lock == null || this.lock.equals(lockable.lock)) && this.orient == lockable.orient;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.box, this.lock, this.orient, this.networkID});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lockable{");
        sb.append("networkID: ");
        sb.append(this.networkID);
        sb.append(", ");
        sb.append(this.box);
        sb.append(", ");
        sb.append(this.lock);
        sb.append(", ");
        sb.append((Object)this.orient);
        sb.append("}");
        return sb.toString();
    }

    public static class State {
        public static final AxisAlignedBB VERT_Z_BB = new AxisAlignedBB(-0.125, -0.1875, 0.03125, 0.125, 0.1875, 0.03125);
        public static final AxisAlignedBB VERT_X_BB = LocksUtil.rotateY(VERT_Z_BB);
        public static final AxisAlignedBB HOR_Z_BB = LocksUtil.rotateX(VERT_Z_BB);
        public static final AxisAlignedBB HOR_X_BB = LocksUtil.rotateY(HOR_Z_BB);
        public final Vec3d pos;
        public final Orientation orient;
        public final AxisAlignedBB bb;

        public State(Vec3d pos, Orientation orient) {
            this(pos, orient, (orient.face == AttachFace.WALL ? (orient.dir.getAxis() == EnumFacing.Axis.Z ? VERT_Z_BB : VERT_X_BB) : (orient.dir.getAxis() == EnumFacing.Axis.Z ? HOR_Z_BB : HOR_X_BB)).offset(pos));
        }

        public State(Vec3d pos, Orientation orient, AxisAlignedBB bounds) {
            this.pos = pos;
            this.orient = orient;
            this.bb = bounds;
        }

        @SideOnly(value=Side.CLIENT)
        public boolean inView(ClippingHelper clippingHelper, Vec3d origin) {
            return clippingHelper.isBoxInFrustum(this.bb.minX - origin.x, this.bb.minY - origin.y, this.bb.minZ - origin.z, this.bb.maxX - origin.x, this.bb.maxY - origin.y, this.bb.maxZ - origin.z);
        }

        @SideOnly(value=Side.CLIENT)
        public boolean inRange(Vec3d pos) {
            double max;
            Minecraft mc = Minecraft.getMinecraft();
            double dist = this.pos.squareDistanceTo(pos);
            return dist < (max = (double)(mc.gameSettings.renderDistanceChunks * 8)) * max;
        }
    }
}

