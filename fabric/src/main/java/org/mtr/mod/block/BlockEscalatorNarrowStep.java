package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorNarrowStep extends BlockEscalatorNarrowBase {
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
        final EnumEscalatorNarrowOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));
        if (orientation == EnumEscalatorNarrowOrientation.FLAT || orientation == EnumEscalatorNarrowOrientation.TRANSITION_BOTTOM) {
            return Block.createCuboidShape(0, 0, 0, 16, 15, 16);
        } else {
            return VoxelShapes.combine(Block.createCuboidShape(1, 0, 1, 15, 16, 15), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        }
    }

    @Override
    public void onEntityCollision2(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision2(state, world, pos, entity);
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final EnumEscalatorNarrowStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
        final float speed = 0.1F;

        if (IBlock.getStatePropertySafe(state, STEP_DIRECTION) != EnumEscalatorNarrowStepDirection.STOP) {
            switch (facing) {
                case NORTH:
                    entity.addVelocity(0, 0, direction == EnumEscalatorNarrowStepDirection.FORWARD ? -speed : speed);
                    break;
                case EAST:
                    entity.addVelocity(direction == EnumEscalatorNarrowStepDirection.FORWARD ? speed : -speed, 0, 0);
                    break;
                case SOUTH:
                    entity.addVelocity(0, 0, direction == EnumEscalatorNarrowStepDirection.FORWARD ? speed : -speed);
                    break;
                case WEST:
                    entity.addVelocity(direction == EnumEscalatorNarrowStepDirection.FORWARD ? -speed : speed, 0, 0);
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
            final EnumEscalatorNarrowStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
            final Direction blockFacing = IBlock.getStatePropertySafe(state, FACING);
            final EnumEscalatorNarrowStepDirection newDirection = switch (direction) {
                case STOP -> EnumEscalatorNarrowStepDirection.FORWARD;
                case FORWARD -> EnumEscalatorNarrowStepDirection.BACKWARD;
                default -> EnumEscalatorNarrowStepDirection.STOP;
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

    private void update(World world, BlockPos pos, Direction offset, EnumEscalatorNarrowStepDirection direction) {
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
