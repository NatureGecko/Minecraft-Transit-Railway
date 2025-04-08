package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockEscalatorNarrowBase extends BlockExtension implements IBlock, DirectionHelper {
    public static final EnumProperty<EnumEscalatorNarrowOrientation> ORIENTATION = EnumProperty.of("orientation", EnumEscalatorNarrowOrientation.class);
    public static final EnumProperty<EnumEscalatorNarrowStepDirection> STEP_DIRECTION = EnumProperty.of("step_direction", EnumEscalatorNarrowStepDirection.class);

    protected BlockEscalatorNarrowBase() {
        super(org.mtr.mod.Blocks.createDefaultBlockSettings(true).nonOpaque());
    }

    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(new Property<>(ORIENTATION.data), getOrientation(BlockView.cast(world), pos, state));
    }

//    @Override
//    public void onPlaced2(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
//        if (!world.isClient()) {
//            final Direction direction = IBlock.getStatePropertySafe(state, FACING);
//            final BlockPos posSide;
//            switch (direction) {
//                case NORTH -> posSide = pos.east();
//                case EAST -> posSide = pos.south();
//                case SOUTH -> posSide = pos.west();
//                default -> posSide = pos.north();
//            }
//            world.setBlockState(posSide, state);
//        }
//    }

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
        final EnumEscalatorNarrowOrientation orientation = getOrientation(world, pos, state);
        if (orientation == EnumEscalatorNarrowOrientation.SLOPE || orientation == EnumEscalatorNarrowOrientation.TRANSITION_TOP) {
            return VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 8, 16), IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 15, 8, IBlock.getStatePropertySafe(state, FACING)));
        } else {
            return VoxelShapes.fullCube();
        }
    }

    @Nonnull
    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(new ItemConvertible(asItem2().data));
    }

    protected final EnumEscalatorNarrowOrientation getOrientation(BlockView world, BlockPos pos, BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

        final BlockPos posAhead = pos.offset(facing);
        final BlockPos posBehind = pos.offset(facing, -1);

        final boolean isAhead = state.isOf(world.getBlockState(posAhead).getBlock());
        final boolean isAheadUp = state.isOf(world.getBlockState(posAhead.up()).getBlock());

        final boolean isBehind = state.isOf(world.getBlockState(posBehind).getBlock());
        final boolean isBehindDown = state.isOf(world.getBlockState(posBehind.down()).getBlock());

        if (isAhead && isBehind) {
            return EnumEscalatorNarrowOrientation.FLAT;
        } else if (isAheadUp && isBehindDown) {
            return EnumEscalatorNarrowOrientation.SLOPE;
        } else if (isAheadUp && isBehind) {
            return EnumEscalatorNarrowOrientation.TRANSITION_BOTTOM;
        } else if (isAhead && isBehindDown) {
            return EnumEscalatorNarrowOrientation.TRANSITION_TOP;
        } else if (isBehind) {
            return EnumEscalatorNarrowOrientation.LANDING_TOP;
        } else {
            return EnumEscalatorNarrowOrientation.LANDING_BOTTOM;
        }
    }

    public enum EnumEscalatorNarrowOrientation implements StringIdentifiable {
        LANDING_BOTTOM("landing_bottom"), LANDING_TOP("landing_top"), FLAT("flat"), SLOPE("slope"), TRANSITION_BOTTOM("transition_bottom"), TRANSITION_TOP("transition_top");
        private final String name;

        EnumEscalatorNarrowOrientation(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }

    public enum EnumEscalatorNarrowStepDirection implements StringIdentifiable {
        STOP("stop"), FORWARD("forward"), BACKWARD("backward");
        private final String name;

        EnumEscalatorNarrowStepDirection(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }
}
