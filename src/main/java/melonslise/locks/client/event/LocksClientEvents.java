/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderGlobal
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.renderer.culling.ClippingHelperImpl
 *  net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
 *  net.minecraft.item.Item
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.ModelRegistryEvent
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.relauncher.Side
 */
package melonslise.locks.client.event;

import melonslise.locks.client.util.LocksClientUtil;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ISelection;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksItems;
import melonslise.locks.common.util.AttachFace;
import melonslise.locks.common.util.Cuboid6i;
import melonslise.locks.common.util.Lockable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="locks", value={Side.CLIENT})
public final class LocksClientEvents {
    private LocksClientEvents() {
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : LocksItems.ITEMS) {
            ModelLoader.setCustomModelResourceLocation((Item)item, (int)0, (ModelResourceLocation)new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.END || mc.world == null || mc.isGamePaused()) {
            return;
        }
        ((ILockableHandler)mc.world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null)).getLoaded().values().forEach(lockable -> {
            if (lockable.box.loaded((World)mc.world)) {
                lockable.tick();
            }
        });
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3d origin = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        ILockableHandler lockables = (ILockableHandler)mc.world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
        GlStateManager.enableRescaleNormal();
        for (Lockable lockable : lockables.getLoaded().values()) {
            Lockable.State state = lockable.getLockState((World)mc.world);
            if (state == null || !state.inRange(origin) || !state.inView(ClippingHelperImpl.getInstance(), origin)) continue;
            GlStateManager.pushMatrix();
            GlStateManager.translate((double)(state.pos.x - origin.x), (double)(state.pos.y - origin.y), (double)(state.pos.z - origin.z));
            GlStateManager.rotate((float)(-state.orient.dir.getHorizontalAngle() - 180.0f), (float)0.0f, (float)1.0f, (float)0.0f);
            if (state.orient.face != AttachFace.WALL) {
                GlStateManager.rotate((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            }
            GlStateManager.translate((double)0.0, (double)0.1, (double)0.0);
            GlStateManager.rotate((float)(MathHelper.sin((float)(LocksClientUtil.cubicBezier1d(1.0f, 1.0f, LocksClientUtil.lerp(lockable.maxShakeTicks - lockable.prevShakeTicks, lockable.maxShakeTicks - lockable.shakeTicks, event.getPartialTicks()) / (float)lockable.maxShakeTicks) * (float)lockable.maxShakeTicks / 5.0f * 3.14f)) * 10.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            GlStateManager.translate((double)0.0, (double)-0.1, (double)0.0);
            GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
            int light = mc.world.getCombinedLight((BlockPos)mutPos.setPos(state.pos.x, state.pos.y, state.pos.z), 0);
            OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)(light % 65536), (float)(light / 65536));
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            mc.entityRenderer.enableLightmap();
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().renderItem(lockable.stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            mc.entityRenderer.disableLightmap();
            GlStateManager.popMatrix();
        }
        GlStateManager.disableRescaleNormal();
        ISelection select = (ISelection)mc.player.getCapability(LocksCapabilities.SELECTION, null);
        BlockPos pos1 = select.get();
        if (pos1 == null) {
            return;
        }
        BlockPos pos2 = mc.objectMouseOver.getBlockPos() != null ? mc.objectMouseOver.getBlockPos() : pos1;
        Cuboid6i box = new Cuboid6i(pos1, pos2);
        float r = 0.0f;
        float g = 0.0f;
        LocksConfig.Server cfg = LocksConfig.getServer((World)mc.world);
        if (box.volume() > cfg.maxLockableVolume || !cfg.canLock((World)mc.world, pos2)) {
            r = 1.0f;
        } else {
            g = 1.0f;
        }
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth((float)1.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.disableDepth();
        mc.entityRenderer.disableLightmap();
        RenderGlobal.drawBoundingBox((double)((double)box.x1 - origin.x), (double)((double)box.y1 - origin.y), (double)((double)box.z1 - origin.z), (double)((double)box.x2 - origin.x), (double)((double)box.y2 - origin.y), (double)((double)box.z2 - origin.z), (float)r, (float)g, (float)0.0f, (float)0.5f);
        GlStateManager.enableDepth();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
    }
}

