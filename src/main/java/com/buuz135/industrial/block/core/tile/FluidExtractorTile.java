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

package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.core.FluidExtractorConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;

public class FluidExtractorTile extends IndustrialAreaWorkingTile<FluidExtractorTile> {

    public static HashMap<DimensionType, HashMap<ChunkPos, HashMap<BlockPos, FluidExtractionProgress>>> EXTRACTION = new HashMap<>();

    private int maxProgress;
    private int powerPerOperation;
    private FluidExtractorRecipe currentRecipe;
    @Save
    private SidedFluidTankComponent<FluidExtractorTile> tank;

    public FluidExtractorTile() {
        super(ModuleCore.FLUID_EXTRACTOR, RangeManager.RangeType.BEHIND, false, FluidExtractorConfig.powerPerOperation);
        addTank(tank = (SidedFluidTankComponent<FluidExtractorTile>) new SidedFluidTankComponent<FluidExtractorTile>("latex", FluidExtractorConfig.maxLatexTankSize, 43, 20, 0).
                setColor(DyeColor.LIGHT_GRAY).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this)
        );
        this.maxProgress = FluidExtractorConfig.maxProgress;
        this.powerPerOperation = FluidExtractorConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        BlockPos pos = getPointedBlockPos();
        if (isLoaded(pos) && !this.world.isAirBlock(pos) && this.tank.getFluidAmount() < this.tank.getCapacity()) {
            if (currentRecipe == null || !currentRecipe.matches(this.world, pos) || currentRecipe.defaultRecipe)
                currentRecipe = findRecipe(this.world, pos);
            if (currentRecipe != null) {//GetDimensionType
                FluidExtractionProgress extractionProgress = EXTRACTION.computeIfAbsent(this.world.getDimensionType(), dimensionType -> new HashMap<>()).computeIfAbsent(this.world.getChunkAt(pos).getPos(), chunkPos -> new HashMap<>()).computeIfAbsent(pos, pos1 -> new FluidExtractionProgress(this.world));
                tank.fillForced(currentRecipe.output.copy(), IFluidHandler.FluidAction.EXECUTE);
                if (this.world.rand.nextDouble() <= currentRecipe.breakChance) {
                    extractionProgress.setProgress(extractionProgress.getProgress() + 1);
                }
                if (extractionProgress.getProgress() > 7) {
                    extractionProgress.setProgress(0);
                    this.world.setBlockState(pos, currentRecipe.result.getDefaultState());
                }
                if (hasEnergy(powerPerOperation)) return new WorkAction(0.4f, powerPerOperation);
                return new WorkAction(1f, 0);
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<FluidExtractorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(FluidExtractorConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nullable
    public FluidExtractorRecipe findRecipe(World world, BlockPos pos) {
        Collection<FluidExtractorRecipe> recipeList = RecipeUtil.getRecipes(world, FluidExtractorRecipe.SERIALIZER.getRecipeType());
        for (FluidExtractorRecipe recipe : recipeList) {
            if (!recipe.defaultRecipe && recipe.matches(world, pos)) return recipe;
        }
        for (FluidExtractorRecipe recipe : recipeList) {
            if (recipe.defaultRecipe && recipe.matches(world, pos)) return recipe;
        }
        return null;
    }

    @Nonnull
    @Override
    public FluidExtractorTile getSelf() {
        return this;
    }

    public static class FluidExtractionProgress {

        private int progress;
        private int breakID;

        public FluidExtractionProgress(World world) {
            this.progress = 0;
            this.breakID = world.rand.nextInt();
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getBreakID() {
            return breakID;
        }

    }
}
