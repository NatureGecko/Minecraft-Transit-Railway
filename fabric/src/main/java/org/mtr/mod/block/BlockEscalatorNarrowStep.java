package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;
import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorNarrowStep extends BlockEscalatorNarrowBase {
    
    // getStateForNeighborUpdate2
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if(direction == Direction.UP && !(world.getBlockState(pos.up()).getBlock().data instanceof BlockEscalatorNarrowSide)){
            return Blocks.getAirMapped().getDefaultState();
        } else {
            return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    // onBreak2
    @Override
	public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak2(world, pos, state, player);
	}


    // getCollisionShape2
    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    // onEntityCollision2
    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    // onUse2

    // addBlockProperties
    @Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(HEADING);
        properties.add(FACING);
        properties.add(ORIENTATION);
    }

    // -- PRIVATE
    // update
    // private void update(World world, BlockPos pos, Direction offset, boolean direction, boolean running){
    //     world.setBlockState(pos, world.getBlockState(pos).with(new Property<>(HEADING.data), direction).with(new Property<>(STATUS.data), running));
    // }

    // -- PRIVATE
    // isStep
    private boolean isStep(World world, BlockPos pos){
        final Block block = world.getBlockState(pos).getBlock();
        return block.data instanceof BlockEscalatorNarrowStep;
    }

}

