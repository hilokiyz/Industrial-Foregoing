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

package com.buuz135.industrial.block.transportstorage.conveyor;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.custom.TextGuiComponent;
import com.buuz135.industrial.gui.component.custom.TextureGuiComponent;
import com.buuz135.industrial.gui.component.custom.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.network.ConveyorSplittingSyncEntityMessage;
import com.buuz135.industrial.utils.MovementUtils;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConveyorSplittingUpgrade extends ConveyorUpgrade {

    public static VoxelShape NORTH = VoxelShapes.create(-0.08 + 0.75 / 2D, 0.1, 0.3 - 0.38, 0.32 + 0.75 / 2D, 0.16, 0.7 - 0.4);
    public static VoxelShape SOUTH = VoxelShapes.create(-0.08 + 0.75 / 2D, 0.1, 0.3 + 0.40, 0.32 + 0.75 / 2D, 0.16, 0.7 + 0.38);
    public static VoxelShape WEST = VoxelShapes.create(-0.08, 0.1, 0.3, 0.30, 0.16, 0.7);
    public static VoxelShape EAST = VoxelShapes.create(-0.05 + 0.75, 0.1, 0.3, 0.32 + 0.75, 0.16, 0.7);

    public List<Integer> handlingEntities;
    private Direction nextFacing;
    private int ratio;
    private int currentRatio;

    public ConveyorSplittingUpgrade(IBlockContainer container, ConveyorUpgradeFactory factory, Direction side) {
        super(container, factory, side);
        this.handlingEntities = new ArrayList<>();
        this.nextFacing = side;
        this.ratio = 1;
        this.currentRatio = 1;
    }

    @Override
    public void handleEntity(Entity entity) {
        super.handleEntity(entity);
        if (!getWorld().isRemote && !this.getContainer().getEntityFilter().contains(entity.getEntityId())) {
            if (nextFacing == this.getSide()) {
                this.handlingEntities.add(entity.getEntityId());
                this.getContainer().getEntityFilter().add(entity.getEntityId());
                IndustrialForegoing.NETWORK.sendToNearby(getContainer().getBlockWorld(), getPos(), 64, new ConveyorSplittingSyncEntityMessage(this.getPos(), entity.getEntityId(), this.getSide()));
                findNextUpgradeAndUpdate();
            }
        }
        if (handlingEntities.contains(entity.getEntityId())) {
            MovementUtils.handleConveyorMovement(entity, this.getSide(), this.getPos(), ((ConveyorTile) this.getContainer()).getConveyorType());
        }
    }

    @Override
    public void update() {
        super.update();
        if (handlingEntities.isEmpty()) return;
        AxisAlignedBB box = this.getWorld().getBlockState(this.getPos()).getCollisionShape(this.getWorld(), this.getPos()).toBoundingBoxList().get(0).offset(this.getPos()).grow(0.04);
        for (Integer integer : new ArrayList<>(handlingEntities)) {
            Entity entity = this.getWorld().getEntityByID(integer);
            if (entity != null && !box.intersects(entity.getBoundingBox())) {
                handlingEntities.remove(integer);
                this.getContainer().getEntityFilter().remove(integer);
            }
        }
    }

    @Override
    public void onUpgradeRemoved() {
        super.onUpgradeRemoved();
        if (nextFacing == this.getSide()) {
            findNextUpgradeAndUpdate();
        }
    }

    public void findNextUpgradeAndUpdate() {
        --currentRatio;
        if (currentRatio > 0) {
            this.getContainer().requestSync();
            return;
        }
        currentRatio = ratio;
        Direction facing = nextFacing.rotateY();
        ConveyorUpgrade conveyorUpgrade = ((ConveyorTile) this.getContainer()).getUpgradeMap().get(facing);
        int y = 0;
        while (!(conveyorUpgrade instanceof ConveyorSplittingUpgrade) && y < 10) {
            facing = facing.rotateY();
            conveyorUpgrade = ((ConveyorTile) this.getContainer()).getUpgradeMap().get(facing);
            ++y;
        }
        if (y >= 10) facing = this.getSide();
        ConveyorTile entityConveyor = (ConveyorTile) this.getContainer();
        for (Direction Direction : entityConveyor.getUpgradeMap().keySet()) {
            ConveyorUpgrade upgrade = entityConveyor.getUpgradeMap().get(Direction);
            if (upgrade instanceof ConveyorSplittingUpgrade) {
                ((ConveyorSplittingUpgrade) upgrade).setNextFacing(facing);
            }
        }
        this.getContainer().requestSync();
    }

    @Override
    public boolean ignoresCollision() {
        return true;
    }

    @Override
    public VoxelShape getBoundingBox() {
        switch (getSide()) {
            default:
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT() == null ? new CompoundNBT() : super.serializeNBT();
        compound.putString("NextFacing", nextFacing.getString());
        compound.putInt("Ratio", ratio);
        compound.putInt("CurrentRatio", currentRatio);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        nextFacing = Direction.byName(nbt.getString("NextFacing"));
        ratio = nbt.getInt("Ratio");
        currentRatio = nbt.getInt("CurrentRatio");
    }

    public Direction getNextFacing() {
        return nextFacing;
    }

    public void setNextFacing(Direction nextFacing) {
        this.nextFacing = nextFacing;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void addComponentsToGui(List<IGuiComponent> componentList) {
        super.addComponentsToGui(componentList);
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
        componentList.add(new TextureGuiComponent(20, 26, 16, 16, res, 40, 234, "splitting_ratio"));
        componentList.add(new TextGuiComponent(40, 31) {
            @Override
            public String getText() {
                return TextFormatting.DARK_GRAY + (ratio < 10 ? " " : "") + ratio;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(0, 60, 26, 14, 14,
                new StateButtonInfo(0, res, 1, 104, new String[]{"increase"})) {
            @Override
            public int getState() {
                return 0;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(1, 76, 26, 14, 14,
                new StateButtonInfo(0, res, 16, 104, new String[]{"decrease"})) {
            @Override
            public int getState() {
                return 0;
            }
        });
    }

    @Override
    public void handleButtonInteraction(int buttonId, CompoundNBT compound) {
        super.handleButtonInteraction(buttonId, compound);
        if (buttonId == 0 && ratio <= 64) {
            ++ratio;
            currentRatio = ratio;
            this.getContainer().requestSync();
        }
        if (buttonId == 1 && ratio > 1) {
            --ratio;
            currentRatio = ratio;
            this.getContainer().requestSync();
        }
    }

    public static class Factory extends ConveyorUpgradeFactory {

        public Factory() {
            setRegistryName("splitting");
        }

        @Override
        public ConveyorUpgrade create(IBlockContainer container, Direction face) {
            return new ConveyorSplittingUpgrade(container, this, face);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, Direction conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_splitting_" + upgradeSide.getString().toLowerCase());
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_splitting_upgrade");
        }

        @Override
        public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem())
                    .patternLine("IPI").patternLine("IDI").patternLine("ICI")
                    .key('I', Tags.Items.INGOTS_IRON)
                    .key('P', ModuleTransportStorage.CONVEYOR)
                    .key('D', Blocks.HOPPER)
                    .key('C', ModuleTransportStorage.CONVEYOR)
                    .build(consumer);
        }
    }
}
