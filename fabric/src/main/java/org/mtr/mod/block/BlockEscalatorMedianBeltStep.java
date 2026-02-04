package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorMedianBeltStep extends BlockEscalatorMedianBelt {
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        final boolean isAboveMatched = world.getBlockState(pos.up()).getBlock().data instanceof BlockEscalatorWideSide;
        if (IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT) {
            IBlock.onBreakCreative(world, player, pos.offset(IBlock.getSideDirection(state)));
        }
        if (isAboveMatched) IBlock.onBreakCreative(world, player, pos.up());
        super.onBreak2(world, pos, state, player);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorBeltOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));

        if (orientation == EnumEscalatorBeltOrientation.SLOPE_1) {
            return VoxelShapes.combine(Block.createCuboidShape(0, -8, 0, 16, 3.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        } else if (orientation == EnumEscalatorBeltOrientation.SLOPE_2) {
            return VoxelShapes.combine(Block.createCuboidShape(0, -6, 0, 16, 7.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        } else if (orientation == EnumEscalatorBeltOrientation.SLOPE_3) {
            return VoxelShapes.combine(Block.createCuboidShape(0, -4, 0, 16, 11.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        } else if (orientation == EnumEscalatorBeltOrientation.SLOPE_4) {
            return VoxelShapes.combine(Block.createCuboidShape(0, -2, 0, 16, 15.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        }


        if (orientation == EnumEscalatorBeltOrientation.FLAT || orientation == EnumEscalatorBeltOrientation.TRANSITION_TOP) {
            return Block.createCuboidShape(0, 0, 0, 16, 15, 16);
        } else {
            return VoxelShapes.combine(Block.createCuboidShape(0, 0, 0, 16, 15.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        }
    }


//    @Nonnull
//    @Override
//    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        return IBlock.checkHoldingBrush(world, player, () -> {
//            final BlockEscalatorBelt.EnumEscalatorBeltStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
//            final Direction blockFacing = IBlock.getStatePropertySafe(state, FACING);
//
//            final BlockEscalatorBelt.EnumEscalatorBeltStepDirection newDirection = switch (direction) {
//                case STOP -> BlockEscalatorBelt.EnumEscalatorBeltStepDirection.FORWARD;
//                case FORWARD -> BlockEscalatorBelt.EnumEscalatorBeltStepDirection.BACKWARD;
//                default -> BlockEscalatorBelt.EnumEscalatorBeltStepDirection.STOP;
//            };
//
//            update(world, pos, blockFacing, newDirection);
//            update(world, pos, blockFacing.getOpposite(), newDirection);
//
//            final BlockPos sidePos = pos.offset(IBlock.getSideDirection(state));
//            if (isStep(world, sidePos)) {
//                final BlockEscalatorBeltStep block = (BlockEscalatorBeltStep) world.getBlockState(sidePos).getBlock().data;
//                block.update(world, sidePos, blockFacing, newDirection);
//                block.update(world, sidePos, blockFacing.getOpposite(), newDirection);
//            }
//        });
//    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(ORIENTATION);
    }


//    private boolean isStep(World world, BlockPos pos) {
//        final Block block = world.getBlockState(pos).getBlock();
//        return block.data instanceof BlockEscalatorBeltStep;
//    }


}
