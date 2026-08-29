/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.common.item;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LockingItem
extends Item {
    public static final String KEY_ID = "Id";

    public LockingItem() {
        this.setMaxStackSize(1);
    }

    public static ItemStack copyId(ItemStack from, ItemStack to) {
        LocksUtil.getTag(to).setInteger(KEY_ID, LockingItem.getOrSetId(from));
        return to;
    }

    public static int getOrSetId(ItemStack stack) {
        NBTTagCompound nbt = LocksUtil.getTag(stack);
        if (!nbt.hasKey(KEY_ID)) {
            nbt.setInteger(KEY_ID, ThreadLocalRandom.current().nextInt());
        }
        return nbt.getInteger(KEY_ID);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isRemote) {
            LockingItem.getOrSetId(stack);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> lines, ITooltipFlag flag) {
        if (LocksUtil.hasKey(stack, KEY_ID)) {
            TextComponentTranslation txt = new TextComponentTranslation("locks.tooltip.id", new Object[]{ItemStack.DECIMALFORMAT.format(LockingItem.getOrSetId(stack))});
            txt.getStyle().setColor(TextFormatting.DARK_GREEN);
            lines.add(txt.getFormattedText());
        }
    }
}

