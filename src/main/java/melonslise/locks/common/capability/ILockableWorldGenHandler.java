/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagInt
 *  net.minecraftforge.common.util.INBTSerializable
 */
package melonslise.locks.common.capability;

import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.INBTSerializable;

public interface ILockableWorldGenHandler
extends INBTSerializable<NBTTagInt> {
    public void setChunkShouldGenerateChests();

    public void tryGeneratingLocks();
}

