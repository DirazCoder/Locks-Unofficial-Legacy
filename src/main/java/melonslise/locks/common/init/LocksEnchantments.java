/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnumEnchantmentType
 *  net.minecraftforge.common.util.EnumHelper
 *  net.minecraftforge.event.RegistryEvent$Register
 *  net.minecraftforge.registries.IForgeRegistryEntry
 */
package melonslise.locks.common.init;

import java.util.ArrayList;
import java.util.List;
import melonslise.locks.common.enchantment.ComplexityEnchantment;
import melonslise.locks.common.enchantment.ShockingEnchantment;
import melonslise.locks.common.enchantment.SturdyEnchantment;
import melonslise.locks.common.item.LockItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class LocksEnchantments {
    public static final EnumEnchantmentType LOCK_TYPE = EnumHelper.addEnchantmentType((String)"LOCK", item -> item instanceof LockItem);
    public static final List<Enchantment> ENCHANTMENTS = new ArrayList<Enchantment>(3);
    public static final Enchantment SHOCKING = LocksEnchantments.add("shocking", new ShockingEnchantment());
    public static final Enchantment STURDY = LocksEnchantments.add("sturdy", new SturdyEnchantment());
    public static final Enchantment COMPLEXITY = LocksEnchantments.add("complexity", new ComplexityEnchantment());

    public static void register(RegistryEvent.Register<Enchantment> event) {
        for (Enchantment ench : ENCHANTMENTS) {
            event.getRegistry().register(ench);
        }
    }

    public static Enchantment add(String name, Enchantment ench) {
        ENCHANTMENTS.add(((Enchantment)ench.setRegistryName("locks", name)).setName("locks." + name));
        return ench;
    }
}

