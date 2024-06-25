
package org.mtr.mod.item;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.Blocks;

import org.mtr.mod.block.BlockFramedEscalatorNarrowBase;
import org.mtr.mod.block.BlockFramedEscalatorNarrowSide;
import org.mtr.mod.block.BlockFramedEscalatorNarrowStep;
import org.mtr.mod.block.IBlock;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemFramedEscalatorNarrow extends ItemExtension implements IBlock {

    public ItemFramedEscalatorNarrow(ItemSettings itemSettings) {
        super(itemSettings);
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if (ItemPSDAPGBase.blocksNotReplaceable(context, 1, 2, null)) {
            return ActionResult.FAIL;
        }

        final World world = context.getWorld();
        Direction playerFacingTO = context.getPlayerFacing();
        BlockPos pos1 = context.getBlockPos().offset(context.getSide());

        final BlockState playerFrontState = world.getBlockState(pos1.offset(playerFacingTO));
        if (playerFrontState.getBlock().data instanceof BlockFramedEscalatorNarrowBase) {
            if (IBlock.getStatePropertySafe(playerFrontState, BlockFramedEscalatorNarrowBase.FACING) == playerFacingTO
                    .getOpposite()) {
                playerFacingTO = playerFacingTO.getOpposite();
            }
        }
        final BlockState stepState = Blocks.FRAMED_ESCALATOR_NARROW_STEP.get().getDefaultState().with(new Property<>(BlockFramedEscalatorNarrowStep.FACING.data), playerFacingTO.data);
        world.setBlockState(pos1, stepState);

        final BlockState sideState = Blocks.FRAMED_ESCALATOR_NARROW_SIDE.get().getDefaultState().with(new Property<>(BlockFramedEscalatorNarrowSide.FACING.data), playerFacingTO.data);
        world.setBlockState(pos1.up(), sideState);

        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
        tooltip.add(TextHelper.translatable("tooltip.mtr.framed_escalator_narrow_1").formatted(TextFormatting.GRAY));
        tooltip.add(TextHelper.translatable("tooltip.mtr.framed_escalator_narrow_2").formatted(TextFormatting.GREEN));
        super.addTooltips(stack, world, tooltip, options);
    }
}
