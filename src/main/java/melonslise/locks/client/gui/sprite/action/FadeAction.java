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
public class FadeAction<S extends Sprite>
extends TimedAction<S> {
    public float fadeSpeed;

    private FadeAction(float fadeSpeed) {
        this.fadeSpeed = fadeSpeed;
    }

    public static <Z extends Sprite> FadeAction<Z> at(float fadeSpeed) {
        return new FadeAction(fadeSpeed);
    }

    public static <Z extends Sprite> FadeAction<Z> to(float delta, int ticks) {
        return (FadeAction)FadeAction.at(delta / (float)ticks).time(ticks);
    }

    public static <Z extends Sprite> FadeAction<Z> to(Sprite sprite, float alpha, int ticks) {
        return FadeAction.to(alpha - sprite.alpha, ticks);
    }

    @Override
    public void update(S sprite) {
        super.update(sprite);
        ((Sprite)sprite).alpha += this.fadeSpeed;
    }
}

