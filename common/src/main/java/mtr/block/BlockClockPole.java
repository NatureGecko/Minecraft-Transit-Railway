package mtr.block;

import mtr.mappings.BlockMapper;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockClockPole extends BlockMapper {

	public BlockClockPole(Properties settings) {
		super(settings);
	}

	public static final EnumProperty<EnumClockPoleVariant> VARIANT = EnumProperty.create("variant", EnumClockPoleVariant.class);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
		final EnumClockPoleVariant variant = IBlock.getStatePropertySafe(state, VARIANT);

		int height = 16;
		if(variant == EnumClockPoleVariant.EXTRA_HALF){
			height = 24;
		}
		return Block.box(7.5, 0, 7.5, 8.5, height, 8.5);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos){
		BlockState blockAbove = level.getBlockState(currentPos.above());
		if(blockAbove.getBlock() instanceof SlabBlock && blockAbove.getValue(SlabBlock.TYPE) == SlabType.TOP){
			return state.setValue(VARIANT , EnumClockPoleVariant.EXTRA_HALF);
		}
		if(blockAbove.getBlock() instanceof StairBlock && blockAbove.getValue(StairBlock.HALF) == Half.TOP){
			return state.setValue(VARIANT , EnumClockPoleVariant.EXTRA_HALF);
		}
		return state.setValue(VARIANT , EnumClockPoleVariant.FULL);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState getBlockAbove = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
		if(getBlockAbove.getBlock() instanceof SlabBlock && getBlockAbove.getValue(SlabBlock.TYPE) == SlabType.TOP){
			return this.defaultBlockState().setValue(VARIANT , EnumClockPoleVariant.EXTRA_HALF);
		}
		if(getBlockAbove.getBlock() instanceof SlabBlock && getBlockAbove.getValue(StairBlock.HALF) == Half.TOP){
			return this.defaultBlockState().setValue(VARIANT , EnumClockPoleVariant.EXTRA_HALF);
		}
		return this.defaultBlockState().setValue(VARIANT , EnumClockPoleVariant.FULL);
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(VARIANT);
	}

	public enum EnumClockPoleVariant implements StringRepresentable {
		FULL("full"),EXTRA_HALF("extra_half");
		private final String name;
		EnumClockPoleVariant(String variant) {
			name = variant;
		}
		@Override
		public String getSerializedName() {
			return name;
		}
	}


}
