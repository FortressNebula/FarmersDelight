package vectorwing.farmersdelight.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import vectorwing.farmersdelight.blocks.MushroomColonyBlock;

import javax.annotation.Nullable;

import net.minecraft.item.Item.Properties;

public class MushroomColonyItem extends BlockItem
{
	public MushroomColonyItem(Block blockIn, Properties properties) {
		super(blockIn, properties);
	}

	@Override
	@Nullable
	protected BlockState getPlacementState(BlockItemUseContext context) {
		BlockState originalState = this.getBlock().getStateForPlacement(context);
		if (originalState != null) {
			BlockState matureState = originalState.setValue(MushroomColonyBlock.COLONY_AGE, 3);
			return this.canPlace(context, matureState) ? matureState : null;
		}
		return null;
	}
}

