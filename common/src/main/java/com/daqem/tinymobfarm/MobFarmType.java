package com.daqem.tinymobfarm;

import com.daqem.tinymobfarm.config.TMFConfig;
import com.daqem.tinymobfarm.item.component.LassoData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum MobFarmType {

    WOOD("wood_farm", Blocks.OAK_WOOD, TMFConfig.woodFarmAllowsHostile, new int[]{2, 3, 3}, TMFConfig.woodFarmSpeed, TMFConfig.woodFarmEnabled),
    STONE("stone_farm", Blocks.STONE, TMFConfig.stoneFarmAllowsHostile, new int[]{1, 2, 3}, TMFConfig.stoneFarmSpeed, TMFConfig.stoneFarmEnabled),
    IRON("iron_farm", Blocks.IRON_BLOCK, TMFConfig.ironFarmAllowsHostile, new int[]{1, 2}, TMFConfig.ironFarmSpeed, TMFConfig.ironFarmEnabled),
    GOLD("gold_farm", Blocks.GOLD_BLOCK, TMFConfig.goldFarmAllowsHostile, new int[]{1, 1, 2}, TMFConfig.goldFarmSpeed, TMFConfig.goldFarmEnabled),
    DIAMOND("diamond_farm", Blocks.DIAMOND_BLOCK, TMFConfig.diamondFarmAllowsHostile, new int[]{1}, TMFConfig.diamondFarmSpeed, TMFConfig.diamondFarmEnabled),
    EMERALD("emerald_farm", Blocks.EMERALD_BLOCK, TMFConfig.emeraldFarmAllowsHostile, new int[]{0, 1, 1}, TMFConfig.emeraldFarmSpeed, TMFConfig.emeraldFarmEnabled),
    INFERNAL("inferno_farm", Blocks.OBSIDIAN, TMFConfig.infernoFarmAllowsHostile, new int[]{0, 0, 1}, TMFConfig.infernoFarmSpeed, TMFConfig.infernoFarmEnabled),
    ULTIMATE("ultimate_farm", Blocks.OBSIDIAN, TMFConfig.ultimateFarmAllowsHostile, new int[]{0}, TMFConfig.ultimateFarmSpeed, TMFConfig.ultimateFarmEnabled);

    private final String registryName;
    private final Block baseBlock;
    private final Supplier<Boolean> canFarmHostile;
    private final int[] damageChance;
    private final Supplier<Double> farmSpeed;
    private final Supplier<Boolean> enabled;
    private final Map<Integer, Integer> normalizedChance;

    MobFarmType(String registryName, Block baseBlock, Supplier<Boolean> canFarmHostile, int[] damageChance, Supplier<Double> farmSpeed, Supplier<Boolean> enabled) {
        this.registryName = registryName;
        this.baseBlock = baseBlock;
        this.canFarmHostile = canFarmHostile;
        this.damageChance = damageChance;
        this.farmSpeed = farmSpeed;
        this.enabled = enabled;

        this.normalizedChance = new HashMap<>();
        for (int i : this.damageChance) {
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
        boolean hasData = lasso.has(TinyMobFarm.LASSO_DATA.get());
        if (hasData) {
            LassoData lassoData = lasso.get(TinyMobFarm.LASSO_DATA.get());
            return this.canFarmHostile.get() || (lassoData != null && !lassoData.mobHostile());
        }
        return false;

    }

    public int getMaxProgress() {
        return (int) (this.farmSpeed.get() * 20);
    }

    public int getRandomDamage(RandomSource rand) {
        return this.damageChance[rand.nextInt(this.damageChance.length)];
    }

    public void addTooltip(Consumer<Component> consumer) {
        if (!this.canFarmHostile.get()) {
            consumer.accept(TinyMobFarm.translatable("tooltip.no_hostile", ChatFormatting.RED));
        }
        consumer.accept(TinyMobFarm.translatable("tooltip.farm_rate", ChatFormatting.GRAY, this.farmSpeed.get()));
        consumer.accept(TinyMobFarm.translatable("tooltip.durability_info", ChatFormatting.GRAY));
        for (int i : this.normalizedChance.keySet()) {
            if (i == 0) {
                consumer.accept(TinyMobFarm.translatable("tooltip.no_durability", ChatFormatting.GRAY, this.normalizedChance.get(i)));
            } else {
                consumer.accept(TinyMobFarm.translatable("tooltip.default_durability", ChatFormatting.GRAY, this.normalizedChance.get(i), i));
            }
        }
    }

    public boolean isEnabled() {
        return enabled.get();
    }
}
