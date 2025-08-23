package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEscalatorMedianStairStep extends BlockEscalatorMedianStair {
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        final boolean isAboveMatched = world.getBlockState(pos.up()).getBlock().data instanceof BlockEscalatorMedianStairSide;
        if (isAboveMatched) IBlock.onBreakCreative(world, player, pos.up());
        super.onBreak2(world, pos, state, player);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final EnumEscalatorOrientation orientation = IBlock.getStatePropertySafe(state, new Property<>(ORIENTATION.data));
        if (orientation == EnumEscalatorOrientation.FLAT || orientation == EnumEscalatorOrientation.LANDING_BOTTOM || orientation == EnumEscalatorOrientation.LANDING_TOP) {
            return Block.createCuboidShape(0, 0, 0, 16, 15, 16);
        }
        return VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 8, 16), IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 15, 8, IBlock.getStatePropertySafe(state, FACING)));
    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            final Direction blockFacing = IBlock.getStatePropertySafe(state, FACING);
            update(world, pos, blockFacing.getOpposite());
            update(world, pos, blockFacing);
        });
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(ORIENTATION);
    }

    private void update(World world, BlockPos pos, Direction offset) {
        final BlockPos offsetPos = pos.offset(offset);
        if (isStep(world, pos)) {
            if (isSide(world, pos.up())) update(world, pos.up(), offset);
            if (isStep(world, offsetPos)) update(world, offsetPos, offset);
            if (isStep(world, offsetPos.up())) update(world, offsetPos.up(), offset);
            if (isStep(world, offsetPos.down())) update(world, offsetPos.down(), offset);
        }
    }

    private boolean isStep(World world, BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        return block.data instanceof BlockEscalatorMedianStairStep;
    }

    private boolean isSide(World world, BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        return block.data instanceof BlockEscalatorMedianStairSide;
    }


}
