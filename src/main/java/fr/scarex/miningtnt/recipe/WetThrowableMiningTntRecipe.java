package fr.scarex.miningtnt.recipe;

import java.util.List;

import com.google.common.collect.Lists;

import fr.scarex.miningtnt.MiningTnt;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * @author SCAREX
 *
 */
public class WetThrowableMiningTntRecipe extends ShapelessOreRecipe
{
    public WetThrowableMiningTntRecipe() {
        super(new ItemStack(MiningTnt.ITEM_THROWABLE_MINING_TNT), MiningTnt.ITEM_THROWABLE_MINING_TNT, Items.string);
        List<ItemStack> list = Lists.newArrayList();
        MiningTnt.ITEM_THROWABLE_MINING_TNT.getSubItems(MiningTnt.ITEM_THROWABLE_MINING_TNT, MiningTnt.ITEM_THROWABLE_MINING_TNT.getCreativeTab(), list);
        List listInput = Lists.newArrayListWithCapacity(list.size());
        for (ItemStack stack : list) {
            ItemStack stackinput = stack.copy();
            stackinput.getTagCompound().setBoolean("wet", true);
            listInput.add(stackinput);
        }
        this.input.set(0, listInput);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() == MiningTnt.ITEM_THROWABLE_MINING_TNT) {
                ItemStack stack = inv.getStackInSlot(i).copy();
                stack.getTagCompound().removeTag("wet");
                return stack;
            }
        }
        return null;
    }
}
