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

package com.buuz135.industrial.block.generator.tile;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.buuz135.industrial.gui.component.GeneratorBackgroundScreenAddon;
import com.buuz135.industrial.worlddata.MycelialDataManager;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MycelialGeneratorTile extends IndustrialGeneratorTile<MycelialGeneratorTile> {

    @Save
    private int powerGeneration;
    private IMycelialGeneratorType type;
    private INBTSerializable<CompoundNBT>[] inputs;
    private ProgressBarComponent<MycelialGeneratorTile> bar;
    @Save
    private String owner;

    public MycelialGeneratorTile(BasicTileBlock<MycelialGeneratorTile> basicTileBlock, IMycelialGeneratorType type) {
        super(basicTileBlock);
        this.type = type;
        this.powerGeneration = 10;
        this.inputs = new INBTSerializable[this.type.getInputs().length];
        addGuiAddonFactory(() -> new GeneratorBackgroundScreenAddon(128, 39, type));
        for (int i = 0; i < this.type.getInputs().length; i++) {
            if (this.type.getInputs()[i] == IMycelialGeneratorType.Input.SLOT){
                SidedInventoryComponent<MycelialGeneratorTile> slot = (SidedInventoryComponent<MycelialGeneratorTile>) new SidedInventoryComponent<MycelialGeneratorTile>(this.type.getName() +".input_" + i, 44 + i * 21, 22, 1, i)
                        .setColor(this.type.getInputColors()[i])
                        .setInputFilter(this.type.getSlotInputPredicates().get(i))
                        .setSlotLimit(type.getSlotSize());
                this.addInventory(slot);
                this.inputs[i] = slot;
            } else if (this.type.getInputs()[i] == IMycelialGeneratorType.Input.TANK){
                SidedFluidTankComponent<MycelialGeneratorTile> slot = (SidedFluidTankComponent<MycelialGeneratorTile>) new SidedFluidTankComponent<MycelialGeneratorTile>(this.type.getName() +".input_" + i, 8000, 44 + i * 21, 20,  i)
                        .setColor(this.type.getInputColors()[i])
                        .setTankAction(FluidTankComponent.Action.FILL)
                        .setValidator(this.type.getTankInputPredicates().get(i));
                this.addTank(slot);
                this.inputs[i] = slot;
            }
        }
    }

    @Override
    public int consumeFuel() {
        Pair<Integer, Integer> inputs = this.type.getTimeAndPowerGeneration(this.inputs);
        this.powerGeneration = inputs.getRight();
        return inputs.getKey();
    }

    @Override
    public boolean canStart() {
        return this.type.canStart(this.inputs);
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return powerGeneration;
    }

    @Override
    public ProgressBarComponent<MycelialGeneratorTile> getProgressBar() {
         bar = new ProgressBarComponent<MycelialGeneratorTile>(30, 20, 0, 100){
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new ProgressBarScreenAddon(30, 20, bar){
                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> tooltip = new ArrayList<>();
                        tooltip.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.titanium.progressbar.progress").getString() +  TextFormatting.WHITE + new DecimalFormat().format(bar.getProgress()) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(bar.getMaxProgress())));
                        int progress = (bar.getMaxProgress() - bar.getProgress());
                        if (!bar.getIncreaseType()) progress = bar.getMaxProgress() - progress;
                        tooltip.add(new StringTextComponent(TextFormatting.GOLD + "ETA: " + TextFormatting.WHITE + new DecimalFormat().format(Math.ceil(progress * bar.getTickingTime() / 20D / bar.getProgressIncrease())) + TextFormatting.DARK_AQUA + "s"));
                        tooltip.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.industrialforegoing.generating").getString() +  TextFormatting.WHITE + powerGeneration + TextFormatting.DARK_AQUA+ " FE" + TextFormatting.GOLD + "/" + TextFormatting.WHITE +TextFormatting.DARK_AQUA+ "t"));
                        return tooltip;
                    }
                });
            }
         }
         .setComponentHarness(this)
         .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
         .setColor(DyeColor.CYAN);
         return bar;
    }

    @Override
    public void tick() {
        if (isServer() && bar.getCanIncrease().test(this) && (bar.getProgress() != 0 || canStart()) && this.world.getGameTime() % 5 == 0){
            MycelialDataManager.setGeneratorInfo(owner, this.world, this.pos, this.type);
            type.onTick(this.world, this.pos);
        }
        super.tick();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        for (int i = 0; i < this.inputs.length; i++) {
            compound.put("input_" + i, this.inputs[i].serializeNBT());
        }
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        for (int i = 0; i < this.inputs.length; i++) {
            this.inputs[i].deserializeNBT(compound.getCompound("input_" + i));
        }
        super.read(state, compound);
    }

    @Override
    public int getEnergyCapacity() {
        return 100000;
    }

    @Override
    public int getExtractingEnergy() {
        return 100000;
    }

    @Nonnull
    @Override
    public MycelialGeneratorTile getSelf() {
        return this;
    }

    @Override
    public boolean isSmart() {
        return true;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
