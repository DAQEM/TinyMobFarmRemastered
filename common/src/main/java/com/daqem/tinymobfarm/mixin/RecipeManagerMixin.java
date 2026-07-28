package com.daqem.tinymobfarm.mixin;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.config.TinyMobFarmConfig;
import net.minecraft.resources.Identifier;
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
    protected void tinymobfarm$filterRecipes(ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfoReturnable<RecipeMap> cir, SortedMap<Identifier, Recipe<?>> sortedMap) {
        // Remove recipes if their config is set to false
        if (!TinyMobFarmConfig.woodFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("wood_farm"));
        if (!TinyMobFarmConfig.stoneFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("stone_farm"));
        if (!TinyMobFarmConfig.ironFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("iron_farm"));
        if (!TinyMobFarmConfig.goldFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("gold_farm"));
        if (!TinyMobFarmConfig.diamondFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("diamond_farm"));
        if (!TinyMobFarmConfig.emeraldFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("emerald_farm"));
        if (!TinyMobFarmConfig.infernoFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("inferno_farm"));
        if (!TinyMobFarmConfig.ultimateFarmEnabled.get()) sortedMap.remove(TinyMobFarm.getId("ultimate_farm"));
    }
}
