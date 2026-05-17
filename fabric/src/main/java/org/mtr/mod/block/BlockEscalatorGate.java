package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockEscalatorGate extends BlockExtension implements IBlock, DirectionHelper {

    public BlockEscalatorGate() {
        super(org.mtr.mod.Blocks.createDefaultBlockSettings(true).nonOpaque());
    }

    public static final EnumProperty<EnumEscalatorGateOrientation> ORIENTATION = EnumProperty.of("orientation", EnumEscalatorGateOrientation.class);
    public static final EnumProperty<EnumEscalatorGateStepDirection> STEP_DIRECTION = EnumProperty.of("step_direction", EnumEscalatorGateStepDirection.class);

    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final EnumEscalatorGateOrientation orientation = IBlock.getStatePropertySafe(state, ORIENTATION);
        final BlockState blockAhead = world.getBlockState(pos.offset(direction, 1));
        final boolean isEscalatorAhead = blockAhead.getBlock().data instanceof BlockEscalatorWideSide || blockAhead.getBlock().data instanceof BlockEscalatorBeltSide;
        if (orientation == EnumEscalatorGateOrientation.DOUBLE) {
            if (isEscalatorAhead) return Blocks.getAirMapped().getDefaultState();
        } else {
            Direction targetDirection = orientation == EnumEscalatorGateOrientation.RIGHT ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
            if (targetDirection == direction && !neighborState.isOf(new Block(this))) {
                return Blocks.getAirMapped().getDefaultState();
            }
            return state.with(new Property<>(ORIENTATION.data), IBlock.getStatePropertySafe(state, ORIENTATION)).with(new Property<>(STEP_DIRECTION.data), getDirection(BlockView.cast(world), pos, state));
        }
        return state.with(new Property<>(ORIENTATION.data), getOrientation(BlockView.cast(world), pos, state)).with(new Property<>(STEP_DIRECTION.data), getDirection(BlockView.cast(world), pos, state));
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable BlockView world, List<MutableText> tooltip, TooltipContext options) {
        tooltip.add(TranslationProvider.TOOLTIP_MTR_ESCALATOR_GATE_PLACEMENT_TIP.getMutableText().formatted(TextFormatting.GRAY));
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final World world = context.getWorld();
        final Direction direction = context.getPlayerFacing();
        final BlockPos pos = context.getBlockPos().offset(direction, 1);
        final BlockState blockAhead = world.getBlockState(pos);
        final boolean isEscalator = blockAhead.getBlock().data instanceof BlockEscalatorWideSide;
        final boolean isWalkway = blockAhead.getBlock().data instanceof BlockEscalatorBeltSide;
        if (isWalkway || isEscalator) {
            boolean isAheadInvalidSide = false;
            final EnumSide targetSide = IBlock.getStatePropertySafe(blockAhead, SIDE);
            if (isEscalator) {
                final BlockEscalator.EnumEscalatorOrientation targetOrientation = IBlock.getStatePropertySafe(blockAhead, BlockEscalatorWide.ORIENTATION);
                if (targetOrientation == BlockEscalator.EnumEscalatorOrientation.LANDING_BOTTOM && targetSide != EnumSide.LEFT) {
                    isAheadInvalidSide = true;
                }
                if (targetOrientation == BlockEscalator.EnumEscalatorOrientation.LANDING_TOP && targetSide != EnumSide.RIGHT) {
                    isAheadInvalidSide = true;
                }
            }
            if (isWalkway) {
                final BlockEscalatorBelt.EnumEscalatorBeltOrientation targetOrientation = IBlock.getStatePropertySafe(blockAhead, BlockEscalatorBelt.ORIENTATION);
                if (targetOrientation == BlockEscalatorBelt.EnumEscalatorBeltOrientation.LANDING_BOTTOM && targetSide != EnumSide.LEFT) {
                    isAheadInvalidSide = true;
                }
                if (targetOrientation == BlockEscalatorBelt.EnumEscalatorBeltOrientation.LANDING_TOP && targetSide != EnumSide.RIGHT) {
                    isAheadInvalidSide = true;
                }
            }
            if (isAheadInvalidSide) {
                if (context.getPlayer() != null) {
                    context.getPlayer().sendMessage(TranslationProvider.TOOLTIP_MTR_ESCALATOR_GATE_PLACEMENT_WARN_AHEAD.getText(), true);
                }
                return null;
            }
            if (!IBlock.isReplaceable(context, direction.rotateYClockwise(), 2)) {
                if (context.getPlayer() != null)
                    context.getPlayer().sendMessage(TranslationProvider.TOOLTIP_MTR_ESCALATOR_GATE_PLACEMENT_WARN_SIDE.getText(), true);
                return null;
            }
        }
        return getDefaultState2().with(new Property<>(FACING.data), context.getPlayerFacing().data);
    }

    @Nonnull
    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape2(state, world, pos, context);
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            final Direction direction = IBlock.getStatePropertySafe(state, FACING);
            final boolean isAskingForTwoBlocksWide = world.getBlockState(pos.offset(direction, 1)).getBlock().data instanceof BlockEscalatorWideSide || world.getBlockState(pos.offset(direction, 1)).getBlock().data instanceof BlockEscalatorBeltSide;
            if (isAskingForTwoBlocksWide) {
                final BlockPos posSide;
                switch (direction) {
                    case NORTH -> posSide = pos.east();
                    case EAST -> posSide = pos.south();
                    case SOUTH -> posSide = pos.west();
                    default -> posSide = pos.north();
                }
                world.setBlockState(posSide, state.with(new Property<>(ORIENTATION.data), EnumEscalatorGateOrientation.RIGHT));
            }
        }
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT) {
            IBlock.onBreakCreative(world, player, pos.offset(IBlock.getSideDirection(state)));
        }
        super.onBreak2(world, pos, state, player);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorGateOrientation orientation = IBlock.getStatePropertySafe(state, ORIENTATION);
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        switch (orientation) {
            case LEFT -> {
                return IBlock.getVoxelShapeByDirection(0, 0, 0, 2.5, 15, 15, facing);
            }
            case RIGHT -> {
                return IBlock.getVoxelShapeByDirection(13.5, 0, 0, 16, 15, 15, facing);
            }
        }
        return VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, 0, 0, 1.7, 15, 15, facing), IBlock.getVoxelShapeByDirection(14.7, 0, 0, 16, 15, 15, facing));


    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(ORIENTATION);
        properties.add(STEP_DIRECTION);
        properties.add(FACING);
    }

    private EnumEscalatorGateOrientation getOrientation(BlockView world, BlockPos pos, BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final BlockState blockAhead = world.getBlockState(pos.offset(facing, 1));
        final boolean isWideEscalator = blockAhead.getBlock().data instanceof BlockEscalatorWideSide;
        final boolean isWalkway = blockAhead.getBlock().data instanceof BlockEscalatorBeltSide;

        // Handle Wide Escalator & Walkway
        if (isWideEscalator || isWalkway) {
            boolean isReversed = false;
            if (isWideEscalator) {
                final BlockEscalator.EnumEscalatorOrientation targetOrientation = IBlock.getStatePropertySafe(blockAhead, BlockEscalator.ORIENTATION);
                if (targetOrientation == BlockEscalator.EnumEscalatorOrientation.LANDING_TOP) isReversed = true;
            }
            if (isWalkway) {
                final BlockEscalatorBelt.EnumEscalatorBeltOrientation targetOrientation = IBlock.getStatePropertySafe(blockAhead, BlockEscalatorBelt.ORIENTATION);
                if (targetOrientation == BlockEscalatorBelt.EnumEscalatorBeltOrientation.LANDING_TOP) isReversed = true;
            }
            final EnumSide side = IBlock.getStatePropertySafe(blockAhead, SIDE);
            if (side == EnumSide.RIGHT) {
                return isReversed ? EnumEscalatorGateOrientation.LEFT : EnumEscalatorGateOrientation.RIGHT;
            }
            return isReversed ? EnumEscalatorGateOrientation.RIGHT : EnumEscalatorGateOrientation.LEFT;
        }
        return EnumEscalatorGateOrientation.DOUBLE;
    }

    private EnumEscalatorGateStepDirection getDirection(BlockView world, BlockPos pos, BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

        final BlockState blockAhead = world.getBlockState(pos.offset(facing, 1));
        final boolean isEscalator = blockAhead.getBlock().data instanceof BlockEscalator;
        final boolean isWalkway = blockAhead.getBlock().data instanceof BlockEscalatorBelt;

        // Handle Escalator & Walkway
        if (isEscalator) {
            final BlockEscalator.EnumEscalatorOrientation targetOrientation = IBlock.getStatePropertySafe(blockAhead, BlockEscalator.ORIENTATION);
            final BlockEscalator.EnumEscalatorStepDirection targetDirection = IBlock.getStatePropertySafe(blockAhead, BlockEscalator.STEP_DIRECTION);
            final boolean isReversed = targetOrientation == BlockEscalator.EnumEscalatorOrientation.LANDING_TOP;
            switch (targetDirection) {
                case BACKWARD -> {
                    if (isReversed) return EnumEscalatorGateStepDirection.FORWARD;
                    return EnumEscalatorGateStepDirection.BACKWARD;
                }
                case FORWARD -> {
                    if (isReversed) return EnumEscalatorGateStepDirection.BACKWARD;
                    return EnumEscalatorGateStepDirection.FORWARD;
                }
            }
        } else if (isWalkway) {
            final BlockEscalatorBelt.EnumEscalatorBeltOrientation targetOrientation = IBlock.getStatePropertySafe(blockAhead, BlockEscalatorBelt.ORIENTATION);
            final BlockEscalatorBelt.EnumEscalatorBeltStepDirection targetDirection = IBlock.getStatePropertySafe(blockAhead, BlockEscalatorBelt.STEP_DIRECTION);
            final boolean isReversed = targetOrientation == BlockEscalatorBelt.EnumEscalatorBeltOrientation.LANDING_TOP;
            switch (targetDirection) {
                case BACKWARD -> {
                    if (isReversed) return EnumEscalatorGateStepDirection.FORWARD;
                    return EnumEscalatorGateStepDirection.BACKWARD;
                }
                case FORWARD -> {
                    if (isReversed) return EnumEscalatorGateStepDirection.BACKWARD;
                    return EnumEscalatorGateStepDirection.FORWARD;
                }
            }
        }

        // Handle Nothing
        return EnumEscalatorGateStepDirection.STOP;
    }

    public enum EnumEscalatorGateOrientation implements StringIdentifiable {
        DOUBLE("double"), LEFT("left"), RIGHT("right");
        private final String name;

        EnumEscalatorGateOrientation(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }

    public enum EnumEscalatorGateStepDirection implements StringIdentifiable {
        STOP("stop"), FORWARD("forward"), BACKWARD("backward");
        private final String name;

        EnumEscalatorGateStepDirection(String nameIn) {
            name = nameIn;
        }

        @Nonnull
        @Override
        public String asString2() {
            return name;
        }
    }
}
