package com.daqem.tinymobfarm.client.gui.components;

import com.daqem.uilib.client.gui.component.AbstractComponent;
import com.daqem.uilib.client.gui.component.SolidColorComponent;
import net.minecraft.client.gui.GuiGraphics;

public class ProgressBarComponent extends AbstractComponent<SolidColorComponent> {

    private final int color;
    private int progress;
    private int maxProgress;

    public ProgressBarComponent(int x, int y, int width, int height, int color, int progress, int maxProgress) {
        super(null, x, y, width, height);
        this.color = color;
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta, int theColor) {
        graphics.fill(0, 0, (int) (getWidth() * getProgressPercentage()), getHeight(), color);
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
}
