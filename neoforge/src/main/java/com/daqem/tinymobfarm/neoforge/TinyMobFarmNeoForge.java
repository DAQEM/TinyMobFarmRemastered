package com.daqem.tinymobfarm.neoforge;

import com.daqem.tinymobfarm.TinyMobFarm;
import dev.architectury.utils.EnvExecutor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayer;

@Mod(TinyMobFarm.MOD_ID)
public class TinyMobFarmNeoForge {

    public TinyMobFarmNeoForge(IEventBus modEventBus) {
        EnvExecutor.getEnvSpecific(
                () -> () -> new SideProxyNeoForge.Client(modEventBus),
                () -> () -> new SideProxyNeoForge.Server(modEventBus)
        );
    }
}
