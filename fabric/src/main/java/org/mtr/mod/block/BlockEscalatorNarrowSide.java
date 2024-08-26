package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;
import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorNarrowSide extends BlockEscalatorNarrowBase {


    // getStateForNeighborUpdate2
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
    }

    // getCollisionShape2
    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
        // return VoxelShapes.combine(getOutlineShape2(state, world, pos, context), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
    }

    // getCameraCollisionShape2
    @Nonnull
    @Override
    public VoxelShape getCameraCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    // onBreak2
    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak2(world, pos, state, player);
    }

    // getOutlineShape2
    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // final EnumFramedEscalatorNarrowOrientation orientation =
        // getOrientation(world, pos, state);
        // final boolean isBottom = orientation ==
        // EnumFramedEscalatorNarrowOrientation.LANDING_BOTTOM;
        // final boolean isTop = orientation ==
        // EnumFramedEscalatorNarrowOrientation.LANDING_TOP;

        double x1 = 0;
        double y1 = 0;
        double z1 = 0;
        double x2 = 16;
        double y2 = 16;
        double z2 = 16;

        return IBlock.getVoxelShapeByDirection(x1, y1, z1, x2, y2, z2, IBlock.getStatePropertySafe(state, FACING));
    }

    // addBlockProperties
    @Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(ORIENTATION);
        properties.add(HEADING);
    }
}