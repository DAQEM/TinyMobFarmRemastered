package com.daqem.tinymobfarm.mixin;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.config.TMFConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.SortedMap;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(
            method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/SortedMap;forEach(Ljava/util/function/BiConsumer;)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    protected void tinymobfarm$filterRecipes(ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfoReturnable<RecipeMap> cir, SortedMap<ResourceLocation, Recipe<?>> sortedMap) {
        // Remove recipes if their config is set to false
        if (!TMFConfig.woodFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("wood_farm"));
        if (!TMFConfig.stoneFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("stone_farm"));
        if (!TMFConfig.ironFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("iron_farm"));
        if (!TMFConfig.goldFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("gold_farm"));
        if (!TMFConfig.diamondFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("diamond_farm"));
        if (!TMFConfig.emeraldFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("emerald_farm"));
        if (!TMFConfig.infernoFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("inferno_farm"));
        if (!TMFConfig.ultimateFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("ultimate_farm"));
    }
}
