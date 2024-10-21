package com.daqem.tinymobfarm.client.gui.components;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.client.gui.MobFarmScreen;
import com.daqem.tinymobfarm.item.LassoItem;
import com.daqem.uilib.client.gui.component.TextComponent;
import com.daqem.uilib.client.gui.component.texture.TextureComponent;
import com.daqem.uilib.client.gui.text.Text;
import com.daqem.uilib.client.gui.text.TruncatedText;
import com.daqem.uilib.client.gui.texture.Texture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MobFarmComponent extends TextureComponent {

    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(TinyMobFarm.MOD_ID, "textures/gui/farm_gui.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;
    private static final Component NO_LASSO = TinyMobFarm.translatable("gui.no_lasso");
    private static final Component REDSTONE_DISABLE = TinyMobFarm.translatable("gui.redstone_disable");
    private static final Component HIGHER_TIER = TinyMobFarm.translatable("gui.higher_tier");

    private final MobFarmScreen parent;
    private final Font font;

    private TextComponent errorText;
    private TextComponent titleComponent;
    private EntityComponent entityComponent;
    private TextComponent entityNameComponent;
    private ProgressBarComponent progressBarComponent;

    public MobFarmComponent(MobFarmScreen parent, Font font) {
        super(new Texture(TEXTURE_LOCATION, 0, 0, WIDTH, HEIGHT), 0, 0, WIDTH, HEIGHT);

        this.parent = parent;
        this.font = font;
    }

    @Override
    public void startRenderable() {
        super.startRenderable();

        this.errorText = new TextComponent(70, 67, new Text(this.font, Component.empty()));
        this.errorText.setScale(0.75F);
        Text titleText = new Text(this.font, parent.getTitle());
        this.titleComponent = new TextComponent(8, 5, titleText);
        this.entityComponent = new EntityComponent(8, 15, 52, 63, parent::getLasso);
        Text entityName = new Text(this.font, Component.empty());
        this.entityNameComponent = new TextComponent(90, 37, entityName);
        this.progressBarComponent = new ProgressBarComponent(71, 55, 97, 5, 0xFF3de031, parent.getMenu().getProgress(), parent.getMenu().getMaxProgress());

        titleText.setTextColor(0x404040);

        this.addChildren(this.errorText, this.titleComponent, this.entityComponent, this.entityNameComponent, this.progressBarComponent);
    }

    @Override
    public void resizeScreenRepositionRenderable(int width, int height) {
        super.resizeScreenRepositionRenderable(width, height);

        Text errorText = this.getLassoError();
        this.errorText.setText(errorText);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        TruncatedText entityText = new TruncatedText(this.font,
                parent.getLasso().getItem() instanceof LassoItem lassoItem ?
                        lassoItem.getMobName(parent.getLasso()) :
                        Component.empty(), 0, 0, 80, 0
        );
        entityText.setTextColor(ChatFormatting.DARK_GRAY);
        this.entityNameComponent.setText(entityText);

        this.progressBarComponent.setProgress(parent.getMenu().getProgress());
        this.progressBarComponent.setMaxProgress(parent.getMenu().getMaxProgress());

        this.resizeScreenRepositionRenderable(this.parent.width, this.parent.height);
    }

    private Text getLassoError() {
        ItemStack lasso = parent.getLasso();

        Component component;
        if (lasso.isEmpty()) component = NO_LASSO;
        else if (parent.getMenu().isPowered()) component = REDSTONE_DISABLE;
        else if (parent.getType() != null && !parent.getType().isLassoValid(lasso)) component = HIGHER_TIER;
        else component = Component.empty();

        Text text = new Text(this.font, component);
        text.setTextColor(ChatFormatting.RED);
        text.setWidth(this.font.width(component));
        return text;
    }
}
