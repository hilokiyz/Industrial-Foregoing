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

package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MagmaGeneratorType implements IMycelialGeneratorType{

    @Override
    public String getName() {
        return "magma";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.TANK, Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList(null, (stack, slot) -> stack.getItem().isIn(Tags.Items.DUSTS_REDSTONE));
    }

    @Override
    public List<Predicate<FluidStack>> getTankInputPredicates() {
        return Arrays.asList(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.LAVA), null);
    }

    @Override
    public boolean canStart(INBTSerializable<CompoundNBT>[] inputs) {
        return inputs.length >= 2 && inputs[0] instanceof SidedFluidTankComponent && inputs[1] instanceof SidedInventoryComponent && ((SidedFluidTankComponent<?>) inputs[0]).getFluidAmount() >= 250;
    }

    @Override
    public Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundNBT>[] inputs) {
        if (inputs.length >= 2 && inputs[0] instanceof SidedFluidTankComponent && inputs[1] instanceof SidedInventoryComponent){
            if (((SidedFluidTankComponent<?>) inputs[0]).getFluidAmount() >= 250){
                ((SidedFluidTankComponent<?>) inputs[0]).drainForced(250, IFluidHandler.FluidAction.EXECUTE);
                boolean hasRedstone = false;
                if (((SidedInventoryComponent<?>) inputs[1]).getStackInSlot(0).getCount() > 0){
                    ((SidedInventoryComponent<?>) inputs[1]).getStackInSlot(0).shrink(1);
                    hasRedstone = true;
                }
                return Pair.of(2 * 250, hasRedstone ? 100 : 50);
            }
        }
        return Pair.of(0,80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.ORANGE, DyeColor.RED};
    }

    @Override
    public Item getDisplay() {
        return Items.LAVA_BUCKET;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        List<MycelialGeneratorRecipe> recipes = new ArrayList<>();
        recipes.add(new MycelialGeneratorRecipe(Arrays.asList(new ArrayList<>(), new ArrayList<>()), Arrays.asList(Arrays.asList(new FluidStack(Fluids.LAVA, 250)), Arrays.asList()), 2 * 250, 50));
        recipes.add(new MycelialGeneratorRecipe(Arrays.asList(new ArrayList<>(), Arrays.asList(Ingredient.fromTag(Tags.Items.DUSTS_REDSTONE))), Arrays.asList(Arrays.asList(new FluidStack(Fluids.LAVA, 250)), Arrays.asList()), 2 * 250, 100));
        return recipes;
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.key('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('C', Items.LAVA_BUCKET)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
        return recipeBuilder;
    }
}
