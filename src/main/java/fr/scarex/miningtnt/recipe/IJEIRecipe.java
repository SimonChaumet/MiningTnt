package fr.scarex.miningtnt.recipe;

import java.util.List;

import net.minecraft.item.crafting.IRecipe;

/**
 * @author SCAREX
 *
 */
public interface IJEIRecipe extends IRecipe
{
    public List getInputs();
    
    public List getOutputs();
}
