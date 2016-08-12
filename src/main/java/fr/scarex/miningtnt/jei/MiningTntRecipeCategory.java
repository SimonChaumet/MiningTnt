package fr.scarex.miningtnt.jei;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import fr.scarex.miningtnt.MiningTnt;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.ingredients.IGuiIngredient;
import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author SCAREX
 *
 */
public class MiningTntRecipeCategory extends BlankRecipeCategory
{
    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot1 = 1;

    @Nonnull
    private final IDrawable background;
    @Nonnull
    private final String localizedName;
    @Nonnull
    private final ICraftingGridHelper craftingGridHelper;

    public MiningTntRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
        background = guiHelper.createDrawable(location, 29, 16, 116, 54);
        localizedName = Translator.translateToLocal("gui.jei.category.craftingTable");
        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
    }

    @Override
    @Nonnull
    public String getUid() {
        return MiningTnt.MODID + ".crafting";
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    @Nonnull
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(craftOutputSlot, false, 94, 18);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot1 + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }
        
        if (recipeWrapper instanceof JEIRecipeWrapper) {
            JEIRecipeWrapper wrapper = (JEIRecipeWrapper) recipeWrapper;
            craftingGridHelper.setInput(guiItemStacks, wrapper.getInputs(), 3, 3);
            craftingGridHelper.setOutput(guiItemStacks, wrapper.getOutputs());
        }
    }
}
