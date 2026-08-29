/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.nbt.NBTTagInt
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.common.util.INBTSerializable
 */
package melonslise.locks.common.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Observer;
import melonslise.locks.common.util.Lockable;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public interface ILockableHandler
extends INBTSerializable<NBTTagInt>,
Observer {
    public int nextId();

    public Int2ObjectMap<Lockable> getLoaded();

    public Int2ObjectMap<Lockable> getInChunk(BlockPos var1);

    public boolean add(Lockable var1);

    public boolean remove(int var1);
}

