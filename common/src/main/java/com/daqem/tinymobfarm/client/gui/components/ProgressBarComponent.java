package com.daqem.tinymobfarm.client.gui.components;

import com.daqem.uilib.gui.component.AbstractComponent;
import net.minecraft.client.gui.GuiGraphics;

public class ProgressBarComponent extends AbstractComponent {

    private final int color;
    private int progress;
    private int maxProgress;

    public ProgressBarComponent(int x, int y, int width, int height, int color, int progress, int maxProgress) {
        super(x, y, width, height);
        this.color = color;
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public float getProgressPercentage() {
        return (float) progress / maxProgress;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int parentWidth, int parentHeight) {
        guiGraphics.fill(getTotalX(), getTotalY(), getTotalX() + (int) (getWidth() * getProgressPercentage()), getTotalY() + getHeight(), color);
    }
}
