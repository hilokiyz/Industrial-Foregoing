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

package com.buuz135.industrial.block.tile;


import com.buuz135.industrial.capability.tile.BigEnergyHandler;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.GeneratorTile;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public abstract class IndustrialGeneratorTile<T extends IndustrialGeneratorTile<T>> extends GeneratorTile<T> {

    public IndustrialGeneratorTile(BasicTileBlock<T> basicTileBlock) {
        super(basicTileBlock);
    }

    @Override
    public ActionResultType onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == ActionResultType.SUCCESS) {
            return ActionResultType.SUCCESS;
        }
        openGui(playerIn);
        return ActionResultType.SUCCESS;
    }

    @Nonnull
    @Override
    protected EnergyStorageComponent<T> createEnergyStorage() {
        return new BigEnergyHandler<T>(getEnergyCapacity(), 0, getExtractingEnergy(), 10, 20) {
            @Override
            public void sync() {
                IndustrialGeneratorTile.this.syncObject(getEnergyStorage());
            }
        };
    }

}
