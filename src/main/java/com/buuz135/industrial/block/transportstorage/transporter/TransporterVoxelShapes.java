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

package com.buuz135.industrial.block.transportstorage.transporter;

import net.minecraft.block.Block;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.stream.Stream;

public class TransporterVoxelShapes {

    public static VoxelShape DOWN_RING = VoxelShapes.combine(Block.makeCuboidShape(4, 0, 4, 12, 1, 12), Block.makeCuboidShape(6, 0, 6, 10, 1, 10), IBooleanFunction.ONLY_FIRST);
    public static VoxelShape DOWN_MIDDLE_EXTRACT = Block.makeCuboidShape(6, 1, 6, 10, 1.5, 10);
    public static VoxelShape DOWN_MIDDLE_INSERT = Block.makeCuboidShape(6, 0, 6, 10, 0.5, 10);

    public static VoxelShape NORTH_RING = Stream.of(
            Block.makeCuboidShape(4, 4, 0, 12, 6, 1),
            Block.makeCuboidShape(10, 6, 0, 12, 10, 1),
            Block.makeCuboidShape(4, 6, 0, 6, 10, 1),
            Block.makeCuboidShape(4, 10, 0, 12, 12, 1)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public static VoxelShape NORTH_MIDDLE_EXTRACT = Block.makeCuboidShape(6, 6, 1, 10, 10, 1.5);
    public static VoxelShape NORTH_MIDDLE_INSERT = Block.makeCuboidShape(6, 6, 0, 10, 10, 0.5);

    public static VoxelShape EAST_RING = Stream.of(
            Block.makeCuboidShape(15, 4, 4, 16, 6, 12),
            Block.makeCuboidShape(15, 6, 10, 16, 10, 12),
            Block.makeCuboidShape(15, 6, 4, 16, 10, 6),
            Block.makeCuboidShape(15, 10, 4, 16, 12, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public static VoxelShape EAST_MIDDLE_EXTRACT = Block.makeCuboidShape(14.5, 6, 6, 15, 10, 10);
    public static VoxelShape EAST_MIDDLE_INSERT = Block.makeCuboidShape(15.5, 6, 6, 16, 10, 10);

    public static VoxelShape WEST_RING = Stream.of(
            Block.makeCuboidShape(0, 4, 4, 1, 6, 12),
            Block.makeCuboidShape(0, 6, 10, 1, 10, 12),
            Block.makeCuboidShape(0, 6, 4, 1, 10, 6),
            Block.makeCuboidShape(0, 10, 4, 1, 12, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public static VoxelShape WEST_MIDDLE_EXTRACT = Block.makeCuboidShape(1, 6, 6, 1.5, 10, 10);
    public static VoxelShape WEST_MIDDLE_INSERT = Block.makeCuboidShape(0, 6, 6, 0.5, 10, 10);

    public static VoxelShape SOUTH_RING = Stream.of(
            Block.makeCuboidShape(4, 4, 15, 12, 6, 16),
            Block.makeCuboidShape(10, 6, 15, 12, 10, 16),
            Block.makeCuboidShape(4, 6, 15, 6, 10, 16),
            Block.makeCuboidShape(4, 10, 15, 12, 12, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public static VoxelShape SOUTH_MIDDLE_EXTRACT = Block.makeCuboidShape(6, 6, 14.5, 10, 10, 15);
    public static VoxelShape SOUTH_MIDDLE_INSERT = Block.makeCuboidShape(6, 6, 15.5, 10, 10, 16);

    public static VoxelShape UP_RING = Stream.of(
            Block.makeCuboidShape(4, 15, 10, 12, 16, 12),
            Block.makeCuboidShape(10, 15, 6, 12, 16, 10),
            Block.makeCuboidShape(4, 15, 6, 6, 16, 10),
            Block.makeCuboidShape(4, 15, 4, 12, 16, 6)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public static VoxelShape UP_MIDDLE_EXTRACT = Block.makeCuboidShape(6, 15, 6, 10, 15.5, 10);
    public static VoxelShape UP_MIDDLE_INSERT = Block.makeCuboidShape(6, 15.5, 6, 10, 16, 10);
}
