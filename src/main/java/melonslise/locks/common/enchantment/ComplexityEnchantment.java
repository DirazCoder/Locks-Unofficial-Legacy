/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.Enchantment$Rarity
 *  net.minecraft.inventory.EntityEquipmentSlot
 */
package melonslise.locks.common.enchantment;

import melonslise.locks.common.init.LocksEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ComplexityEnchantment
extends Enchantment {
    public ComplexityEnchantment() {
        super(Enchantment.Rarity.VERY_RARE, LocksEnchantments.LOCK_TYPE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
    }

    public int getMinEnchantability(int level) {
        return 7 + level * 10;
    }

    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 15;
    }

    public int getMaxLevel() {
        return 3;
    }
}

