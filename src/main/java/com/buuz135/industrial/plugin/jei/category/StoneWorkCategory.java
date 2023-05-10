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

package com.buuz135.industrial.plugin.jei.category;

import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class StoneWorkCategory implements IRecipeCategory<StoneWorkCategory.Wrapper> {

    public static ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "stonework");

    private final IGuiHelper helper;

    public StoneWorkCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends Wrapper> getRecipeClass() {
        return Wrapper.class;
    }

    @Override
    public String getTitle() {
        return "Material StoneWork Factory";
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 94, 0, 160, 26);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(Wrapper wrapper, IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, wrapper.getInput());
        iIngredients.setOutput(VanillaTypes.ITEM, wrapper.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, Wrapper wrapper, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(0, true, 0, 4);
        iRecipeLayout.getItemStacks().set(0, wrapper.getInput());

        iRecipeLayout.getItemStacks().init(10, false, 138, 4);
        iRecipeLayout.getItemStacks().set(10, wrapper.getOutput());

        for (int i = 0; i < wrapper.getModes().size(); i++) {
            iRecipeLayout.getItemStacks().init(i +1, true, 28 + i * 24, 4);
            iRecipeLayout.getItemStacks().set(1+ i, wrapper.getModes().get(i).getIcon());
            //ItemStackUtils.renderItemIntoGUI(matrixStack, recipe.getModes().get(i).getIcon(), 29 + i * 24, 5);
        }
    }

    @Override
    public void draw(Wrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {


    }

    public static class Wrapper {

        private final ItemStack input;
        private final List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes;
        private final ItemStack output;

        public Wrapper(ItemStack input, List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes, ItemStack output) {
            this.input = input;
            this.modes = modes;
            this.output = output;
            while (this.modes.size() < 4) {
                this.modes.add(MaterialStoneWorkFactoryTile.ACTION_RECIPES[4]);
            }
        }

        public ItemStack getInput() {
            return input;
        }

        public List<MaterialStoneWorkFactoryTile.StoneWorkAction> getModes() {
            return modes;
        }

        public ItemStack getOutput() {
            return output;
        }
    }
}
