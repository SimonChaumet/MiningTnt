package fr.scarex.miningtnt.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * @author SCAREX
 *
 */
public interface IStackedBlock
{
    public ItemStack createNewStackedBlock(IBlockState state);
}
