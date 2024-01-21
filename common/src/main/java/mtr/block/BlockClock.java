package mtr.block;

import mtr.BlockEntityTypes;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockMapper;
import mtr.mappings.EntityBlockMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockClock extends BlockMapper implements EntityBlockMapper {

	public static final BooleanProperty FACING = BooleanProperty.create("facing");

	public BlockClock(Properties settings) {
		super(settings);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		final boolean facing = ctx.getHorizontalDirection().getAxis() == Direction.Axis.X;
		BlockState getBlockAbove = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
		if(getBlockAbove.getBlock() instanceof SlabBlock && getBlockAbove.getValue(SlabBlock.TYPE) == SlabType.TOP){
			return createDefaultBlockState(facing).setValue(BlockClockPole.VARIANT , BlockClockPole.EnumClockPoleVariant.EXTRA_HALF);
		}
		if(getBlockAbove.getBlock() instanceof StairBlock && getBlockAbove.getValue(StairBlock.HALF) == Half.TOP){
			return createDefaultBlockState(facing).setValue(BlockClockPole.VARIANT , BlockClockPole.EnumClockPoleVariant.EXTRA_HALF);
		}
		return this.createDefaultBlockState(facing).setValue(BlockClockPole.VARIANT , BlockClockPole.EnumClockPoleVariant.FULL);
	}

	private BlockState createDefaultBlockState(boolean facing){
		return this.defaultBlockState().setValue(FACING, facing);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
		BlockState blockAbovePole = world.getBlockState(pos.above());
		if(blockAbovePole.getBlock() instanceof SlabBlock && blockAbovePole.getValue(SlabBlock.TYPE) == SlabType.TOP){
			return state.setValue(BlockClockPole.VARIANT,BlockClockPole.EnumClockPoleVariant.EXTRA_HALF);
		}
		if(blockAbovePole.getBlock() instanceof SlabBlock && blockAbovePole.getValue(SlabBlock.TYPE) == SlabType.TOP){
			return state.setValue(BlockClockPole.VARIANT,BlockClockPole.EnumClockPoleVariant.EXTRA_HALF);
		}
		return state.setValue(BlockClockPole.VARIANT,BlockClockPole.EnumClockPoleVariant.FULL);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
		final Direction facing = IBlock.getStatePropertySafe(state, FACING) ? Direction.EAST : Direction.NORTH;
		final BlockClockPole.EnumClockPoleVariant variant = IBlock.getStatePropertySafe(state, BlockClockPole.VARIANT);

		int height = 16;
		if(variant == BlockClockPole.EnumClockPoleVariant.EXTRA_HALF){
			height = 24;
		}

		return Shapes.or(IBlock.getVoxelShapeByDirection(3, 0, 6, 13, 12, 10, facing), Block.box(7.5, 12, 7.5, 8.5, height, 8.5));
	}

	@Override
	public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityClock(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING,BlockClockPole.VARIANT);
	}

	public static class TileEntityClock extends BlockEntityMapper {

		public TileEntityClock(BlockPos pos, BlockState state) {
			super(BlockEntityTypes.CLOCK_TILE_ENTITY.get(), pos, state);
		}
	}
}
