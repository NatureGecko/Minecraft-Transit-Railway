package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorWideSide extends BlockEscalatorWide {
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world.getBlockState(pos.down()).isAir()) {
            return Blocks.getAirMapped().getDefaultState();
        }
        return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
    }

    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT) {
            IBlock.onBreakCreative(world, player, pos.offset(IBlock.getSideDirection(state)));
        }
        super.onBreak2(world, pos, state, player);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final World world = context.getWorld();
        final BlockPos pos = context.getBlockPos().down();
        final boolean isAir = world.getBlockState(pos).isAir();
        final boolean isAir2 = world.getBlockState(pos.offset(context.getPlayerFacing().rotateYClockwise())).isAir();
        if (!isAir && !isAir2) return super.getPlacementState2(context);
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
    public VoxelShape getCameraCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combine(getOutlineShape2(state, world, pos, context), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final boolean isRight = IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT;
        final EnumEscalatorOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

        if (orientation == EnumEscalatorOrientation.FLAT || orientation == EnumEscalatorOrientation.LANDING_BOTTOM || orientation == EnumEscalatorOrientation.LANDING_TOP) {
            if (isRight) return IBlock.getVoxelShapeByDirection(12.7, 0, 0, 15.9, 16, 16, facing);
            return IBlock.getVoxelShapeByDirection(0.1, 0, 0, 3.7, 16, 16, facing);
        }

        if (isRight) {
            return VoxelShapes.union(IBlock.getVoxelShapeByDirection(12.7, -8, 8, 15.9, 8, 16, facing), IBlock.getVoxelShapeByDirection(12.7, 0, 0, 15.9, 16, 8, facing));
        }
        return VoxelShapes.union(IBlock.getVoxelShapeByDirection(0.1, -8, 8, 3.7, 8, 16, facing), IBlock.getVoxelShapeByDirection(0.1, 0, 0, 3.7, 16, 8, facing));
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(STEP_DIRECTION);
        properties.add(ORIENTATION);
        properties.add(SIDE);
    }
}
