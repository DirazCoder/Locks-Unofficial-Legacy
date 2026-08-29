/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityLockableLoot
 *  net.minecraft.util.NonNullList
 */
package melonslise.locks.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={TileEntityLockableLoot.class})
public interface TileEntityLockableLootAccessor {
    @Invoker(value="getItems")
    public NonNullList<ItemStack> getItems();
}

