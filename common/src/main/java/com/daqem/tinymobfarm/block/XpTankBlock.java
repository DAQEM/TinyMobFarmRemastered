package com.daqem.tinymobfarm.block;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.blockentity.XpTankBlockEntity;
import com.daqem.tinymobfarm.item.TinyMobFarmItems;
import com.daqem.tinymobfarm.util.XpUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class XpTankBlock extends BaseEntityBlock {

    public static final IntegerProperty FILL = IntegerProperty.create("fill", 0, 9);

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 15, 15);

    public XpTankBlock(Properties properties) {
        super(properties.mapColor(MapColor.DIAMOND).strength(2.0f, 6.0f).sound(SoundType.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(FILL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILL);
    }

    @Override
    public @NotNull VoxelShape getShape(@NonNull BlockState blockState, @NonNull BlockGetter blockGetter, @NonNull BlockPos blockPos, @NonNull CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NonNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new XpTankBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, TinyMobFarm.XP_TANK_ENTITY.get(), XpTankBlockEntity::tick);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull BlockHitResult blockHitResult) {
        return this.handleUse(level, blockPos, player);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull InteractionHand interactionHand, @NonNull BlockHitResult blockHitResult) {
        return this.handleUse(level, blockPos, player);
    }

    private InteractionResult handleUse(Level level, BlockPos blockPos, Player player) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        // Let block placement win (e.g. placing the XP Faucet on the tank's side).
        if (player.getMainHandItem().getItem() instanceof BlockItem) return InteractionResult.PASS;
        if (level.getBlockEntity(blockPos) instanceof XpTankBlockEntity tank) {
            if (player.isShiftKeyDown()) {
                // Sneak-right-click: quickly store the player's XP into the tank.
                int added = tank.addXp(XpUtils.getPlayerXp(player));
                XpUtils.addPlayerXp(player, -added);
            } else {
                // Right-click: open the tank interface (store / withdraw via buttons).
                player.openMenu(tank);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean hasAnalogOutputSignal(@NonNull BlockState blockState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Direction direction) {
        if (level.getBlockEntity(blockPos) instanceof XpTankBlockEntity tank && tank.getCapacity() > 0) {
            return Math.min(15, tank.getStoredXp() * 15 / tank.getCapacity());
        }
        return 0;
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @Nullable LivingEntity placer, @NonNull ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, placer, itemStack);
        if (level.getBlockEntity(blockPos) instanceof XpTankBlockEntity tank) {
            Integer xp = itemStack.get(TinyMobFarm.TANK_XP_DATA.get());
            if (xp != null && xp > 0) {
                tank.setStoredXp(xp);
            }
        }
    }

    @Override
    public void playerDestroy(@NonNull Level level, @NonNull Player player, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @Nullable BlockEntity blockEntity, @NonNull ItemStack itemStack) {
        // Drop the tank item carrying its stored XP (the loot table for the tank is empty,
        // so we handle the drop ourselves). Creative breaks return before this method, so no dupes.
        if (!level.isClientSide()) {
            ItemStack drop = new ItemStack(TinyMobFarmItems.XP_TANK.get());
            if (blockEntity instanceof XpTankBlockEntity tank && tank.getStoredXp() > 0) {
                drop.set(TinyMobFarm.TANK_XP_DATA.get(), tank.getStoredXp());
            }
            popResource(level, blockPos, drop);
        }
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(XpTankBlock::new);
    }
}
