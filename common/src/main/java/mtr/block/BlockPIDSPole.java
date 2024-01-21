package mtr.block;

import mtr.mappings.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockPIDSPole extends BlockPoleCheckBase {
	public BlockPIDSPole(Properties settings) {
		super(settings);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
		final  BlockPoleCheckBase.EnumPoleVariant variant = IBlock.getStatePropertySafe(state, BlockPoleCheckBase.VARIANT);
 		int height = 16;
 		if(variant == BlockPoleCheckBase.EnumPoleVariant.EXTRA_HALF){ height = 24; }
		return mtr.block.IBlock.getVoxelShapeByDirection(7.5, 0, 12.5, 8.5, height, 13.5, IBlock.getStatePropertySafe(state, FACING));
	}

	@Override
	protected boolean isBlock(Block block) {
		return block instanceof BlockPIDSBase || block instanceof BlockPIDSPole;
	}

	@Override
	protected Component getTooltipBlockText() {
		return Text.translatable("block.mtr.pids_1");
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING,BlockPoleCheckBase.VARIANT);
	}
}
