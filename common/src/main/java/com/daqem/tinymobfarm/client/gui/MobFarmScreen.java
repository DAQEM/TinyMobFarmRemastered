package com.daqem.tinymobfarm.client.gui;

import com.daqem.tinymobfarm.client.gui.components.MobFarmComponent;
import com.daqem.uilib.gui.AbstractContainerScreen;
import com.daqem.uilib.gui.background.BlurredBackground;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

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

	//Makes sure the default titles are not rendered
	@Override
	protected void renderLabels(GuiGraphicsExtractor guiGraphics, int i, int j) {
	}

	@Override
	public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	public ItemStack getLasso() {
		return this.menu.slots.getFirst().getItem();
	}
}
