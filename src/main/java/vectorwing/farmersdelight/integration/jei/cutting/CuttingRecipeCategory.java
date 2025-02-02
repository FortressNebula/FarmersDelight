package vectorwing.farmersdelight.integration.jei.cutting;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.crafting.ingredients.ChanceResult;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModItems;
import vectorwing.farmersdelight.utils.TextUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CuttingRecipeCategory implements IRecipeCategory<CuttingBoardRecipe>
{
	public static final ResourceLocation UID = new ResourceLocation(FarmersDelight.MODID, "cutting");
	public static final int OUTPUT_GRID_X = 76;
	public static final int OUTPUT_GRID_Y = 10;
	public static final int SLOT_SPRITE_SIZE = 18;
	private final IDrawable slot;
	private final IDrawable slotChance;
	private final String title;
	private final IDrawable background;
	private final IDrawable icon;
	private final CuttingBoardModel cuttingBoard;

	public CuttingRecipeCategory(IGuiHelper helper) {
		title = I18n.get(FarmersDelight.MODID + ".jei.cutting");
		ResourceLocation backgroundImage = new ResourceLocation(FarmersDelight.MODID, "textures/gui/jei/cutting_board.png");
		slot = helper.createDrawable(backgroundImage, 0, 58, 18, 18);
		slotChance = helper.createDrawable(backgroundImage, 18, 58, 18, 18);
		background = helper.createDrawable(backgroundImage, 0, 0, 117, 57);
		icon = helper.createDrawableIngredient(new ItemStack(ModItems.CUTTING_BOARD.get()));
		cuttingBoard = new CuttingBoardModel(() -> new ItemStack(ModBlocks.CUTTING_BOARD.get()));
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public Class<? extends CuttingBoardRecipe> getRecipeClass() {
		return CuttingBoardRecipe.class;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setIngredients(CuttingBoardRecipe cuttingBoardRecipe, IIngredients ingredients) {
		ingredients.setInputIngredients(cuttingBoardRecipe.getIngredientsAndTool());
		ingredients.setOutputs(VanillaTypes.ITEM, cuttingBoardRecipe.getResults());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CuttingBoardRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		NonNullList<ChanceResult> recipeOutputs = recipe.getRollableResults();

		// Draw required tool
		itemStacks.init(0, true, 15, 7);
		itemStacks.set(0, Arrays.asList(recipe.getTool().getItems()));

		// Draw input
		itemStacks.init(1, true, 15, 26);
		itemStacks.set(1, Arrays.asList(recipe.getIngredients().get(0).getItems()));

		// Draw outputs
		int size = recipeOutputs.size();
		int centerX = size > 1 ? 0 : 9;
		int centerY = size > 2 ? 0 : 9;

		for (int i = 0; i < size; i++) {
			int xOffset = centerX + (i % 2 == 0 ? 0 : 19);
			int yOffset = centerY + ((i / 2) * 19);

			itemStacks.init(i + 2, false, OUTPUT_GRID_X + xOffset, OUTPUT_GRID_Y + yOffset);
			itemStacks.set(i + 2, recipeOutputs.get(i).getStack());
		}

		itemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
			if (input || slotIndex < 2) {
				return;
			}
			ChanceResult output = recipeOutputs.get(slotIndex - 2);
			float chance = output.getChance();
			if (chance != 1)
				tooltip.add(1, TextUtils.getTranslation("jei.chance", chance < 0.01 ? "<1" : (int) (chance * 100))
						.withStyle(TextFormatting.GOLD));
		});
	}

	@Override
	public void draw(CuttingBoardRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		cuttingBoard.draw(matrixStack, 15, 19);
		NonNullList<ChanceResult> recipeOutputs = recipe.getRollableResults();

		int size = recipe.getResults().size();
		int centerX = size > 1 ? 0 : 9;
		int centerY = size > 2 ? 0 : 9;

		for (int i = 0; i < size; i++) {
			int xOffset = centerX + (i % 2 == 0 ? 0 : 19);
			int yOffset = centerY + ((i / 2) * 19);

			if (recipeOutputs.get(i).getChance() != 1) {
				slotChance.draw(matrixStack, OUTPUT_GRID_X + xOffset, OUTPUT_GRID_Y + yOffset);
			} else {
				slot.draw(matrixStack, OUTPUT_GRID_X + xOffset, OUTPUT_GRID_Y + yOffset);
			}
		}
	}
}
