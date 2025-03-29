package com.daqem.tinymobfarm.client.render;

import com.daqem.tinymobfarm.blockentity.MobFarmBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MobFarmRenderer implements BlockEntityRenderer<MobFarmBlockEntity> {

    public MobFarmRenderer(@SuppressWarnings("unused") BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MobFarmBlockEntity mobFarmBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource typeBuffer, int combinedLight, int combinedOverlay, Vec3 vec3) {
        LivingEntity livingEntity = mobFarmBlockEntity.getLivingEntity();
        if (livingEntity != null) {
            AABB box = livingEntity.getBoundingBox();
            double length = Math.max(Math.max(box.maxX - box.minX, box.maxY - box.minY), box.maxZ - box.minZ);
            float modelScale = (float) (0.5 / length);

            poseStack.pushPose();
            poseStack.translate(0.5, 0.125, 0.5);
            poseStack.scale(modelScale, modelScale, modelScale);

            livingEntity.setXRot(0);
            livingEntity.setYBodyRot(90);
            livingEntity.setYHeadRot(90);

            LocalPlayer player = Minecraft.getInstance().player;
            Vec3 playerPos = player.position();

            if (playerPos.distanceTo(mobFarmBlockEntity.getBlockPos().getCenter()) < 5) {
                Vec3 mobPos = mobFarmBlockEntity.getBlockPos().getCenter();

                // Calculate the difference in positions
                double dX = playerPos.x() - mobPos.x();
                double dY = (playerPos.y() + player.getEyeHeight()) - mobPos.y();
                double dZ = playerPos.z() - mobPos.z();

                // Calculate yaw (horizontal rotation)
                float yaw = (float) Math.toDegrees(Math.atan2(dZ, dX)) - 90F;

                // Calculate pitch (vertical rotation)
                double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);
                float pitch = (float) -Math.toDegrees(Math.atan2(dY, horizontalDistance));

                if (yaw < 0) yaw += 360;
                if (yaw > 270) yaw = 0;
                else if (yaw > 180) yaw = 180;

                livingEntity.setYHeadRot(yaw);
                livingEntity.setXRot(pitch);
            }

            Minecraft.getInstance().getEntityRenderDispatcher().render(livingEntity, 0, 0, 0, 1, poseStack, typeBuffer, combinedLight);
            poseStack.popPose();
        }
    }
}
