package com.daqem.tinymobfarm.blockentity;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.block.XpTankBlock;
import com.daqem.tinymobfarm.client.gui.XpTankMenu;
import com.daqem.tinymobfarm.config.TinyMobFarmConfig;
import com.daqem.tinymobfarm.config.XpTankMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class XpTankBlockEntity extends BlockEntity implements MenuProvider {

    private static final double ABSORB_DIST_SQ = 1.3 * 1.3;
    private static final String STORED_XP = "storedXp";

    private int storedXp;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> XpTankBlockEntity.this.storedXp;
                case 1 -> XpTankBlockEntity.this.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public XpTankBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TinyMobFarm.XP_TANK_ENTITY.get(), blockPos, blockState);
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, XpTankBlockEntity blockEntity) {
        blockEntity.tick();
    }

    private void tick() {
        if (this.level == null || this.level.isClientSide()) return;

        int radius = TinyMobFarmConfig.xpTankRadius.get().intValue();
        AABB box = AABB.ofSize(Vec3.atCenterOf(this.worldPosition), radius * 2.0, radius * 2.0, radius * 2.0);
        List<ExperienceOrb> orbs = this.level.getEntitiesOfClass(ExperienceOrb.class, box,
                orb -> !orb.entityTags().contains(TinyMobFarm.XP_MARKER_TAG));

        Vec3 center = Vec3.atCenterOf(this.worldPosition);
        boolean changed = false;
        for (ExperienceOrb orb : orbs) {
            if (orb.isRemoved()) continue;
            double distSq = orb.distanceToSqr(center);
            if (distSq <= ABSORB_DIST_SQ) {
                int value = orb.getValue();
                if (value > 0 && this.storedXp <= this.getCapacity() - value) {
                    this.storedXp += value;
                    orb.discard();
                    changed = true;
                }
            } else if (TinyMobFarmConfig.xpTankMode.get() == XpTankMode.MAGNET) {
                Vec3 dir = center.subtract(orb.position()).normalize();
                orb.setDeltaMovement(orb.getDeltaMovement().scale(0.85).add(dir.scale(0.25)));
            }
        }
        if (changed) this.saveAndSync();
        this.updateFillState();
    }

    /**
     * Updates the block's {@code fill} state so the glass tank visually shows how full
     * it is relative to its capacity (0..9 liquid levels).
     */
    private void updateFillState() {
        if (this.level == null || this.level.isClientSide()) return;
        int capacity = this.getCapacity();
        int fill = capacity <= 0 ? 0 : (int) Math.min(9, (long) this.storedXp * 9 / capacity);
        BlockState state = this.level.getBlockState(this.worldPosition);
        if (state.hasProperty(XpTankBlock.FILL) && state.getValue(XpTankBlock.FILL) != fill) {
            this.level.setBlock(this.worldPosition, state.setValue(XpTankBlock.FILL, fill), 3);
        }
    }

    public int getCapacity() {
        return TinyMobFarmConfig.xpTankCapacity.get();
    }

    public int getStoredXp() {
        return this.storedXp;
    }

    /**
     * Sets the stored XP (e.g. restored from the item when the tank is placed).
     */
    public void setStoredXp(int storedXp) {
        this.storedXp = Math.max(0, Math.min(storedXp, this.getCapacity()));
        this.saveAndSync();
        this.updateFillState();
    }

    /**
     * Adds XP points, clamped to capacity. Returns how many were actually added.
     */
    public int addXp(int amount) {
        int canAdd = Math.max(0, this.getCapacity() - this.storedXp);
        int added = Math.min(canAdd, amount);
        if (added > 0) {
            this.storedXp += added;
            this.saveAndSync();
        }
        return added;
    }

    /**
     * Removes up to {@code amount} XP points. Returns how many were actually removed.
     */
    public int removeXp(int amount) {
        int removed = Math.min(amount, this.storedXp);
        if (removed > 0) {
            this.storedXp -= removed;
            this.saveAndSync();
        }
        return removed;
    }

    public void saveAndSync() {
        if (this.level == null) return;
        BlockState state = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        this.setChanged();
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NonNull Inventory inventory, @NonNull Player player) {
        return new XpTankMenu(windowId, inventory, this.dataAccess, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.tinymobfarm.xp_tank");
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        input.getInt(STORED_XP).ifPresent(value -> this.storedXp = value);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(STORED_XP, this.storedXp);
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
