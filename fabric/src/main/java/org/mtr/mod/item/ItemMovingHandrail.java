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

public class ItemMovingHandrail extends ItemExtension implements IBlock {

	private final MapColor color;

    public ItemMovingHandrail(ItemSettings itemSettings, MapColor color) {
        super(itemSettings);
        this.color = color;
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
        tooltip.add(TextHelper.translatable("tooltip.mtr.moving_handrail").formatted(TextFormatting.GRAY));
		super.addTooltips(stack, world, tooltip, options);
    }
}
