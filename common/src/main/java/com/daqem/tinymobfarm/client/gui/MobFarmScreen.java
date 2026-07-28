package com.daqem.tinymobfarm.client.gui;

import com.daqem.tinymobfarm.client.gui.components.MobFarmComponent;
import com.daqem.uilib.gui.AbstractContainerScreen;
import com.daqem.uilib.gui.background.BlurredBackground;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class MobFarmScreen extends AbstractContainerScreen<MobFarmMenu> {

    public MobFarmScreen(MobFarmMenu container, Inventory inv, Component text) {
		super(container, inv, text);

		this.setBackground(new BlurredBackground());
	}

	@Override
	public void init() {
		MobFarmComponent mobFarmComponent = new MobFarmComponent(this);
		mobFarmComponent.center();
		this.addComponent(mobFarmComponent);

		super.init();
	}

	@Override
	protected void extractLabels(@NonNull GuiGraphicsExtractor guiGraphics, int i, int j) {
	}

	@Override
	public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
		this.extractTooltip(guiGraphics, mouseX, mouseY);
	}

	public ItemStack getLasso() {
		return this.menu.slots.getFirst().getItem();
	}
}
