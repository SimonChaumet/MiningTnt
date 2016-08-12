package fr.scarex.miningtnt.recipe;

import java.util.List;

import com.google.common.collect.Lists;

import fr.scarex.miningtnt.MiningTnt;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author SCAREX
 *
 */
public class ThrowableMiningTntRecipe implements IJEIRecipe
{
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundMiningTnt = false;
        boolean foundString = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && (stack.getItem() == Items.string || stack.getItem() == Item.getItemFromBlock(MiningTnt.BLOCK_MINING_TNT))) {
                if (stack.getItem() == Item.getItemFromBlock(MiningTnt.BLOCK_MINING_TNT)) {
                    if (foundMiningTnt) return false;
                    foundMiningTnt = true;
                } else if (stack.getItem() == Items.string) {
                    if (foundString) return false;
                    foundString = true;
                }
            } else if (stack != null) { return false; }
        }
        return foundMiningTnt && foundString;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() == Item.getItemFromBlock(MiningTnt.BLOCK_MINING_TNT)) {
                ItemStack stack = new ItemStack(MiningTnt.ITEM_THROWABLE_MINING_TNT);
                stack.setTagCompound(inv.getStackInSlot(i).getTagCompound().getCompoundTag("BlockEntityTag"));
                return stack;
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(MiningTnt.ITEM_THROWABLE_MINING_TNT);
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return null;
    }

    @Override
    public List getInputs() {
        List list = Lists.newArrayListWithCapacity(2);
        List listMiningTnts = Lists.newArrayList();
        MiningTnt.BLOCK_MINING_TNT.getSubBlocks(Item.getItemFromBlock(MiningTnt.BLOCK_MINING_TNT), MiningTnt.BLOCK_MINING_TNT.getCreativeTabToDisplayOn(), listMiningTnts);
        list.add(listMiningTnts);
        list.add(new ItemStack(Items.string));
        return list;
    }

    @Override
    public List getOutputs() {
        List list = Lists.newArrayList();
        MiningTnt.ITEM_THROWABLE_MINING_TNT.getSubItems(MiningTnt.ITEM_THROWABLE_MINING_TNT, MiningTnt.ITEM_THROWABLE_MINING_TNT.getCreativeTab(), list);
        return list;
    }
}
