/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.ICommand
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLServerStartingEvent
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package melonslise.locks;

import melonslise.locks.common.command.CommandLocksDebug;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksNetworks;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Requires MixinBooter: since build.gradle stopped shading its own copy
// of Mixin (needed to fix an AP/MixinGradle version-pairing bug - see
// build.gradle's own comments on mixin:0.8), this mod now relies on
// MixinBooter to bootstrap the shared Mixin subsystem and read this
// jar's MixinConfigs manifest attribute. Without it, Forge fails at
// launch with a bare ClassNotFoundException on MixinTweaker rather than
// a readable error, so declare the dependency explicitly.
@Mod(modid="locks", name="Locks", version="3.0.0", acceptedMinecraftVersions="1.12.2", dependencies="required-after:mixinbooter")
public final class Locks {
    public static final String ID = "locks";
    public static final String NAME = "Locks";
    public static final String VERSION = "3.0.0";
    public static final String GAMEVERSIONS = "1.12.2";
    @Mod.Instance(value="locks")
    public static Locks instance = null;
    public static boolean debug = false;
    public static Logger logger = LogManager.getLogger((String)"Locks");

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        LocksCapabilities.register();
        LocksNetworks.register();
        LocksConfig.init();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)new CommandLocksDebug());
    }
}