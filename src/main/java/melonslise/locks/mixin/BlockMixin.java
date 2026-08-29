/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 */
package melonslise.locks.mixin;

import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.util.LocksPredicates;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Block.class})
public class BlockMixin {
    @Inject(at={@At(value="RETURN")}, method={"getExplosionResistance(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/world/Explosion;)F"}, cancellable=true, remap=false)
    private void getExplosionResistance(World world, BlockPos pos, Entity ent, Explosion ex, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(Float.valueOf(Math.max(cir.getReturnValue().floatValue(), LocksUtil.intersecting(world, pos).filter(LocksPredicates.LOCKED).findFirst().map(lkb -> Float.valueOf(LockItem.getResistance(lkb.stack))).orElse(Float.valueOf(0.0f)).floatValue())));
    }
}

