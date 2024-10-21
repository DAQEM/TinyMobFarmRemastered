package com.daqem.tinymobfarm.fabric;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

public class TintMobFarmExpectPlatformImpl {

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static ServerPlayer getFakePlayer(ServerLevel serverLevel, GameProfile gameProfile) {
        return FakePlayer.get(serverLevel, gameProfile);
    }
}
