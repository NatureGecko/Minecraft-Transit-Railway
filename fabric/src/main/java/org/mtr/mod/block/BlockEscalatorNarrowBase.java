package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.BlockHelper;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mod.Items;

import javax.annotation.Nonnull;

public abstract class BlockEscalatorNarrowBase extends BlockExtension implements IBlock, DirectionHelper {

    public static final EnumProperty<EnumFramedEscalatorNarrowHeading> HEADING = EnumProperty.of("heading",
            EnumFramedEscalatorNarrowHeading.class);
    public static final EnumProperty<EnumFramedEscalatorNarrowOrientation> ORIENTATION = EnumProperty.of("orientation",
            EnumFramedEscalatorNarrowOrientation.class);

    protected BlockEscalatorNarrowBase() {
        super(BlockHelper.createBlockSettings(true).nonOpaque());
    }

    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (IBlock.getStatePropertySafe(state, FACING) == direction && !neighborState.isOf(new Block(this))) {
            return Blocks.getAirMapped().getDefaultState();
        } else {
            BlockState block = state.with(new Property<>(ORIENTATION.data),
                    getOrientation(BlockView.cast(world), pos, state));
            return block;
        }
    }

    // getOutlineShape2
    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumFramedEscalatorNarrowOrientation orientation = getOrientation(world, pos, state);
        if (orientation == EnumFramedEscalatorNarrowOrientation.SLOPE
                || orientation == EnumFramedEscalatorNarrowOrientation.TRANSITION_TOP) {
            return VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 8, 16),
                    IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 15, 8, IBlock.getStatePropertySafe(state, FACING)));
        } else {
            return VoxelShapes.fullCube();
        }
    }

    // getCollisionShape2
    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumFramedEscalatorNarrowOrientation orientation = getOrientation(world, pos, state);
        if (orientation == EnumFramedEscalatorNarrowOrientation.SLOPE
                || orientation == EnumFramedEscalatorNarrowOrientation.TRANSITION_TOP) {
            return VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 8, 16),
                    IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 15, 8, IBlock.getStatePropertySafe(state, FACING)));
        } else {
            return VoxelShapes.fullCube();
        }
    }

    @Nonnull
    @Override
    public Item asItem2() {
        return Items.ESCALATOR.get();
    }

    @Nonnull
    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(new ItemConvertible(asItem2().data));
    }

    // =----=----=----=----=----=----=----=----=----=----=----=----=----=----=----=----=
    // Custom Private Functions
    protected final EnumFramedEscalatorNarrowOrientation getOrientation(BlockView world, BlockPos pos,
            BlockState state) {
        final Direction facintTo = IBlock.getStatePropertySafe(state, FACING);

        final boolean isAhead = state.isOf(world.getBlockState(pos.offset(facintTo)).getBlock());
        final boolean isAheadUp = state.isOf(world.getBlockState(pos.offset(facintTo).up()).getBlock());
        final boolean isBehind = state.isOf(world.getBlockState(pos.offset(facintTo, -1)).getBlock());
        final boolean isBehindDown = state.isOf(world.getBlockState(pos.offset(facintTo, -1).down()).getBlock());

        if (isAhead && isBehind) {
            return EnumFramedEscalatorNarrowOrientation.FLAT;
        } else if (isAheadUp && isBehindDown) {
            return EnumFramedEscalatorNarrowOrientation.SLOPE;
        } else if (isAheadUp && isBehind) {
            return EnumFramedEscalatorNarrowOrientation.TRANSITION_BOTTOM;
        } else if (isAhead && isBehindDown) {
            return EnumFramedEscalatorNarrowOrientation.TRANSITION_TOP;
        } else if (isBehind) {
            return EnumFramedEscalatorNarrowOrientation.LANDING_TOP;
        }
        return EnumFramedEscalatorNarrowOrientation.LANDING_BOTTOM;
    }

    // =----=----=----=----=----=----=----=----=----=----=----=----=----=----=----=----=
    // Custom State

    // * EnumFramedEscalatorNarrowOrientation
    public enum EnumFramedEscalatorNarrowOrientation implements StringIdentifiable {
        LANDING_BOTTOM("landing_bottom"), LANDING_TOP("landing_top"), FLAT("flat"), SLOPE("slope"),
        TRANSITION_BOTTOM("transition_bottom"), TRANSITION_TOP("transition_top");

        private final String name;

        EnumFramedEscalatorNarrowOrientation(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }

    public enum EnumFramedEscalatorNarrowHeading implements StringIdentifiable {
        STOP("stop"), FORWARD("forward"), BACKWARD("backward");

        private final String name;

        EnumFramedEscalatorNarrowHeading(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }

}
