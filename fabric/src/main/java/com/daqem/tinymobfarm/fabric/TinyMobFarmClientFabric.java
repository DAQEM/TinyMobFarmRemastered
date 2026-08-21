package com.daqem.tinymobfarm.fabric;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.client.gui.MobFarmScreen;
import com.daqem.tinymobfarm.client.gui.XpTankScreen;
import com.daqem.tinymobfarm.client.render.MobFarmRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class TinyMobFarmClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(TinyMobFarm.MOB_FARM_CONTAINER.get(), MobFarmScreen::new);
        MenuScreens.register(TinyMobFarm.XP_TANK_MENU.get(), XpTankScreen::new);
        BlockEntityRenderers.register(TinyMobFarm.MOB_FARM_TILE_ENTITY.get(), MobFarmRenderer::new);
    }
}