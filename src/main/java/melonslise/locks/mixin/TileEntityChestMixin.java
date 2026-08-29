/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.items.CapabilityItemHandler
 *  net.minecraftforge.items.wrapper.EmptyHandler
 */
package melonslise.locks.mixin;

import javax.annotation.Nullable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={TileEntityChest.class})
public class TileEntityChestMixin {
    @Inject(at={@At(value="HEAD")}, method={"getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Ljava/lang/Object;"}, cancellable=true, remap=false)
    private void getCapability(Capability cap, @Nullable EnumFacing facing, CallbackInfoReturnable<Object> cir) {
        TileEntity te = (TileEntity)(Object)this;
        if (!te.isInvalid() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && te.hasWorld() && LocksUtil.locked(te.getWorld(), te.getPos())) {
            cir.setReturnValue(EmptyHandler.INSTANCE);
        }
    }
}

