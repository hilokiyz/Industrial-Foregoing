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

package com.buuz135.industrial.block.transportstorage;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.transportstorage.tile.BlackHoleControllerTile;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class BlackHoleControllerBlock extends IndustrialBlock<BlackHoleControllerTile> {

    public BlackHoleControllerBlock() {
        super("black_hole_controller", Properties.from(Blocks.IRON_BLOCK), BlackHoleControllerTile.class, ModuleTransportStorage.TAB_TRANSPORT);
    }

    @Override
    public IFactory<BlackHoleControllerTile> getTileEntityFactory() {
        return BlackHoleControllerTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PGP").patternLine("NEN").patternLine("PMP")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('G', IndustrialTags.Items.GEAR_DIAMOND)
                .key('N', Items.ENDER_EYE)
                .key('E', Tags.Items.CHESTS_ENDER)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_SUPREME)
                .build(consumer);
    }
}
