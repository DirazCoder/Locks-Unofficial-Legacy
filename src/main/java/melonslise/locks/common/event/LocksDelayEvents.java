/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.event.world.ChunkWatchEvent$Watch
 *  net.minecraftforge.event.world.WorldEvent$Load
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 */
package melonslise.locks.common.event;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import melonslise.locks.common.capability.ILockableWorldGenHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid="locks")
public class LocksDelayEvents {
    private static Map<Integer, Map<ChunkPos, ChunkDelay>> chunksToPoll = new ConcurrentHashMap<Integer, Map<ChunkPos, ChunkDelay>>();
    public static int delay = 20;

    private LocksDelayEvents() {
    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        LocksDelayEvents.tryAddChunkToPoll(event.getChunkInstance());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.world.isRemote) {
            return;
        }
        LocksDelayEvents.pollChunksInWorld(event.world);
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote) {
            return;
        }
        LocksDelayEvents.clearDimensionInPollMap(event.getWorld());
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            return;
        }
        LocksDelayEvents.clearDimensionInPollMap(event.getWorld());
    }

    private static void tryAddChunkToPoll(Chunk c) {
        int dimension = c.getWorld().provider.getDimension();
        Map<ChunkPos, ChunkDelay> dimChunkMap = LocksDelayEvents.getChunksToPollMap(dimension);
        if (!dimChunkMap.containsKey(c.getPos())) {
            dimChunkMap.put(c.getPos(), new ChunkDelay(delay));
        }
    }

    private static void clearDimensionInPollMap(World world) {
        int dimension = world.provider.getDimension();
        Map<ChunkPos, ChunkDelay> dimChunkMap = LocksDelayEvents.getChunksToPollMap(dimension);
        dimChunkMap.clear();
    }

    private static void pollChunksInWorld(World world) {
        int dimension = world.provider.getDimension();
        Map<ChunkPos, ChunkDelay> dimChunkMap = LocksDelayEvents.getChunksToPollMap(dimension);
        Iterator<Map.Entry<ChunkPos, ChunkDelay>> it = dimChunkMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ChunkPos, ChunkDelay> entry = it.next();
            if (!entry.getValue().tick()) continue;
            ChunkPos cpos = entry.getKey();
            if (LocksUtil.hasChunk(world, cpos.x, cpos.z)) {
                if (LocksUtil.hasChunk(world, cpos.x + 1, cpos.z) && LocksUtil.hasChunk(world, cpos.x - 1, cpos.z) && LocksUtil.hasChunk(world, cpos.x, cpos.z - 1) && LocksUtil.hasChunk(world, cpos.x, cpos.z + 1)) {
                    ((ILockableWorldGenHandler)world.getChunkFromChunkCoords(cpos.x, cpos.z).getCapability(LocksCapabilities.LOCKABLE_WORLDGEN_HANDLER, null)).tryGeneratingLocks();
                    it.remove();
                    continue;
                }
                entry.getValue().reset();
                continue;
            }
            it.remove();
        }
    }

    @Nonnull
    private static Map<ChunkPos, ChunkDelay> getChunksToPollMap(int dimension) {
        Map<ChunkPos, ChunkDelay> dimChunkMap = chunksToPoll.get(dimension);
        if (dimChunkMap == null) {
            dimChunkMap = new ConcurrentHashMap<ChunkPos, ChunkDelay>();
            chunksToPoll.put(dimension, dimChunkMap);
        }
        return dimChunkMap;
    }

    private static class ChunkDelay {
        private int delay;

        public ChunkDelay(int delay) {
            this.delay = delay;
        }

        public boolean tick() {
            --this.delay;
            return this.delay <= 0;
        }

        public void reset() {
            this.delay = delay;
        }
    }
}

