package com.daqem.tinymobfarm.item;

import com.daqem.tinymobfarm.config.TinyMobFarmConfig;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.item.component.LassoData;
import com.daqem.tinymobfarm.util.EntityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class LassoItem extends Item {

    public LassoItem(Properties properties) {
        //noinspection UnstableApiUsage
        super(properties
                .enchantable(1)
                .durability(TinyMobFarmConfig.lassoDurability.get()));
    }

    public @NotNull InteractionResult interactMob(ItemStack stack, Player player, LivingEntity target) {
        if (stack.has(TinyMobFarm.LASSO_DATA.get())
                || !target.isAlive()
                || !(target instanceof Mob)) {
            return InteractionResult.FAIL;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            Identifier targetLocation = EntityType.getKey(target.getType());
            if (TinyMobFarmConfig.blacklistedMobs.get().contains(targetLocation.toString())) {
                serverPlayer.sendSystemMessage(TinyMobFarm.translatable("error.blacklist_mob").withStyle(ChatFormatting.RED));
                return InteractionResult.SUCCESS;
            }
            try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(target.problemPath(), TinyMobFarm.API.LOGGER)) {
                TagValueOutput mobData = TagValueOutput.createWithContext(scopedCollector, target.registryAccess());
                target.saveWithoutId(mobData);

                mobData.store("Rotation", Vec2.CODEC, new Vec2(target.getYRot(), target.getXRot()));
                mobData.discard("Fire");
                mobData.discard("HurtTime");

                LassoData lassoData = new LassoData(
                        target.getName().getString(),
                        targetLocation,
                        mobData.buildResult(),
                        target.getHealth(),
                        target.getMaxHealth(),
                        target instanceof Monster,
                        target.getLootTable().get().identifier()
                );

                stack.set(TinyMobFarm.LASSO_DATA.get(), lassoData);
                target.discard();
                player.getInventory().setChanged();
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.FAIL;

        ItemStack stack = context.getItemInHand();
        if (!stack.has(TinyMobFarm.LASSO_DATA.get())) return InteractionResult.FAIL;

        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos().offset(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        Level level = context.getLevel();

        if (!player.mayUseItemAt(pos, facing, stack)) return InteractionResult.FAIL;

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Entity mob = EntityHelper.getEntityFromLasso(stack, pos, level);
            if (mob != null) level.addFreshEntity(mob);

            stack.remove(TinyMobFarm.LASSO_DATA.get());
            stack.hurtAndBreak(1, serverLevel, serverPlayer, player1 -> {
            });
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @NonNull TooltipContext tooltipContext, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        if (itemStack.has(TinyMobFarm.LASSO_DATA.get())) {
            LassoData data = itemStack.get(TinyMobFarm.LASSO_DATA.get());
            consumer.accept(TinyMobFarm.translatable("tooltip.release_mob.key", ChatFormatting.GRAY));
            consumer.accept(TinyMobFarm.translatable("tooltip.mob_name.key", ChatFormatting.GRAY, getMobName(itemStack)));
            consumer.accept(TinyMobFarm.translatable("tooltip.mob_id.key", ChatFormatting.GRAY, data.mobId().toString()));
            consumer.accept(TinyMobFarm.translatable("tooltip.health.key", ChatFormatting.GRAY, data.mobHealth(), data.mobMaxHealth()));
            if (data.mobHostile()) {
                consumer.accept(TinyMobFarm.translatable("tooltip.hostile.key", ChatFormatting.GRAY));
            }
        } else {
            consumer.accept(TinyMobFarm.translatable("tooltip.capture.key", ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(TinyMobFarm.LASSO_DATA.get()) || super.isFoil(stack);
    }

    public Component getMobName(ItemStack itemStack) {
        if (!itemStack.has(TinyMobFarm.LASSO_DATA.get())) return TinyMobFarm.translatable("tooltip.unknown.key");
        return TinyMobFarm.literal(itemStack.get(TinyMobFarm.LASSO_DATA.get()).mobName());
    }

}
