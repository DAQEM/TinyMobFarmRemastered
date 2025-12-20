package com.daqem.tinymobfarm.fabric;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TinyMobFarmExpectPlatformImpl {

    public static ServerPlayer getFakePlayer(ServerLevel serverLevel, GameProfile gameProfile) {
        return FakePlayer.get(serverLevel, gameProfile);
    }

    public static ItemStack insertItem(Level level, BlockPos pos, Direction direction, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        // Find item storage at the target position
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, direction);

        if (storage == null) {
            // No inventory found, return the original stack (it will be dropped in world)
            return stack;
        }

        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
            transaction.commit();

            if (inserted == stack.getCount()) {
                return ItemStack.EMPTY;
            } else {
                ItemStack remainder = stack.copy();
                remainder.shrink((int) inserted);
                return remainder;
            }
        }
    }
}
