package com.daqem.tinymobfarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daqem.tinymobfarm.ConfigTinyMobFarm;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.util.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;;

public enum MobFarmType {

	WOOD("wood_farm", Blocks.OAK_WOOD, false, new int[] {2, 3, 3}),
	STONE("stone_farm", Blocks.STONE, false, new int[] {1, 2, 3}),
	IRON("iron_farm", Blocks.IRON_BLOCK, true, new int[] {1, 2}),
	GOLD("gold_farm", Blocks.GOLD_BLOCK, true, new int[] {1, 1, 2}),
	DIAMOND("diamond_farm", Blocks.DIAMOND_BLOCK, true, new int[] {1}),
	EMERALD("emerald_farm", Blocks.EMERALD_BLOCK, true, new int[] {0, 1, 1}),
	INFERNAL("inferno_farm", Blocks.OBSIDIAN, true, new int[] {0, 0, 1}),
	ULTIMATE("ultimate_farm", Blocks.OBSIDIAN, true, new int[] {0});
	
	private static final double[] MOB_FARM_SPEED = {ConfigTinyMobFarm.woodFarmSpeed.get(),
			ConfigTinyMobFarm.stoneFarmSpeed.get(), ConfigTinyMobFarm.ironFarmSpeed.get(),
			ConfigTinyMobFarm.goldFarmSpeed.get(), ConfigTinyMobFarm.diamondFarmSpeed.get(),
			ConfigTinyMobFarm.emeraldFarmSpeed.get(), ConfigTinyMobFarm.infernoFarmSpeed.get(),
			ConfigTinyMobFarm.ultimateFarmSpeed.get()};
	
	private final String registryName;
	private final Block baseBlock;
	private final boolean canFarmHostile;
	private final int[] damageChance;
	private final Map<Integer, Integer> normalizedChance;
	
	MobFarmType(String registryName, Block baseBlock, boolean canFarmHostile, int[] damageChance) {
		this.registryName = registryName;
		this.baseBlock = baseBlock;
		this.canFarmHostile = canFarmHostile;
		this.damageChance = damageChance;
		
		this.normalizedChance = new HashMap<>();
		for (int i: this.damageChance) {
			if (!this.normalizedChance.containsKey(i)) this.normalizedChance.put(i, 0);
			this.normalizedChance.put(i, this.normalizedChance.get(i) + 1);
		}
		int denominator = this.damageChance.length;
        this.normalizedChance.replaceAll((i, v) -> (int) (v * 100.0 / denominator));
	}
	
	public String getRegistryName() {
		return this.registryName;
	}
	
	public String getUnlocalizedName() {
		return String.format("block.%s.%s", TinyMobFarm.MOD_ID, this.registryName);
	}
	
	public Block getBaseBlock() {
		return this.baseBlock;
	}
	
	public boolean isLassoValid(ItemStack lasso) {
		return NBTHelper.hasMob(lasso) && (this.canFarmHostile || !NBTHelper.hasHostileMob(lasso));
	}
	
	public int getMaxProgress() {
		return (int) (MOB_FARM_SPEED[this.ordinal()] * 20);
	}
	
	public int getRandomDamage(RandomSource rand) {
		return this.damageChance[rand.nextInt(this.damageChance.length)];
	}
	
	public void addTooltip(List<Component> tooltip) {
		if (!this.canFarmHostile) {
			tooltip.add(TinyMobFarm.translatable("tooltip.no_hostile", ChatFormatting.RED));
		}
		tooltip.add(TinyMobFarm.translatable("tooltip.farm_rate", ChatFormatting.GRAY, MOB_FARM_SPEED[this.ordinal()]));
		tooltip.add(TinyMobFarm.translatable("tooltip.durability_info", ChatFormatting.GRAY));
		for (int i: this.normalizedChance.keySet()) {
			if (i == 0) {
				tooltip.add(TinyMobFarm.translatable("tooltip.no_durability", ChatFormatting.GRAY, this.normalizedChance.get(i)));
			}
			else {
				tooltip.add(TinyMobFarm.translatable("tooltip.default_durability", ChatFormatting.GRAY, this.normalizedChance.get(i), i));
			}
		}
	}
}
