package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorNarrowSide extends BlockEscalatorNarrow {
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world.getBlockState(pos.down()).isAir()) return Blocks.getAirMapped().getDefaultState();
        return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
    }

    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak2(world, pos, state, player);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final World world = context.getWorld();
        final BlockPos pos = context.getBlockPos().down();
        final boolean isAir = world.getBlockState(pos).isAir();
        if (!isAir) return super.getPlacementState2(context);
        if (context.getPlayer() != null) {
            context.getPlayer().sendMessage(TranslationProvider.TOOLTIP_MTR_ESCALATOR_WIDE_PLACEMENT_WARN_BELOW.getText(), true);
        }
        return null;
    }

    @Nonnull
    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combine(getOutlineShape2(state, world, pos, context), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
    }

    @Nonnull
    @Override
    public VoxelShape getCameraCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

//        if (orientation == EnumEscalatorOrientation.FLAT || orientation == EnumEscalatorOrientation.TRANSITION_BOTTOM_1 || orientation == EnumEscalatorOrientation.LANDING_BOTTOM || orientation == EnumEscalatorOrientation.LANDING_TOP || orientation == EnumEscalatorOrientation.TRANSITION_TOP) {
//            return VoxelShapes.union(IBlock.getVoxelShapeByDirection(0.1, 0, 0, 1.7, 16, 16, facing), IBlock.getVoxelShapeByDirection(14.7, 0, 0, 15.9, 16, 16, facing));
//        }

        final VoxelShape shape1 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0.1, -8, 8, 1.7, 8, 16, facing), IBlock.getVoxelShapeByDirection(14.7, -8, 8, 15.9, 8, 16, facing));
        final VoxelShape shape2 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0.1, 0, 0, 1.7, 16, 8, facing), IBlock.getVoxelShapeByDirection(14.7, 0, 0, 15.9, 16, 8, facing));
        return VoxelShapes.union(shape1, shape2);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(STEP_DIRECTION);
        properties.add(ORIENTATION);
    }
}
