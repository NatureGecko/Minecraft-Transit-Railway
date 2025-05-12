package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorBeltStep extends BlockEscalatorBelt {
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

        if(orientation == EnumEscalatorBeltOrientation.SLOPE_1){
            return VoxelShapes.combine(Block.createCuboidShape(0, -8, 0, 16, 3.95, 16),  super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        }else if(orientation == EnumEscalatorBeltOrientation.SLOPE_2){
            return VoxelShapes.combine(Block.createCuboidShape(0, -6, 0, 16, 7.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        }else if(orientation == EnumEscalatorBeltOrientation.SLOPE_3){
            return VoxelShapes.combine(Block.createCuboidShape(0, -4, 0, 16, 11.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        }else if(orientation == EnumEscalatorBeltOrientation.SLOPE_4){
            return VoxelShapes.combine(Block.createCuboidShape(0, -2, 0, 16, 15.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        }


        if (orientation == EnumEscalatorBeltOrientation.FLAT || orientation == EnumEscalatorBeltOrientation.TRANSITION_TOP) {
            return Block.createCuboidShape(0, 0, 0, 16, 15, 16);
        } else {
            return VoxelShapes.combine(Block.createCuboidShape(0, 0, 0, 16, 15.95, 16), super.getCollisionShape2(state, world, pos, context), BooleanBiFunction.getAndMapped());
        }
    }

    @Override
    public void onEntityCollision2(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision2(state, world, pos, entity);
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final EnumEscalatorBeltOrientation orientation = IBlock.getStatePropertySafe(state, ORIENTATION);
        final EnumEscalatorBeltStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
        final float speed = 0.1F;

        if (orientation == EnumEscalatorBeltOrientation.LANDING_BOTTOM || orientation == EnumEscalatorBeltOrientation.LANDING_TOP) {
            return;
        }

        if (IBlock.getStatePropertySafe(state, STEP_DIRECTION) != EnumEscalatorBeltStepDirection.STOP) {
            switch (facing) {
                case NORTH:
                    entity.addVelocity(0, 0, direction == EnumEscalatorBeltStepDirection.FORWARD ? -speed : speed);
                    break;
                case EAST:
                    entity.addVelocity(direction == EnumEscalatorBeltStepDirection.FORWARD ? speed : -speed, 0, 0);
                    break;
                case SOUTH:
                    entity.addVelocity(0, 0, direction == EnumEscalatorBeltStepDirection.FORWARD ? speed : -speed);
                    break;
                case WEST:
                    entity.addVelocity(direction == EnumEscalatorBeltStepDirection.FORWARD ? -speed : speed, 0, 0);
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
            final EnumEscalatorBeltStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
            final Direction blockFacing = IBlock.getStatePropertySafe(state, FACING);

            final EnumEscalatorBeltStepDirection newDirection = switch (direction) {
                case STOP -> EnumEscalatorBeltStepDirection.FORWARD;
                case FORWARD -> EnumEscalatorBeltStepDirection.BACKWARD;
                default -> EnumEscalatorBeltStepDirection.STOP;
            };

            update(world, pos, blockFacing, newDirection);
            update(world, pos, blockFacing.getOpposite(), newDirection);

            final BlockPos sidePos = pos.offset(IBlock.getSideDirection(state));
            if (isStep(world, sidePos)) {
                final BlockEscalatorBeltStep block = (BlockEscalatorBeltStep) world.getBlockState(sidePos).getBlock().data;
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

    private void update(World world, BlockPos pos, Direction offset, EnumEscalatorBeltStepDirection direction) {
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
        return block.data instanceof BlockEscalatorBeltStep;
    }

    private boolean isSide(World world, BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        return block.data instanceof BlockEscalatorBeltSide;
    }
}
