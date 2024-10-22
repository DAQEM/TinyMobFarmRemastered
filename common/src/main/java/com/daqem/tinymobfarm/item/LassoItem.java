package com.daqem.tinymobfarm.item;

import com.daqem.tinymobfarm.ConfigTinyMobFarm;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.util.EntityHelper;
import com.daqem.tinymobfarm.util.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LassoItem extends Item {

    public LassoItem(Properties properties) {
        //noinspection UnstableApiUsage
        super(properties.arch$tab(TinyMobFarm.JOBSPLUS_TOOLS_TAB).defaultDurability(ConfigTinyMobFarm.lassoDurability.get()));
    }

    public @NotNull InteractionResult interactMob(ItemStack stack, Player player, LivingEntity target, InteractionHand interactionHand) {
        if (NBTHelper.hasMob(stack)
                || !target.isAlive()
                || !(target instanceof Mob)) {
            return InteractionResult.FAIL;
        }

        if (player.level() instanceof ServerLevel) {
            if (!target.canChangeDimensions()) {
                player.sendSystemMessage(TinyMobFarm.translatable("error.cannot_capture_boss"));
                return InteractionResult.SUCCESS;
            }
            CompoundTag nbt = NBTHelper.getBaseTag(stack);
            CompoundTag mobData = target.saveWithoutId(new CompoundTag());
            mobData.put("Rotation", NBTHelper.createNBTList(
                    DoubleTag.valueOf(0), DoubleTag.valueOf(0)));
            mobData.remove("Fire");
            mobData.remove("HurtTime");

            nbt.put(NBTHelper.MOB_DATA, mobData);

            nbt.putString(NBTHelper.MOB_NAME, target.getName().getString());
            nbt.putString(NBTHelper.MOB_ID, String.valueOf(target.getType().arch$registryName()));
            nbt.putString(NBTHelper.MOB_LOOTTABLE_LOCATION, EntityHelper.getLootTableLocation(target));
            nbt.putDouble(NBTHelper.MOB_HEALTH, Math.round(target.getHealth() * 10) / 10.0);
            nbt.putDouble(NBTHelper.MOB_MAX_HEALTH, target.getMaxHealth());
            nbt.putBoolean(NBTHelper.MOB_HOSTILE, target instanceof Monster);

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
        if (!NBTHelper.hasMob(stack)) return InteractionResult.FAIL;

        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos().offset(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        Level level = context.getLevel();

        if (!player.mayUseItemAt(pos, facing, stack)) return InteractionResult.FAIL;

        if (!level.isClientSide()) {
            Entity mob = EntityHelper.getEntityFromLasso(stack, pos, level);
            if (mob != null) level.addFreshEntity(mob);

            stack.removeTagKey(NBTHelper.MOB);
            stack.hurtAndBreak(1, player, player1 -> {
            });
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        if (NBTHelper.hasMob(stack)) {
            CompoundTag nbt = NBTHelper.getBaseTag(stack);
            String id = nbt.getString(NBTHelper.MOB_ID);
            double health = nbt.getDouble(NBTHelper.MOB_HEALTH);
            double maxHealth = nbt.getDouble(NBTHelper.MOB_MAX_HEALTH);

            tooltip.add(TinyMobFarm.translatable("tooltip.release_mob.key", ChatFormatting.GRAY));
            tooltip.add(TinyMobFarm.translatable("tooltip.mob_name.key", ChatFormatting.GRAY, getMobName(stack)));
            tooltip.add(TinyMobFarm.translatable("tooltip.mob_id.key", ChatFormatting.GRAY, id));
            tooltip.add(TinyMobFarm.translatable("tooltip.health.key", ChatFormatting.GRAY, health, maxHealth));
            if (nbt.getBoolean(NBTHelper.MOB_HOSTILE)) {
                tooltip.add(TinyMobFarm.translatable("tooltip.hostile.key", ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(TinyMobFarm.translatable("tooltip.capture.key", ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return NBTHelper.hasMob(stack);
    }

    public Component getMobName(ItemStack itemStack) {
        CompoundTag nbt = NBTHelper.getBaseTag(itemStack);
        return TinyMobFarm.literal(nbt.getString(NBTHelper.MOB_NAME));
    }
}
