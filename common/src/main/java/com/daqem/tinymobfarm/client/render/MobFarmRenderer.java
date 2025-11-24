package com.daqem.tinymobfarm.client.render;

import com.daqem.tinymobfarm.block.MobFarmBlock;
import com.daqem.tinymobfarm.blockentity.MobFarmBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobFarmRenderer implements BlockEntityRenderer<MobFarmBlockEntity, MobFarmRenderState> {
    private final EntityRenderDispatcher entityRenderer;

    public MobFarmRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    public @NotNull MobFarmRenderState createRenderState() {
        return new MobFarmRenderState();
    }

    public void extractRenderState(
            MobFarmBlockEntity mobFarmBlockEntity,
            MobFarmRenderState MobFarmRenderState,
            float f,
            Vec3 vec3,
            @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(mobFarmBlockEntity, MobFarmRenderState, f, vec3, crumblingOverlay);
        if (mobFarmBlockEntity.getLevel() != null) {
            Entity entity = mobFarmBlockEntity.getLivingEntity();
            extractMobFarmData(MobFarmRenderState, f, entity, this.entityRenderer);
        }
        MobFarmRenderState.angle = mobFarmBlockEntity.getBlockState().getValue(MobFarmBlock.FACING).toYRot();
    }

    static void extractMobFarmData(
            MobFarmRenderState renderState,
            float f,
            @Nullable Entity entity,
            EntityRenderDispatcher entityRenderDispatcher
    ) {
        if (entity != null) {
            renderState.displayEntity = entityRenderDispatcher.extractEntity(entity, f);
            renderState.displayEntity.lightCoords = renderState.lightCoords;
            renderState.scale = 0.53125F;
            float g = Math.max(entity.getBbWidth(), entity.getBbHeight());
            if (g > 1.0) {
                renderState.scale /= g;
            }
        }
    }

    public void submit(
            MobFarmRenderState renderState,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState
    ) {
        if (renderState.displayEntity != null) {
            submitEntityInMobFarm(
                    poseStack,
                    submitNodeCollector,
                    renderState.displayEntity,
                    this.entityRenderer,
                    renderState.scale,
                    renderState.angle,
                    cameraRenderState
            );
        }
    }

    public static void submitEntityInMobFarm(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            EntityRenderState entityRenderState,
            EntityRenderDispatcher entityRenderDispatcher,
            float scale,
            float angle,
            CameraRenderState cameraRenderState
    ) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.125F, 0.5F);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(-angle));
        entityRenderDispatcher.submit(entityRenderState, cameraRenderState, 0.0, 0.0, 0.0, poseStack, submitNodeCollector);
        poseStack.popPose();
    }
}
