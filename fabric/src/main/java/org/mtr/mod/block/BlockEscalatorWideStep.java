package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class BlockEscalatorWideStep extends BlockEscalatorWide {
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
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0, -8, 0, 16, 16, 16);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorWideOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));
        if (orientation == EnumEscalatorWideOrientation.FLAT || orientation == EnumEscalatorWideOrientation.TRANSITION_BOTTOM_1) {
            return Block.createCuboidShape(0, 0, 0, 16, 15, 16);
        }
        return VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 8, 16), IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 15, 8, IBlock.getStatePropertySafe(state, FACING)));
    }

    @Override
    public void onEntityCollision2(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision2(state, world, pos, entity);
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final EnumEscalatorWideOrientation orientation = IBlock.getStatePropertySafe(state, ORIENTATION);
        final EnumEscalatorWideStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
        final float speed = 0.1F;

        if (orientation == EnumEscalatorWideOrientation.LANDING_BOTTOM || orientation == EnumEscalatorWideOrientation.LANDING_TOP) {
            return;
        }

        if (IBlock.getStatePropertySafe(state, STEP_DIRECTION) != EnumEscalatorWideStepDirection.STOP) {
            switch (facing) {
                case NORTH:
                    entity.addVelocity(0, 0, direction == EnumEscalatorWideStepDirection.FORWARD ? -speed : speed);
                    break;
                case EAST:
                    entity.addVelocity(direction == EnumEscalatorWideStepDirection.FORWARD ? speed : -speed, 0, 0);
                    break;
                case SOUTH:
                    entity.addVelocity(0, 0, direction == EnumEscalatorWideStepDirection.FORWARD ? speed : -speed);
                    break;
                case WEST:
                    entity.addVelocity(direction == EnumEscalatorWideStepDirection.FORWARD ? -speed : speed, 0, 0);
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
            final EnumEscalatorWideStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
            final Direction blockFacing = IBlock.getStatePropertySafe(state, FACING);

            final EnumEscalatorWideStepDirection newDirection = switch (direction) {
                case STOP -> EnumEscalatorWideStepDirection.FORWARD;
                case FORWARD -> EnumEscalatorWideStepDirection.BACKWARD;
                default -> EnumEscalatorWideStepDirection.STOP;
            };

            update(world, pos, blockFacing, newDirection);
            update(world, pos, blockFacing.getOpposite(), newDirection);

            final BlockPos sidePos = pos.offset(IBlock.getSideDirection(state));
            final BlockPos basePos = pos.offset(IBlock.getSideDirection(state));
            if (isStep(world, basePos)) {

            }
            if (isStep(world, sidePos)) {
                final BlockEscalatorWideStep block = (BlockEscalatorWideStep) world.getBlockState(sidePos).getBlock().data;
                block.update(world, sidePos, blockFacing, newDirection);
                block.update(world, sidePos, blockFacing.getOpposite(), newDirection);
            }
        });
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(STEP_DIRECTION);
        properties.add(ORIENTATION);
        properties.add(SIDE);
    }

    private void update(World world, BlockPos pos, Direction offset, EnumEscalatorWideStepDirection direction) {
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
        return block.data instanceof BlockEscalatorWideStep;
    }

    private boolean isSide(World world, BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        return block.data instanceof BlockEscalatorWideSide;
    }

    private boolean isBase(World world, BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        return block.data instanceof BlockEscalatorWideSide;
    }
}
