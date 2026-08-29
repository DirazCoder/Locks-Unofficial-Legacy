/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.IWorldEventListener
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.common.config.Config$Type
 *  net.minecraftforge.common.config.ConfigManager
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 *  net.minecraftforge.event.RegistryEvent$Register
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.terraingen.PopulateChunkEvent$Post
 *  net.minecraftforge.event.world.BlockEvent$BreakEvent
 *  net.minecraftforge.event.world.ChunkEvent$Load
 *  net.minecraftforge.event.world.ChunkEvent$Unload
 *  net.minecraftforge.event.world.ChunkWatchEvent$UnWatch
 *  net.minecraftforge.event.world.ChunkWatchEvent$Watch
 *  net.minecraftforge.event.world.WorldEvent$Load
 *  net.minecraftforge.fml.client.event.ConfigChangedEvent$OnConfigChangedEvent
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerRespawnEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$PlayerTickEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package melonslise.locks.common.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import melonslise.locks.Locks;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ILockableStorage;
import melonslise.locks.common.capability.ILockableWorldGenHandler;
import melonslise.locks.common.capability.ISelection;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksEnchantments;
import melonslise.locks.common.init.LocksItems;
import melonslise.locks.common.init.LocksNetworks;
import melonslise.locks.common.init.LocksSoundEvents;
import melonslise.locks.common.item.KeyItem;
import melonslise.locks.common.item.KeyRingItem;
import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.item.LockPickItem;
import melonslise.locks.common.item.LockingItem;
import melonslise.locks.common.network.toclient.AddLockablePacket;
import melonslise.locks.common.network.toclient.ConfigSyncPacket;
import melonslise.locks.common.network.toclient.RemoveLockablePacket;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksPredicates;
import melonslise.locks.common.util.LocksUtil;
import melonslise.locks.common.world.LocksWorldEventListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@Mod.EventBusSubscriber(modid="locks")
public final class LocksEvents {
    public static final LocksWorldEventListener LISTENER = new LocksWorldEventListener();
    public static final ITextComponent LOCKED_MESSAGE = new TextComponentTranslation("locks.status.locked", new Object[0]);

    private LocksEvents() {
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        LocksItems.register(event);
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        LocksEnchantments.register(event);
    }

    @SubscribeEvent
    public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        LocksSoundEvents.register(event);
    }

    @SubscribeEvent
    public static void attachCapabilitiesToWorld(AttachCapabilitiesEvent<World> event) {
        LocksCapabilities.attachToWorld(event);
    }

    @SubscribeEvent
    public static void attachCapabilitiesToChunk(AttachCapabilitiesEvent<Chunk> event) {
        LocksCapabilities.attachToChunk(event);
    }

    @SubscribeEvent
    public static void attachCapabilitiesToEntity(AttachCapabilitiesEvent<Entity> event) {
        LocksCapabilities.attachToEntity(event);
    }

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!event.getModID().equals("locks")) {
            return;
        }
        ConfigManager.sync((String)"locks", (Config.Type)Config.Type.INSTANCE);
        LocksConfig.SERVER.init();
        LocksConfig.getServerClient().init();
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            return;
        }
        server.addScheduledTask(() -> LocksNetworks.MAIN.sendToAll((IMessage)new ConfigSyncPacket(LocksConfig.SERVER)));
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener((IWorldEventListener)LISTENER);
    }

    @SubscribeEvent
    public static void onChunkPopulate(PopulateChunkEvent.Post event) {
        Chunk chunk = event.getWorld().getChunkProvider().getLoadedChunk(event.getChunkX(), event.getChunkZ());
        if (chunk == null) {
            return;
        }
        ((ILockableWorldGenHandler)chunk.getCapability(LocksCapabilities.LOCKABLE_WORLDGEN_HANDLER, null)).setChunkShouldGenerateChests();
    }

    @SubscribeEvent
    public static void onPlayerLogIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        LocksNetworks.MAIN.sendTo((IMessage)new ConfigSyncPacket(LocksConfig.SERVER), (EntityPlayerMP)event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangeDim(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
    }

    @SubscribeEvent
    public static void onPlayerRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        ISelection select = (ISelection)event.player.getCapability(LocksCapabilities.SELECTION, null);
        if (select.get() == null) {
            return;
        }
        for (ItemStack stack : event.player.getHeldEquipment()) {
            if (!(stack.getItem() instanceof LockItem)) continue;
            return;
        }
        select.set(null);
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();
        ILockableHandler handler = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
        List<Lockable> intersecting = handler.getInChunk(pos).values().stream().filter(lockable1 -> lockable1.box.intersects(pos)).collect(Collectors.toList());
        if (intersecting.isEmpty()) {
            return;
        }
        if (event.getHand() != EnumHand.MAIN_HAND) {
            event.setUseBlock(Event.Result.DENY);
            return;
        }
        Optional<Lockable> locked = intersecting.stream().filter(LocksPredicates.LOCKED).findFirst();
        if (locked.isPresent()) {
            Lockable lkb = locked.get();
            event.setUseBlock(Event.Result.DENY);
            ItemStack stack = event.getItemStack();
            Item item = stack.getItem();
            if (!(item instanceof LockPickItem || item == LocksItems.MASTER_KEY || item instanceof KeyItem && LockingItem.getOrSetId(stack) == lkb.lock.id || item == LocksItems.KEY_RING && KeyRingItem.containsId(stack, lkb.lock.id))) {
                lkb.shake(20);
                world.playSound(null, pos, LocksSoundEvents.LOCK_RATTLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (world.isRemote && LocksConfig.CLIENT.deafMode) {
                    player.sendStatusMessage(LOCKED_MESSAGE, true);
                }
            }
            player.swingArm(EnumHand.MAIN_HAND);
            return;
        }
        if (LocksConfig.getServer((World)world).allowRemovingLocks && player.isSneaking() && event.getItemStack().isEmpty()) {
            List<Lockable> matching = intersecting.stream().filter(LocksPredicates.NOT_LOCKED).collect(Collectors.toList());
            if (matching.isEmpty()) {
                return;
            }
            event.setUseBlock(Event.Result.DENY);
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 0.8f, 0.8f + world.rand.nextFloat() * 0.4f);
            player.swingArm(EnumHand.MAIN_HAND);
            if (world.isRemote) {
                return;
            }
            for (Lockable lockable : matching) {
                world.spawnEntity((Entity)new EntityItem(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, lockable.stack));
                handler.remove(lockable.networkID);
            }
        }
    }

    public static boolean canBreakLockable(EntityPlayer player, BlockPos pos) {
        return !LocksConfig.getServer((World)player.world).protectLockables || player.isCreative() || !LocksUtil.locked(player.world, pos);
    }

    @SubscribeEvent
    public static void onBlockBreaking(PlayerEvent.BreakSpeed event) {
        if (!LocksEvents.canBreakLockable(event.getEntityPlayer(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!LocksEvents.canBreakLockable(event.getPlayer(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        EntityPlayerMP player = event.getPlayer();
        ((ILockableStorage)event.getChunkInstance().getCapability(LocksCapabilities.LOCKABLE_STORAGE, null)).get().values().stream().forEach(lkb -> LocksNetworks.MAIN.sendTo((IMessage)new AddLockablePacket((Lockable)lkb), player));
    }

    @SubscribeEvent
    public static void onChunkUnWatch(ChunkWatchEvent.UnWatch event) {
        EntityPlayerMP player = event.getPlayer();
        ((ILockableStorage)event.getChunkInstance().getCapability(LocksCapabilities.LOCKABLE_STORAGE, null)).get().keySet().stream().forEach(id -> LocksNetworks.MAIN.sendTo((IMessage)new RemoveLockablePacket((int)id), player));
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        Chunk ch = event.getChunk();
        World world = ch.getWorld();
        if (world.isRemote) {
            return;
        }
        ILockableHandler handler = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
        ((ILockableStorage)ch.getCapability(LocksCapabilities.LOCKABLE_STORAGE, null)).get().values().forEach(lkb -> {
            ChunkPos unloadPos = ch.getPos();
            List<ChunkPos> chunkList = lkb.box.containedChunkPosList();
            for (ChunkPos chunkPos : chunkList) {
                if (!unloadPos.equals((Object)chunkPos) && !LocksUtil.hasChunk(world, chunkPos.x, chunkPos.z)) continue;
            }
            handler.getLoaded().remove(lkb.networkID);
            lkb.deleteObserver(handler);
            if (Locks.debug) {
                Locks.logger.debug("Removing lockable from loaded with id: " + lkb.networkID + " ::: " + lkb.toString());
            }
        });
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        Chunk ch = event.getChunk();
        World world = ch.getWorld();
        if (world.isRemote) {
            return;
        }
        ILockableHandler handler = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
        Int2ObjectMap<Lockable> loadedLockables = handler.getLoaded();
        ((ILockableStorage)ch.getCapability(LocksCapabilities.LOCKABLE_STORAGE, null)).get().values().forEach(lkb -> {
            if (!loadedLockables.containsKey(lkb.networkID)) {
                lkb.addObserver(handler);
                loadedLockables.put(lkb.networkID, lkb);
                if (Locks.debug) {
                    Locks.logger.debug("Placing lockable into loaded with id: " + lkb.networkID + " ::: " + lkb.toString());
                }
            }
        });
    }
}

