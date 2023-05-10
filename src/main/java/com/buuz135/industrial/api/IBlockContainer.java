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

package com.buuz135.industrial.api;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface IBlockContainer<T> {

    World getBlockWorld();

    BlockPos getBlockPosition();

    void requestSync();

    void requestFluidSync();

    boolean hasUpgrade(Direction facing);

    void addUpgrade(Direction facing, T factory);

    void removeUpgrade(Direction facing, boolean drop);

    List<Integer> getEntityFilter();

    class Empty implements IBlockContainer<ConveyorUpgradeFactory> {
        @Override
        public World getBlockWorld() {
            return null;
        }

        @Override
        public BlockPos getBlockPosition() {
            return null;
        }

        @Override
        public void requestSync() {

        }

        @Override
        public void requestFluidSync() {

        }

        @Override
        public boolean hasUpgrade(Direction facing) {
            return false;
        }

        @Override
        public void addUpgrade(Direction facing, ConveyorUpgradeFactory factory) {

        }

        @Override
        public void removeUpgrade(Direction facing, boolean drop) {

        }

        @Override
        public List<Integer> getEntityFilter() {
            return new ArrayList<>();
        }
    }
}