/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.Capability$IStorage
 *  net.minecraftforge.common.util.INBTSerializable
 */
package melonslise.locks.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityStorage<A extends INBTSerializable>
implements Capability.IStorage<A> {
    public NBTBase writeNBT(Capability<A> cap, A inst, EnumFacing side) {
        return inst.serializeNBT();
    }

    public void readNBT(Capability<A> cap, A inst, EnumFacing side, NBTBase nbt) {
        inst.deserializeNBT(nbt);
    }
}

