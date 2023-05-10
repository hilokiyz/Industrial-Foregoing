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

package com.buuz135.industrial.api.conveyor;

import com.buuz135.industrial.api.IBlockContainer;
import com.google.common.collect.ImmutableSet;
import com.hrznstudio.titanium.api.IRecipeProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class ConveyorUpgradeFactory extends ForgeRegistryEntry<ConveyorUpgradeFactory> implements IRecipeProvider {

    public static final ImmutableSet<Direction> HORIZONTAL = ImmutableSet.copyOf(Direction.Plane.HORIZONTAL.iterator());
    public static final ImmutableSet<Direction> DOWN = ImmutableSet.of(Direction.DOWN);
    public static final List<ConveyorUpgradeFactory> FACTORIES = new ArrayList<>();

    private Item upgradeItem;

    public ConveyorUpgradeFactory() {
        FACTORIES.add(this);
    }

    public abstract ConveyorUpgrade create(IBlockContainer container, Direction face);

    @Nonnull
    public Set<Direction> getValidFacings() {
        return HORIZONTAL;
    }

    @Nonnull
    public abstract ResourceLocation getModel(Direction upgradeSide, Direction conveyorFacing);

    @Nonnull
    public abstract ResourceLocation getItemModel();

    public Set<ResourceLocation> getTextures() {
        return Collections.emptySet();
    }

    public Direction getSideForPlacement(World world, BlockPos pos, PlayerEntity player) {
        return player.getHorizontalFacing();
    }

    public Item getUpgradeItem() {
        return upgradeItem;
    }

    public void setUpgradeItem(Item upgradeItem) {
        this.upgradeItem = upgradeItem;
    }
}