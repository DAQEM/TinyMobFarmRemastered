package com.daqem.tinymobfarm.blockentity;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.block.XpFaucetBlock;
import com.daqem.tinymobfarm.config.TinyMobFarmConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class XpFaucetBlockEntity extends BlockEntity {

    private static final String PLAYER_OPEN = "playerOpen";

    private boolean playerOpen;

    public XpFaucetBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TinyMobFarm.XP_FAUCET_ENTITY.get(), blockPos, blockState);
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, XpFaucetBlockEntity blockEntity) {
        blockEntity.tick();
    }

    private void tick() {
        if (this.level == null || this.level.isClientSide()) return;
        BlockState state = this.level.getBlockState(this.worldPosition);
        if (!state.hasProperty(XpFaucetBlock.OPEN) || !state.getValue(XpFaucetBlock.OPEN)) return;

        Direction facing = state.getValue(XpFaucetBlock.FACING);
        BlockEntity tankBE = this.level.getBlockEntity(this.worldPosition.relative(facing.getOpposite()));
        if (!(tankBE instanceof XpTankBlockEntity tank)) return;

        int removed = tank.removeXp(TinyMobFarmConfig.xpFaucetFlowPerTick.get());
        if (removed > 0) {
            Vec3 normal = facing.getUnitVec3();
            Vec3 tip = Vec3.atCenterOf(this.worldPosition).add(normal.scale(0.6));
            ExperienceOrb orb = new ExperienceOrb(this.level, tip.x, tip.y, tip.z, removed);
            orb.addTag(TinyMobFarm.XP_MARKER_TAG);
            orb.setDeltaMovement(normal.scale(0.3).add(new Vec3(
                    this.level.getRandom().nextDouble() - 0.5,
                    this.level.getRandom().nextDouble() - 0.5,
                    this.level.getRandom().nextDouble() - 0.5).scale(0.03)));
            this.level.addFreshEntity(orb);
        }
    }

    public boolean isPlayerOpen() {
        return this.playerOpen;
    }

    public void togglePlayerOpen() {
        this.playerOpen = !this.playerOpen;
        this.saveAndSync();
    }

    private void saveAndSync() {
        if (this.level == null) return;
        BlockState state = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        this.setChanged();
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        input.getInt(PLAYER_OPEN).ifPresent(value -> this.playerOpen = value != 0);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(PLAYER_OPEN, this.playerOpen ? 1 : 0);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        return this.saveWithoutMetadata(provider);
    }
}
