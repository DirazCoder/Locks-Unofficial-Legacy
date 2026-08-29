/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraftforge.event.RegistryEvent$Register
 *  net.minecraftforge.oredict.OreDictionary
 *  net.minecraftforge.registries.IForgeRegistryEntry
 */
package melonslise.locks.common.init;

import java.util.ArrayList;
import java.util.List;
import melonslise.locks.common.init.LocksCreativeTabs;
import melonslise.locks.common.item.KeyItem;
import melonslise.locks.common.item.KeyRingItem;
import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.item.LockPickItem;
import melonslise.locks.common.item.MasterKeyItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class LocksItems {
    public static final List<Item> ITEMS = new ArrayList<Item>(18);
    public static final String OREDICT_LOCK = "locksLock";
    public static final String OREDICT_LOCKPICK = "locksLockPick";
    public static final Item SPRING = LocksItems.add("spring", new Item().setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item WOOD_LOCK_MECHANISM = LocksItems.add("wood_lock_mechanism", new Item().setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item IRON_LOCK_MECHANISM = LocksItems.add("iron_lock_mechanism", new Item().setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item STEEL_LOCK_MECHANISM = LocksItems.add("steel_lock_mechanism", new Item().setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item KEY_BLANK = LocksItems.add("key_blank", new Item().setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item WOOD_LOCK = LocksItems.add("wood_lock", new LockItem(5, 15, 4.0f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item IRON_LOCK = LocksItems.add("iron_lock", new LockItem(7, 14, 12.0f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item STEEL_LOCK = LocksItems.add("steel_lock", new LockItem(9, 12, 20.0f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item GOLD_LOCK = LocksItems.add("gold_lock", new LockItem(6, 22, 6.0f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item DIAMOND_LOCK = LocksItems.add("diamond_lock", new LockItem(11, 10, 100.0f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item KEY = LocksItems.add("key", new KeyItem().setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item MASTER_KEY = LocksItems.add("master_key", new MasterKeyItem().setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item KEY_RING = LocksItems.add("key_ring", new KeyRingItem(1).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item WOOD_LOCK_PICK = LocksItems.add("wood_lock_pick", new LockPickItem(0.2f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item IRON_LOCK_PICK = LocksItems.add("iron_lock_pick", new LockPickItem(0.35f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item STEEL_LOCK_PICK = LocksItems.add("steel_lock_pick", new LockPickItem(0.7f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item GOLD_LOCK_PICK = LocksItems.add("gold_lock_pick", new LockPickItem(0.25f).setCreativeTab(LocksCreativeTabs.TAB));
    public static final Item DIAMOND_LOCK_PICK = LocksItems.add("diamond_lock_pick", new LockPickItem(0.85f).setCreativeTab(LocksCreativeTabs.TAB));

    private LocksItems() {
    }

    public static void register(RegistryEvent.Register<Item> event) {
        for (Item item : ITEMS) {
            event.getRegistry().register(item);
        }
        OreDictionary.registerOre((String)OREDICT_LOCK, (Item)WOOD_LOCK);
        OreDictionary.registerOre((String)OREDICT_LOCK, (Item)IRON_LOCK);
        OreDictionary.registerOre((String)OREDICT_LOCK, (Item)STEEL_LOCK);
        OreDictionary.registerOre((String)OREDICT_LOCK, (Item)GOLD_LOCK);
        OreDictionary.registerOre((String)OREDICT_LOCK, (Item)DIAMOND_LOCK);
        OreDictionary.registerOre((String)OREDICT_LOCKPICK, (Item)WOOD_LOCK_PICK);
        OreDictionary.registerOre((String)OREDICT_LOCKPICK, (Item)IRON_LOCK_PICK);
        OreDictionary.registerOre((String)OREDICT_LOCKPICK, (Item)STEEL_LOCK_PICK);
        OreDictionary.registerOre((String)OREDICT_LOCKPICK, (Item)GOLD_LOCK_PICK);
        OreDictionary.registerOre((String)OREDICT_LOCKPICK, (Item)DIAMOND_LOCK_PICK);
    }

    public static Item add(String name, Item item) {
        ITEMS.add(((Item)item.setRegistryName("locks", name)).setUnlocalizedName("locks." + name));
        return item;
    }
}

