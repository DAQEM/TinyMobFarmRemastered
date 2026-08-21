package com.daqem.tinymobfarm.block;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.blockentity.XpFaucetBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class XpFaucetBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    private static final VoxelShape SHAPE = Block.box(4, 4, 4, 12, 12, 12);

    public XpFaucetBlock(Properties properties) {
        super(properties.mapColor(MapColor.METAL).strength(1.5f).sound(SoundType.METAL));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(OPEN, false);
    }

    @Override
    protected boolean canSurvive(@NonNull BlockState blockState, @NonNull LevelReader levelReader, @NonNull BlockPos blockPos) {
        return levelReader.getBlockState(blockPos.relative(blockState.getValue(FACING).getOpposite()))
                .is(TinyMobFarmBlocks.XP_TANK_BLOCK.get());
    }

    @Override
    protected @NotNull BlockState updateShape(@NonNull BlockState blockState, @NonNull LevelReader levelReader, @NonNull ScheduledTickAccess scheduledTickAccess, @NonNull BlockPos blockPos, @NonNull Direction direction, @NonNull BlockPos neighborPos, @NonNull BlockState neighborState, @NonNull RandomSource randomSource) {
        if (levelReader instanceof ServerLevel && !blockState.canSurvive(levelReader, blockPos)) {
            scheduledTickAccess.scheduleTick(blockPos, this, 2);
        }
        return blockState;
    }

    @Override
    protected void tick(@NonNull BlockState blockState, @NonNull ServerLevel serverLevel, @NonNull BlockPos blockPos, @NonNull RandomSource randomSource) {
        serverLevel.destroyBlock(blockPos, true);
    }

    @Override
    protected void neighborChanged(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Block block, @Nullable Orientation orientation, boolean moved) {
        this.updateOpen(blockState, level, blockPos);
    }

    @Override
    protected void onPlace(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull BlockState oldState, boolean moved) {
        this.updateOpen(blockState, level, blockPos);
    }

    private void updateOpen(BlockState blockState, Level level, BlockPos blockPos) {
        if (level.isClientSide()) return;
        boolean powered = level.getBestNeighborSignal(blockPos) != 0;
        boolean playerOpen = level.getBlockEntity(blockPos) instanceof XpFaucetBlockEntity faucet && faucet.isPlayerOpen();
        boolean open = powered || playerOpen;
        if (blockState.getValue(OPEN) != open) {
            level.setBlock(blockPos, blockState.setValue(OPEN, open), 3);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull BlockHitResult blockHitResult) {
        return this.toggle(blockState, level, blockPos);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull InteractionHand interactionHand, @NonNull BlockHitResult blockHitResult) {
        return this.toggle(blockState, level, blockPos);
    }

    private InteractionResult toggle(BlockState blockState, Level level, BlockPos blockPos) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(blockPos) instanceof XpFaucetBlockEntity faucet) {
            faucet.togglePlayerOpen();
        }
        this.updateOpen(blockState, level, blockPos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NonNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new XpFaucetBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, TinyMobFarm.XP_FAUCET_ENTITY.get(), XpFaucetBlockEntity::tick);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(XpFaucetBlock::new);
    }
}
