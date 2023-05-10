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

import com.buuz135.industrial.block.tile.IndustrialMachineTile;
import com.buuz135.industrial.config.machine.resourceproduction.FluidLaserBaseConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.recipe.LaserDrillFluidRecipe;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.hrznstudio.titanium.util.FacingUtil;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FluidLaserBaseTile extends IndustrialMachineTile<FluidLaserBaseTile> implements ILaserBase<FluidLaserBaseTile>{

    @Save
    private ProgressBarComponent<FluidLaserBaseTile> work;
    @Save
    private SidedInventoryComponent<FluidLaserBaseTile> catalyst;
    @Save
    private SidedFluidTankComponent<FluidLaserBaseTile> output;
    @Save
    private int miningDepth;

    public FluidLaserBaseTile() {
        super(ModuleResourceProduction.FLUID_LASER_BASE);
        setShowEnergy(false);
        this.miningDepth = this.getPos().getY();
        this.addProgressBar(work = new ProgressBarComponent<FluidLaserBaseTile>(74, 24 + 18, 0, FluidLaserBaseConfig.maxProgress){
                    @Override
                    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                        return Collections.singletonList(() -> new ProgressBarScreenAddon<FluidLaserBaseTile>(work.getPosX(), work.getPosY(), this){
                            @Override
                            public List<ITextComponent> getTooltipLines() {
                                List<ITextComponent> tooltip = new ArrayList<>();
                                tooltip.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.titanium.progressbar.progress").getString() + TextFormatting.WHITE + new DecimalFormat().format(work.getProgress()) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(work.getMaxProgress())));
                                return tooltip;
                            }
                        });
                    }
                }
                .setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT)
                .setCanIncrease(oreLaserBaseTile -> true)
                .setProgressIncrease(0)
                .setCanReset(oreLaserBaseTile -> true)
                .setOnStart(() -> {
                    int maxProgress = (int) Math.floor(FluidLaserBaseConfig.maxProgress * (this.hasAugmentInstalled(AugmentTypes.EFFICIENCY) ? AugmentWrapper.getType(this.getInstalledAugments(AugmentTypes.EFFICIENCY).get(0), AugmentTypes.EFFICIENCY) : 1));
                    work.setMaxProgress(maxProgress);
                })
                .setOnFinishWork(this::onWork)
        );
        this.addInventory(catalyst = (SidedInventoryComponent<FluidLaserBaseTile>) new SidedInventoryComponent<FluidLaserBaseTile>("lens" , 50, 24 + 18, 1, 0)
                .setColor(DyeColor.BLUE)
                .setRange(2,3)
                .setSlotLimit(1)
                //.setInputFilter((stack, integer) -> stack.getItem() instanceof LaserLensItem)
        );
        catalyst.getFacingModes().keySet().forEach(sideness -> catalyst.getFacingModes().put(sideness, IFacingComponent.FaceMode.NONE));
        this.addTank(output = (SidedFluidTankComponent<FluidLaserBaseTile>) new SidedFluidTankComponent<FluidLaserBaseTile>("output", 32000, 102, 20, 1)
            .setColor(DyeColor.ORANGE)
                .setTankAction(FluidTankComponent.Action.DRAIN)
        );
        int y = 84;
        this.addButton(new ArrowButtonComponent(53, y, 14, 14, FacingUtil.Sideness.LEFT).setPredicate((playerEntity, compoundNBT) -> {
            this.miningDepth = Math.max(0, miningDepth -1);
            markForUpdate();
        }));
        this.addButton(new ArrowButtonComponent(126, y, 14, 14, FacingUtil.Sideness.RIGHT).setPredicate((playerEntity, compoundNBT) -> {
            this.miningDepth = Math.min(255, miningDepth + 1);
            markForUpdate();
        }));
        this.addGuiAddonFactory(() -> new TextScreenAddon("" ,70, y + 3, false){
            @Override
            public String getText() {
                return TextFormatting.DARK_GRAY +  new TranslationTextComponent("text.industrialforegoing.depth").getString() + miningDepth;
            }
        });
    }

    @Override
    public void setWorldAndPos(World world, BlockPos pos) {
        super.setWorldAndPos(world, pos);
        if (this.miningDepth == 0) this.miningDepth = this.pos.getY();
    }

    private void onWork(){
        if (!catalyst.getStackInSlot(0).isEmpty()){
            VoxelShape box = VoxelShapes.create(-1, 0, -1, 2, 3, 2).withOffset(this.pos.getX(), this.pos.getY() - 1, this.pos.getZ());
            RecipeUtil.getRecipes(this.world, LaserDrillFluidRecipe.SERIALIZER.getRecipeType())
                    .stream()
                    .filter(laserDrillFluidRecipe -> laserDrillFluidRecipe.catalyst.test(catalyst.getStackInSlot(0)))
                    .filter(laserDrillFluidRecipe -> laserDrillFluidRecipe.getValidRarity(this.world.getBiome(this.pos).getRegistryName(), this.miningDepth) != null)
                    .findFirst()
                    .ifPresent(laserDrillFluidRecipe -> {
                        if (!LaserDrillFluidRecipe.EMPTY.equals(laserDrillFluidRecipe.entity)){
                            List<LivingEntity> entities = this.world.getEntitiesWithinAABB(LivingEntity.class, box.getBoundingBox(), entity -> entity.getType().getRegistryName().equals(laserDrillFluidRecipe.entity));
                            if (entities.size() > 0){
                                LivingEntity first = entities.get(0);
                                if (first.getHealth() > 5){
                                    first.attackEntityFrom(DamageSource.GENERIC, 5);
                                    output.fillForced(FluidStack.loadFluidStackFromNBT(laserDrillFluidRecipe.output), IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        } else {
                            output.fillForced(FluidStack.loadFluidStackFromNBT(laserDrillFluidRecipe.output), IFluidHandler.FluidAction.EXECUTE);
                        }
                    });
        }
    }

    @Override
    public FluidLaserBaseTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<FluidLaserBaseTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(0, 4, 10);
    }

    @Override
    public ProgressBarComponent<FluidLaserBaseTile> getBar() {
        return work;
    }

    @Override
    public boolean canAcceptAugment(ItemStack augment) {
        if (AugmentWrapper.hasType(augment, AugmentTypes.SPEED)) {
            return false;
        }
        return super.canAcceptAugment(augment);
    }

}
