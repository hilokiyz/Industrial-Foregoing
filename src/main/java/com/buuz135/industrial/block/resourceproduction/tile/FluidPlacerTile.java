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

package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.FluidPlacerConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidPlacerTile extends IndustrialAreaWorkingTile<FluidPlacerTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<FluidPlacerTile> tank;

    public FluidPlacerTile() {
        super(ModuleResourceProduction.FLUID_PLACER, RangeManager.RangeType.BEHIND, false, FluidPlacerConfig.powerPerOperation);
        this.addTank(this.tank = (SidedFluidTankComponent<FluidPlacerTile>) new SidedFluidTankComponent<FluidPlacerTile>("input", FluidPlacerConfig.maxInputTankSize, 43, 20, 0)
                .setColor(DyeColor.BLUE)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setComponentHarness(this)
        );
        this.getMaxProgress = FluidPlacerConfig.maxProgress;
        this.getPowerPerOperation = FluidPlacerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            BlockPos pointed = getPointedBlockPos();
            if (isLoaded(pointed) && BlockUtils.canBlockBeBroken(this.world, pointed) && !world.getFluidState(pointed).isSource() && tank.getFluidAmount() >= FluidAttributes.BUCKET_VOLUME) {
                if (tank.getFluid().getFluid().isEquivalentTo(Fluids.WATER) && world.getBlockState(pointed).hasProperty(BlockStateProperties.WATERLOGGED) && !world.getBlockState(pointed).get(BlockStateProperties.WATERLOGGED)) {
                    world.setBlockState(pointed, world.getBlockState(pointed).with(BlockStateProperties.WATERLOGGED, true));
                    tank.drainForced(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                    increasePointer();
                    return new WorkAction(1, getPowerPerOperation);
                } else if (world.isAirBlock(pointed) || (!world.getFluidState(pointed).isEmpty() && !world.getFluidState(pointed).isSource())) {
                    world.setBlockState(pointed, tank.getFluid().getFluid().getDefaultState().getBlockState());
                    tank.drainForced(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                    increasePointer();
                    return new WorkAction(1, getPowerPerOperation);
                }
            }
        }
        increasePointer();
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<FluidPlacerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(FluidPlacerConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    @Nonnull
    @Override
    public FluidPlacerTile getSelf() {
        return this;
    }
}
