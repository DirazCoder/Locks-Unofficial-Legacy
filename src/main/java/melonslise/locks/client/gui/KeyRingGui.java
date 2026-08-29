/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.client.gui;

import melonslise.locks.common.container.KeyRingContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class KeyRingGui
extends GuiContainer {
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    public final String title;
    public final String invTitle;

    public KeyRingGui(KeyRingContainer container) {
        super((Container)container);
        this.title = container.stack.getDisplayName();
        this.invTitle = container.player.inventory.getDisplayName().getFormattedText();
        this.ySize = 114 + container.rows * 18;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.title, 8, 6, 0x404040);
        this.fontRenderer.drawString(this.invTitle, 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        int rows = ((KeyRingContainer)this.inventorySlots).rows;
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, rows * 18 + 17);
        this.drawTexturedModalRect(cornerX, cornerY + rows * 18 + 17, 0, 126, this.xSize, 96);
    }
}

