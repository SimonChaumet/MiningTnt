package fr.scarex.miningtnt.jei;

import fr.scarex.miningtnt.MiningTnt;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

/**
 * @author SCAREX
 *
 */
@JEIPlugin
public class MiningTntJEIPlugin extends BlankModPlugin
{
    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        
        registry.addRecipeCategories(new MiningTntRecipeCategory(guiHelper));
        
//        registry.addRecipeHandlers(new MiningTntRecipeHandler());
//        registry.addRecipeHandlers(new ThrowableMiningTntRecipeHandler());
        registry.addRecipeHandlers(new JEIRecipeHandler());
    }
}
