/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.client.gui.sprite.action;

import melonslise.locks.client.gui.sprite.Sprite;
import melonslise.locks.client.gui.sprite.action.TimedAction;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class MoveAction<S extends Sprite>
extends TimedAction<S> {
    public float speedX;
    public float speedY;

    public MoveAction(float speedX, float speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public static <Z extends Sprite> MoveAction<Z> at(float speedX, float speedY) {
        return new MoveAction(speedX, speedY);
    }

    public static <Z extends Sprite> MoveAction<Z> to(float shiftX, float shiftY, int ticks) {
        return (MoveAction)MoveAction.at(shiftX / (float)ticks, shiftY / (float)ticks).time(ticks);
    }

    public static <Z extends Sprite> MoveAction<Z> to(Sprite sprite, float posX, float posY, int ticks) {
        return MoveAction.to(posX - sprite.posX, posY - sprite.posY, ticks);
    }

    @Override
    public void update(S sprite) {
        super.update(sprite);
        ((Sprite)sprite).posX += this.speedX;
        ((Sprite)sprite).posY += this.speedY;
    }
}

