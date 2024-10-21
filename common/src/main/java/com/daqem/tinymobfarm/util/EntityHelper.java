package com.daqem.tinymobfarm.util;

import com.daqem.tinymobfarm.ConfigTinyMobFarm;
import com.daqem.tinymobfarm.TinyMobFarm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;

public class EntityHelper {
	
	public static String getLootTableLocation(LivingEntity livingEntity) {
		return livingEntity.getLootTable().toString();
	}
	
	public static List<ItemStack> generateLoot(ResourceLocation lootTableLocation, ServerLevel level, ItemStack stack) {
		Entity entity = getEntityFromLasso(stack, BlockPos.ZERO, level);
		if (entity == null) return new ArrayList<>();

		LootDataManager lootTableManager = level.getServer().getLootData();
		LootTable lootTable = lootTableManager.getLootTable(lootTableLocation);
		ServerPlayer daniel = FakePlayerHelper.getPlayer(level);

		int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, stack);
		ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
		if (ConfigTinyMobFarm.allowLassoLooting.get()) {
			if (lootingLevel > 0) {
				sword.enchant(Enchantments.MOB_LOOTING, lootingLevel);
			}
		}
		daniel.addItem(sword);

		Holder<DamageType> damageTypeHolder = level.registryAccess().lookup(Registries.DAMAGE_TYPE)
				.flatMap(lookup -> lookup.get(DamageTypes.PLAYER_ATTACK))
				.orElseThrow(() -> new IllegalStateException("Damage type not found"));
		DamageSource damageSource = new DamageSource(damageTypeHolder, daniel);

		LootParams lootParams = new LootParams.Builder(level)
				.withParameter(LootContextParams.KILLER_ENTITY, daniel)
				.withParameter(LootContextParams.KILLER_ENTITY, daniel)
				.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, daniel)
				.withParameter(LootContextParams.THIS_ENTITY, entity)
				.withParameter(LootContextParams.ORIGIN, entity.position())
				.withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
				.create(LootContextParamSets.ENTITY);

		return lootTable.getRandomItems(lootParams);
	}

	public static Entity getEntityFromLasso(ItemStack lasso, BlockPos pos, Level level) {
		CompoundTag nbt = NBTHelper.getBaseTag(lasso);
		CompoundTag mobData = nbt.getCompound(NBTHelper.MOB_DATA);
		String id = nbt.getString(NBTHelper.MOB_ID);

		DoubleTag x = DoubleTag.valueOf(pos.getX() + 0.5);
		DoubleTag y = DoubleTag.valueOf(pos.getY());
		DoubleTag z = DoubleTag.valueOf(pos.getZ() + 0.5);
		ListTag mobPos = NBTHelper.createNBTList(x, y, z);
		mobData.put("Pos", mobPos);
		mobData.putString("id", id);

		return EntityType.loadEntityRecursive(mobData, level, entity -> entity);
	}
}
