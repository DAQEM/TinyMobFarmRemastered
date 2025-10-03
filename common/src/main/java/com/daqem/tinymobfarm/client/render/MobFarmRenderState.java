package com.daqem.tinymobfarm.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;

public class MobFarmRenderState extends BlockEntityRenderState {
    @Nullable
    public EntityRenderState displayEntity;
    public float scale;
    public float angle;
}
