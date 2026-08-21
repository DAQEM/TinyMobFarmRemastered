package com.daqem.tinymobfarm.client.gui;

import com.daqem.tinymobfarm.util.XpUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class XpTankScreen extends AbstractContainerScreen<XpTankMenu> {

    public XpTankScreen(XpTankMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 92);
    }

    @Override
    public void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.translatable("tinymobfarm.gui.xp_tank.deposit"), b -> this.sendButton(0))
                .bounds(this.leftPos + 54, this.topPos + 62, 68, 20).build());
    }

    private void sendButton(int buttonId) {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.connection.send(new ServerboundContainerButtonClickPacket(this.menu.containerId, buttonId));
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Draw a custom dark panel behind everything (the new renderer draws no container background).
        graphics.fill(this.leftPos - 4, this.topPos - 4, this.leftPos + this.imageWidth + 4, this.topPos + this.imageHeight + 4, 0xD0101010);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        // Draw the info text through the same ActiveTextCollector path that vanilla buttons use,
        // so text reliably renders in this MC version.
        int stored = this.menu.getStoredXp();
        int capacity = this.menu.getCapacity();
        int level = XpUtils.getLevelForExperience(stored);
        int x = this.leftPos + 8;

        ActiveTextCollector textRenderer = graphics.textRenderer();
        textRenderer.accept(x, this.topPos + 6, this.title);
        textRenderer.accept(x, this.topPos + 24, Component.translatable("tinymobfarm.gui.xp_tank.points", stored));
        textRenderer.accept(x, this.topPos + 36, Component.translatable("tinymobfarm.gui.xp_tank.levels", level).withStyle(ChatFormatting.GREEN));
        textRenderer.accept(x, this.topPos + 48, Component.translatable("tinymobfarm.gui.xp_tank.capacity", capacity).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Intentionally empty: all text is drawn in extractRenderState above.
    }
}
