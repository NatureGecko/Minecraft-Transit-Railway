package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorBeltSide extends BlockEscalatorBelt {
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
        final EnumEscalatorBeltOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

        double heightBase = 16;
        double lowBase = 0;

        if (orientation == EnumEscalatorBeltOrientation.SLOPE_1) {
            heightBase = 2.95;
            lowBase = -8;
        } else if (orientation == EnumEscalatorBeltOrientation.SLOPE_2) {
            heightBase = 6.95;
            lowBase = -6;
        } else if (orientation == EnumEscalatorBeltOrientation.SLOPE_3) {
            heightBase = 10.95;
            lowBase = -4;
        } else if (orientation == EnumEscalatorBeltOrientation.SLOPE_4) {
            heightBase = 14.95;
            lowBase = -2;
        }

        return IBlock.getVoxelShapeByDirection(isRight ? 12 : 0.1, lowBase, 0, isRight ? 15.9 : 4, heightBase, 16, facing);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(STEP_DIRECTION);
        properties.add(ORIENTATION);
        properties.add(SIDE);
    }
}
