package mtr.block;

import mtr.mappings.BlockDirectionalMapper;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.List;

public abstract class BlockPoleCheckBase extends BlockDirectionalMapper {

	public BlockPoleCheckBase(Properties settings) {
		super(settings);
	}

	protected static final EnumProperty<EnumPoleVariant> VARIANT = EnumProperty.create("variant",EnumPoleVariant.class);

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState stateBelow = ctx.getLevel().getBlockState(ctx.getClickedPos().below());
		BlockState blockAbove = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
		if (isBlock(stateBelow.getBlock())) {
			if(blockAbove.getBlock() instanceof SlabBlock && blockAbove.getValue(SlabBlock.TYPE) == SlabType.TOP){
				return placeWithState(stateBelow).setValue(VARIANT, EnumPoleVariant.EXTRA_HALF);
			}
			if(blockAbove.getBlock() instanceof StairBlock && blockAbove.getValue(StairBlock.HALF) == Half.TOP){
				return placeWithState(stateBelow).setValue(VARIANT, EnumPoleVariant.EXTRA_HALF);
			}
			return placeWithState(stateBelow);
		} else {
			return null;
		}
	}

	@Override
	public void appendHoverText(ItemStack itemStack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
		final String[] strings = Text.translatable("tooltip.mtr.pole_placement", getTooltipBlockText()).getString().split("\n");
		for (final String string : strings) {
			tooltip.add(Text.literal(string).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
		}
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		BlockState blockAbovePole = level.getBlockState(currentPos.above());
		if(blockAbovePole.getBlock() instanceof SlabBlock && blockAbovePole.getValue(SlabBlock.TYPE) == SlabType.TOP){
			return state.setValue(VARIANT ,EnumPoleVariant.EXTRA_HALF);
		}
		if(blockAbovePole.getBlock() instanceof StairBlock && blockAbovePole.getValue(StairBlock.HALF) == Half.TOP){
			return state.setValue(VARIANT, EnumPoleVariant.EXTRA_HALF);
		}
		return state.setValue(VARIANT ,EnumPoleVariant.FULL);
	}

	protected BlockState placeWithState(BlockState state) {
		return defaultBlockState().setValue(FACING, IBlock.getStatePropertySafe(state, FACING));
	}

	protected abstract boolean isBlock(Block block);

	protected abstract Component getTooltipBlockText();

	protected enum EnumPoleVariant implements StringRepresentable {
		FULL("full"),EXTRA_HALF("extra_half");
		private final String name;
		EnumPoleVariant(String variant) {
			name = variant;
		}
		@Override
		public String getSerializedName() {
			return name;
		}
	}
}
