/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraftforge.items.IItemHandlerModifiable
 *  net.minecraftforge.items.ItemHandlerHelper
 */
package melonslise.locks.common.capability;

import javax.annotation.Nonnull;
import melonslise.locks.common.init.LocksItems;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class KeyRingInventory
implements IItemHandlerModifiable {
    public final int size;
    public final ItemStack stack;

    public KeyRingInventory(ItemStack stack, int rows, int col) {
        this.size = rows * col;
        this.stack = stack;
    }

    public int getSlots() {
        return this.size;
    }

    public ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        NBTTagList list = LocksUtil.getTag(this.stack).getTagList("Items", 10);
        for (int a = 0; a < list.tagCount(); ++a) {
            NBTTagCompound nbt = list.getCompoundTagAt(a);
            if (nbt.getInteger("Slot") != slot) continue;
            return new ItemStack(nbt);
        }
        return ItemStack.EMPTY;
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        NBTTagCompound nbt = null;
        if (!stack.isEmpty()) {
            nbt = new NBTTagCompound();
            nbt.setInteger("Slot", slot);
            stack.writeToNBT(nbt);
        }
        NBTTagList list = LocksUtil.getTag(this.stack).getTagList("Items", 10);
        for (int a = 0; a < list.tagCount(); ++a) {
            NBTTagCompound existing = list.getCompoundTagAt(a);
            if (existing.getInteger("Slot") != slot) continue;
            if (!stack.isEmpty()) {
                list.set(a, (NBTBase)nbt);
            } else {
                list.removeTag(a);
            }
            return;
        }
        if (!stack.isEmpty()) {
            list.appendTag((NBTBase)nbt);
        }
        LocksUtil.getTag(this.stack).setTag("Items", (NBTBase)list);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        boolean reachedLimit;
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.validateSlotIndex(slot);
        ItemStack existing = this.getStackInSlot(slot);
        int limit = stack.getMaxStackSize();
        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack((ItemStack)stack, (ItemStack)existing)) {
                return stack;
            }
            limit -= existing.getCount();
        }
        if (limit <= 0) {
            return stack;
        }
        boolean bl = reachedLimit = stack.getCount() > limit;
        if (!simulate) {
            if (existing.getCount() <= 0) {
                existing = reachedLimit ? ItemHandlerHelper.copyStackWithSize((ItemStack)stack, (int)limit) : stack;
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            this.setStackInSlot(slot, existing);
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize((ItemStack)stack, (int)(stack.getCount() - limit)) : ItemStack.EMPTY;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }
        this.validateSlotIndex(slot);
        ItemStack existing = this.getStackInSlot(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.setStackInSlot(slot, ItemStack.EMPTY);
            }
            return existing;
        }
        if (!simulate) {
            this.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize((ItemStack)existing, (int)(existing.getCount() - toExtract)));
        }
        return ItemHandlerHelper.copyStackWithSize((ItemStack)existing, (int)toExtract);
    }

    public int getSlotLimit(int slot) {
        return 64;
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.getSlots()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.getSlots() + ")");
        }
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() == LocksItems.KEY;
    }
}

