package com.daqem.tinymobfarm.block;

import java.util.function.Consumer;
import java.util.function.Function;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.blockentity.MobFarmBlockEntity;
import com.daqem.tinymobfarm.MobFarmType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class MobFarmBlock extends BaseEntityBlock {

	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

	private static final VoxelShape BOUNDING_BOX = Block.box(1, 0, 1, 15, 14, 15);

	private final MobFarmType mobFarmType;

	public MobFarmBlock(MobFarmType mobFarmType, BlockBehaviour.Properties properties) {
		super(properties
				.mapColor(MapColor.STONE)
				.instrument(NoteBlockInstrument.BASEDRUM)
				.requiresCorrectToolForDrops()
				.strength(1.5f, 6.0f));

		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
		this.mobFarmType = mobFarmType;
	}

	public MobFarmType getMobFarmType() {
		return mobFarmType;
	}

	public void getTooltipBuilder(Consumer<Component> consumer) {
		this.mobFarmType.addTooltip(consumer);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getHorizontalDirection().getOpposite();
		return this.defaultBlockState().setValue(FACING, facing);
	}

	@Override
	public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		BlockEntity tileEntity = level.getBlockEntity(pos);
		if (tileEntity instanceof MobFarmBlockEntity mobFarmBlockEntity) {
			mobFarmBlockEntity.setMobFarmData(mobFarmType);
			mobFarmBlockEntity.updateRedstone();
		}
	}

	@Override
	protected @NotNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull InteractionHand interactionHand, @NonNull BlockHitResult blockHitResult) {
		if (level.isClientSide()) return InteractionResult.SUCCESS;

		BlockEntity tileEntity = level.getBlockEntity(blockPos);
		if (tileEntity instanceof MobFarmBlockEntity mobFarmBlockEntity) {
			player.openMenu(mobFarmBlockEntity);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	protected @NotNull BlockState updateShape(@NonNull BlockState blockState, LevelReader levelReader, @NonNull ScheduledTickAccess scheduledTickAccess, @NonNull BlockPos blockPos, @NonNull Direction direction, @NonNull BlockPos blockPos2, @NonNull BlockState blockState2, @NonNull RandomSource randomSource) {
		BlockEntity tileEntity = levelReader.getBlockEntity(blockPos);
		if (tileEntity instanceof MobFarmBlockEntity mobFarmBlockEntity) {
			mobFarmBlockEntity.updateRedstone();
			mobFarmBlockEntity.saveAndSync();
		}
		return blockState;
	}

	@Override
	protected void neighborChanged(@NonNull BlockState blockState, Level level, @NonNull BlockPos blockPos, @NonNull Block block, @Nullable Orientation orientation, boolean bl) {
		BlockEntity tileEntity = level.getBlockEntity(blockPos);
		if (tileEntity instanceof MobFarmBlockEntity mobFarmBlockEntity) {
			mobFarmBlockEntity.updateRedstone();
			mobFarmBlockEntity.saveAndSync();
		}
	}

	@Override
	public void playerDestroy(Level level, @NonNull Player player, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @Nullable BlockEntity blockEntity, @NonNull ItemStack itemStack) {
		BlockEntity tileEntity = level.getBlockEntity(blockPos);
		if (tileEntity instanceof MobFarmBlockEntity mobFarmBlockEntity) {
			ItemStack lasso = mobFarmBlockEntity.getLasso();
			if (!lasso.isEmpty()) {
				ItemEntity drop = new ItemEntity(level, blockPos.getX() + 0.5, blockPos.getY() + 0.3, blockPos.getZ() + 0.5, lasso);
				level.addFreshEntity(drop);
			}
		}
		super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
	}

	@Override
	public @NotNull VoxelShape getShape(@NonNull BlockState blockState, @NonNull BlockGetter blockGetter, @NonNull BlockPos blockPos, @NonNull CollisionContext collisionContext) {
		return BOUNDING_BOX;
	}

	@Override
	protected boolean propagatesSkylightDown(@NonNull BlockState blockState) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		return new MobFarmBlockEntity(blockPos, blockState);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockEntityType<T> blockEntityType) {
		return MobFarmBlock.createTickerHelper(blockEntityType, TinyMobFarm.MOB_FARM_TILE_ENTITY.get(), MobFarmBlockEntity::tick);
	}

	@Override
	protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return MobFarmBlock.simpleCodecWithMobFarmType(x -> new MobFarmBlock(x, BlockBehaviour.Properties.of()), mobFarmType);
	}

	public static <B extends Block> MapCodec<B> simpleCodecWithMobFarmType(Function<MobFarmType, B> function, MobFarmType mobFarmType) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(BlockBehaviour.propertiesCodec()).apply(instance, properties1 -> function.apply(mobFarmType)));
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NonNull BlockState blockState) {
		return RenderShape.MODEL;
	}
}
