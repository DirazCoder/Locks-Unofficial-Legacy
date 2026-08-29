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
public class WaitAction<S extends Sprite>
extends TimedAction<S> {
    public static <Z extends Sprite> WaitAction<Z> ticks(int ticks) {
        return (WaitAction)new WaitAction().time(ticks);
    }
}

