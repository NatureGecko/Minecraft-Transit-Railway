package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.DirectionHelper;

import javax.annotation.Nonnull;

// !INFO
// Escalator Base - A parent class for the following features
// - Narrow Escalators
// - Wide Escalator
// - Escalator Median
public class BlockEscalator extends BlockExtension implements IBlock, DirectionHelper {
    protected BlockEscalator() {
        super(org.mtr.mod.Blocks.createDefaultBlockSettings(true).nonOpaque());
    }

    public static final EnumProperty<EnumEscalatorOrientation> ORIENTATION = EnumProperty.of("orientation", EnumEscalatorOrientation.class);
    public static final EnumProperty<EnumEscalatorStepDirection> STEP_DIRECTION = EnumProperty.of("step_direction", EnumEscalatorStepDirection.class);

    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(new Property<>(ORIENTATION.data), getOrientation(BlockView.cast(world), pos, state));
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        return getDefaultState2().with(new Property<>(FACING.data), context.getPlayerFacing().data);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getOutlineShape2(state, world, pos, context);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorOrientation orientation = getOrientation(world, pos, state);
        if (orientation == EnumEscalatorOrientation.SLOPE || orientation == EnumEscalatorOrientation.TRANSITION_TOP) {
            return VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 8, 16), IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 15, 8, IBlock.getStatePropertySafe(state, FACING)));
        }
        return VoxelShapes.fullCube();
    }

    @Nonnull
    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(new ItemConvertible(asItem2().data));
    }

    protected final EnumEscalatorOrientation getOrientation(BlockView world, BlockPos pos, BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

        final BlockPos posAhead = pos.offset(facing);
        final BlockPos posBehind = pos.offset(facing, -1);

        final boolean isAhead = state.isOf(world.getBlockState(posAhead).getBlock());
        final boolean isAheadUp = state.isOf(world.getBlockState(posAhead.up()).getBlock());

        final boolean isBehind = state.isOf(world.getBlockState(posBehind).getBlock());
        final boolean isBehindDown = state.isOf(world.getBlockState(pos.offset(facing, -1).down()).getBlock());

        if (isBehind) {
            if (isAhead) return EnumEscalatorOrientation.FLAT;
            if (isAheadUp) return EnumEscalatorOrientation.TRANSITION_BOTTOM;
            return EnumEscalatorOrientation.LANDING_TOP;
        } else if (isBehindDown) {
            if (isAheadUp) return EnumEscalatorOrientation.SLOPE;
            if (isAhead) return EnumEscalatorOrientation.TRANSITION_TOP;
        }
        return EnumEscalatorOrientation.LANDING_BOTTOM;
    }

    public enum EnumEscalatorOrientation implements StringIdentifiable {
        LANDING_BOTTOM("landing_bottom"), LANDING_TOP("landing_top"), FLAT("flat"), SLOPE("slope"), TRANSITION_BOTTOM("transition_bottom"), TRANSITION_TOP("transition_top");
        private final String name;

        EnumEscalatorOrientation(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }

    public enum EnumEscalatorStepDirection implements StringIdentifiable {
        STOP("stop"), FORWARD("forward"), BACKWARD("backward");
        private final String name;

        EnumEscalatorStepDirection(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }
}
