package com.daqem.tinymobfarm.neoforge;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

public class TinyMobFarmExpectPlatformImpl {

    public static ServerPlayer getFakePlayer(ServerLevel serverLevel, GameProfile gameProfile) {
        return new FakePlayer(serverLevel, gameProfile);
    }

    public static ItemStack insertItem(Level level, BlockPos pos, Direction direction, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        // 1. Get the new ResourceHandler capability
        ResourceHandler<@NotNull ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, direction);

        if (handler == null) {
            return stack; // No inventory found
        }

        // 2. Create the immutable resource
        ItemResource resource = ItemResource.of(stack);

        // 3. Execute within a Transaction
        try (Transaction tx = Transaction.open(null)) {
            long inserted = handler.insert(resource, stack.getCount(), tx);

            // 4. Commit if items were moved
            if (inserted > 0) {
                tx.commit();
            }

            // 5. Calculate remainder
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
