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

import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.config.machine.resourceproduction.WaterCondensatorConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class WaterCondensatorTile extends IndustrialWorkingTile<WaterCondensatorTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<WaterCondensatorTile> water;

    public WaterCondensatorTile() {
        super(ModuleResourceProduction.WATER_CONDENSATOR, WaterCondensatorConfig.powerPerOperation);
        this.addTank(water = (SidedFluidTankComponent<WaterCondensatorTile>) new SidedFluidTankComponent<WaterCondensatorTile>("water", WaterCondensatorConfig.maxWaterTankSize, 30 + 13, 20, 0).
                setColor(DyeColor.BLUE).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER)));
        this.getMaxProgress = WaterCondensatorConfig.maxProgress;
        this.getPowerPerOperation = WaterCondensatorConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        int water = getWaterSources();
        if (water >= 2) {
            if (hasEnergy(getPowerPerOperation)) {
                this.water.fillForced(new FluidStack(Fluids.WATER, water * 100), IFluidHandler.FluidAction.EXECUTE);
                return new WorkAction(0.1f, getPowerPerOperation);
            } else {
                this.water.fillForced(new FluidStack(Fluids.WATER, water * 50), IFluidHandler.FluidAction.EXECUTE);
                return new WorkAction(0.5f, 0);
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<WaterCondensatorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(WaterCondensatorConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    private int getWaterSources() {
        int amount = 0;
        for (Direction value : Direction.values()) {
            if (!world.isAreaLoaded(this.pos.offset(value), this.pos.offset(value))) continue;
            FluidState fluidState = this.world.getFluidState(this.pos.offset(value));
            if (fluidState.getFluid().equals(Fluids.WATER) && fluidState.isSource()) {
                ++amount;
            }
        }
        return amount;
    }

    @Nonnull
    @Override
    public WaterCondensatorTile getSelf() {
        return this;
    }
}
