/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.Capability$IStorage
 */
package melonslise.locks.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class EmptyCapabilityStorage<A>
implements Capability.IStorage<A> {
    public NBTBase writeNBT(Capability<A> cap, A inst, EnumFacing side) {
        return null;
    }

    public void readNBT(Capability<A> cap, A inst, EnumFacing side, NBTBase nbt) {
    }
}

