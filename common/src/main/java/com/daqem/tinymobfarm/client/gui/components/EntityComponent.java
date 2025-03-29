package com.daqem.tinymobfarm.client.gui.components;

import com.daqem.tinymobfarm.util.EntityHelper;
import com.daqem.uilib.client.gui.component.AbstractComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public class EntityComponent extends AbstractComponent<EntityComponent> {

    private final Supplier<ItemStack> itemStackSupplier;

    public EntityComponent(int x, int y, int width, int height, Supplier<ItemStack> itemStackSupplier) {
        super(null, x, y, width, height);
        this.itemStackSupplier = itemStackSupplier;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta, int color) {
        renderEntity(graphics, mouseX, mouseY);
    }

    public void renderEntity(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack lasso = itemStackSupplier.get();
        if (lasso.isEmpty()) return;
        LivingEntity livingEntity = (LivingEntity) EntityHelper.getEntityFromLasso(lasso, BlockPos.ZERO, Minecraft.getInstance().level);
        if (livingEntity == null) return;
        float entityHeight = livingEntity.getBbHeight();
        float entityWidth = livingEntity.getBbWidth();
        float scale = Math.max(entityHeight / 2, entityWidth);

        float xCenter = getWidth() / 2.0F;
        float yCenter = getHeight() / 2.0F;
        guiGraphics.enableScissor(0, 0, getWidth(), getHeight());
        float p = (float)Math.atan((xCenter - mouseX + getTotalX()) / 40.0F);
        float q = (float)Math.atan((yCenter - mouseY + getTotalY()) / 40.0F);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(q * 20.0F * (float) (Math.PI / 180.0));
        quaternionf.mul(quaternionf2);
        float r = livingEntity.yBodyRot;
        float s = livingEntity.getYRot();
        float t = livingEntity.getXRot();
        float u = livingEntity.yHeadRotO;
        float v = livingEntity.yHeadRot;
        livingEntity.yBodyRot = 180.0F + p * 20.0F;
        livingEntity.setYRot(180.0F + p * 40.0F);
        livingEntity.setXRot(-q * 20.0F);
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();
        Vector3f vector3f = new Vector3f(0.0F, livingEntity.getBbHeight() / 2.0F + 0.0625F * scale, 0.0F);
        float x = 30 / scale;
        renderEntityInInventory(guiGraphics, xCenter, yCenter, x, vector3f, quaternionf, quaternionf2, livingEntity);
        livingEntity.yBodyRot = r;
        livingEntity.setYRot(s);
        livingEntity.setXRot(t);
        livingEntity.yHeadRotO = u;
        livingEntity.yHeadRot = v;
        guiGraphics.disableScissor();
    }
}
