/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin$MCVersion
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin$SortingIndex
 */
package melonslise.locks.mixin;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion(value="1.12.2")
@IFMLLoadingPlugin.SortingIndex(value=-5000)
public class MixinLoader
implements IFMLLoadingPlugin {
    // MixinBooter (present in this runtime environment) already calls
    // MixinBootstrap.init() once and reads the MixinConfigs manifest
    // attribute (declared in build.gradle's jar{} block) to register
    // mixins.locks.json itself. Doing that again here - as the original
    // jar's self-contained/shaded-Mixin build needed to - collides with
    // MixinBooter's own bootstrap and silently prevents this mod's
    // mixins from ever being added (confirmed: "Adding [mixins.locks.json]
    // mixin configuration" never appears in the log, unlike every other
    // mod present). This class stays only because the jar manifest's
    // FMLCorePlugin entry still points at it.
    public MixinLoader() {
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }
}