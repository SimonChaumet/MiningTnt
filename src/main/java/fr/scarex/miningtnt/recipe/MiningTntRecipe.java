package fr.scarex.miningtnt.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fr.scarex.miningtnt.MiningTnt;
import fr.scarex.miningtnt.MiningTntConfiguration;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author SCAREX
 *
 */
public class MiningTntRecipe implements IJEIRecipe
{
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack pickaxe = inv.getStackInSlot(4);
        if (pickaxe == null) return false;
        int strength = -1;
        if (pickaxe.getItem() instanceof ItemPickaxe && ((ItemPickaxe) pickaxe.getItem()).getHarvestLevel(pickaxe, "pickaxe") >= 2) {
            for (int i = 0; i < 9; i++) {
                if (i != 4) {
                    if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.tnt))
                        strength++;
                    else if (inv.getStackInSlot(i) != null) return false;
                }
            }
        }
        if (strength == -1) return false;
        System.out.println(pickaxe.getItemDamage());
        if ((pickaxe.getMaxDamage() - pickaxe.getItemDamage()) < MiningTntConfiguration.explosionDurability[strength]) return false;
        if (MiningTntConfiguration.explosionStrength[strength] > 0) return true;
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack result = new ItemStack(MiningTnt.BLOCK_MINING_TNT);
        int strength = -1;
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.tnt)) strength++;
            }
        }
        NBTTagCompound comp = new NBTTagCompound();
        NBTTagCompound compBlock = new NBTTagCompound();
        compBlock.setInteger("strength", strength);
        comp.setTag("BlockEntityTag", compBlock);
        result.setTagCompound(comp);
        ItemStack pickaxe = inv.getStackInSlot(4);
        result.setItemDamage(EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, pickaxe) > 0 ? 4 : EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, pickaxe));
        return result;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(MiningTnt.BLOCK_MINING_TNT);
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        int strength = -1;
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.tnt)) strength++;
            }
        }
        inv.getStackInSlot(4).setItemDamage(inv.getStackInSlot(4).getItemDamage() + MiningTntConfiguration.explosionDurability[strength]);
        if (inv.getStackInSlot(4).getItemDamage() >= inv.getStackInSlot(4).getMaxDamage()) inv.getStackInSlot(4).stackSize--;
        if (inv.getStackInSlot(4).stackSize <= 0) inv.setInventorySlotContents(4, null);
        return new ItemStack[] { null,
                null, null, null,
                inv.getStackInSlot(4),
                null, null, null,
                null };
    }

    public List getInputs() {
        Items.iron_pickaxe.setHasSubtypes(true);
        ItemStack ironSimple = new ItemStack(Items.iron_pickaxe);

        ItemStack ironFortune1 = new ItemStack(Items.iron_pickaxe);
        HashMap<Integer, Integer> fortune1Enchant = new HashMap<Integer, Integer>();
        fortune1Enchant.put(Enchantment.fortune.effectId, 1);
        EnchantmentHelper.setEnchantments(fortune1Enchant, ironFortune1);

        ItemStack ironFortune2 = new ItemStack(Items.iron_pickaxe);
        HashMap<Integer, Integer> fortune2Enchant = new HashMap<Integer, Integer>();
        fortune2Enchant.put(Enchantment.fortune.effectId, 2);
        EnchantmentHelper.setEnchantments(fortune2Enchant, ironFortune2);

        ItemStack ironFortune3 = new ItemStack(Items.iron_pickaxe);
        HashMap<Integer, Integer> fortune3Enchant = new HashMap<Integer, Integer>();
        fortune3Enchant.put(Enchantment.fortune.effectId, 3);
        EnchantmentHelper.setEnchantments(fortune3Enchant, ironFortune3);

        ItemStack ironSilkTouch = new ItemStack(Items.iron_pickaxe);
        HashMap<Integer, Integer> silkTouchEnchant = new HashMap<Integer, Integer>();
        silkTouchEnchant.put(Enchantment.silkTouch.effectId, 1);
        EnchantmentHelper.setEnchantments(silkTouchEnchant, ironSilkTouch);

        List l = new ArrayList();
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                List<ItemStack> stacks = Arrays.asList(ironSimple, ironFortune1, ironFortune2, ironFortune3, ironSilkTouch);
                l.add(stacks);
            } else {
                l.add(Arrays.asList(new ItemStack(Blocks.tnt)));
            }
        }
        return l;
    }

    public List getOutputs() {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        MiningTnt.BLOCK_MINING_TNT.getSubBlocks(Item.getItemFromBlock(MiningTnt.BLOCK_MINING_TNT), MiningTnt.CREATIVE_TAB, stacks);
        return stacks;
    }
}
