package org.mtr.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockEscalatorWide extends BlockEscalator {
    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (getSideDirection(state) == direction && !neighborState.isOf(new Block(this))) {
            return Blocks.getAirMapped().getDefaultState();
        }
        return state.with(new Property<>(ORIENTATION.data), getOrientation(BlockView.cast(world), pos, state));
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            final Direction direction = IBlock.getStatePropertySafe(state, FACING);
            final BlockPos posSide;
            switch (direction) {
                case NORTH -> posSide = pos.east();
                case EAST -> posSide = pos.south();
                case SOUTH -> posSide = pos.west();
                default -> posSide = pos.north();
            }
            world.setBlockState(posSide, state.with(new Property<>(SIDE.data), EnumSide.RIGHT));
        }
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final Direction direction = context.getPlayerFacing().rotateYClockwise();
        final boolean allowPlacement = IBlock.isReplaceable(context, direction, 2);
        if (allowPlacement) return getDefaultState2().with(new Property<>(FACING.data), context.getPlayerFacing().data);
        if (context.getPlayer() != null) {
            context.getPlayer().sendMessage(TranslationProvider.TOOLTIP_MTR_ESCALATOR_WIDE_PLACEMENT_WARN.getText(), true);
        }
        return null;
    }


    private Direction getSideDirection(BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
    }

}
