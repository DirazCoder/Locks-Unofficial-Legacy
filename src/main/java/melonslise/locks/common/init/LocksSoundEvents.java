/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 *  net.minecraftforge.event.RegistryEvent$Register
 *  net.minecraftforge.registries.IForgeRegistryEntry
 */
package melonslise.locks.common.init;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class LocksSoundEvents {
    private static final List<SoundEvent> SOUNDS = new ArrayList<SoundEvent>(6);
    public static final SoundEvent KEY_RING = LocksSoundEvents.add("key_ring");
    public static final SoundEvent LOCK_CLOSE = LocksSoundEvents.add("lock.close");
    public static final SoundEvent LOCK_OPEN = LocksSoundEvents.add("lock.open");
    public static final SoundEvent LOCK_RATTLE = LocksSoundEvents.add("lock.rattle");
    public static final SoundEvent PIN_FAIL = LocksSoundEvents.add("pin.fail");
    public static final SoundEvent PIN_MATCH = LocksSoundEvents.add("pin.match");
    public static final SoundEvent SHOCK = LocksSoundEvents.add("shock");

    private LocksSoundEvents() {
    }

    public static void register(RegistryEvent.Register<SoundEvent> event) {
        for (SoundEvent sound : SOUNDS) {
            event.getRegistry().register(sound);
        }
    }

    public static SoundEvent add(String name) {
        ResourceLocation rl = new ResourceLocation("locks", name);
        SoundEvent sound = (SoundEvent)new SoundEvent(rl).setRegistryName(rl);
        SOUNDS.add(sound);
        return sound;
    }
}

