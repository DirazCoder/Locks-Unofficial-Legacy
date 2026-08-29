/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.common.config.Config
 *  net.minecraftforge.common.config.Config$Comment
 *  net.minecraftforge.common.config.Config$Ignore
 *  net.minecraftforge.common.config.Config$Name
 *  net.minecraftforge.common.config.Config$RangeDouble
 *  net.minecraftforge.fml.common.registry.ForgeRegistries
 */
package melonslise.locks.common.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Config(modid="locks")
public final class LocksConfig {
    public static final Common COMMON = new Common();
    public static final Server SERVER = new Server();
    protected static final Server SERVER_CLIENT = new Server();
    public static final Client CLIENT = new Client();
    @Config.Ignore
    public static NavigableMap<Integer, Integer> weightedGeneratedPins;
    @Config.Ignore
    public static NavigableMap<Integer, Item> weightedGeneratedLocks;
    @Config.Ignore
    public static int weightTotal;
    @Config.Ignore
    public static Set<String> alwaysGenerateItems;
    @Config.Ignore
    public static Set<String> skipGeneratingItems;

    public static Server getServer(World world) {
        return world.isRemote ? SERVER_CLIENT : SERVER;
    }

    public static Server getServerClient() {
        return SERVER_CLIENT;
    }

    private LocksConfig() {
    }

    public static void init() {
        int a;
        weightedGeneratedLocks = new TreeMap<Integer, Item>();
        weightedGeneratedPins = new TreeMap<Integer, Integer>();
        alwaysGenerateItems = new HashSet<String>();
        skipGeneratingItems = new HashSet<String>();
        weightTotal = 0;
        String[] locks = LocksConfig.COMMON.generatedLocks;
        int[] weights = LocksConfig.COMMON.generatedLockWeights;
        int[] pins = LocksConfig.COMMON.generatedLockPins;
        for (a = 0; a < locks.length; ++a) {
            weightedGeneratedLocks.put(weightTotal += weights[a], (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(locks[a])));
            weightedGeneratedPins.put(weightTotal, pins[a]);
        }
        for (a = 0; a < LocksConfig.COMMON.chestItemListAlwaysGenerate.length; ++a) {
            alwaysGenerateItems.add(LocksConfig.COMMON.chestItemListAlwaysGenerate[a].trim());
        }
        for (a = 0; a < LocksConfig.COMMON.chestItemListSkipGenerating.length; ++a) {
            skipGeneratingItems.add(LocksConfig.COMMON.chestItemListSkipGenerating[a].trim());
        }
    }

    public static boolean isItemAlwaysLocked(ItemStack stack) {
        String regName = stack.getItem().getRegistryName().toString();
        String regNameMeta = regName + ":" + stack.getMetadata();
        return alwaysGenerateItems.contains(regName) || alwaysGenerateItems.contains(regNameMeta);
    }

    public static boolean isItemSkipped(ItemStack stack) {
        String regName = stack.getItem().getRegistryName().toString();
        String regNameMeta = regName + ":" + stack.getMetadata();
        return skipGeneratingItems.contains(regName) || skipGeneratingItems.contains(regNameMeta);
    }

    public static boolean canGen(Random rng) {
        return LocksUtil.chance(rng, LocksConfig.COMMON.generationChance);
    }

    public static boolean canEnchant(Random rng) {
        return LocksUtil.chance(rng, LocksConfig.COMMON.generationEnchantChance);
    }

    public static ItemStack getRandomLock(Random rng) {
        int rngNumber = rng.nextInt(weightTotal) + 1;
        ItemStack stack = new ItemStack(weightedGeneratedLocks.ceilingEntry(rngNumber).getValue());
        int pinout = MathHelper.clamp((int)weightedGeneratedPins.ceilingEntry(rngNumber).getValue(), (int)0, (int)30);
        if (pinout != 0) {
            boolean replacePins = true;
            if (stack.getItem() instanceof LockItem && ((LockItem)stack.getItem()).length == pinout) {
                replacePins = false;
            }
            if (replacePins) {
                NBTTagCompound nbt = LocksUtil.getTag(stack);
                nbt.setByte("Length", (byte)pinout);
            }
        }
        return LocksConfig.canEnchant(rng) ? EnchantmentHelper.addRandomEnchantment((Random)rng, (ItemStack)stack, (int)(5 + rng.nextInt(30)), (boolean)false) : stack;
    }

    public static class Client {
        @Config.Name(value="Deaf Mode")
        @Config.Comment(value={"Display visual feedback when trying to use a locked block for certain hearing impaired individuals"})
        public boolean deafMode = true;
    }

    public static class Server {
        @Config.Name(value="Max Lockable Volume")
        @Config.Comment(value={"Maximum amount of blocks that can be locked at once"})
        public int maxLockableVolume = 6;
        @Config.Name(value="Lockable Blocks")
        @Config.Comment(value={"Blocks that can be locked. Each entry is the mod domain followed by the block's registry name. Can include regular expressions"})
        public String[] lockableBlocks = new String[]{"minecraft:.*chest", "minecraft:dispenser", "minecraft:dropper", "minecraft:hopper", "minecraft:.*door", "minecraft:.*trapdoor", "minecraft:.*fence_gate", "minecraft:.*shulker_box"};
        @Config.Name(value="Allow Removing Locks")
        @Config.Comment(value={"Open locks can be removed with an empty hand while sneaking"})
        public boolean allowRemovingLocks = true;
        @Config.Name(value="Protect Lockables")
        @Config.Comment(value={"Locked blocks cannot be destroyed in survival mode"})
        public boolean protectLockables = true;
        @Config.Name(value="Easy Lock")
        @Config.Comment(value={"Lock blocks with just one click! It's magic! (Will probably fail spectacularly with custom doors, custom double chests, etc)"})
        public boolean easyLock = true;
        @Config.Ignore
        public Pattern[] lockableBlocksRegex;

        public void init() {
            this.lockableBlocksRegex = (Pattern[])Arrays.stream(this.lockableBlocks).map(regex -> Pattern.compile(regex)).toArray(Pattern[]::new);
        }

        public boolean canLock(World world, BlockPos pos) {
            if (this.lockableBlocksRegex == null) {
                this.init();
            }
            for (Pattern pat : this.lockableBlocksRegex) {
                if (!pat.matcher(world.getBlockState(pos).getBlock().getRegistryName().toString()).matches()) continue;
                return true;
            }
            return false;
        }
    }

    public static class Common {
        @Config.Name(value="Generation Chance")
        @Config.Comment(value={"Chance to generate a random lock on every new chest during world generation. Set to 0 to disable"})
        @Config.RangeDouble(min=0.0, max=1.0)
        public double generationChance = 0.85;
        @Config.Name(value="Generation Enchant Chance")
        @Config.Comment(value={"Chance to randomly enchant a generated lock during world generation. Set to 0 to disable"})
        @Config.RangeDouble(min=0.0, max=1.0)
        public double generationEnchantChance = 0.4;
        @Config.Name(value="Generated Locks")
        @Config.Comment(value={"Items that can be generated as locks (must be instance of LockItem in code!)"})
        public String[] generatedLocks = new String[]{"locks:wood_lock", "locks:iron_lock", "locks:steel_lock", "locks:gold_lock", "locks:diamond_lock"};
        @Config.Name(value="Generated Lock Chances")
        @Config.Comment(value={"WARNING: THE AMOUNT OF NUMBERS SHOULD BE EQUAL TO THE AMOUNT OF GENERATED LOCK ITEMS!!! The relative probability that the corresponding lock item will be generated on a chest. Higher number = higher chance to generate"})
        public int[] generatedLockWeights = new int[]{3, 3, 2, 2, 1};
        @Config.Name(value="Generated Lock Pins")
        @Config.Comment(value={"WARNING: THE AMOUNT OF NUMBERS SHOULD BE EQUAL TO THE AMOUNT OF GENERATED LOCK ITEMS!!! The number of pins on the lock item. Overrides the defaults if not zero"})
        public int[] generatedLockPins = new int[]{0, 0, 0, 0, 0};
        @Config.Name(value="Automatically Orient Placed Locks")
        @Config.Comment(value={"Placed locks will try to orient themselves smartly to doors and chests regardless of how they were placed"})
        public boolean automaticallyOrientPlacedLocks = false;
        @Config.Name(value="Skip Generation Empty Chests")
        @Config.Comment(value={"Skip generating locks on empty chests"})
        public boolean skipGenerationEmptyChests = true;
        @Config.Name(value="Chest Item List Always Generate")
        @Config.Comment(value={"Always generate locks if the chests contain these items. Metadata can be specified (ex. minecraft:bed:0)"})
        public String[] chestItemListAlwaysGenerate = new String[]{"minecraft:nether_star"};
        @Config.Name(value="Chest Item List Skip Generating")
        @Config.Comment(value={"Worldgen assumes chests with only these items are empty. Metadata can be specified (ex. minecraft:bed:0)"})
        public String[] chestItemListSkipGenerating = new String[]{"minecraft:beetroot_seeds", "minecraft:bone", "minecraft:book", "minecraft:bowl", "minecraft:bread", "minecraft:brown_mushroom", "minecraft:clay_ball", "minecraft:cobblestone", "minecraft:dirt", "minecraft:dye", "minecraft:egg", "minecraft:gravel", "minecraft:hay_block", "minecraft:melon_seeds", "minecraft:painting", "minecraft:paper", "minecraft:pumpkin_seeds", "minecraft:red_mushroom", "minecraft:rotten_flesh", "minecraft:sapling", "minecraft:sign", "minecraft:spider_eye", "minecraft:stick", "minecraft:stone", "minecraft:stone_button", "minecraft:string", "minecraft:vine", "minecraft:waterlily", "minecraft:web", "minecraft:wheat", "minecraft:wheat_seeds", "minecraft:wooden_button"};
    }
}

