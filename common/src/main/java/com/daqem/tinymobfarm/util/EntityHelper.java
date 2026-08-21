package com.daqem.tinymobfarm.util;

import com.daqem.knot.api.util.EntityHooks;
import com.daqem.tinymobfarm.config.TinyMobFarmConfig;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.item.component.LassoData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class EntityHelper {

    public record FarmResult(List<ItemStack> loot, int xp) {
    }

    public static List<ItemStack> generateLoot(ServerLevel level, ItemStack stack) {
        return generateLootAndXp(level, stack).loot();
    }

    /**
     * Generates loot and the experience reward as if the captured mob were killed by
     * the fake player. Looting from the lasso applies to both items and XP.
     */
    public static FarmResult generateLootAndXp(ServerLevel level, ItemStack stack) {
        Entity entity = getEntityFromLasso(stack, BlockPos.ZERO, level);
        if (entity == null) return new FarmResult(new ArrayList<>(), 0);

        ServerPlayer daniel = FakePlayerHelper.getPlayer(level);
        if (daniel == null) {
            throw new IllegalStateException("Failed to retrieve fake player");
        }

        // If lasso looting is allowed, pass the lasso as the weapon to apply its looting level.
        // Otherwise, pass an empty stack so no looting is applied.
        ItemStack weapon = TinyMobFarmConfig.allowLassoLooting.get() ? stack : ItemStack.EMPTY;

        List<ItemStack> loot = EntityHooks.generateLoot(level, entity, daniel, weapon);

        int xp = 0;
        if (entity instanceof LivingEntity living) {
            ItemStack previous = daniel.getMainHandItem();
            daniel.setItemInHand(InteractionHand.MAIN_HAND, weapon);
            try {
                xp = living.getExperienceReward(level, daniel);
            } finally {
                daniel.setItemInHand(InteractionHand.MAIN_HAND, previous);
            }
        }
        return new FarmResult(loot, xp);
    }

    public static Entity getEntityFromLasso(ItemStack lasso, BlockPos pos, Level level) {
        if (!lasso.has(TinyMobFarm.LASSO_DATA.get())) return null;
        LassoData data = lasso.get(TinyMobFarm.LASSO_DATA.get());
        if (data == null) return null;
        CompoundTag mobData = data.mobData().copy();
        Identifier id = data.mobId();

        DoubleTag x = DoubleTag.valueOf(pos.getX() + 0.5);
        DoubleTag y = DoubleTag.valueOf(pos.getY());
        DoubleTag z = DoubleTag.valueOf(pos.getZ() + 0.5);
        ListTag mobPos = new ListTag();
        mobPos.add(x);
        mobPos.add(y);
        mobPos.add(z);
        mobData.put("Pos", mobPos);
        mobData.putString("id", id.toString());

        Entity entity = EntityType.loadEntityRecursive(mobData, level, new EntitySpawnRequest(EntitySpawnReason.MOB_SUMMONED, true), e -> e);
        if (entity != null) {
            entity.xRotO = entity.getXRot();
            entity.yRotO = entity.getYRot();
            if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.yBodyRotO = living.yBodyRot;
                living.yHeadRotO = living.yHeadRot;
            }
        }
        return entity;
    }
}