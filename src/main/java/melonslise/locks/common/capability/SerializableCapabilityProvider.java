/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.util.INBTSerializable
 */
package melonslise.locks.common.capability;

import melonslise.locks.common.capability.CapabilityProvider;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class SerializableCapabilityProvider<A>
extends CapabilityProvider<A>
implements INBTSerializable {
    public SerializableCapabilityProvider(Capability cap, A inst) {
        super(cap, inst);
    }

    public NBTBase serializeNBT() {
        return this.cap.getStorage().writeNBT(this.cap, this.inst, null);
    }

    public void deserializeNBT(NBTBase nbt) {
        this.cap.getStorage().readNBT(this.cap, this.inst, null, nbt);
    }
}

