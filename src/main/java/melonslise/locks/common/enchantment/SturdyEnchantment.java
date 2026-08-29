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

public class SturdyEnchantment
extends Enchantment {
    public SturdyEnchantment() {
        super(Enchantment.Rarity.RARE, LocksEnchantments.LOCK_TYPE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
    }

    public int getMinEnchantability(int level) {
        return 5 + (level - 1) * 15;
    }

    public int getMaxEnchantability(int level) {
        return 50;
    }

    public int getMaxLevel() {
        return 3;
    }
}

