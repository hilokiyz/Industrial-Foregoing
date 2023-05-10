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

package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.block.generator.tile.MycelialReactorTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import java.awt.*;

public class MycelialReactorTESR extends TileEntityRenderer<MycelialReactorTile> {

    public static RenderType TYPE = createRenderType();

    public static RenderType createRenderType() {
        RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(new ResourceLocation("industrialforegoing", "textures/blocks/mycelial.png"), false, false)).transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        }, () -> {
            RenderSystem.disableBlend();
        })).build(true);
        return RenderType.makeType("mycelial_render", DefaultVertexFormats.POSITION_TEX_COLOR, 7, 262144, false, true, state);
    }

    public MycelialReactorTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MycelialReactorTile te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (te.getBar().getProgress() == 0) return;
        stack.push();
        stack.translate(0.5, 1.75, 0.5D);
        stack.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        stack.rotate(Vector3f.ZP.rotationDegrees(90f));
        stack.rotate(Vector3f.XP.rotationDegrees(90f));
        double speed = 60;
        //if (true) speed = 20;
        float sin = ((float) Math.sin(te.getWorld().getGameTime() / speed)) * 0.05f;

        IVertexBuilder buffer1 = buffer.getBuffer(TYPE);
        Matrix4f matrix = stack.getLast().getMatrix();
        float pX1 = 1;
        float u = 1;
        float pX2 = 0;
        float u2 = 0;
        int color = Color.CYAN.getRGB();
        int red = ColorHelper.PackedColor.getRed(color);
        int green = ColorHelper.PackedColor.getGreen(color);
        int blue = ColorHelper.PackedColor.getBlue(color);
        float xOffset =-0.75f;
        float yOffset =-0f;
        float zOffset = -0.75f;
        int alpha = 256;
        buffer1.pos(matrix, pX2 + xOffset + sin , yOffset, 0 + zOffset + sin).tex(u2, 0).color(red, green, blue, alpha).endVertex();
        buffer1.pos(matrix, pX1 + xOffset - sin + 0.5f, yOffset, 0 + zOffset + sin).tex(u, 0).color(red, green, blue, alpha).endVertex();
        buffer1.pos(matrix, pX1 + xOffset - sin + 0.5f, yOffset, 1.5f + zOffset - sin).tex(u, 1).color(red, green, blue, alpha).endVertex();
        buffer1.pos(matrix, pX2 + xOffset +sin , yOffset, 1.5f + zOffset - sin).tex(u2, 1).color(red, green, blue, alpha).endVertex();
        yOffset = 0.01f;
        sin = ((float) Math.cos(te.getWorld().getGameTime() / speed)) * 0.05f;
        color = new Color(0xB578FF).getRGB();
        red = ColorHelper.PackedColor.getRed(color);
        green = ColorHelper.PackedColor.getGreen(color);
        blue = ColorHelper.PackedColor.getBlue(color);
        buffer1.pos(matrix, pX2 + xOffset + sin , yOffset, 0 + zOffset + sin).tex(u2, 0).color(red, green, blue, alpha).endVertex();
        buffer1.pos(matrix, pX1 + xOffset - sin + 0.5f, yOffset, 0 + zOffset - sin).tex(u, 0).color(red, green, blue, alpha).endVertex();
        buffer1.pos(matrix, pX1 + xOffset + sin + 0.5f, yOffset, 1.5f + zOffset + sin).tex(u, 1).color(red, green, blue, alpha).endVertex();
        buffer1.pos(matrix, pX2 + xOffset -sin, yOffset, 1.5f + zOffset + sin).tex(u2, 1).color(red, green, blue, alpha).endVertex();
        stack.pop();
    }
}
