package org.mtr.mod.item;

import org.apache.logging.log4j.core.config.builder.api.Component;
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

public class ItemEscalatorStep extends ItemExtension implements IBlock {

    public ItemEscalatorStep(ItemSettings itemSettings) {
        super(itemSettings);
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
        tooltip.add(TextHelper.translatable("tooltip.mtr.escalator_step").formatted(TextFormatting.GRAY));
		super.addTooltips(stack, world, tooltip, options);
    }
}
