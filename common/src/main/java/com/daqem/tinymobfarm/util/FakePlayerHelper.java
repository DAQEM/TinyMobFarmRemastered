package com.daqem.tinymobfarm.util;

import java.util.UUID;

import com.daqem.tinymobfarm.TintMobFarmExpectPlatform;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FakePlayerHelper {
	private static ServerPlayer fakePlayer;

	public static ServerPlayer getPlayer(ServerLevel serverLevel) {
		if (fakePlayer == null) fakePlayer = TintMobFarmExpectPlatform.getFakePlayer(serverLevel, new GameProfile(UUID.randomUUID(), "[TinyMobFarm_DanielTheEgg]"));
		return fakePlayer;
	}
}
