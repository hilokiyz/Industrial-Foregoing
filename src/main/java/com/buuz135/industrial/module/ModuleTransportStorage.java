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

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.*;
import com.buuz135.industrial.block.transportstorage.conveyor.*;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterFluidType;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterItemType;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterWorldType;
import com.buuz135.industrial.gui.conveyor.ContainerConveyor;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.gui.transporter.ContainerTransporter;
import com.buuz135.industrial.gui.transporter.GuiTransporter;
import com.buuz135.industrial.item.ItemConveyorUpgrade;
import com.buuz135.industrial.item.ItemTransporterType;
import com.buuz135.industrial.proxy.client.model.ConveyorBlockModel;
import com.buuz135.industrial.proxy.client.model.TransporterBlockModel;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.ImmutableMap;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleTransportStorage implements IModule {

    public static AdvancedTitaniumTab TAB_TRANSPORT = new AdvancedTitaniumTab(Reference.MOD_ID + "_transport", true);

    public static ConveyorUpgradeFactory upgrade_extraction = new ConveyorExtractionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_insertion = new ConveyorInsertionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_detector = new ConveyorDetectorUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_bouncing = new ConveyorBouncingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_dropping = new ConveyorDroppingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_blinking = new ConveyorBlinkingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_splitting = new ConveyorSplittingUpgrade.Factory();

    public static ConveyorBlock CONVEYOR = new ConveyorBlock(TAB_TRANSPORT);
    public static HashMap<ResourceLocation, IBakedModel> CONVEYOR_UPGRADES_CACHE = new HashMap<>();

    public static BlackHoleUnitBlock BLACK_HOLE_UNIT_COMMON = new BlackHoleUnitBlock(Rarity.COMMON);
    public static BlackHoleUnitBlock BLACK_HOLE_UNIT_PITY = new BlackHoleUnitBlock(ModuleCore.PITY_RARITY);
    public static BlackHoleUnitBlock BLACK_HOLE_UNIT_SIMPLE = new BlackHoleUnitBlock(ModuleCore.SIMPLE_RARITY);
    public static BlackHoleUnitBlock BLACK_HOLE_UNIT_ADVANCED = new BlackHoleUnitBlock(ModuleCore.ADVANCED_RARITY);
    public static BlackHoleUnitBlock BLACK_HOLE_UNIT_SUPREME = new BlackHoleUnitBlock(ModuleCore.SUPREME_RARITY);

    public static BlackHoleTankBlock BLACK_HOLE_TANK_COMMON = new BlackHoleTankBlock(Rarity.COMMON);
    public static BlackHoleTankBlock BLACK_HOLE_TANK_PITY = new BlackHoleTankBlock(ModuleCore.PITY_RARITY);
    public static BlackHoleTankBlock BLACK_HOLE_TANK_SIMPLE = new BlackHoleTankBlock(ModuleCore.SIMPLE_RARITY);
    public static BlackHoleTankBlock BLACK_HOLE_TANK_ADVANCED = new BlackHoleTankBlock(ModuleCore.ADVANCED_RARITY);
    public static BlackHoleTankBlock BLACK_HOLE_TANK_SUPREME = new BlackHoleTankBlock(ModuleCore.SUPREME_RARITY);

    public static BlackHoleControllerBlock BLACK_HOLE_CONTROLLER = new BlackHoleControllerBlock();


    public static TransporterTypeFactory ITEM_TRANSPORTER = new TransporterItemType.Factory();
    public static TransporterTypeFactory FLUID_TRANSPORTER = new TransporterFluidType.Factory();
    public static TransporterTypeFactory WORLD_TRANSPORTER = new TransporterWorldType.Factory();

    public static TransporterBlock TRANSPORTER = new TransporterBlock(TAB_TRANSPORT);
    public static HashMap<ResourceLocation, IBakedModel> TRANSPORTER_CACHE = new HashMap<>();

    @Override
    public List<Feature.Builder> generateFeatures() {
        TAB_TRANSPORT.addIconStack(new ItemStack(CONVEYOR));
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("conveyor")
                .content(Block.class, CONVEYOR)
                .content(ContainerType.class, (ContainerType) IForgeContainerType.create(ContainerConveyor::new).setRegistryName(new ResourceLocation(Reference.MOD_ID, "conveyor")))
                .eventClient(() -> () -> EventManager.mod(FMLClientSetupEvent.class).process(this::onClientSetupConveyor))
        );
        Feature.Builder builder = Feature.builder("conveyor_upgrades")
                .eventClient(() -> () -> EventManager.mod(ModelBakeEvent.class).process(this::conveyorBake))
                .eventClient(() -> () -> EventManager.mod(TextureStitchEvent.Pre.class).process(this::textureStitch));
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> builder.content(Item.class, new ItemConveyorUpgrade(conveyorUpgradeFactory, TAB_TRANSPORT)));
        features.add(builder);
        features.add(Feature.builder("black_hole_units")
                .content(Block.class,BLACK_HOLE_UNIT_COMMON )
                .content(Block.class, BLACK_HOLE_UNIT_PITY)
                .content(Block.class, BLACK_HOLE_UNIT_SIMPLE)
                .content(Block.class, BLACK_HOLE_UNIT_ADVANCED)
                .content(Block.class, BLACK_HOLE_UNIT_SUPREME)
        );
        features.add(Feature.builder("black_hole_tanks")
                .content(Block.class, BLACK_HOLE_TANK_COMMON)
                .content(Block.class, BLACK_HOLE_TANK_PITY)
                .content(Block.class, BLACK_HOLE_TANK_SIMPLE)
                .content(Block.class, BLACK_HOLE_TANK_ADVANCED)
                .content(Block.class, BLACK_HOLE_TANK_SUPREME)
        );
        features.add(createFeature(BLACK_HOLE_CONTROLLER));
        Feature.Builder transporters = Feature.builder("transporters")
                .content(Block.class, TRANSPORTER)
                .eventClient(() -> () -> EventManager.mod(ModelBakeEvent.class).process(this::transporterBake))
                .eventClient(() -> () -> EventManager.mod(TextureStitchEvent.Pre.class).process(this::transporterTextureStitch))
                .content(ContainerType.class, (ContainerType) IForgeContainerType.create(ContainerTransporter::new).setRegistryName(new ResourceLocation(Reference.MOD_ID, "transporter")))
                .eventClient(() -> () -> EventManager.mod(FMLClientSetupEvent.class).process(this::onClientSetupTransporter));
        TransporterTypeFactory.FACTORIES.forEach(transporterTypeFactory -> builder.content(Item.class, new ItemTransporterType(transporterTypeFactory, TAB_TRANSPORT)));
        features.add(transporters);
        return features;
    }

    @OnlyIn(Dist.CLIENT)
    private void conveyorBake(ModelBakeEvent event) {
        for (ResourceLocation resourceLocation : event.getModelRegistry().keySet()) {
            if (resourceLocation.getNamespace().equals(Reference.MOD_ID)) {
                if (resourceLocation.getPath().contains("conveyor") && !resourceLocation.getPath().contains("upgrade"))
                    event.getModelRegistry().put(resourceLocation, new ConveyorBlockModel(event.getModelRegistry().get(resourceLocation)));
            }
        }
        for (ConveyorUpgradeFactory conveyorUpgradeFactory : ConveyorUpgradeFactory.FACTORIES) {
            for (Direction upgradeFacing : conveyorUpgradeFactory.getValidFacings()) {
                for (Direction conveyorFacing : ConveyorBlock.FACING.getAllowedValues()) {
                    try {
                        ResourceLocation resourceLocation = conveyorUpgradeFactory.getModel(upgradeFacing, conveyorFacing);
                        IUnbakedModel unbakedModel = event.getModelLoader().getUnbakedModel(resourceLocation);
                        CONVEYOR_UPGRADES_CACHE.put(resourceLocation, unbakedModel.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new SimpleModelTransform(ImmutableMap.of()), resourceLocation));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void textureStitch(TextureStitchEvent.Pre pre) {
        if (pre.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE))
            ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> conveyorUpgradeFactory.getTextures().forEach(pre::addSprite));
    }

    @OnlyIn(Dist.CLIENT)
    private void transporterBake(ModelBakeEvent event) {
        for (ResourceLocation resourceLocation : event.getModelRegistry().keySet()) {
            if (resourceLocation.getNamespace().equals(Reference.MOD_ID)) {
                if (resourceLocation.getPath().contains("transporter") && !resourceLocation.getPath().contains("transporters/") && !resourceLocation.getPath().contains("type"))
                    event.getModelRegistry().put(resourceLocation, new TransporterBlockModel(event.getModelRegistry().get(resourceLocation)));
            }
        }
        for (TransporterTypeFactory transporterTypeFactory : TransporterTypeFactory.FACTORIES) {
            String itemRL = Reference.MOD_ID + ":" + transporterTypeFactory.getRegistryName().getPath() + "_transporter_type#inventory";
            for (Direction upgradeFacing : transporterTypeFactory.getValidFacings()) {
                for (TransporterTypeFactory.TransporterAction actions : TransporterTypeFactory.TransporterAction.values()) {
                    try {
                        ResourceLocation resourceLocation = transporterTypeFactory.getModel(upgradeFacing, actions);
                        IUnbakedModel unbakedModel = event.getModelLoader().getUnbakedModel(resourceLocation);
                        TRANSPORTER_CACHE.put(resourceLocation, unbakedModel.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new SimpleModelTransform(ImmutableMap.of()), resourceLocation));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            for (ResourceLocation resourceLocation : event.getModelRegistry().keySet()) {
                if (resourceLocation.getNamespace().equals(Reference.MOD_ID)) {
                    if (resourceLocation.toString().equals(itemRL))
                        event.getModelRegistry().put(resourceLocation, TRANSPORTER_CACHE.get(transporterTypeFactory.getItemModel()));
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void transporterTextureStitch(TextureStitchEvent.Pre pre) {
        if (pre.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE))
            TransporterTypeFactory.FACTORIES.forEach(transporterTypeFactory -> transporterTypeFactory.getTextures().forEach(pre::addSprite));
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetupConveyor(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ContainerConveyor.TYPE, GuiConveyor::new);
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetupTransporter(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ContainerTransporter.TYPE, GuiTransporter::new);
    }
}
