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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public interface IAction<S extends Sprite> {
    public boolean isFinished(S var1);

    public void update(S var1);

    public void finish(S var1);

    public IAction<S> then(BiConsumer<IAction<S>, S> var1);
}

