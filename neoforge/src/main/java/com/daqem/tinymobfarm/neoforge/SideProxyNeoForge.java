package com.daqem.tinymobfarm.neoforge;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.client.gui.MobFarmScreen;
import com.daqem.tinymobfarm.client.render.MobFarmRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class SideProxyNeoForge {

    public SideProxyNeoForge() {
        TinyMobFarm.init();
    }

    public static class Client extends SideProxyNeoForge {

        public Client(IEventBus modEventBus) {
            modEventBus.addListener(this::registerScreens);
            modEventBus.addListener(this::registerRenderers);
        }

        @SubscribeEvent
        private void registerScreens(RegisterMenuScreensEvent event) {
            event.register(TinyMobFarm.MOB_FARM_CONTAINER.get(), MobFarmScreen::new);
        }

        @SubscribeEvent
        public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(TinyMobFarm.MOB_FARM_TILE_ENTITY.get(), MobFarmRenderer::new);
        }
    }

    public static class Server extends SideProxyNeoForge {

        public Server(IEventBus modEventBus) {
            //Run server code
        }
    }
}
