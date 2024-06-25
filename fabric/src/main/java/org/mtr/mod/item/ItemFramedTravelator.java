package org.mtr.mod.item;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.Blocks;
import org.mtr.mod.block.BlockEscalatorBase;
import org.mtr.mod.block.BlockEscalatorSide;
import org.mtr.mod.block.BlockEscalatorStep;
import org.mtr.mod.block.IBlock;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemFramedTravelator extends ItemExtension implements IBlock {

    public ItemFramedTravelator(ItemSettings itemSettings) {
        super(itemSettings);
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if(ItemPSDAPGBase.blocksNotReplaceable(context, 2, 2, null)){
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
        tooltip.add(TextHelper.translatable("tooltip.mtr.framed_travelator_1").formatted(TextFormatting.GRAY));
        tooltip.add(TextHelper.translatable("tooltip.mtr.framed_travelator_2").formatted(TextFormatting.GREEN));
		super.addTooltips(stack, world, tooltip, options);
    }
    
}
