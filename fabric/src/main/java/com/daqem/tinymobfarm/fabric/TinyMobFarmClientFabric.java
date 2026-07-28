package com.daqem.tinymobfarm.fabric;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.client.gui.MobFarmScreen;
import com.daqem.tinymobfarm.client.render.MobFarmRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class TinyMobFarmClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(TinyMobFarm.MOB_FARM_CONTAINER.get(), MobFarmScreen::new);
        BlockRenderLayerMap.putBlock(TinyMobFarm.WOODEN_MOB_FARM_BLOCK.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(TinyMobFarm.STONE_MOB_FARM_BLOCK.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(TinyMobFarm.IRON_MOB_FARM_BLOCK.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(TinyMobFarm.GOLD_MOB_FARM_BLOCK.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(TinyMobFarm.DIAMOND_MOB_FARM_BLOCK.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(TinyMobFarm.EMERALD_MOB_FARM_BLOCK.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(TinyMobFarm.INFERNAL_MOB_FARM_BLOCK.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(TinyMobFarm.ULTIMATE_MOB_FARM_BLOCK.get(), ChunkSectionLayer.CUTOUT);
        BlockEntityRenderers.register(TinyMobFarm.MOB_FARM_TILE_ENTITY.get(), MobFarmRenderer::new);
    }
}
