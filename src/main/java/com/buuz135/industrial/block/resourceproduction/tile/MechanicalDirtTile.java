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
import com.buuz135.industrial.config.machine.resourceproduction.MechanicalDirtConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.entity.*;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class MechanicalDirtTile extends IndustrialWorkingTile<MechanicalDirtTile> {

    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<MechanicalDirtTile> meat;

    public MechanicalDirtTile() {
        super(ModuleResourceProduction.MECHANICAL_DIRT, MechanicalDirtConfig.powerPerOperation);
        addTank(meat = (SidedFluidTankComponent<MechanicalDirtTile>) new SidedFluidTankComponent<MechanicalDirtTile>("meat", MechanicalDirtConfig.maxMeatTankSize, 43, 20, 0).
                setColor(DyeColor.BROWN).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.FILL).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.MEAT.getSourceFluid()))
        );
        this.getPowerPerOperation = MechanicalDirtConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (world.rand.nextDouble() > 0.1
                || world.getDifficulty() == Difficulty.PEACEFUL
                || (world.isDaytime() && world.getBrightness(pos.up()) > 0.5f && world.canBlockSeeSky(pos.up()))
                || world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos).grow(3)).size() > 10
                || world.getLight(pos.up()) > 7) {
            if (hasEnergy(getPowerPerOperation / 10)) return new WorkAction(0.5f, getPowerPerOperation / 10);
            return new WorkAction(1, 0);
        }
        if (meat.getFluidAmount() >= 20 && isServer()) {
            MobEntity entity = getMobToSpawn();
            if (entity != null) {
                world.addEntity(entity);
                meat.drainForced(20, IFluidHandler.FluidAction.EXECUTE);
                if (hasEnergy(getPowerPerOperation)) return new WorkAction(0.5f, getPowerPerOperation);
            }
        }
        return new WorkAction(1, 0);
    }

    private MobEntity getMobToSpawn() {
        List<MobSpawnInfo.Spawners> spawnListEntries = ((ServerWorld)this.world).getChunkProvider().getChunkGenerator().func_230353_a_(this.world.getBiome(this.pos.up()), ((ServerWorld)this.world).func_241112_a_() ,EntityClassification.MONSTER, this.pos.up());
        if (spawnListEntries.size() == 0) return null;
        MobSpawnInfo.Spawners spawnListEntry = spawnListEntries.get(world.rand.nextInt(spawnListEntries.size()));
        if (!EntitySpawnPlacementRegistry.canSpawnEntity(spawnListEntry.type, (IServerWorld) this.world, SpawnReason.NATURAL, pos.up(), world.rand))
            return null;
        Entity entity = spawnListEntry.type.create(world);
        if (entity instanceof MobEntity) {
            ((MobEntity) entity).onInitialSpawn((IServerWorld) world, world.getDifficultyForLocation(pos), SpawnReason.NATURAL, null, null);
            entity.setPosition(pos.getX() + 0.5, pos.getY() + 1.001, pos.getZ() + 0.5);
            if (world.hasNoCollisions(entity) && world.checkNoEntityCollision(entity, world.getBlockState(pos.up()).getShape(world, pos.up()))) { //doesNotCollide
                return (MobEntity) entity;
            }
        }
        return null;
    }

    public SidedFluidTankComponent<MechanicalDirtTile> getMeat() {
        return meat;
    }

    @Override
    public void tick() {
        super.tick();
        if (isServer() && this.world.getGameTime() % 5 == 0) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                TileEntity tile = world.getTileEntity(pos.offset(direction));
                if (tile instanceof MechanicalDirtTile) {
                    int difference = meat.getFluidAmount() - ((MechanicalDirtTile) tile).getMeat().getFluidAmount();
                    if (difference > 0) {
                        if (difference <= 25) difference = difference / 2;
                        else difference = 25;
                        if (meat.getFluidAmount() >= difference) {
                            meat.drainForced(((MechanicalDirtTile) tile).getMeat().fill(new FluidStack(ModuleCore.MEAT.getSourceFluid(), meat.drainForced(difference, IFluidHandler.FluidAction.SIMULATE).getAmount()), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                    difference = getEnergyStorage().getEnergyStored() - ((MechanicalDirtTile) tile).getEnergyStorage().getEnergyStored();
                    if (difference > 0) {
                        if (difference <= 1000 && difference > 1) difference = difference / 2;
                        if (difference > 1000) difference = 1000;
                        if (getEnergyStorage().getEnergyStored() >= difference) {
                            getEnergyStorage().extractEnergy(((MechanicalDirtTile) tile).getEnergyStorage().receiveEnergy(difference, false), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected EnergyStorageComponent<MechanicalDirtTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(MechanicalDirtConfig.maxStoredPower, 10, 20);
    }

    @Nonnull
    @Override
    public MechanicalDirtTile getSelf() {
        return this;
    }
}
