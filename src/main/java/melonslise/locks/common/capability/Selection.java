/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 */
package melonslise.locks.common.capability;

import melonslise.locks.common.capability.ISelection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class Selection
implements ISelection {
    public static final ResourceLocation ID = new ResourceLocation("locks", "lock_selection");
    public BlockPos pos;

    @Override
    public BlockPos get() {
        return this.pos;
    }

    @Override
    public void set(BlockPos pos) {
        this.pos = pos;
    }
}

