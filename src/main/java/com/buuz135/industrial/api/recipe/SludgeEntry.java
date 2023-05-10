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

package com.buuz135.industrial.api.recipe;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SludgeEntry {

    public static List<SludgeEntry> SLUDGE_RECIPES = new ArrayList<>();

    private ItemStack stack;
    private int weight;

    /**
     * Represents an entry for the sludge refiner.
     *
     * @param stack  The ItemStack as an output.
     * @param weight The weight in the global pool of items.
     */
    public SludgeEntry(ItemStack stack, int weight) {
        this.stack = stack;
        this.weight = weight;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getWeight() {
        return weight;
    }
}
