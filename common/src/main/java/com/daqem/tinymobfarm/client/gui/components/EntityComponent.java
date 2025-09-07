package com.daqem.tinymobfarm.client.gui.components;

import com.daqem.tinymobfarm.util.EntityHelper;
import com.daqem.uilib.gui.component.AbstractComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse;

public class EntityComponent extends AbstractComponent {

    private final Supplier<ItemStack> itemStackSupplier;

    public EntityComponent(int x, int y, int width, int height, Supplier<ItemStack> itemStackSupplier) {
        super(x, y, width, height);
        this.itemStackSupplier = itemStackSupplier;
    }

    public void renderEntity(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack lasso = itemStackSupplier.get();
        if (lasso.isEmpty()) return;
        LivingEntity livingEntity = (LivingEntity) EntityHelper.getEntityFromLasso(lasso, BlockPos.ZERO, Minecraft.getInstance().level);
        if (livingEntity == null) return;
        renderEntityInInventoryFollowsMouse(
                guiGraphics,
                getTotalX(),
                getTotalY(),
                getTotalX() + getWidth(),
                getTotalY() + getHeight(),
                30,
                0.0625F,
                mouseX,
                mouseY,
                livingEntity
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int parentWidth, int parentHeight) {
        renderEntity(guiGraphics, mouseX, mouseY);
    }
}
