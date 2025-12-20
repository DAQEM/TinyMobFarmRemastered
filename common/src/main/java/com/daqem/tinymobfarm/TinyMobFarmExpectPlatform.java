package com.daqem.tinymobfarm;

import com.mojang.authlib.GameProfile;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TinyMobFarmExpectPlatform {

    @ExpectPlatform
    public static ServerPlayer getFakePlayer(ServerLevel serverLevel, GameProfile gameProfile) {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    /**
     * Inserts an ItemStack into a container at the specified position.
     *
     * @param level     The level.
     * @param pos       The position of the container.
     * @param direction The side of the container to insert into.
     * @param stack     The stack to insert.
     * @return The remainder of the stack that could not be inserted.
     */
    @ExpectPlatform
    public static ItemStack insertItem(Level level, BlockPos pos, Direction direction, ItemStack stack) {
        throw new AssertionError();
    }
}
