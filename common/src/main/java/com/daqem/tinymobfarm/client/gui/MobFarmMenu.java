package com.daqem.tinymobfarm.client.gui;

import com.daqem.tinymobfarm.MobFarmType;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.blockentity.MobFarmBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class MobFarmMenu extends AbstractContainerMenu {

	public static final int LASSO_SLOT = 0;
	public static final int LASSO_SLOT_X = 71;
	public static final int LASSO_SLOT_Y = 33;

	private final ContainerData containerData;

	public MobFarmMenu(int windowId, Inventory inv, Container container, ContainerData containerData) {
		super(TinyMobFarm.MOB_FARM_CONTAINER.get(), windowId);

		this.containerData = containerData;

		this.addSlot(new LassoSlot(container, LASSO_SLOT, LASSO_SLOT_X, LASSO_SLOT_Y) {
			@Override
			public void setChanged() {
				super.setChanged();
				if (this.container instanceof MobFarmBlockEntity mobFarmBlockEntity) {
					mobFarmBlockEntity.saveAndSync();
				}
			}
		});
		
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inv, i, i * 18 + 8, 142));
		}
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new Slot(inv, i + j * 9 + 9, i * 18 + 8, j * 18 + 84));
			}
		}

		this.addDataSlots(containerData);
	}

	public MobFarmMenu(int i, Inventory inventory) {
		this(i, inventory, new SimpleContainer(1), new SimpleContainerData(4));
	}

	@Override
	public boolean stillValid(@NonNull Player player) {
		return true;
	}
	
	@Override
	public @NotNull ItemStack quickMoveStack(@NonNull Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack originalItemStack = slot.getItem();
		ItemStack copiedItemStack = originalItemStack.copy();

		int containerSlots = slots.size() - player.getInventory().getNonEquipmentItems().size();
		boolean moved = index < containerSlots
				? this.moveItemStackTo(originalItemStack, containerSlots, slots.size(), true)
				: this.moveItemStackTo(originalItemStack, 0, containerSlots, false);

		if (!moved) {
			return ItemStack.EMPTY;
		}

		if (originalItemStack.getCount() == 0) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}

		if (originalItemStack.getCount() == copiedItemStack.getCount()) {
			return ItemStack.EMPTY;
		}

		slot.onTake(player, originalItemStack);
		return copiedItemStack;
	}

	public int getProgress() {
		return this.containerData.get(0);
	}

	public int getMaxProgress() {
		return this.containerData.get(1);
	}

	public boolean isPowered() {
		return this.containerData.get(2) > 0;
	}

	public @Nullable MobFarmType getMobFarmType() {
		int ordinal = this.containerData.get(3);
		return ordinal >= 0 && ordinal < MobFarmType.values().length ? MobFarmType.values()[ordinal] : null;
	}
}
