package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockEscalatorBeltStep extends BlockEscalatorBelt {
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable BlockView world, List<MutableText> tooltip, TooltipContext options) {
        final String[] textStrings = TranslationProvider.TOOLTIP_MTR_ESCALATOR_BELT_PLACEMENT_TIP.getString().split("\n");
        for (final String text : textStrings) {
            tooltip.add(TextHelper.literal(text).formatted(TextFormatting.GRAY));
        }
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
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

        double heightBase = 0;
        double lowBase = 0;

        switch (orientation) {
            case FLAT, TRANSITION_BOTTOM:
                return Block.createCuboidShape(0, 0, 0, 16, 15.95, 16);
            case SLOPE_1:
                heightBase = 0.95;
                lowBase = -8;
                break;
            case SLOPE_2:
                heightBase = 4.95;
                lowBase = -6;
                break;
            case SLOPE_3:
                heightBase = 8.95;
                lowBase = -4;
                break;
            case SLOPE_4, TRANSITION_TOP:
                heightBase = 12.95;
                lowBase = -2;
                break;
            default:
                return Block.createCuboidShape(0, 0, 0, 16, 16, 16);
        }

        final VoxelShape shape1 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, lowBase, 0, 16, heightBase, 16, facing), IBlock.getVoxelShapeByDirection(0, lowBase, 0, 16, heightBase + 1, 12, facing));
        final VoxelShape shape2 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, lowBase, 0, 16, heightBase + 2, 8, facing), IBlock.getVoxelShapeByDirection(0, lowBase, 0, 16, heightBase + 3, 4, facing));
        return VoxelShapes.union(shape1, shape2);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorBeltOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

        double heightBase = 0;
        double lowBase = 0;


        switch (orientation) {
            case TRANSITION_BOTTOM:
                final VoxelShape shape1 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, 16, 4, 16, 17, 8, facing), IBlock.getVoxelShapeByDirection(0, 16, 0, 16, 18, 4, facing));
                final VoxelShape shape2 = IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 16, 16, facing);
                return VoxelShapes.union(shape1, shape2);
            case SLOPE_1:
                heightBase = 3;
                lowBase = -8;
                break;
            case SLOPE_2:
                heightBase = 7;
                lowBase = -6;
                break;
            case SLOPE_3:
                heightBase = 11;
                lowBase = -4;
                break;
            case SLOPE_4:
                heightBase = 15;
                lowBase = -2;
                break;
            case TRANSITION_TOP:
                return VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, 0, 12, 16, 15, 16, facing), IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 16, 12, facing));
            default:
                return Block.createCuboidShape(0, 0, 0, 16, 16, 16);
        }

        final VoxelShape shape1 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, lowBase, 0, 16, heightBase, 16, facing), IBlock.getVoxelShapeByDirection(0, lowBase, 0, 16, heightBase + 1, 12, facing));
        final VoxelShape shape2 = VoxelShapes.union(IBlock.getVoxelShapeByDirection(0, lowBase, 0, 16, heightBase + 2, 8, facing), IBlock.getVoxelShapeByDirection(0, lowBase, 0, 16, heightBase + 3, 4, facing));
        return VoxelShapes.union(shape1, shape2);
    }

    @Override
    public void onEntityCollision2(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision2(state, world, pos, entity);
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final EnumEscalatorBeltOrientation orientation = IBlock.getStatePropertySafe(state, ORIENTATION);
        final EnumEscalatorBeltStepDirection direction = IBlock.getStatePropertySafe(state, STEP_DIRECTION);
        final float speed = 0.024F;

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
