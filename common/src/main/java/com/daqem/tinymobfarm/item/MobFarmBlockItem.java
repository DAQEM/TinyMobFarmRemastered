package com.daqem.tinymobfarm.item;

import com.daqem.knot.registry.creativetab.ItemPropertiesExtension;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.block.MobFarmBlock;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class MobFarmBlockItem extends BlockItem {

    private final MobFarmBlock block;

    public MobFarmBlockItem(MobFarmBlock block, Properties builder) {
        super(block, buildProperties(block, builder));
        this.block = block;
    }

    private static Properties buildProperties(MobFarmBlock block, Properties builder) {
        if (builder instanceof ItemPropertiesExtension extension && block.getMobFarmType().isEnabled()) {
            builder = extension.knot$tab(TinyMobFarm.TINY_MOB_FARM_TAB.getKey());
        }
        return builder.stacksTo(64);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext tooltipContext, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT))
            this.block.getTooltipBuilder(consumer);
        else consumer.accept(TinyMobFarm.translatable("tooltip.hold_shift", ChatFormatting.GRAY));
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}
