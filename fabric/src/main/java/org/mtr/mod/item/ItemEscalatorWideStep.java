package org.mtr.mod.item;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mod.block.*;

import javax.annotation.Nonnull;

public class ItemEscalatorWideStep extends ItemExtension implements IBlock {

    private final Block variant;

    public ItemEscalatorWideStep(ItemSettings itemSettings, Block variant) {
        super(itemSettings);
        this.variant = variant;
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if (ItemPSDAPGBase.blocksNotReplaceable(context, 2, 2, null)) {
            return ActionResult.FAIL;
        }

        final World world = context.getWorld();
        Direction playerFacing = context.getPlayerFacing();
        BlockPos pos1 = context.getBlockPos().offset(context.getSide());
        BlockPos pos2 = pos1.offset(playerFacing.rotateYClockwise());

        final BlockState frontState = world.getBlockState(pos1.offset(playerFacing));
        if (frontState.getBlock().data instanceof BlockEscalatorWide) {
            if (IBlock.getStatePropertySafe(frontState, BlockEscalatorWide.FACING) == playerFacing.getOpposite()) {
                playerFacing = playerFacing.getOpposite();
                final BlockPos pos3 = pos1;
                pos1 = pos2;
                pos2 = pos3;
            }
        }

        final BlockState sideState = variant.getDefaultState().with(new Property<>(BlockEscalatorSide.FACING.data), playerFacing.data);
        world.setBlockState(pos1, sideState.with(new Property<>(SIDE.data), EnumSide.LEFT));
        world.setBlockState(pos2, sideState.with(new Property<>(SIDE.data), EnumSide.RIGHT));

        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }
}
