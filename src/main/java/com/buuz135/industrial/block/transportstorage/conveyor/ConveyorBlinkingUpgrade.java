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

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.block.transportstorage.ConveyorBlock;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.custom.FilterGuiComponent;
import com.buuz135.industrial.gui.component.custom.TextGuiComponent;
import com.buuz135.industrial.gui.component.custom.TextureGuiComponent;
import com.buuz135.industrial.gui.component.custom.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.ItemStackFilter;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ConveyorBlinkingUpgrade extends ConveyorUpgrade {

    public static VoxelShape BB = VoxelShapes.create(0.0625 * 3, 0.0625, 0.0625 * 3, 0.0625 * 13, 0.0625 * 1.2, 0.0625 * 13);

    private ItemStackFilter filter;
    private boolean whitelist;
    private int verticalDisplacement;
    private int horizontalDisplacement;

    public ConveyorBlinkingUpgrade(IBlockContainer container, ConveyorUpgradeFactory factory, Direction side) {
        super(container, factory, side);
        this.filter = new ItemStackFilter(20, 20, 3, 3);
        this.whitelist = false;
        this.verticalDisplacement = 0;
        this.horizontalDisplacement = 1;
    }

    @Override
    public void handleEntity(Entity entity) {
        super.handleEntity(entity);
        if (whitelist != filter.matches(entity)) return;
        Direction direction = this.getContainer().getBlockWorld().getBlockState(this.getContainer().getBlockPosition()).get(ConveyorBlock.FACING);
        Vector3d vec3d = new Vector3d(horizontalDisplacement * direction.getDirectionVec().getX(), verticalDisplacement, horizontalDisplacement * direction.getDirectionVec().getZ());
        BlockPos pos = this.getPos().add(vec3d.x, vec3d.y, vec3d.z);
        entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
        this.getWorld().playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.AMBIENT, 0.5f, 1f);
    }

    @Override
    public VoxelShape getBoundingBox() {
        return BB;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT() == null ? new CompoundNBT() : super.serializeNBT();
        compound.put("Filter", filter.serializeNBT());
        compound.putBoolean("Whitelist", whitelist);
        compound.putDouble("VerticalDisplacement", verticalDisplacement);
        compound.putDouble("HorizontalDisplacement", horizontalDisplacement);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Filter")) filter.deserializeNBT(nbt.getCompound("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
        horizontalDisplacement = nbt.getInt("HorizontalDisplacement");
        verticalDisplacement = nbt.getInt("VerticalDisplacement");
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public boolean ignoresCollision() {
        return true;
    }

    @Override
    public void handleButtonInteraction(int buttonId, CompoundNBT compound) {
        super.handleButtonInteraction(buttonId, compound);
        if (buttonId >= 0 && buttonId < filter.getFilter().length) {
            this.filter.setFilter(buttonId, ItemStack.read(compound));
            this.getContainer().requestSync();
        }
        if (buttonId == 10) {
            whitelist = !whitelist;
            this.getContainer().requestSync();
        }
        if (buttonId == 11 && horizontalDisplacement < 8) {
            horizontalDisplacement += 1;
            this.getContainer().requestSync();
        }
        if (buttonId == 12 && horizontalDisplacement > 1) {
            horizontalDisplacement -= 1;
            this.getContainer().requestSync();
        }
        if (buttonId == 13 && verticalDisplacement < 8) {
            verticalDisplacement += 1;
            this.getContainer().requestSync();
        }
        if (buttonId == 14 && verticalDisplacement > -8) {
            verticalDisplacement -= 1;
            this.getContainer().requestSync();
        }
    }

    @Override
    public void addComponentsToGui(List<IGuiComponent> componentList) {
        super.addComponentsToGui(componentList);
        componentList.add(new FilterGuiComponent(this.filter.getLocX(), this.filter.getLocY(), this.filter.getSizeX(), this.filter.getSizeY()) {
            @Override
            public IFilter getFilter() {
                return ConveyorBlinkingUpgrade.this.filter;
            }
        });
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
        componentList.add(new TexturedStateButtonGuiComponent(10, 80, 19, 18, 18,
                new StateButtonInfo(0, res, 1, 214, new String[]{"whitelist"}),
                new StateButtonInfo(1, res, 20, 214, new String[]{"blacklist"})) {
            @Override
            public int getState() {
                return whitelist ? 0 : 1;
            }
        });
        componentList.add(new TextureGuiComponent(80, 40, 16, 16, res, 2, 234, "distance_horizontal"));
        componentList.add(new TextureGuiComponent(80, 56, 16, 16, res, 21, 234, "distance_vertical"));
        componentList.add(new TextGuiComponent(104, 44) {
            @Override
            public String getText() {
                return TextFormatting.DARK_GRAY + " " + horizontalDisplacement;
            }
        });
        componentList.add(new TextGuiComponent(104, 61) {
            @Override
            public String getText() {
                return TextFormatting.DARK_GRAY + (verticalDisplacement >= 0 ? " " : "") + verticalDisplacement;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(11, 130, 40, 14, 14,
                new StateButtonInfo(0, res, 1, 104, new String[]{"increase"})) {
            @Override
            public int getState() {
                return 0;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(12, 146, 40, 14, 14,
                new StateButtonInfo(0, res, 16, 104, new String[]{"decrease"})) {
            @Override
            public int getState() {
                return 0;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(13, 130, 56, 14, 14,
                new StateButtonInfo(0, res, 1, 104, new String[]{"increase"})) {
            @Override
            public int getState() {
                return 0;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(14, 146, 56, 14, 14,
                new StateButtonInfo(0, res, 16, 104, new String[]{"decrease"})) {
            @Override
            public int getState() {
                return 0;
            }
        });
    }


    public static class Factory extends ConveyorUpgradeFactory {

        public Factory() {
            setRegistryName("blinking");
        }

        @Override
        public ConveyorUpgrade create(IBlockContainer container, Direction face) {
            return new ConveyorBlinkingUpgrade(container, this, face);
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Collections.singleton(new ResourceLocation(Reference.MOD_ID, "blocks/conveyor_blinking_upgrade"));
        }

        @Nonnull
        @Override
        public Set<Direction> getValidFacings() {
            return DOWN;
        }

        @Override
        public Direction getSideForPlacement(World world, BlockPos pos, PlayerEntity player) {
            return Direction.DOWN;
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, Direction conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_blinking");
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_blinking_upgrade");
        }

        @Override
        public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem()).patternLine("IPI").patternLine("IDI").patternLine("ICI")
                    .key('I', Tags.Items.INGOTS_IRON)
                    .key('P', Items.CHORUS_FRUIT)
                    .key('D', Blocks.PISTON)
                    .key('C', ModuleTransportStorage.CONVEYOR)
                    .build(consumer);
        }
    }
}
