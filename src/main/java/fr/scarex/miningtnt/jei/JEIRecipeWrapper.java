package fr.scarex.miningtnt.jei;

import java.util.List;

import fr.scarex.miningtnt.recipe.IJEIRecipe;
import fr.scarex.miningtnt.recipe.MiningTntRecipe;
import mezz.jei.api.recipe.BlankRecipeWrapper;

/**
 * @author SCAREX
 *
 */
public class JEIRecipeWrapper extends BlankRecipeWrapper
{
    private final IJEIRecipe recipe;

    public JEIRecipeWrapper(IJEIRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public List getInputs() {
        return recipe.getInputs();
    }

    @Override
    public List getOutputs() {
        return recipe.getOutputs();
    }
}
