package fr.scarex.miningtnt.block.itemblock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;

/**
 * @author SCAREX
 *
 */
public class ItemBlockMiningTnt extends ItemBlock
{
    public ItemBlockMiningTnt(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        int fortune = stack.getItemDamage();
        switch (fortune) {
        case 1:
        case 2:
        case 3:
            tooltip.add(StatCollector.translateToLocal("enchantment.lootBonusDigger") + " " + StatCollector.translateToLocal("enchantment.level." + fortune));
            break;
        case 4:
            tooltip.add(StatCollector.translateToLocal("enchantment.untouching"));
            break;
        }

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND) && stack.getTagCompound().getCompoundTag("BlockEntityTag").hasKey("strength", Constants.NBT.TAG_INT)) {
            int strength = stack.getTagCompound().getCompoundTag("BlockEntityTag").getInteger("strength") + 1;
            tooltip.add(StatCollector.translateToLocal("tile.mining_tnt.name_strength") + " " + StatCollector.translateToLocal("enchantment.level." + strength));
        }
    }
}
