package com.daqem.tinymobfarm.client.gui.components;

import com.daqem.tinymobfarm.MobFarmType;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.client.gui.MobFarmScreen;
import com.daqem.tinymobfarm.item.LassoItem;
import com.daqem.uilib.gui.component.sprite.SpriteComponent;
import com.daqem.uilib.gui.component.text.TextAlign;
import com.daqem.uilib.gui.component.text.TextComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MobFarmComponent extends SpriteComponent {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;
    private static final Component REDSTONE_DISABLE = TinyMobFarm.translatable("gui.redstone_disable");
    private static final Component HIGHER_TIER = TinyMobFarm.translatable("gui.higher_tier");

    private final MobFarmScreen parent;

    private final TextComponent errorText;
    private final TextComponent entityNameComponent;
    private final ProgressBarComponent progressBarComponent;

    public MobFarmComponent(MobFarmScreen parent) {
        super(0, 0, WIDTH, HEIGHT, TinyMobFarm.getId("mob_farm_background"));
        this.parent = parent;

        this.errorText = new TextComponent(0, -10, Component.empty());
        this.errorText.centerHorizontally();
        this.errorText.setTextAlign(TextAlign.CENTER);
        TextComponent titleComponent = new TextComponent(8, 5, parent.getTitle(), 0xFF404040);
        EntityComponent entityComponent = new EntityComponent(8, 15, 52, 63, parent::getLasso);
        this.entityNameComponent = new TextComponent(90, 37, Component.empty(), 0xFF555555);
        this.progressBarComponent = new ProgressBarComponent(71, 55, 97, 5, 0xFF3de031, parent.getMenu().getProgress(), parent.getMenu().getMaxProgress());

        this.addComponent(this.errorText);
        this.addComponent(titleComponent);
        this.addComponent(entityComponent);
        this.addComponent(this.entityNameComponent);
        this.addComponent(this.progressBarComponent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int parentWidth, int parentHeight) {
        super.render(guiGraphics, mouseX, mouseY, partialTick, parentWidth, parentHeight);
        errorText.setText(getLassoError());

        this.entityNameComponent.setText(
                parent.getLasso().getItem() instanceof LassoItem lassoItem ?
                        lassoItem.getMobName(parent.getLasso()) :
                        Component.empty()
        );

        this.progressBarComponent.setProgress(parent.getMenu().getProgress());
        this.progressBarComponent.setMaxProgress(parent.getMenu().getMaxProgress());
    }

    private Component getLassoError() {
        ItemStack lasso = parent.getLasso();

        Component component;
        if (parent.getMenu().isPowered()) {
            component = REDSTONE_DISABLE;
        } else {
            MobFarmType type = parent.getMenu().getMobFarmType();
            if (type != null && lasso != null && !lasso.is(Items.AIR) && !type.isLassoValid(lasso)) {
                component = HIGHER_TIER;
            } else {
                return Component.empty();
            }
        }

        return component.copy().withColor(0xFFFF5555);
    }
}
