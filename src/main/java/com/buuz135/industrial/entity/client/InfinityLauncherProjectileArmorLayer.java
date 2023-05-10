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

package com.buuz135.industrial.entity.client;

import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class InfinityLauncherProjectileArmorLayer<T extends LivingEntity, M extends PlayerModel<T>> extends StuckInBodyLayer<T, M> {

    public static HashMap<String, Integer> PROJECTILE_AMOUNT = new HashMap<>();

    private final EntityRendererManager entityRendererManager;
    private InfinityLauncherProjectileEntity projectile;

    public InfinityLauncherProjectileArmorLayer(LivingRenderer<T, M> livingRenderer) {
        super(livingRenderer);
        this.entityRendererManager = livingRenderer.getRenderManager();
    }

    @Override
    protected int func_225631_a_(T entity) {
        return PROJECTILE_AMOUNT.getOrDefault(entity.getUniqueID().toString(), 0);
    }

    @Override
    protected void func_225632_a_(MatrixStack p_225632_1_, IRenderTypeBuffer p_225632_2_, int p_225632_3_, Entity p_225632_4_, float p_225632_5_, float p_225632_6_, float p_225632_7_, float p_225632_8_) {
        float f = MathHelper.sqrt(p_225632_5_ * p_225632_5_ + p_225632_7_ * p_225632_7_);
        this.projectile = new InfinityLauncherProjectileEntity(p_225632_4_.world, p_225632_4_.getPosX(), p_225632_4_.getPosY(), p_225632_4_.getPosZ());
        this.projectile.rotationYaw = (float) (Math.atan2((double) p_225632_5_, (double) p_225632_7_) * (double) (180F / (float) Math.PI));
        this.projectile.rotationPitch = (float) (Math.atan2((double) p_225632_6_, (double) f) * (double) (180F / (float) Math.PI));
        this.projectile.prevRotationYaw = this.projectile.rotationYaw;
        this.projectile.prevRotationPitch = this.projectile.rotationPitch;
        this.entityRendererManager.renderEntityStatic(this.projectile, 0.0D, 0.0D, 0.0D, 0.0F, p_225632_8_, p_225632_1_, p_225632_2_, p_225632_3_);
    }
}
