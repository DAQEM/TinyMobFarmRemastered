package com.daqem.tinymobfarm.neoforge;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.nio.file.Path;

public class TintMobFarmExpectPlatformImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static ServerPlayer getFakePlayer(ServerLevel serverLevel, GameProfile gameProfile) {
        return new FakePlayer(serverLevel, gameProfile);
    }
}
