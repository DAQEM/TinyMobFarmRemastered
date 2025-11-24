package com.daqem.tinymobfarm.item;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.block.MobFarmBlock;
import com.mojang.blaze3d.platform.InputConstants;
import com.sun.jna.platform.unix.solaris.LibKstat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class MobFarmBlockItem extends BlockItem {

    private final MobFarmBlock block;

    public MobFarmBlockItem(MobFarmBlock block, Properties builder) {
        super(block, buildProperties(block, builder));
        this.block = block;
    }

    private static Properties buildProperties(MobFarmBlock block, Properties builder) {
        if (block.getMobFarmType().isEnabled()) {
            builder = builder.arch$tab(TinyMobFarm.TINY_MOB_FARM_TAB);
        }
        return builder.stacksTo(64);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 340)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 344))
            this.block.getTooltipBuilder(consumer);
        else consumer.accept(TinyMobFarm.translatable("tooltip.hold_shift", ChatFormatting.GRAY));
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}
