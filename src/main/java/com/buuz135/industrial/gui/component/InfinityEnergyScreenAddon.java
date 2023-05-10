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

import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import static com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon.drawBackground;
import static com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon.drawForeground;

public class InfinityEnergyScreenAddon extends BasicScreenAddon {

    private final InfinityEnergyStorage handler;
    private final long tier;
    private IAsset background;

    public InfinityEnergyScreenAddon(int posX, int posY, InfinityEnergyStorage handler) {
        super(posX, posY);
        this.handler = handler;
        this.tier = handler.getLongCapacity();
    }

    public static java.util.List<ITextComponent> getTooltip(long stored, long capacity) {
        return Arrays.asList(new StringTextComponent(TextFormatting.GOLD + "Power:"), new StringTextComponent(new DecimalFormat().format(stored) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(capacity) + TextFormatting.DARK_AQUA + " FE"));
    }

    @Override
    public int getXSize() {
        return background != null ? background.getArea().width : 0;
    }

    @Override
    public int getYSize() {
        return background != null ? background.getArea().height : 0;
    }

    @Override
    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        background = drawBackground(stack, screen, provider, getPosX(), getPosY(), guiX, guiY);
    }

    @Override
    public void drawForegroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {
        drawForeground(stack, screen, provider, getPosX(), getPosY(), guiX, guiY, handler.getLongEnergyStored(), tier);
    }

    @Override
    public List<ITextComponent> getTooltipLines() {
        return getTooltip(handler.getLongEnergyStored(), tier);
    }
}