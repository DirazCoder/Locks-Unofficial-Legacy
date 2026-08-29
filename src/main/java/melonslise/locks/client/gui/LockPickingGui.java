/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.input.Keyboard
 */
package melonslise.locks.client.gui;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import melonslise.locks.client.gui.sprite.SpringSprite;
import melonslise.locks.client.gui.sprite.Sprite;
import melonslise.locks.client.gui.sprite.Texture;
import melonslise.locks.client.gui.sprite.action.AccelerateAction;
import melonslise.locks.client.gui.sprite.action.FadeAction;
import melonslise.locks.client.gui.sprite.action.IAction;
import melonslise.locks.client.gui.sprite.action.MoveAction;
import melonslise.locks.client.gui.sprite.action.WaitAction;
import melonslise.locks.common.container.LockPickingContainer;
import melonslise.locks.common.init.LocksNetworks;
import melonslise.locks.common.network.toserver.CheckPinPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(value=Side.CLIENT)
public class LockPickingGui
extends GuiContainer {
    public static final ITextComponent HINT = new TextComponentTranslation("locks.gui.lockpicking.open", new Object[0]);
    public static final Texture FRONT_WALL_TEX = new Texture(6, 0, 4, 60, 48, 80);
    public static final Texture COLUMN_TEX = new Texture(10, 0, 8, 60, 48, 80);
    public static final Texture INNER_WALL_TEX = new Texture(18, 0, 4, 60, 48, 80);
    public static final Texture BACK_WALL_TEX = new Texture(22, 0, 4, 60, 48, 80);
    public static final Texture HANDLE_TEX = new Texture(26, 0, 19, 73, 48, 80);
    public static final Texture UPPER_PIN_TEX = new Texture(0, 0, 6, 8, 48, 80);
    public static final Texture LOCK_PICK_TEX = new Texture(0, 0, 160, 12, 160, 16);
    public static final Texture[] PIN_TUMBLER_TEX = new Texture[]{new Texture(0, 8, 6, 11, 48, 80), new Texture(0, 19, 6, 13, 48, 80), new Texture(0, 32, 6, 15, 48, 80)};
    public static final Texture[] SPRING_TEX = new Texture[]{new Texture(0, 57, 6, 23, 48, 80), new Texture(6, 60, 6, 20, 48, 80), new Texture(12, 64, 6, 16, 48, 80), new Texture(18, 69, 6, 11, 48, 80), new Texture(24, 73, 6, 7, 48, 80)};
    public final ResourceLocation lockTex;
    protected ResourceLocation pickTex;
    protected Collection<Sprite> sprites;
    protected Sprite lockPick;
    protected Sprite leftPickPart;
    protected Sprite rightPickPart;
    protected Sprite[] pinTumblers;
    protected Sprite[] upperPins;
    protected Sprite[] springs;
    public final BiConsumer<IAction<Sprite>, Sprite> unfreezeCb = (action, sprite) -> {
        this.frozen = false;
    };
    public final BiConsumer<IAction<Sprite>, Sprite> resetPickCb = (action, sprite) -> this.resetPick();
    public final int length;
    public final boolean[] pins;
    public final EnumHand hand;
    protected int currPin;
    protected boolean frozen = true;
    protected boolean holdingLeft;
    protected boolean holdingRight;

    public LockPickingGui(LockPickingContainer cont) {
        super((Container)cont);
        this.length = cont.lockable.lock.getLength();
        this.pins = new boolean[this.length];
        this.hand = cont.hand;
        this.lockTex = LockPickingGui.getTextureFor(cont.lockable.stack);
        this.xSize = LockPickingGui.FRONT_WALL_TEX.width + this.length * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width) + LockPickingGui.HANDLE_TEX.width;
        this.ySize = LockPickingGui.HANDLE_TEX.height;
        this.sprites = new ArrayDeque<Sprite>(this.length * 3 + 4);
        this.pinTumblers = new Sprite[this.length];
        this.upperPins = new Sprite[this.length];
        this.springs = new Sprite[this.length];
        for (int a = 0; a < this.pinTumblers.length; ++a) {
            int r = ThreadLocalRandom.current().nextInt(3);
            this.pinTumblers[a] = this.addSprite(new Sprite(PIN_TUMBLER_TEX[r]).position(LockPickingGui.FRONT_WALL_TEX.width + 1 + a * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width), 43 - LockPickingGui.PIN_TUMBLER_TEX[r].height));
            this.upperPins[a] = new Sprite(UPPER_PIN_TEX).position(LockPickingGui.FRONT_WALL_TEX.width + 1 + a * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width), 43 - LockPickingGui.PIN_TUMBLER_TEX[r].height - LockPickingGui.UPPER_PIN_TEX.height);
            this.springs[a] = this.addSprite(new SpringSprite(SPRING_TEX, this.upperPins[a]).position(LockPickingGui.FRONT_WALL_TEX.width + 1 + a * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width), 3.0f));
            this.addSprite(this.upperPins[a]);
        }
        this.lockPick = this.addSprite(new Sprite(LOCK_PICK_TEX).position(0.0f, -4 + LockPickingGui.COLUMN_TEX.height - LockPickingGui.LOCK_PICK_TEX.height));
        this.resetPick();
        this.rightPickPart = this.addSprite(new Sprite(new Texture(0, 0, 0, 12, 160, 16)).position(-10.0f, this.lockPick.posY).alpha(0.0f));
        this.leftPickPart = this.addSprite(new Sprite(new Texture(0, 0, 0, 12, 160, 16)).position(0.0f, this.lockPick.posY).rotation(-30.0f, -10.0f, this.lockPick.posY + 13.0f).alpha(0.0f));
        this.holdingLeft = false;
        this.holdingRight = false;
        this.xSize *= 2;
        this.ySize *= 2;
    }

    public static ResourceLocation getTextureFor(ItemStack stack) {
        return new ResourceLocation("locks", "textures/gui/" + stack.getItem().getRegistryName().getResourcePath() + ".png");
    }

    public Sprite addSprite(Sprite sprite) {
        this.sprites.add(sprite);
        return sprite;
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
    }

    public void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        float pt = this.mc.getRenderPartialTicks();
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(this.lockTex);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)cornerX, (float)cornerY, (float)0.0f);
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        FRONT_WALL_TEX.draw(0.0f, 0.0f, 1.0f);
        for (int a = 0; a < this.length; ++a) {
            COLUMN_TEX.draw(LockPickingGui.FRONT_WALL_TEX.width + a * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width), 0.0f, 1.0f);
            if (a == this.length - 1) continue;
            INNER_WALL_TEX.draw(LockPickingGui.FRONT_WALL_TEX.width + LockPickingGui.COLUMN_TEX.width + a * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width), 0.0f, 1.0f);
        }
        BACK_WALL_TEX.draw(this.length * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width), 0.0f, 1.0f);
        HANDLE_TEX.draw(LockPickingGui.BACK_WALL_TEX.width + this.length * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width), 2.0f, 1.0f);
        for (Sprite sprite : this.sprites) {
            if (sprite == this.lockPick) {
                this.mc.getTextureManager().bindTexture(this.pickTex);
            }
            sprite.draw(pt);
        }
        GlStateManager.popMatrix();
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(LockPickingContainer.TITLE.getFormattedText(), 0, -this.fontRenderer.FONT_HEIGHT, 0xFFFFFF);
        if (((LockPickingContainer)this.inventorySlots).isOpen()) {
            this.fontRenderer.drawString(HINT.getFormattedText(), this.xSize / 2 - this.fontRenderer.getStringWidth(HINT.getFormattedText()) / 2, this.ySize + 10, 0xFFFFFF);
        }
    }

    public void updateScreen() {
        super.updateScreen();
        this.updateLockPickSpeed();
        for (Sprite sprite : this.sprites) {
            sprite.update();
        }
        if (!this.frozen) {
            this.boundLockPick();
        }
        this.updatePickParts();
    }

    protected void updatePickParts() {
        this.rightPickPart.posY = this.lockPick.posY;
        this.rightPickPart.tex.width = 10 + (int)this.lockPick.posX + this.lockPick.tex.width;
        this.rightPickPart.tex.startX = this.rightPickPart.tex.canvasWidth - this.rightPickPart.tex.width;
        this.leftPickPart.posY = this.lockPick.posY;
        this.leftPickPart.tex.width = this.rightPickPart.tex.startX;
        this.leftPickPart.posX = -10 - this.leftPickPart.tex.width;
    }

    protected void boundLockPick() {
        this.lockPick.posX = (float)(10 - LockPickingGui.LOCK_PICK_TEX.width) + MathHelper.clamp((float)(this.lockPick.posX - 10.0f + (float)LockPickingGui.LOCK_PICK_TEX.width), (float)0.0f, (float)((this.length - 1) * (LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width)));
    }

    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        int code = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            this.keyPressed(code);
        } else {
            this.keyReleased(code);
        }
    }

    public void keyPressed(int code) {
        if (code == this.mc.gameSettings.keyBindLeft.getKeyCode()) {
            this.holdingLeft = true;
            this.updateLockPickSpeed();
        } else if (code == this.mc.gameSettings.keyBindRight.getKeyCode()) {
            this.holdingRight = true;
            this.updateLockPickSpeed();
        } else if (!this.frozen && code == this.mc.gameSettings.keyBindForward.getKeyCode() && !this.lockPick.isExecuting() && this.pullPin(this.getSelectedPin())) {
            this.lockPick.execute(MoveAction.at(0.0f, -2.5f).time(3), MoveAction.at(0.0f, 2.5f).time(3));
        }
    }

    public void keyReleased(int code) {
        if (code == this.mc.gameSettings.keyBindLeft.getKeyCode()) {
            this.holdingLeft = false;
            this.updateLockPickSpeed();
        } else if (code == this.mc.gameSettings.keyBindRight.getKeyCode()) {
            this.holdingRight = false;
            this.updateLockPickSpeed();
        }
    }

    protected void updateLockPickSpeed() {
        this.lockPick.speedX = this.frozen ? 0.0f : (this.holdingLeft && this.holdingRight ? 0.0f : (this.holdingLeft ? -4.0f : (this.holdingRight ? 4.0f : 0.0f)));
    }

    protected int getSelectedPin() {
        return (int)((this.lockPick.posX - 10.0f + (float)LockPickingGui.LOCK_PICK_TEX.width) / (float)(LockPickingGui.COLUMN_TEX.width + LockPickingGui.INNER_WALL_TEX.width) + 0.5f);
    }

    protected boolean pullPin(int pin) {
        if (this.pins[pin]) {
            return false;
        }
        this.currPin = pin;
        LocksNetworks.MAIN.sendToServer((IMessage)new CheckPinPacket((byte)pin));
        return true;
    }

    public void handlePin(boolean correct, boolean reset) {
        this.pinTumblers[this.currPin].execute(MoveAction.at(0.0f, -6.0f).time(2), MoveAction.at(0.0f, 6.0f).time(2));
        this.upperPins[this.currPin].execute(MoveAction.at(0.0f, -6.0f).time(2));
        if (correct) {
            this.pins[this.currPin] = true;
            this.upperPins[this.currPin].execute(MoveAction.to(this.upperPins[this.currPin], this.upperPins[this.currPin].posX, 29.0f, 2));
        } else {
            this.upperPins[this.currPin].execute(MoveAction.at(0.0f, 6.0f).time(2));
        }
        if (reset) {
            this.reset();
        }
    }

    public void reset() {
        for (int a = 0; a < this.pins.length; ++a) {
            if (!this.pins[a]) continue;
            this.pins[a] = false;
            this.upperPins[a].execute(MoveAction.to(this.upperPins[a], this.upperPins[a].posX, this.pinTumblers[a].posY - (float)LockPickingGui.UPPER_PIN_TEX.height, 2));
        }
        this.lockPick.alpha(0.0f);
        this.rightPickPart.alpha(1.0f).execute(WaitAction.ticks(10), FadeAction.to(this.rightPickPart, 0.0f, 4));
        this.leftPickPart.alpha(1.0f).execute(WaitAction.ticks(10), FadeAction.to(this.rightPickPart, 0.0f, 4).then(this.resetPickCb));
        this.frozen = true;
        this.updateLockPickSpeed();
    }

    public void resetPick() {
        this.pickTex = LockPickingGui.getTextureFor(Minecraft.getMinecraft().player.getHeldItem(this.hand));
        this.lockPick.position(-22 - LockPickingGui.LOCK_PICK_TEX.width, this.lockPick.posY).alpha(1.0f).execute(AccelerateAction.to(32.0f, 0.0f, 4, false).then(this.unfreezeCb));
    }
}

