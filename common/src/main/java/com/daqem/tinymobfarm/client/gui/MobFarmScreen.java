package com.daqem.tinymobfarm.client.gui;

import com.daqem.tinymobfarm.MobFarmType;
import com.daqem.tinymobfarm.client.gui.components.MobFarmComponent;
import com.daqem.tinymobfarm.util.EntityHelper;
import com.daqem.uilib.client.gui.AbstractContainerScreen;
import com.daqem.uilib.client.gui.background.Backgrounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MobFarmScreen extends AbstractContainerScreen<MobFarmMenu> {

	private final @Nullable MobFarmType type;

    public MobFarmScreen(MobFarmMenu container, Inventory inv, Component text) {
		super(container, inv, text);

		String id = ((TranslatableContents) this.title.getContents()).getKey().split("\\.")[1];
		this.type = Arrays.stream(MobFarmType.values()).filter(farm -> farm.getRegistryName().equals(id)).findFirst().orElse(null);
	}

	@Override
	public void startScreen() {
		setBackground(null);

		MobFarmComponent mobFarmComponent = new MobFarmComponent(this, this.font);
		mobFarmComponent.center();
		addComponents(mobFarmComponent);
	}

	@Override
	public void onTickScreen(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
	}

	//Makes sure the default titles are not rendered
	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
	}

	public ItemStack getLasso() {
		return this.menu.slots.get(0).getItem();
	}

	public @Nullable MobFarmType getType() {
		return this.type;
	}
}
