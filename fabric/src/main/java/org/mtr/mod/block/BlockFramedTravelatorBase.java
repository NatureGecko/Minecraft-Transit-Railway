package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.BlockHelper;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mod.Items;

import javax.annotation.Nonnull;

public abstract class BlockFramedTravelatorBase extends BlockExtension implements IBlock, DirectionHelper {

    public static final EnumProperty<EnumTravelatorOrientation> ORIENTATION = EnumProperty.of("orientation",
            EnumTravelatorOrientation.class);

    // * BlockFramedTravelatorBase
    public BlockFramedTravelatorBase(BlockSettings blockSettings) {
        super(BlockHelper.createBlockSettings(true).nonOpaque());
    }

    // * getStateForNeighborUpdate2
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (getSideDirection(state) == direction && !neighborState.isOf(new Block(this))) {
            return Blocks.getAirMapped().getDefaultState();
        } else {
            return state.with(new Property<>(ORIENTATION.data), getOrientation(BlockView.cast(world), pos, state));
        }
    }

    // * getOutlineShape2
    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getOutlineShape2(state, world, pos, context);
    }

    // * getCollisionShape2
    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumTravelatorOrientation orientation = getOrientation(world, pos, state);

        if (orientation == EnumTravelatorOrientation.SLOPE_1 || orientation == EnumTravelatorOrientation.SLOPE_2
                || orientation == EnumTravelatorOrientation.SLOPE_3 || orientation == EnumTravelatorOrientation.SLOPE_4
                || orientation == EnumTravelatorOrientation.TRANSITION_TOP) {
            return VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 8, 16),
                    IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 15, 8, IBlock.getStatePropertySafe(state, FACING)));
        } else {
            return VoxelShapes.fullCube();
        }
    }

    // * asItem2
    // @Nonnull
    // @Override
    // public Item asItem2() {
    //     // return Items.FRAMED_TRAVELATOR.get();
    // }

    // * getPickStack2
    @Nonnull
    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(new ItemConvertible(asItem2().data));
    }

    // * getSideDirection
    public Direction getSideDirection(BlockState state) {
        final Direction facingTo = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT ? facingTo.rotateYCounterclockwise()
                : facingTo.rotateYClockwise();
    }

    // * getOrientation
    protected final EnumTravelatorOrientation getOrientation(BlockView world, BlockPos blockPos,
            BlockState blockState) {
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

        // Position
        final BlockPos posAdead = blockPos.offset(facing);
        final BlockPos posAdead1 = blockPos.offset(facing, 1);
        final BlockPos posAdead2 = blockPos.offset(facing, 2);
        final BlockPos posAdead3 = blockPos.offset(facing, 3);

        final BlockPos posBehind1 = blockPos.offset(facing, -1);
        final BlockPos posBehind2 = blockPos.offset(facing, -2);
        final BlockPos posBehind3 = blockPos.offset(facing, -3);
        final BlockPos posBehind4 = blockPos.offset(facing, -4);

        // Same Y Level
        final boolean isAhead = blockState.isOf(world.getBlockState(posAdead).getBlock());
        final boolean isAhead1 = blockState.isOf(world.getBlockState(posAdead1).getBlock());
        final boolean isAhead2 = blockState.isOf(world.getBlockState(posAdead2).getBlock());
        final boolean isBehind1 = blockState.isOf(world.getBlockState(posBehind1).getBlock());
        final boolean isBehind2 = blockState.isOf(world.getBlockState(posBehind2).getBlock());
        final boolean isBehind3 = blockState.isOf(world.getBlockState(posBehind3).getBlock());

        // Upper Y Level
        final boolean isAheadUpper = blockState.isOf(world.getBlockState(posAdead.up()).getBlock());
        final boolean isAheadUpper1 = blockState.isOf(world.getBlockState(posAdead1.up()).getBlock());
        final boolean isAheadUpper2 = blockState.isOf(world.getBlockState(posAdead2.up()).getBlock());
        final boolean isAheadUpper3 = blockState.isOf(world.getBlockState(posAdead3.up()).getBlock());
        final boolean isBehindUpper1 = blockState.isOf(world.getBlockState(posBehind1.up()).getBlock());

        // Lower Y Level
        final boolean isAheadLower = blockState.isOf(world.getBlockState(posAdead.down()).getBlock());
        final boolean isBehindLower1 = blockState.isOf(world.getBlockState(posBehind1.down()).getBlock());
        final boolean isBehindLower2 = blockState.isOf(world.getBlockState(posBehind2.down()).getBlock());
        final boolean isBehindLower3 = blockState.isOf(world.getBlockState(posBehind3.down()).getBlock());
        final boolean isBehindLower4 = blockState.isOf(world.getBlockState(posBehind4.down()).getBlock());

        // SLOPE_1
        if (isAhead && isAhead1 && isAhead2 && isAheadUpper3 && isBehindLower1) {
            return EnumTravelatorOrientation.SLOPE_1;
        }
        // TRANSITION_TOP
        if (isAhead && isBehindLower1 && !isAheadUpper3) {
            return EnumTravelatorOrientation.TRANSITION_TOP;
        }
        // SLOPE_2
        if (isAhead && isAhead1 && isAheadUpper2 && isBehind1 && isBehindLower2) {
            return EnumTravelatorOrientation.SLOPE_2;
        }
        // SLOPE_3
        if (isAhead && isAheadUpper1 && isBehind1 && isBehind2 && isBehindLower3) {
            return EnumTravelatorOrientation.SLOPE_3;
        }
        // SLOPE_4
        if (isAheadUpper && isBehind1 && isBehind2 && isBehind3 && isBehindLower4) {
            return EnumTravelatorOrientation.SLOPE_4;
        }
        // TRANSITION_BOTTOM
        if (!isBehind1 && !isBehindUpper1 && !isBehindLower1) {
            return EnumTravelatorOrientation.TRANSITION_BOTTOM;
        }
        // Landing Top
        if (!isAhead && !isAheadLower && !isAheadUpper) {
            return EnumTravelatorOrientation.LANDING_TOP;
        }
        // Landing Uppper
        return EnumTravelatorOrientation.FLAT;
    }

    // # EnumTravelatorOrientation
    public enum EnumTravelatorOrientation implements StringIdentifiable {
        LANDING_BOTTOM("landing_bottom"), LANDING_TOP("landing_top"), TRANSITION_TOP("transition_top"),
        TRANSITION_BOTTOM("transition_bottom"), FLAT("flat"), SLOPE_1("slope_1"),
        SLOPE_2("slope_2"), SLOPE_3("slope_3"), SLOPE_4("slope_4");

        private final String name;

        EnumTravelatorOrientation(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }
}
