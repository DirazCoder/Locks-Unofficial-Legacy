/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package melonslise.locks.mixin;

import melonslise.locks.common.util.LocksUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={World.class})
public class WorldMixin {
    @Inject(at={@At(value="HEAD")}, method={"isBlockPowered(Lnet/minecraft/util/math/BlockPos;)Z"}, cancellable=true)
    private void isBlockPowered(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (LocksUtil.locked((World)(Object)this, pos)) {
            cir.setReturnValue(false);
        }
    }
}

