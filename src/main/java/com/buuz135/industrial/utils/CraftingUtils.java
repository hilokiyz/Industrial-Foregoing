/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2023, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.utils;

import com.buuz135.industrial.recipe.CrusherRecipe;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CraftingUtils {

    public static Set<ItemStack[]> missingRecipes = new HashSet<>();
    private static HashMap<ItemStack, ItemStack> cachedRecipes = new HashMap<>();

    public static ItemStack findOutput(int size, ItemStack input, World world) {
        if (input.getCount() < size * size) return ItemStack.EMPTY;
        ItemStack cachedStack = input.copy();
        cachedStack.setCount(size * size);
        for (Map.Entry<ItemStack, ItemStack> entry : cachedRecipes.entrySet()) {
            if (entry.getKey().isItemEqual(cachedStack) && entry.getKey().getCount() == cachedStack.getCount()) {
                return entry.getValue().copy();
            }
        }
        CraftingInventory inventoryCrafting = new CraftingInventory(new Container(null, 0) {
            @Override
            public boolean canInteractWith(PlayerEntity playerIn) {
                return false;
            }
        }, size, size);
        for (int i = 0; i < size * size; i++) {
            inventoryCrafting.setInventorySlotContents(i, input.copy());
        }
        ICraftingRecipe recipe = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inventoryCrafting, world).orElse(null);
        if (recipe != null) {
            ItemStack output = recipe.getRecipeOutput();
            cachedRecipes.put(cachedStack, output.copy());
            return output.copy();
        }
        return ItemStack.EMPTY;
    }

    public static CraftingInventory genCraftingInventory(World world, ItemStack... inputs) {
        CraftingInventory inventoryCrafting = new CraftingInventory(new Container(null, 0) {
            @Override
            public boolean canInteractWith(PlayerEntity playerIn) {
                return false;
            }
        }, 3, 3);
        for (int i = 0; i < 9; ++i) {
            inventoryCrafting.setInventorySlotContents(i, inputs[i]);
        }
        return inventoryCrafting;
    }

    public static IRecipe findRecipe(World world, ItemStack... inputs) {
        for (ItemStack[] missingRecipe : missingRecipes) {
            if (doesStackArrayEquals(missingRecipe, inputs)) return null;
        }
        IRecipe recipe = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, genCraftingInventory(world, inputs), world).orElseGet(null);
        if (recipe == null) missingRecipes.add(inputs);
        return recipe;
    }

    public static boolean doesStackArrayEquals(ItemStack[] original, ItemStack[] compare) {
        if (original.length != compare.length) return false;
        for (int i = 0; i < original.length; i++) {
            if (original[i].isEmpty() && compare[i].isEmpty()) continue;
            if (!original[i].isItemEqual(compare[i])) return false;
        }
        return true;
    }

    public static ItemStack getCrushOutput(World world, ItemStack stack) {
        for (CrusherRecipe recipe : RecipeUtil.getRecipes(world, CrusherRecipe.SERIALIZER.getRecipeType())) {
            if (recipe.input.test(stack)) return recipe.output.getMatchingStacks()[0];
        }
        return ItemStack.EMPTY;
    }

}
