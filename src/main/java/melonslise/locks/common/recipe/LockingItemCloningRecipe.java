/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.inventory.InventoryCrafting
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.item.crafting.Ingredient
 *  net.minecraft.item.crafting.ShapedRecipes
 *  net.minecraft.util.JsonUtils
 *  net.minecraft.util.NonNullList
 *  net.minecraft.world.World
 *  net.minecraftforge.common.crafting.CraftingHelper
 *  net.minecraftforge.common.crafting.IRecipeFactory
 *  net.minecraftforge.common.crafting.JsonContext
 *  net.minecraftforge.common.util.RecipeMatcher
 *  net.minecraftforge.registries.IForgeRegistryEntry$Impl
 */
package melonslise.locks.common.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.List;
import melonslise.locks.common.item.LockingItem;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class LockingItemCloningRecipe
extends IForgeRegistryEntry.Impl<IRecipe>
implements IRecipe {
    public final Ingredient locking;
    public final NonNullList<Ingredient> blanks;
    public final ItemStack result;

    public LockingItemCloningRecipe(Ingredient locking, NonNullList<Ingredient> blanks, ItemStack result) {
        this.locking = locking;
        this.blanks = blanks;
        this.result = result;
    }

    public ItemStack getRecipeOutput() {
        return this.result;
    }

    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack locking = ItemStack.EMPTY;
        ArrayList<ItemStack> blanks = Lists.newArrayList();
        for (int a = 0; a < inv.getSizeInventory(); ++a) {
            ItemStack stack = inv.getStackInSlot(a);
            if (stack.isEmpty()) continue;
            if (LocksUtil.hasKey(stack, "Id") && this.locking.test(stack)) {
                locking = stack;
                continue;
            }
            blanks.add(stack);
        }
        return !locking.isEmpty() && RecipeMatcher.findMatches(blanks, this.blanks) != null;
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int a = 0; a < inv.getSizeInventory(); ++a) {
            ItemStack stack = inv.getStackInSlot(a);
            if (!this.locking.test(stack)) continue;
            stacks.set(a, stack.copy());
        }
        return stacks;
    }

    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack locking = ItemStack.EMPTY;
        for (int a = 0; a < inventory.getSizeInventory() && locking.isEmpty(); ++a) {
            ItemStack stack = inventory.getStackInSlot(a);
            if (stack.isEmpty() || !LocksUtil.hasKey(stack, "Id") || !this.locking.test(stack)) continue;
            locking = stack;
        }
        return LockingItem.copyId(locking, this.result.copy());
    }

    public boolean canFit(int width, int height) {
        return this.blanks.size() + 1 <= width * height;
    }

    public static class Factory
    implements IRecipeFactory {
        public IRecipe parse(JsonContext ctx, JsonObject json) {
            Ingredient locking = CraftingHelper.getIngredient((JsonElement)json.get("locking"), (JsonContext)ctx);
            NonNullList<Ingredient> blanks = Factory.deserializeIngredients(JsonUtils.getJsonArray((JsonObject)json, (String)"blanks"));
            if (blanks.isEmpty()) {
                throw new JsonParseException("Not enough items for locking item cloning recipe");
            }
            if (blanks.size() > 8) {
                throw new JsonParseException("Too many items for locking item cloning recipe");
            }
            ItemStack result = ShapedRecipes.deserializeItem((JsonObject)JsonUtils.getJsonObject((JsonObject)json, (String)"result"), (boolean)true);
            return new LockingItemCloningRecipe(locking, blanks, result);
        }

        private static NonNullList<Ingredient> deserializeIngredients(JsonArray array) {
            NonNullList list = NonNullList.create();
            for (int a = 0; a < array.size(); ++a) {
                Ingredient ingredient = ShapedRecipes.deserializeIngredient((JsonElement)array.get(a));
                if (ingredient == Ingredient.EMPTY) continue;
                list.add((Object)ingredient);
            }
            return list;
        }
    }
}

