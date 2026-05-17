package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorNarrowStep extends BlockEscalatorNarrow {
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        final boolean isAboveMatched = world.getBlockState(pos.up()).getBlock().data instanceof BlockEscalatorWideSide;
        if (isAboveMatched) IBlock.onBreakCreative(world, player, pos.up());
        super.onBreak2(world, pos, state, player);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final EnumEscalatorStepDirection stepDirection = IBlock.getStatePropertySafe(state, new Property<>(STEP_DIRECTION.data));

        if (orientation == EnumEscalatorOrientation.FLAT || orientation == EnumEscalatorOrientation.TRANSITION_BOTTOM || orientation == EnumEscalatorOrientation.LANDING_BOTTOM || orientation == EnumEscalatorOrientation.LANDING_TOP) {
            return Block.createCuboidShape(0, -8, 0, 16, 15, 16);
        }

        if (stepDirection != EnumEscalatorStepDirection.STOP) {
            final VoxelShape shape1 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, -16, 14, 16, 1, 16, facing), IBlock.getVoxelShapeByDirection(0, -14, 12, 16, 3, 14, facing));
            final VoxelShape shape2 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, -12, 10, 16, 5, 12, facing), IBlock.getVoxelShapeByDirection(0, -10, 8, 16, 7, 10, facing));
            final VoxelShape shape3 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, -8, 6, 16, 9, 8, facing), IBlock.getVoxelShapeByDirection(0, -6, 4, 16, 11, 6, facing));
            final VoxelShape shape4 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, -4, 2, 16, 13, 4, facing), IBlock.getVoxelShapeByDirection(0, -2, 0, 16, 15, 2, facing));
            return VoxelShapes.union(VoxelShapes.union(shape1, shape2), VoxelShapes.union(shape3, shape4));
        }
        return VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, -8, 8, 16, 1, 16, facing), IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 9, 8, facing));

    }


    @Override
    public void onEntityCollision2(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision2(state, world, pos, entity);
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final EnumEscalatorOrientation orientation = IBlock.getStatePropertySafe(state, ORIENTATION);
        final EnumEscalatorStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
        final float speed = 0.024F;

        if (orientation == EnumEscalatorOrientation.LANDING_BOTTOM || orientation == EnumEscalatorOrientation.LANDING_TOP) {
            return;
        }

        if (IBlock.getStatePropertySafe(state, STEP_DIRECTION) != EnumEscalatorStepDirection.STOP) {
            switch (facing) {
                case NORTH:
                    entity.addVelocity(0, 0, direction == EnumEscalatorStepDirection.FORWARD ? -speed : speed);
                    break;
                case EAST:
                    entity.addVelocity(direction == EnumEscalatorStepDirection.FORWARD ? speed : -speed, 0, 0);
                    break;
                case SOUTH:
                    entity.addVelocity(0, 0, direction == EnumEscalatorStepDirection.FORWARD ? speed : -speed);
                    break;
                case WEST:
                    entity.addVelocity(direction == EnumEscalatorStepDirection.FORWARD ? -speed : speed, 0, 0);
                    break;
                default:
                    break;
            }
        }
    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            final EnumEscalatorStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
            final Direction blockFacing = IBlock.getStatePropertySafe(state, FACING);
            final EnumEscalatorStepDirection newDirection = switch (direction) {
                case STOP -> EnumEscalatorStepDirection.FORWARD;
                case FORWARD -> EnumEscalatorStepDirection.BACKWARD;
                default -> EnumEscalatorStepDirection.STOP;
            };
            update(world, pos, blockFacing.getOpposite(), newDirection);
            update(world, pos, blockFacing, newDirection);
        });
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(STEP_DIRECTION);
        properties.add(ORIENTATION);
    }

    private void update(World world, BlockPos pos, Direction offset, EnumEscalatorStepDirection direction) {
        world.setBlockState(pos, world.getBlockState(pos).with(new Property<>(STEP_DIRECTION.data), direction));
        final BlockPos offsetPos = pos.offset(offset);
        if (isStep(world, pos)) {
            if (isSide(world, pos.up())) update(world, pos.up(), offset, direction);
            if (isStep(world, offsetPos)) update(world, offsetPos, offset, direction);
            if (isStep(world, offsetPos.up())) update(world, offsetPos.up(), offset, direction);
            if (isStep(world, offsetPos.down())) update(world, offsetPos.down(), offset, direction);
        }
    }

    private boolean isStep(World world, BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        return block.data instanceof BlockEscalatorNarrowStep;
    }

    private boolean isSide(World world, BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        return block.data instanceof BlockEscalatorNarrowSide;
    }
}
