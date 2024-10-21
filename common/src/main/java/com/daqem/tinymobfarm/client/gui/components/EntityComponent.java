package com.daqem.tinymobfarm.client.gui.components;

import com.daqem.tinymobfarm.util.EntityHelper;
import com.daqem.uilib.client.gui.component.AbstractComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fStack;

import java.util.function.Supplier;

public class EntityComponent extends AbstractComponent<EntityComponent> {

    private final Supplier<ItemStack> itemStackSupplier;

    public EntityComponent(int x, int y, int width, int height, Supplier<ItemStack> itemStackSupplier) {
        super(null, x, y, width, height);
        this.itemStackSupplier = itemStackSupplier;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderEntity(graphics, mouseX, mouseY);
    }

    public void renderEntity(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack lasso = itemStackSupplier.get();
        if (lasso.isEmpty()) return;
        LivingEntity livingEntity = (LivingEntity) EntityHelper.getEntityFromLasso(lasso, BlockPos.ZERO, Minecraft.getInstance().level);
        float entityHeight = livingEntity.getBbHeight();
        float entityWidth = livingEntity.getBbWidth();
        float scale = 50.0F / entityHeight;
        if (entityWidth >= entityHeight) {
            scale = 30.0F / entityWidth;
        }

        int x = getWidth() / 2;
        int y = getHeight() - 5;
        double yaw = 180 - mouseX;
        double pitch = 90 - mouseY;


        guiGraphics.enableScissor(
                getTotalX(),
                getTotalY(),
                getTotalX() + getWidth(),
                getTotalY() + getHeight()
        );
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(guiGraphics.pose().last().pose());
        modelViewStack.translate(x, y, 50.0F);
        modelViewStack.scale((float) -scale, (float) scale, (float) scale);
        PoseStack mobPoseStack = new PoseStack();
        mobPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        mobPoseStack.mulPose(Axis.XN.rotationDegrees(((float) Math.atan((pitch / 180.0F))) * 20.0F));
        mobPoseStack.mulPose(Axis.YP.rotationDegrees(10.0F));
        livingEntity.yo = (float) Math.atan(yaw / 40.0F) * 20.0F;
        float yRot = (float) Math.atan(yaw / 40.0F) * 40.0F;
        float xRot = -((float) Math.atan(pitch / 40.0F)) * 20.0F;
        livingEntity.setYRot(yRot);
        livingEntity.setYRot(yRot);
        livingEntity.setXRot(xRot);
        livingEntity.yHeadRot = yRot;
        livingEntity.yHeadRotO = yRot;
        mobPoseStack.translate(0.0F, livingEntity.getY(), 0.0F);
        RenderSystem.applyModelViewMatrix();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, mobPoseStack, bufferSource, 15728880);
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
        guiGraphics.disableScissor();
    }
}
