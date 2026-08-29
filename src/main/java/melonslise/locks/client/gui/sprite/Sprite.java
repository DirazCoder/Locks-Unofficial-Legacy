/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.client.gui.sprite;

import java.util.ArrayDeque;
import java.util.Queue;
import melonslise.locks.client.gui.sprite.Texture;
import melonslise.locks.client.gui.sprite.action.IAction;
import melonslise.locks.client.util.LocksClientUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class Sprite {
    private Queue<IAction> actions = new ArrayDeque<IAction>(4);
    public Texture tex;
    public float posX;
    public float posY;
    public float oldPosX;
    public float oldPosY;
    public float speedX;
    public float speedY;
    public float rot;
    public float oldRot;
    public float rotSpeed;
    public float originX;
    public float originY;
    public float alpha = 1.0f;
    public float oldAlpha = 1.0f;

    public Sprite(Texture tex) {
        this.tex = tex;
    }

    public Sprite position(float posX, float posY) {
        this.posX = this.oldPosX = posX;
        this.posY = this.oldPosY = posY;
        return this;
    }

    public Sprite rotation(float rot, float originX, float originY) {
        this.rot = this.oldRot = rot;
        this.originX = originX;
        this.originY = originY;
        return this;
    }

    public Sprite alpha(float alpha) {
        this.alpha = this.oldAlpha = alpha;
        return this;
    }

    public void execute(IAction ... actions) {
        for (IAction action : actions) {
            this.actions.offer(action);
        }
    }

    public boolean isExecuting() {
        return !this.actions.isEmpty();
    }

    public void draw(float partialTick) {
        if (this.alpha <= 0.0f) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.originX, (float)this.originY, (float)0.0f);
        GlStateManager.rotate((float)LocksClientUtil.lerp(this.oldRot, this.rot, partialTick), (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((float)(-this.originX), (float)(-this.originY), (float)0.0f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)LocksClientUtil.lerp(this.oldAlpha, this.alpha, partialTick));
        this.tex.draw(LocksClientUtil.lerp(this.oldPosX, this.posX, partialTick), LocksClientUtil.lerp(this.oldPosY, this.posY, partialTick), LocksClientUtil.lerp(this.oldAlpha, this.alpha, partialTick));
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void update() {
        this.oldPosX = this.posX;
        this.oldPosY = this.posY;
        this.oldRot = this.rot;
        this.oldAlpha = this.alpha;
        this.posX += this.speedX;
        this.posY += this.speedY;
        this.rot += this.rotSpeed;
        IAction action = this.actions.peek();
        if (action == null) {
            return;
        }
        if (action.isFinished(this)) {
            this.actions.poll().finish(this);
        } else {
            action.update(this);
        }
    }
}

