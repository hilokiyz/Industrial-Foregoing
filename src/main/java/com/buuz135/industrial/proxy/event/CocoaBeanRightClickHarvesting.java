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

package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.utils.Reference;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CocoaBeanRightClickHarvesting {

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        /*
        if (!BlockRegistry.plantInteractorBlock.isRightClickCocoBeansEnabled()) return;
        if (event.getWorld().isRemote) return;
        if (event.getHand() != Hand.MAIN_HAND) return;
        if (event.getPlayerEntity() == null) return;
        BlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.getBlock() instanceof BlockCocoa && state.getValue(BlockCocoa.AGE) == 2) {
            ItemStack main = event.getPlayerEntity().getHeldItemMainhand();
            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, main);
            NonNullList<ItemStack> drops = NonNullList.create();
            state.getBlock().getDrops(drops, event.getWorld(), event.getPos(), state, fortune);
            if (drops.size() > 0) {
                drops.get(0).shrink(1);
            }
            ForgeEventFactory.fireBlockHarvesting(drops, event.getWorld(), event.getPos(), state, fortune, 1f, false, event.getPlayerEntity());
            event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockCocoa.AGE, 0));
            for (ItemStack stack : drops) ItemHandlerHelper.giveItemToPlayer(event.getPlayerEntity(), stack);
        }

         */
    }
}
