package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockEscalatorBeltBase extends BlockExtension implements IBlock, DirectionHelper {
    public static final EnumProperty<EnumEscalatorBeltOrientation> ORIENTATION = EnumProperty.of("orientation", EnumEscalatorBeltOrientation.class);
    public static final EnumProperty<EnumEscalatorBeltStepDirection> STEP_DIRECTION = EnumProperty.of("step_direction", EnumEscalatorBeltStepDirection.class);

    protected BlockEscalatorBeltBase() {
        super(org.mtr.mod.Blocks.createDefaultBlockSettings(true).nonOpaque());
    }

    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (getSideDirection(state) == direction && !neighborState.isOf(new Block(this))) {
            return Blocks.getAirMapped().getDefaultState();
        } else {
            return state.with(new Property<>(ORIENTATION.data), getOrientation(BlockView.cast(world), pos, state));
        }
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            final Direction direction = IBlock.getStatePropertySafe(state, FACING);
            final BlockPos posSide;
            switch (direction) {
                case NORTH -> posSide = pos.east();
                case EAST -> posSide = pos.south();
                case SOUTH -> posSide = pos.west();
                default -> posSide = pos.north();
            }
            world.setBlockState(posSide, state.with(new Property<>(SIDE.data), EnumSide.RIGHT));
        }
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final Direction direction = context.getPlayerFacing().rotateYClockwise();
        final boolean allowPlacement = IBlock.isReplaceable(context, direction, 2);
        if (allowPlacement) return getDefaultState2().with(new Property<>(FACING.data), context.getPlayerFacing().data);
        if (context.getPlayer() != null) {
            context.getPlayer().sendMessage(TranslationProvider.TOOLTIP_MTR_ESCALATOR_WIDE_PLACEMENT_WARN.getText(), true);
        }
        return null;
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getOutlineShape2(state, world, pos, context);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorBeltOrientation orientation = getOrientation(world, pos, state);
        if (orientation == EnumEscalatorBeltOrientation.SLOPE_1 || orientation == EnumEscalatorBeltOrientation.TRANSITION_TOP) {
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

    protected final EnumEscalatorBeltOrientation getOrientation(BlockView world, BlockPos pos, BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

//        final BlockPos posAhead = pos.offset(facing);
//        final BlockPos posBehind = pos.offset(facing, -1);

//        final boolean isAhead = state.isOf(world.getBlockState(posAhead).getBlock());
//        final boolean isAheadUp = state.isOf(world.getBlockState(posAhead.up()).getBlock());
//
//        final boolean isBehind = state.isOf(world.getBlockState(posBehind).getBlock());
//        final boolean isBehindDown = state.isOf(world.getBlockState(posBehind.down()).getBlock());

        final boolean isBehindDown1 = state.isOf(world.getBlockState(pos.offset(facing, -1).down()).getBlock());
        final boolean isBehindDown2 = state.isOf(world.getBlockState(pos.offset(facing, -2).down()).getBlock());
        final boolean isBehindDown3 = state.isOf(world.getBlockState(pos.offset(facing, -3).down()).getBlock());
        final boolean isBehindDown4 = state.isOf(world.getBlockState(pos.offset(facing, -4).down()).getBlock());
//        final boolean isBehindDown5 = state.isOf(world.getBlockState(pos.offset(facing, -5).down()).getBlock());
//        final boolean isBehindDownEnd = state.isOf(world.getBlockState(pos.offset(facing, -5).down().down()).getBlock());

        final boolean isBehind1 = state.isOf(world.getBlockState(pos.offset(facing, -1)).getBlock());
        final boolean isBehind2 = state.isOf(world.getBlockState(pos.offset(facing, -2)).getBlock());
        final boolean isBehind3 = state.isOf(world.getBlockState(pos.offset(facing, -3)).getBlock());
        final boolean isBehind4 = state.isOf(world.getBlockState(pos.offset(facing, -4)).getBlock());

        final boolean isBehindUp1 = state.isOf(world.getBlockState(pos.offset(facing, -1).up()).getBlock());
//        final boolean isBehindUp2 = state.isOf(world.getBlockState(pos.offset(facing, -2).up()).getBlock());
//        final boolean isBehindUp3 = state.isOf(world.getBlockState(pos.offset(facing, -3).up()).getBlock());
//        final boolean isBehindUp4 = state.isOf(world.getBlockState(pos.offset(facing, -4).up()).getBlock());

        final boolean isForwardDown1 = state.isOf(world.getBlockState(pos.offset(facing, 1).down()).getBlock());
//        final boolean isForwardDown2 = state.isOf(world.getBlockState(pos.offset(facing, 2).down()).getBlock());
//        final boolean isForwardDown3 = state.isOf(world.getBlockState(pos.offset(facing, 3).down()).getBlock());
//        final boolean isForwardDown4 = state.isOf(world.getBlockState(pos.offset(facing, 4).down()).getBlock());

        final boolean isForward1 = state.isOf(world.getBlockState(pos.offset(facing, 1)).getBlock());
        final boolean isForward2 = state.isOf(world.getBlockState(pos.offset(facing, 2)).getBlock());
        final boolean isForward3 = state.isOf(world.getBlockState(pos.offset(facing, 3)).getBlock());
//        final boolean isForward4 = state.isOf(world.getBlockState(pos.offset(facing, 4)).getBlock());

        final boolean isForwardUp1 = state.isOf(world.getBlockState(pos.offset(facing, 1).up()).getBlock());
        final boolean isForwardUp2 = state.isOf(world.getBlockState(pos.offset(facing, 2).up()).getBlock());
        final boolean isForwardUp3 = state.isOf(world.getBlockState(pos.offset(facing, 3).up()).getBlock());
        final boolean isForwardUp4 = state.isOf(world.getBlockState(pos.offset(facing, 4).up()).getBlock());
//        final boolean isForwardUpEnd = state.isOf(world.getBlockState(pos.offset(facing, 5).up()).getBlock());

        if (!isForward1 && !isForwardDown1 && !isForwardUp1 && (isBehind1 || isBehindDown1)) {
            return EnumEscalatorBeltOrientation.LANDING_TOP;
        } else if (!isBehind1 && !isBehindDown1 && !isBehindUp1 && (isForward1 || isForwardUp1)) {
            return EnumEscalatorBeltOrientation.LANDING_BOTTOM;
        } else if (isForward1 && isForward2 && isForward3 && isBehindDown1) {
            return EnumEscalatorBeltOrientation.SLOPE_1;
        } else if (isBehindDown2 && isBehind1 && isForward1 && isForward2) {
            return EnumEscalatorBeltOrientation.SLOPE_2;
        } else if (!isBehind3 && isBehind1 && isBehind2 && isForward1 && isBehindDown3) {
            return EnumEscalatorBeltOrientation.SLOPE_3;
        } else if (!isBehind4 && isBehind3 && isBehind2 && isBehind1 && isBehindDown4) {
            if (isForward1) return EnumEscalatorBeltOrientation.TRANSITION_TOP;
            return EnumEscalatorBeltOrientation.SLOPE_4;
        } else if (isBehind1 && isForwardUp1 && isForwardUp2 && isForwardUp3 && isForwardUp4) {
            return EnumEscalatorBeltOrientation.TRANSITION_BOTTOM;
        }
        return EnumEscalatorBeltOrientation.FLAT;
    }

    private Direction getSideDirection(BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
    }

    public enum EnumEscalatorBeltOrientation implements StringIdentifiable {
        LANDING_BOTTOM("landing_bottom"), LANDING_TOP("landing_top"), FLAT("flat"), SLOPE_1("slope_1"), SLOPE_2("slope_2"), SLOPE_3("slope_3"), SLOPE_4("slope_4"), TRANSITION_BOTTOM("transition_bottom"), TRANSITION_TOP("transition_top");
        private final String name;

        EnumEscalatorBeltOrientation(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }

    public enum EnumEscalatorBeltStepDirection implements StringIdentifiable {
        STOP("stop"), FORWARD("forward"), BACKWARD("backward");
        private final String name;

        EnumEscalatorBeltStepDirection(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }
}
