package com.daqem.tinymobfarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daqem.tinymobfarm.item.component.LassoData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;;

public enum MobFarmType {

	WOOD("wood_farm", Blocks.OAK_WOOD, false, new int[] {2, 3, 3}, ConfigTinyMobFarm.woodFarmSpeed.get()),
	STONE("stone_farm", Blocks.STONE, false, new int[] {1, 2, 3}, ConfigTinyMobFarm.stoneFarmSpeed.get()),
	IRON("iron_farm", Blocks.IRON_BLOCK, true, new int[] {1, 2}, ConfigTinyMobFarm.ironFarmSpeed.get()),
	GOLD("gold_farm", Blocks.GOLD_BLOCK, true, new int[] {1, 1, 2}, ConfigTinyMobFarm.goldFarmSpeed.get()),
	DIAMOND("diamond_farm", Blocks.DIAMOND_BLOCK, true, new int[] {1}, ConfigTinyMobFarm.diamondFarmSpeed.get()),
	EMERALD("emerald_farm", Blocks.EMERALD_BLOCK, true, new int[] {0, 1, 1}, ConfigTinyMobFarm.emeraldFarmSpeed.get()),
	INFERNAL("inferno_farm", Blocks.OBSIDIAN, true, new int[] {0, 0, 1}, ConfigTinyMobFarm.infernoFarmSpeed.get()),
	ULTIMATE("ultimate_farm", Blocks.OBSIDIAN, true, new int[] {0}, ConfigTinyMobFarm.ultimateFarmSpeed.get());
	
	private final String registryName;
	private final Block baseBlock;
	private final boolean canFarmHostile;
	private final int[] damageChance;
	private final double farmSpeed;
	private final Map<Integer, Integer> normalizedChance;
	
	MobFarmType(String registryName, Block baseBlock, boolean canFarmHostile, int[] damageChance, double farmSpeed) {
		this.registryName = registryName;
		this.baseBlock = baseBlock;
		this.canFarmHostile = canFarmHostile;
		this.damageChance = damageChance;
		this.farmSpeed = farmSpeed;
		
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
		return lasso.has(TinyMobFarm.LASSO_DATA.get()) && (this.canFarmHostile || !lasso.get(TinyMobFarm.LASSO_DATA.get()).mobHostile());
	}
	
	public int getMaxProgress() {
		return (int) (this.farmSpeed * 20);
	}
	
	public int getRandomDamage(RandomSource rand) {
		return this.damageChance[rand.nextInt(this.damageChance.length)];
	}
	
	public void addTooltip(List<Component> tooltip) {
		if (!this.canFarmHostile) {
			tooltip.add(TinyMobFarm.translatable("tooltip.no_hostile", ChatFormatting.RED));
		}
		tooltip.add(TinyMobFarm.translatable("tooltip.farm_rate", ChatFormatting.GRAY, this.farmSpeed));
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
