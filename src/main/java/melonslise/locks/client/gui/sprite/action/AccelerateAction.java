/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.client.gui.sprite.action;

import melonslise.locks.client.gui.sprite.Sprite;
import melonslise.locks.client.gui.sprite.action.MoveAction;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class AccelerateAction<S extends Sprite>
extends MoveAction<S> {
    public float accelX;
    public float accelY;

    public AccelerateAction(float speedX, float speedY, float accelX, float accelY) {
        super(speedX, speedY);
        this.accelX = accelX;
        this.accelY = accelY;
    }

    public static <Z extends Sprite> AccelerateAction<Z> at(float speedX, float speedY, float accelX, float accelY) {
        return new AccelerateAction(speedX, speedY, accelX, accelY);
    }

    public static <Z extends Sprite> AccelerateAction<Z> to(float shiftX, float shiftY, int ticks, boolean accel) {
        float speedX = 2.0f * shiftX / (float)ticks / (accel ? 3.0f : 1.0f);
        float speedY = 2.0f * shiftY / (float)ticks / (accel ? 3.0f : 1.0f);
        float accelX = speedX / (float)ticks * (accel ? 1.0f : -1.0f);
        float accelY = speedY / (float)ticks * (accel ? 1.0f : -1.0f);
        return (AccelerateAction)AccelerateAction.at(speedX, speedY, accelX, accelY).time(ticks);
    }

    public static <Z extends Sprite> AccelerateAction<Z> to(Sprite sprite, float posX, float posY, int ticks, boolean accel) {
        return AccelerateAction.to(posX - sprite.posX, posY - sprite.posY, ticks, accel);
    }

    @Override
    public void update(S sprite) {
        super.update(sprite);
        ((Sprite)sprite).posX += this.accelX * 0.5f;
        ((Sprite)sprite).posY += this.accelY * 0.5f;
        this.speedX += this.accelX;
        this.speedY += this.accelY;
    }
}

