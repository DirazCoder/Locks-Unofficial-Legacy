/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.client.gui.sprite.action;

import melonslise.locks.client.gui.sprite.Sprite;
import melonslise.locks.client.gui.sprite.action.SingleCallbackAction;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public abstract class TimedAction<S extends Sprite>
extends SingleCallbackAction<S> {
    public int ticks;

    public TimedAction<S> time(int ticks) {
        this.ticks = ticks;
        return this;
    }

    @Override
    public boolean isFinished(S sprite) {
        return this.ticks == 0;
    }

    @Override
    public void update(S sprite) {
        --this.ticks;
    }
}

