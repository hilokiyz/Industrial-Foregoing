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

package com.buuz135.industrial.module;

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.block.MachineFrameBlock;
import com.buuz135.industrial.block.core.DarkGlassBlock;
import com.buuz135.industrial.block.core.DissolutionChamberBlock;
import com.buuz135.industrial.block.core.FluidExtractorBlock;
import com.buuz135.industrial.block.core.LatexProcessingUnitBlock;
import com.buuz135.industrial.block.core.tile.FluidExtractorTile;
import com.buuz135.industrial.fluid.OreFluidInstance;
import com.buuz135.industrial.item.*;
import com.buuz135.industrial.item.addon.EfficiencyAddonItem;
import com.buuz135.industrial.item.addon.ProcessingAddonItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.item.addon.SpeedAddonItem;
import com.buuz135.industrial.proxy.StrawRegistry;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.fluid.TitaniumFluidInstance;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.ArrayList;
import java.util.List;

public class ModuleCore implements IModule {

    public static Rarity PITY_RARITY;
    public static Rarity SIMPLE_RARITY;
    public static Rarity ADVANCED_RARITY;
    public static Rarity SUPREME_RARITY;

    public static AdvancedTitaniumTab TAB_CORE = new AdvancedTitaniumTab(Reference.MOD_ID + "_core", true);

    public static IFCustomItem TINY_DRY_RUBBER = new RecipelessCustomItem("tinydryrubber", TAB_CORE);
    public static IFCustomItem DRY_RUBBER = new RecipelessCustomItem("dryrubber", TAB_CORE);
    public static IFCustomItem PLASTIC = new RecipelessCustomItem("plastic", TAB_CORE);
    public static FertilizerItem FERTILIZER = new FertilizerItem(TAB_CORE);
    public static IFCustomItem PINK_SLIME_ITEM = new RecipelessCustomItem("pink_slime", TAB_CORE);
    public static IFCustomItem PINK_SLIME_INGOT = new RecipelessCustomItem("pink_slime_ingot", TAB_CORE);
    public static ItemStraw STRAW = new ItemStraw(TAB_CORE);
    public static MachineFrameBlock PITY;
    public static MachineFrameBlock SIMPLE;
    public static MachineFrameBlock ADVANCED;
    public static MachineFrameBlock SUPREME;
    public static FluidExtractorBlock FLUID_EXTRACTOR = new FluidExtractorBlock();
    public static LatexProcessingUnitBlock LATEX_PROCESSING = new LatexProcessingUnitBlock();
    public static DissolutionChamberBlock DISSOLUTION_CHAMBER = new DissolutionChamberBlock();
    public static RangeAddonItem[] RANGE_ADDONS = new RangeAddonItem[12];
    public static LaserLensItem[] LASER_LENS = new LaserLensItem[DyeColor.values().length];
    public static SpeedAddonItem SPEED_ADDON_1 = new SpeedAddonItem(1, TAB_CORE);
    public static SpeedAddonItem SPEED_ADDON_2 = new SpeedAddonItem(2, TAB_CORE);
    public static EfficiencyAddonItem EFFICIENCY_ADDON_1 = new EfficiencyAddonItem(1, TAB_CORE);
    public static EfficiencyAddonItem EFFICIENCY_ADDON_2 = new EfficiencyAddonItem(2, TAB_CORE);
    public static ProcessingAddonItem PROCESSING_ADDON_1 = new ProcessingAddonItem(1, TAB_CORE);
    public static ProcessingAddonItem PROCESSING_ADDON_2 = new ProcessingAddonItem(2, TAB_CORE);
    public static DarkGlassBlock DARK_GLASS = new DarkGlassBlock();

    public static TitaniumFluidInstance LATEX = new TitaniumFluidInstance(Reference.MOD_ID, "latex", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/latex_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/latex_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance MEAT = new TitaniumFluidInstance(Reference.MOD_ID, "meat", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/meat_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/meat_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance SEWAGE = new TitaniumFluidInstance(Reference.MOD_ID, "sewage", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sewage_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sewage_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance ESSENCE = new TitaniumFluidInstance(Reference.MOD_ID, "essence", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/essence_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/essence_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance SLUDGE = new TitaniumFluidInstance(Reference.MOD_ID, "sludge", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sludge_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sludge_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance PINK_SLIME = new TitaniumFluidInstance(Reference.MOD_ID, "pink_slime", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/pink_slime_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/pink_slime_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance BIOFUEL = new TitaniumFluidInstance(Reference.MOD_ID, "biofuel", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/biofuel_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/biofuel_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance ETHER = new TitaniumFluidInstance(Reference.MOD_ID, "ether_gas", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ether_gas_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ether_gas_flow")).gaseous(), true, TAB_CORE);
    public static OreFluidInstance RAW_ORE_MEAT = new OreFluidInstance(Reference.MOD_ID, "raw_ore_meat", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/raw_ore_meat_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/raw_ore_meat_flow")), true, TAB_CORE);
    public static OreFluidInstance FERMENTED_ORE_MEAT = new OreFluidInstance(Reference.MOD_ID, "fermented_ore_meat", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ether_gas_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ether_gas_flow")), true, TAB_CORE);


    @Override
    public List<Feature.Builder> generateFeatures() {
        PITY_RARITY = Rarity.create("pity", TextFormatting.GREEN);
        SIMPLE_RARITY = Rarity.create("simple", TextFormatting.AQUA);
        ADVANCED_RARITY = Rarity.create("advanced", TextFormatting.LIGHT_PURPLE);
        SUPREME_RARITY = Rarity.create("supreme", TextFormatting.GOLD);
        PITY = (MachineFrameBlock) new MachineFrameBlock(PITY_RARITY, TAB_CORE).setRegistryName("machine_frame_pity");
        SIMPLE = (MachineFrameBlock) new MachineFrameBlock(SIMPLE_RARITY, TAB_CORE).setRegistryName("machine_frame_simple");
        ADVANCED = (MachineFrameBlock) new MachineFrameBlock(ADVANCED_RARITY, TAB_CORE).setRegistryName("machine_frame_advanced");
        SUPREME = (MachineFrameBlock) new MachineFrameBlock(SUPREME_RARITY, TAB_CORE).setRegistryName("machine_frame_supreme");
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("plastic").
                content(Item.class, TINY_DRY_RUBBER).
                content(Item.class, DRY_RUBBER).
                content(Item.class, PLASTIC).
                content(TitaniumFluidInstance.class, LATEX).
                eventClient(() -> () -> EventManager.mod(TextureStitchEvent.Pre.class).process(this::textureStitch)));
        features.add(Feature.builder("plastic_generation").
                content(Block.class, FLUID_EXTRACTOR).
                content(Block.class, LATEX_PROCESSING).
                event(EventManager.forge(TickEvent.WorldTickEvent.class).
                        filter(worldTickEvent -> worldTickEvent.phase == TickEvent.Phase.END && worldTickEvent.type == TickEvent.Type.WORLD && worldTickEvent.world.getGameTime() % 40 == 0 && FluidExtractorTile.EXTRACTION.containsKey(worldTickEvent.world.getDimensionType())).
                        process(worldTickEvent -> FluidExtractorTile.EXTRACTION.get(worldTickEvent.world.getDimensionType()).values().forEach(blockPosFluidExtractionProgressHashMap -> blockPosFluidExtractionProgressHashMap.keySet().forEach(pos -> worldTickEvent.world.sendBlockBreakProgress(blockPosFluidExtractionProgressHashMap.get(pos).getBreakID(), pos, blockPosFluidExtractionProgressHashMap.get(pos).getProgress()))))));
        features.add(Feature.builder("pink_slime").
                content(Item.class, PINK_SLIME_ITEM).
                content(Item.class, PINK_SLIME_INGOT).
                content(TitaniumFluidInstance.class, PINK_SLIME));
        features.add(Feature.builder("fertilizer").
                content(Item.class, FERTILIZER));
        features.add(Feature.builder("straw").
                event(EventManager.modGeneric(RegistryEvent.Register.class, StrawHandler.class).process(register -> StrawRegistry.register((RegistryEvent.Register<StrawHandler>) register))).
                content(Item.class, STRAW));
        features.add(Feature.builder("machine_frames").
                content(Block.class, PITY).
                content(Block.class, SIMPLE).
                content(Block.class, ADVANCED).
                content(Block.class, SUPREME));
        features.add(Feature.builder("tier_2_production").
                content(Block.class, DISSOLUTION_CHAMBER));
        Feature.Builder builder = Feature.builder("range_addons");
        for (int i = 0; i < RANGE_ADDONS.length; i++) {
            RANGE_ADDONS[i] = new RangeAddonItem(i, TAB_CORE);
            builder.content(Item.class, RANGE_ADDONS[i]);
        }
        features.add(builder);
        features.add(Feature.builder("speed_addons").content(Item.class, SPEED_ADDON_1).content(Item.class, SPEED_ADDON_2));
        features.add(Feature.builder("efficiency_addons").content(Item.class, EFFICIENCY_ADDON_1).content(Item.class, EFFICIENCY_ADDON_2));
        features.add(Feature.builder("processing_addons").content(Item.class, PROCESSING_ADDON_1).content(Item.class, PROCESSING_ADDON_2));
        features.add(Feature.builder("meat").content(TitaniumFluidInstance.class, MEAT));
        features.add(Feature.builder("sewage").content(TitaniumFluidInstance.class, SEWAGE));
        features.add(Feature.builder("essence").content(TitaniumFluidInstance.class, ESSENCE));
        features.add(Feature.builder("sludge").content(TitaniumFluidInstance.class, SLUDGE));
        features.add(Feature.builder("biofuel").content(TitaniumFluidInstance.class, BIOFUEL));
        features.add(Feature.builder("ether").content(TitaniumFluidInstance.class, ETHER));
        features.add(Feature.builder("ore_meat").content(OreFluidInstance.class, RAW_ORE_MEAT).content(OreFluidInstance.class, FERMENTED_ORE_MEAT));
        TAB_CORE.addIconStack(new ItemStack(PLASTIC));
        features.add(createFeature(DARK_GLASS));
        builder = Feature.builder("laser_lens");
        for (DyeColor value : DyeColor.values()) {
            builder.content(Item.class, LASER_LENS[value.getId()] = new LaserLensItem(value.getId()));
        }
        features.add(builder);
        return features;
    }

    @OnlyIn(Dist.CLIENT)
    public void textureStitch(TextureStitchEvent.Pre event) {
        event.addSprite(LATEX.getSourceFluid().getAttributes().getFlowingTexture());
        event.addSprite(LATEX.getSourceFluid().getAttributes().getStillTexture());
    }
}
