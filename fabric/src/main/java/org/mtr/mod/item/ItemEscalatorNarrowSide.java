
package org.mtr.mod.item;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.block.BlockEscalatorNarrowBase;
import org.mtr.mod.block.BlockEscalatorNarrowSide;
import org.mtr.mod.block.IBlock;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemEscalatorNarrowSide extends ItemExtension implements IBlock {

    private final BlockRegistryObject handraiBlocks;

    public ItemEscalatorNarrowSide(BlockRegistryObject handraiBlocks, ItemSettings itemSettings) {
        super(itemSettings);
        this.handraiBlocks = handraiBlocks; 
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        final World world = context.getWorld();
        Direction playerFacingTO = context.getPlayerFacing();
        BlockPos pos1 = context.getBlockPos().offset(context.getSide());
        
        final BlockState playerFrontState = world.getBlockState(pos1.offset(playerFacingTO));
        if (playerFrontState.getBlock().data instanceof BlockEscalatorNarrowBase) {
            if (IBlock.getStatePropertySafe(playerFrontState, BlockEscalatorNarrowBase.FACING) == playerFacingTO
                    .getOpposite()) {
                playerFacingTO = playerFacingTO.getOpposite();
            }
        }

        final BlockState sideState = handraiBlocks.get().getDefaultState()
                .with(new Property<>(BlockEscalatorNarrowSide.FACING.data), playerFacingTO.data);
        world.setBlockState(pos1, sideState);
        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
        tooltip.add(TextHelper.translatable("tooltip.mtr.escalator_narrow_1").formatted(TextFormatting.GRAY));
        tooltip.add(TextHelper.translatable("tooltip.mtr.escalator_narrow_2").formatted(TextFormatting.GREEN));
        super.addTooltips(stack, world, tooltip, options);
    }
}
