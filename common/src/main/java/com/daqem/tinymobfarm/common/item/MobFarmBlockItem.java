package com.daqem.tinymobfarm.common.item;

import java.util.List;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.common.block.MobFarmBlock;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MobFarmBlockItem extends BlockItem {

	private Consumer<List<Component>> tooltipBuilder;
	
	public MobFarmBlockItem(MobFarmBlock block, Properties builder) {
		//noinspection UnstableApiUsage
		super(block, builder.arch$tab(TinyMobFarm.JOBSPLUS_TOOLS_TAB.get()));
		this.tooltipBuilder = block.getTooltipBuilder();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
		if (Screen.hasShiftDown()) this.tooltipBuilder.accept(tooltip);
		else tooltip.add(TinyMobFarm.translatable("tooltip.hold_shift", ChatFormatting.GRAY));

		super.appendHoverText(stack, level, tooltip, tooltipFlag);
	}
}
