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

package com.buuz135.industrial.gui.component;

import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.ItemHandlerHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class BigItemGuiAddon extends BasicScreenAddon {

    private boolean tooltip;

    protected BigItemGuiAddon(int posX, int posY) {
        super(posX, posY);
        this.tooltip = true;
    }

    @Override
    public int getXSize() {
        return 18;
    }

    @Override
    public int getYSize() {
        return 18;
    }


    @Override
    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
        //RenderSystem.setupGui3DDiffuseLighting();
        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(ItemHandlerHelper.copyStackWithSize(getItemStack(), 1), guiX + getPosX() + 1, guiY + getPosY() + 1);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.enableAlphaTest();
        stack.push();
        stack.translate(0,0, 260);
        stack.scale(0.5f, 0.5f, 0.5f);
        String amount = getAmountDisplay();
        Minecraft.getInstance().fontRenderer.drawString(stack, TextFormatting.DARK_GRAY + amount , (guiX + getPosX() + 16 - Minecraft.getInstance().fontRenderer.getStringWidth(amount) / 2f)*2, (guiY + getPosY() + 19)*2, 0xFFFFFF);
        stack.pop();
    }

    @Override
    public void drawForegroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {

    }

    public BigItemGuiAddon withoutTooltip() {
        this.tooltip = false;
        return this;
    }

    @Override
    public List<ITextComponent> getTooltipLines() {
        if (this.tooltip && !getItemStack().isEmpty()){
            List<ITextComponent> tp = Minecraft.getInstance().currentScreen.getTooltipFromItem(getItemStack());
            tp.add(new StringTextComponent(TextFormatting.GOLD + new DecimalFormat().format(getAmount())));
            return tp;
        }
        return new ArrayList<>();
    }

    public abstract ItemStack getItemStack();

    public abstract int getAmount();

    public abstract String getAmountDisplay();
}
