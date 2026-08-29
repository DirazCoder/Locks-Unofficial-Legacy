/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraftforge.common.util.INBTSerializable
 */
package melonslise.locks.common.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import melonslise.locks.common.util.Lockable;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public interface ILockableStorage
extends INBTSerializable<NBTTagList> {
    public Int2ObjectMap<Lockable> get();

    public void add(Lockable var1);

    public void remove(int var1);
}

