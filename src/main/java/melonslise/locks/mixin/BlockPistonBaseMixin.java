/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockPistonBase
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package melonslise.locks.mixin;

import melonslise.locks.common.util.LocksUtil;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockPistonBase.class})
public class BlockPistonBaseMixin {
    @Inject(at={@At(value="HEAD")}, method={"canPush(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;ZLnet/minecraft/util/EnumFacing;)Z"}, cancellable=true)
    private static void canPush(IBlockState blockState, World world, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing pushOnlyFacing, CallbackInfoReturnable<Boolean> cir) {
        if (LocksUtil.locked(world, pos)) {
            cir.setReturnValue(false);
        }
    }
}

