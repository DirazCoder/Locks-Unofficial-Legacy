/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.enchantment.EnumEnchantmentType
 *  net.minecraft.item.ItemStack
 */
package melonslise.locks.common.init;

import melonslise.locks.common.init.LocksEnchantments;
import melonslise.locks.common.init.LocksItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public final class LocksCreativeTabs {
    public static final CreativeTabs TAB = new CreativeTabs("locks"){

        public ItemStack getTabIconItem() {
            return new ItemStack(LocksItems.IRON_LOCK);
        }
    }.setRelevantEnchantmentTypes(new EnumEnchantmentType[]{LocksEnchantments.LOCK_TYPE});

    private LocksCreativeTabs() {
    }
}

