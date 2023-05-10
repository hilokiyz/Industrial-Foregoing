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

package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class DoubleTallPlantRecollectable extends PlantRecollectable {

    public DoubleTallPlantRecollectable() {
        super("blocksugarandcactus");
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, BlockState blockState) {
        return (blockState.getBlock() instanceof CactusBlock && world.getBlockState(pos.up()).getBlock() instanceof CactusBlock)
                || (blockState.getBlock() instanceof SugarCaneBlock && world.getBlockState(pos.up()).getBlock() instanceof SugarCaneBlock);
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, BlockState blockState) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        harvestBlock(stacks, world, pos.offset(Direction.UP, 2));
        harvestBlock(stacks, world, pos.offset(Direction.UP, 1));
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, BlockState blockState) {
        return true;
    }

    private void harvestBlock(NonNullList<ItemStack> stacks, World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof CactusBlock || blockState.getBlock() instanceof SugarCaneBlock) {
            stacks.addAll(BlockUtils.getBlockDrops(world, pos));
            if (!world.getFluidState(pos).isEmpty()) {
                world.setBlockState(pos, Blocks.WATER.getDefaultState());
            } else {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public List<String> getRecollectablesNames() {
        return Arrays.asList("text.industrialforegoing.plant.sugar_cane", "text.industrialforegoing.plant.cactus");
    }
}
