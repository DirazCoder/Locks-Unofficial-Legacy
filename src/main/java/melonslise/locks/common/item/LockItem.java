/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.BlockChest
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockDoor$EnumDoorHalf
 *  net.minecraft.block.BlockDoor$EnumHingePosition
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.common.item;

import java.util.List;
import javax.annotation.Nullable;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ISelection;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksSoundEvents;
import melonslise.locks.common.item.LockingItem;
import melonslise.locks.common.util.Cuboid6i;
import melonslise.locks.common.util.Lock;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import melonslise.locks.common.util.Orientation;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LockItem
extends LockingItem {
    public final int length;
    public final int enchantmentValue;
    public final float resistance;
    public static final String KEY_LENGTH = "Length";
    public static final String KEY_OPEN = "Open";

    public LockItem(int length, int enchVal, float resist) {
        this.length = length;
        this.enchantmentValue = enchVal;
        this.resistance = resist;
        this.addPropertyOverride(new ResourceLocation("locks:open"), new IItemPropertyGetter(){

            @SideOnly(value=Side.CLIENT)
            public float apply(ItemStack stack, World world, EntityLivingBase entity) {
                if (stack.getItem() instanceof LockItem) {
                    return LockItem.isOpen(stack) ? 1.0f : 0.0f;
                }
                return 0.0f;
            }
        });
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    public int getItemEnchantability() {
        return this.enchantmentValue;
    }

    public static boolean isOpen(ItemStack stack) {
        NBTTagCompound nbt = LocksUtil.getTag(stack);
        if (!nbt.hasKey(KEY_OPEN)) {
            nbt.setBoolean(KEY_OPEN, false);
        }
        return nbt.getBoolean(KEY_OPEN);
    }

    public static void setOpen(ItemStack stack, boolean open) {
        LocksUtil.getTag(stack).setBoolean(KEY_OPEN, open);
    }

    public static byte getLength(ItemStack stack) {
        NBTTagCompound nbt = LocksUtil.getTag(stack);
        if (!nbt.hasKey(KEY_LENGTH)) {
            return (byte)((LockItem)stack.getItem()).length;
        }
        return nbt.getByte(KEY_LENGTH);
    }

    public static float getResistance(ItemStack stack) {
        return ((LockItem)stack.getItem()).resistance;
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
        ILockableHandler lockables = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, null);
        if (!LocksConfig.getServer(world).canLock(world, pos) || lockables.getInChunk(pos).values().stream().anyMatch(lockable1 -> lockable1.box.intersects(pos))) {
            return EnumActionResult.PASS;
        }
        return LocksConfig.getServer((World)world).easyLock ? this.easyLock(lockables, player, world, pos, hand, face, hitX, hitY, hitZ) : this.freeLock(lockables, player, world, pos, hand, face, hitX, hitY, hitZ);
    }

    public EnumActionResult freeLock(ILockableHandler lockables, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
        ISelection select = (ISelection)player.getCapability(LocksCapabilities.SELECTION, null);
        ItemStack stack = player.getHeldItem(hand);
        BlockPos pos1 = select.get();
        if (pos1 == null) {
            select.set(pos);
        } else {
            select.set(null);
            world.playSound(player, pos, LocksSoundEvents.LOCK_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (world.isRemote) {
                return EnumActionResult.SUCCESS;
            }
            ItemStack lockStack = stack.copy();
            lockStack.setCount(1);
            if (!lockables.add(new Lockable(new Cuboid6i(pos1, pos), Lock.from(lockStack), Orientation.fromDirection(face, player.getHorizontalFacing().getOpposite()), lockStack, world))) {
                return EnumActionResult.PASS;
            }
            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    public EnumActionResult easyLock(ILockableHandler lockables, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
        world.playSound(player, pos, LocksSoundEvents.LOCK_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        IBlockState state = world.getBlockState(pos);
        BlockPos pos1 = pos;
        TileEntity te = world.getTileEntity(pos);
        Orientation placedOrientation = Orientation.fromDirection(face, player.getHorizontalFacing().getOpposite());
        if (te instanceof TileEntityChest) {
            BlockPos adjPos = LocksUtil.getAdjacentChest((TileEntityChest)te);
            if (adjPos != null) {
                pos1 = adjPos;
            }
            if (LocksConfig.COMMON.automaticallyOrientPlacedLocks && state.getBlock() instanceof BlockChest) {
                placedOrientation = Orientation.fromDirection((EnumFacing)world.getBlockState(pos).getValue((IProperty)BlockChest.FACING), EnumFacing.NORTH);
            }
        } else if (state.getBlock() instanceof BlockDoor) {
            BlockDoor.EnumDoorHalf clickedDoorHalf = (BlockDoor.EnumDoorHalf)state.getValue((IProperty)BlockDoor.HALF);
            BlockDoor.EnumHingePosition clickedDoorHinge = (BlockDoor.EnumHingePosition)state.getValue((IProperty)BlockDoor.HINGE);
            EnumFacing clickedDoorFacing = (EnumFacing)state.getValue((IProperty)BlockDoor.FACING);
            boolean isOpen = (Boolean)state.getValue((IProperty)BlockDoor.OPEN);
            pos1 = clickedDoorHalf == BlockDoor.EnumDoorHalf.LOWER ? pos.up() : pos.down();
            IBlockState otherHalfState = world.getBlockState(pos1);
            if (clickedDoorHalf == BlockDoor.EnumDoorHalf.UPPER && otherHalfState.getBlock() instanceof BlockDoor) {
                isOpen = (Boolean)otherHalfState.getValue((IProperty)BlockDoor.OPEN);
                clickedDoorFacing = (EnumFacing)otherHalfState.getValue((IProperty)BlockDoor.FACING);
            }
            if (clickedDoorHalf == BlockDoor.EnumDoorHalf.LOWER && otherHalfState.getBlock() instanceof BlockDoor) {
                clickedDoorHinge = (BlockDoor.EnumHingePosition)otherHalfState.getValue((IProperty)BlockDoor.HINGE);
            }
            if (!(otherHalfState.getBlock() instanceof BlockDoor) || otherHalfState.getValue((IProperty)BlockDoor.HALF) == clickedDoorHalf) {
                pos1 = pos;
            } else if (!isOpen) {
                int xAdd = 0;
                int zAdd = 0;
                switch (clickedDoorFacing) {
                    default: {
                        zAdd = clickedDoorHinge == BlockDoor.EnumHingePosition.RIGHT ? -1 : 1;
                        break;
                    }
                    case NORTH: {
                        xAdd = clickedDoorHinge == BlockDoor.EnumHingePosition.RIGHT ? -1 : 1;
                        break;
                    }
                    case SOUTH: {
                        xAdd = clickedDoorHinge == BlockDoor.EnumHingePosition.RIGHT ? 1 : -1;
                        break;
                    }
                    case WEST: {
                        zAdd = clickedDoorHinge == BlockDoor.EnumHingePosition.RIGHT ? 1 : -1;
                    }
                }
                IBlockState otherTop = world.getBlockState((clickedDoorHalf == BlockDoor.EnumDoorHalf.LOWER ? pos1 : pos).add(xAdd, 0, zAdd));
                IBlockState otherBottom = world.getBlockState((clickedDoorHalf == BlockDoor.EnumDoorHalf.LOWER ? pos : pos1).add(xAdd, 0, zAdd));
                if (otherTop.getBlock() instanceof BlockDoor && otherBottom.getBlock() instanceof BlockDoor && !((Boolean)otherBottom.getValue((IProperty)BlockDoor.OPEN)).booleanValue() && otherTop.getValue((IProperty)BlockDoor.HINGE) == (clickedDoorHinge == BlockDoor.EnumHingePosition.RIGHT ? BlockDoor.EnumHingePosition.LEFT : BlockDoor.EnumHingePosition.RIGHT) && otherBottom.getValue((IProperty)BlockDoor.FACING) == clickedDoorFacing && otherTop.getValue((IProperty)BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER && otherBottom.getValue((IProperty)BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
                    pos1 = pos1.add(xAdd, 0, zAdd);
                }
            }
            if (LocksConfig.COMMON.automaticallyOrientPlacedLocks) {
                if (isOpen) {
                    EnumFacing curfacing = clickedDoorFacing;
                    switch (clickedDoorFacing) {
                        case NORTH: 
                        case SOUTH: {
                            curfacing = EnumFacing.WEST;
                            break;
                        }
                        case EAST: 
                        case WEST: {
                            curfacing = EnumFacing.SOUTH;
                            break;
                        }
                    }
                    placedOrientation = Orientation.fromDirection(curfacing.getOpposite(), EnumFacing.NORTH);
                } else {
                    placedOrientation = Orientation.fromDirection(clickedDoorFacing.getOpposite(), EnumFacing.NORTH);
                }
            }
        }
        ItemStack stack = player.getHeldItem(hand);
        ItemStack lockStack = stack.copy();
        lockStack.setCount(1);
        if (!lockables.add(new Lockable(new Cuboid6i(pos1, pos), Lock.from(lockStack), placedOrientation, lockStack, world))) {
            return EnumActionResult.PASS;
        }
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!LockItem.isOpen(stack)) {
            return super.onItemRightClick(world, player, hand);
        }
        LockItem.setOpen(stack, false);
        world.playSound(player, player.posX, player.posY, player.posZ, LocksSoundEvents.PIN_MATCH, SoundCategory.PLAYERS, 1.0f, 1.0f);
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> lines, ITooltipFlag flag) {
        super.addInformation(stack, world, lines, flag);
        int length = LocksUtil.hasKey(stack, KEY_LENGTH) ? (int)stack.getTagCompound().getByte(KEY_LENGTH) : this.length;
        TextComponentTranslation txt = new TextComponentTranslation("locks.tooltip.length", new Object[]{ItemStack.DECIMALFORMAT.format(length)});
        txt.getStyle().setColor(TextFormatting.DARK_GREEN);
        lines.add(txt.getFormattedText());
        float resist = (int)this.resistance;
        String resistString = ".tooltip.resistance.weak";
        if (resist >= 10.0f) {
            resistString = ".tooltip.resistance.strong";
        }
        if (resist >= 40.0f) {
            resistString = ".tooltip.resistance.supreme";
        }
        TextComponentTranslation txt2 = new TextComponentTranslation("locks" + resistString, new Object[0]);
        txt2.getStyle().setColor(TextFormatting.DARK_GREEN);
        lines.add(txt2.getFormattedText());
    }
}

