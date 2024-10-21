package com.daqem.tinymobfarm.item;

import com.daqem.tinymobfarm.ConfigTinyMobFarm;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.item.component.LassoData;
import com.daqem.tinymobfarm.util.EntityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LassoItem extends Item {

    public LassoItem(Properties properties) {
        //noinspection UnstableApiUsage
        super(properties
                .arch$tab(TinyMobFarm.JOBSPLUS_TOOLS_TAB)
                .durability(ConfigTinyMobFarm.lassoDurability.get()));
    }

    public @NotNull InteractionResult interactMob(ItemStack stack, Player player, LivingEntity target, InteractionHand interactionHand) {
        if (stack.has(TinyMobFarm.LASSO_DATA.get())
                || !target.isAlive()
                || !(target instanceof Mob)) {
            return InteractionResult.FAIL;
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            if (!target.canUsePortal(false)) {
                player.sendSystemMessage(TinyMobFarm.translatable("error.cannot_capture_boss"));
                return InteractionResult.SUCCESS;
            }
            CompoundTag mobData = target.saveWithoutId(new CompoundTag());
            ListTag pos = new ListTag();
            pos.add(DoubleTag.valueOf(target.getX()));
            pos.add(DoubleTag.valueOf(target.getY()));
            mobData.put("Rotation", pos);
            mobData.remove("Fire");
            mobData.remove("HurtTime");

            LassoData lassoData = new LassoData(
                    target.getName().getString(),
                    target.getType().arch$registryName(),
                    mobData,
                    target.getHealth(),
                    target.getMaxHealth(),
                    target instanceof Monster,
                    target.getLootTable().location()
            );

            stack.set(TinyMobFarm.LASSO_DATA.get(), lassoData);
            target.discard();
            player.getInventory().setChanged();
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
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.has(TinyMobFarm.LASSO_DATA.get())) {
            LassoData data = itemStack.get(TinyMobFarm.LASSO_DATA.get());
            list.add(TinyMobFarm.translatable("tooltip.release_mob.key", ChatFormatting.GRAY));
            list.add(TinyMobFarm.translatable("tooltip.mob_name.key", ChatFormatting.GRAY, getMobName(itemStack)));
            list.add(TinyMobFarm.translatable("tooltip.mob_id.key", ChatFormatting.GRAY, data.mobId().toString()));
            list.add(TinyMobFarm.translatable("tooltip.health.key", ChatFormatting.GRAY, data.mobHealth(), data.mobMaxHealth()));
            if (data.mobHostile()) {
                list.add(TinyMobFarm.translatable("tooltip.hostile.key", ChatFormatting.GRAY));
            }
        } else {
            list.add(TinyMobFarm.translatable("tooltip.capture.key", ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(TinyMobFarm.LASSO_DATA.get());
    }

    public Component getMobName(ItemStack itemStack) {
        if (!itemStack.has(TinyMobFarm.LASSO_DATA.get())) return TinyMobFarm.translatable("tooltip.unknown.key");
        return TinyMobFarm.literal(itemStack.get(TinyMobFarm.LASSO_DATA.get()).mobName());
    }
}
