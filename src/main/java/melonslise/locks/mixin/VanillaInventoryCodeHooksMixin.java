/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.items.VanillaInventoryCodeHooks
 *  net.minecraftforge.items.wrapper.EmptyHandler
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package melonslise.locks.mixin;

import melonslise.locks.common.util.LocksUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={VanillaInventoryCodeHooks.class})
public class VanillaInventoryCodeHooksMixin {
    @Inject(at={@At(value="HEAD")}, method={"getItemHandler(Lnet/minecraft/world/World;DDDLnet/minecraft/util/EnumFacing;)Lorg/apache/commons/lang3/tuple/Pair;"}, cancellable=true, remap=false)
    private static void getItemHandler(World world, double x, double y, double z, EnumFacing side, CallbackInfoReturnable<Pair> cir) {
        if (world == null) {
            return;
        }
        BlockPos pos = new BlockPos(x, y, z);
        if (LocksUtil.locked(world, pos)) {
            TileEntity te = world.getTileEntity(pos);
            if (te == null) {
                cir.setReturnValue(null);
            } else {
                cir.setReturnValue((Pair)ImmutablePair.of((Object)EmptyHandler.INSTANCE, (Object)te));
            }
        }
    }
}

