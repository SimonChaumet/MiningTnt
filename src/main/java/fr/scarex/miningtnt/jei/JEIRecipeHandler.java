package fr.scarex.miningtnt.jei;

import fr.scarex.miningtnt.MiningTnt;
import fr.scarex.miningtnt.recipe.IJEIRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * @author SCAREX
 *
 */
public class JEIRecipeHandler implements IRecipeHandler<IJEIRecipe>
{
    @Override
    public Class<IJEIRecipe> getRecipeClass() {
        return IJEIRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return MiningTnt.MODID + ".crafting";
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(IJEIRecipe recipe) {
        return new JEIRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(IJEIRecipe recipe) {
        return true;
    }
}
