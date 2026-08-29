/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.common.item;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import melonslise.locks.Locks;
import melonslise.locks.common.init.LocksEnchantments;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksPredicates;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LockPickItem
extends Item {
    public static final ITextComponent TOO_COMPLEX_MESSAGE = new TextComponentTranslation("locks.status.too_complex", new Object[0]);
    public static final String KEY_STRENGTH = "Strength";
    public final float strength;

    public LockPickItem(float strength) {
        this.strength = strength;
    }

    public static float getStrength(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return ((LockPickItem)stack.getItem()).strength;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey(KEY_STRENGTH)) {
            return ((LockPickItem)stack.getItem()).strength;
        }
        return nbt.getFloat(KEY_STRENGTH);
    }

    public static boolean canPick(ItemStack stack, int cmp) {
        return LockPickItem.getStrength(stack) > (float)cmp * 0.25f;
    }

    public static boolean canPick(ItemStack stack, Lockable lkb) {
        return LockPickItem.canPick(stack, EnchantmentHelper.getEnchantmentLevel((Enchantment)LocksEnchantments.COMPLEXITY, (ItemStack)lkb.stack));
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
        List<Lockable> matching = LocksUtil.intersecting(world, pos).filter(LocksPredicates.LOCKED).collect(Collectors.toList());
        if (matching.isEmpty()) {
            return EnumActionResult.PASS;
        }
        Lockable lkb = matching.get(0);
        if (!LockPickItem.canPick(player.getHeldItem(hand), lkb)) {
            if (world.isRemote) {
                player.sendStatusMessage(TOO_COMPLEX_MESSAGE, true);
            }
            return EnumActionResult.PASS;
        }
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        player.openGui((Object)Locks.instance, 1, world, lkb.networkID, hand == EnumHand.MAIN_HAND ? 0 : 1, 0);
        return EnumActionResult.SUCCESS;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> lines, ITooltipFlag flag) {
        super.addInformation(stack, world, lines, flag);
        float strength = LocksUtil.hasKey(stack, KEY_STRENGTH) ? stack.getTagCompound().getFloat(KEY_STRENGTH) : this.strength;
        TextComponentTranslation txt = new TextComponentTranslation("locks.tooltip.strength", new Object[]{ItemStack.DECIMALFORMAT.format(strength)});
        txt.getStyle().setColor(TextFormatting.DARK_GREEN);
        lines.add(txt.getFormattedText());
    }
}

