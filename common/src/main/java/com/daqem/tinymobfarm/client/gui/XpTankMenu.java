package com.daqem.tinymobfarm.client.gui;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.blockentity.XpTankBlockEntity;
import com.daqem.tinymobfarm.util.XpUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class XpTankMenu extends AbstractContainerMenu {

    private final ContainerData data;
    private final XpTankBlockEntity tank;

    public XpTankMenu(int containerId, @NonNull Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainerData(2), null);
    }

    public XpTankMenu(int containerId, @NonNull Inventory playerInventory, ContainerData data, @Nullable XpTankBlockEntity tank) {
        super(TinyMobFarm.XP_TANK_MENU.get(), containerId);
        this.data = data;
        this.tank = tank;
        this.addDataSlots(data);
    }

    public int getStoredXp() {
        return this.data.get(0);
    }

    public int getCapacity() {
        return this.data.get(1);
    }

    @Override
    public boolean clickMenuButton(@NonNull Player player, int buttonId) {
        if (this.tank == null || buttonId != 0) return false;
        // Deposit all of the player's XP into the tank. Withdrawal is done via the XP Faucet.
        int added = this.tank.addXp(XpUtils.getPlayerXp(player));
        XpUtils.addPlayerXp(player, -added);
        return true;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        if (this.tank != null && this.tank.getLevel() != null) {
            return Container.stillValidBlockEntity(this.tank, player);
        }
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        return ItemStack.EMPTY;
    }
}
