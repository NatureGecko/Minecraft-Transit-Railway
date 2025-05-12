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
        // Prevents culling optimization mods from culling our see-through escalator side
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
        final EnumEscalatorNarrowOrientation orientation = getOrientation(world, pos, state);
        final boolean isBottom = orientation == EnumEscalatorNarrowOrientation.LANDING_BOTTOM;
        final boolean isTop = orientation == EnumEscalatorNarrowOrientation.LANDING_TOP;
        return IBlock.getVoxelShapeByDirection(0, 0, isTop ? 8 : 0, 4, 16, isBottom ? 8 : 16, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(STEP_DIRECTION);
        properties.add(ORIENTATION);
    }
}
