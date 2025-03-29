package com.daqem.tinymobfarm.item;

import java.util.function.Consumer;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.block.MobFarmBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class MobFarmBlockItem extends BlockItem {

	private final MobFarmBlock block;
	
	public MobFarmBlockItem(MobFarmBlock block, Properties builder) {
		//noinspection UnstableApiUsage
		super(block, builder.arch$tab(TinyMobFarm.TINY_MOB_FARM_TAB).stacksTo(64));
		this.block = block;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
		if (Screen.hasShiftDown()) this.block.getTooltipBuilder(consumer);
		else consumer.accept(TinyMobFarm.translatable("tooltip.hold_shift", ChatFormatting.GRAY));
		super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
	}
}
