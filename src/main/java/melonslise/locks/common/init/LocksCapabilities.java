/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.CapabilityInject
 *  net.minecraftforge.common.capabilities.CapabilityManager
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 */
package melonslise.locks.common.init;

import melonslise.locks.common.capability.CapabilityProvider;
import melonslise.locks.common.capability.CapabilityStorage;
import melonslise.locks.common.capability.EmptyCapabilityStorage;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ILockableStorage;
import melonslise.locks.common.capability.ILockableWorldGenHandler;
import melonslise.locks.common.capability.ISelection;
import melonslise.locks.common.capability.LockableHandler;
import melonslise.locks.common.capability.LockableStorage;
import melonslise.locks.common.capability.LockableWorldGenHandler;
import melonslise.locks.common.capability.Selection;
import melonslise.locks.common.capability.SerializableCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class LocksCapabilities {
    @CapabilityInject(value=ILockableHandler.class)
    public static final Capability<ILockableHandler> LOCKABLE_HANDLER = null;
    @CapabilityInject(value=ILockableStorage.class)
    public static final Capability<ILockableStorage> LOCKABLE_STORAGE = null;
    @CapabilityInject(value=ILockableWorldGenHandler.class)
    public static final Capability<ILockableWorldGenHandler> LOCKABLE_WORLDGEN_HANDLER = null;
    @CapabilityInject(value=ISelection.class)
    public static final Capability<ISelection> SELECTION = null;

    private LocksCapabilities() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ILockableHandler.class, new CapabilityStorage(), () -> null);
        CapabilityManager.INSTANCE.register(ILockableStorage.class, new CapabilityStorage(), () -> null);
        CapabilityManager.INSTANCE.register(ILockableWorldGenHandler.class, new CapabilityStorage(), () -> null);
        CapabilityManager.INSTANCE.register(ISelection.class, new EmptyCapabilityStorage(), Selection::new);
    }

    public static void attachToWorld(AttachCapabilitiesEvent<World> event) {
        event.addCapability(LockableHandler.ID, new SerializableCapabilityProvider<LockableHandler>((Capability)LOCKABLE_HANDLER, new LockableHandler((World)event.getObject())));
    }

    public static void attachToChunk(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(LockableStorage.ID, new SerializableCapabilityProvider<LockableStorage>((Capability)LOCKABLE_STORAGE, new LockableStorage((Chunk)event.getObject())));
        event.addCapability(LockableWorldGenHandler.ID, new SerializableCapabilityProvider<LockableWorldGenHandler>((Capability)LOCKABLE_WORLDGEN_HANDLER, new LockableWorldGenHandler((Chunk)event.getObject())));
    }

    public static void attachToEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(Selection.ID, new CapabilityProvider<ISelection>(SELECTION, new Selection()));
        }
    }
}

