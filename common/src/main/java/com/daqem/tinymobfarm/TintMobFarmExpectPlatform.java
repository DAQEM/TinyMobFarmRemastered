package com.daqem.tinymobfarm;

import com.mojang.authlib.GameProfile;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

public class TintMobFarmExpectPlatform {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ServerPlayer getFakePlayer(ServerLevel serverLevel, GameProfile gameProfile) {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
}
