package com.daqem.tinymobfarm.mixin;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.item.TinyMobFarmItems;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @ModifyReturnValue(method = "canEnchant", at = @At("RETURN"))
    private boolean canEnchant(boolean original, ItemStack itemStack) {
        if (itemStack.is(TinyMobFarmItems.LASSO.get())) {
            Enchantment enchantment = (Enchantment)(Object)this;
            if (enchantment.description().getContents() instanceof TranslatableContents translatableContents) {
                if (translatableContents.getKey().equals("enchantment.minecraft.looting")) {
                    return true;
                }
                if (translatableContents.getKey().equals("enchantment.minecraft.unbreaking")) {
                    return true;
                }
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "isSupportedItem", at = @At("RETURN"))
    private boolean isSupportedItem(boolean original, ItemStack itemStack) {
        if (itemStack.is(TinyMobFarmItems.LASSO.get())) {
            Enchantment enchantment = (Enchantment)(Object)this;
            if (enchantment.description().getContents() instanceof TranslatableContents translatableContents) {
                if (translatableContents.getKey().equals("enchantment.minecraft.looting")) {
                    return true;
                }
                if (translatableContents.getKey().equals("enchantment.minecraft.unbreaking")) {
                    return true;
                }
            }
        }
        return original;
    }
}
