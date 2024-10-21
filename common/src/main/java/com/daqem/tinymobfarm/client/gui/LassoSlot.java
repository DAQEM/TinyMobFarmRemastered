package com.daqem.tinymobfarm.client.gui;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.item.LassoItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LassoSlot extends Slot {

	public LassoSlot(Container container, int i, int j, int k) {
		super(container, i, j, k);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem() instanceof LassoItem && stack.has(TinyMobFarm.LASSO_DATA.get());

	}
	
	@Override
	public int getMaxStackSize() {
		return 1;
	}
}
