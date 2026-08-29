/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.client.gui.sprite.action;

import java.util.function.BiConsumer;
import melonslise.locks.client.gui.sprite.Sprite;
import melonslise.locks.client.gui.sprite.action.IAction;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public abstract class SingleCallbackAction<S extends Sprite>
implements IAction<S> {
    public BiConsumer<IAction<S>, S> cb;

    @Override
    public void finish(S sprite) {
        if (this.cb != null) {
            this.cb.accept(this, sprite);
        }
    }

    @Override
    public IAction<S> then(BiConsumer<IAction<S>, S> cb) {
        this.cb = cb;
        return this;
    }
}

