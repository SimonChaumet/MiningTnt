package fr.scarex.miningtnt.item;

import java.util.List;

import fr.scarex.miningtnt.MiningTnt;
import fr.scarex.miningtnt.entity.EntityThrowableMiningTnt;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author SCAREX
 *
 */
public class ItemThrowableMiningTnt extends Item
{
    public ItemThrowableMiningTnt() {
        this.setUnlocalizedName("throwable_mining_tnt");
        this.setCreativeTab(MiningTnt.CREATIVE_TAB);
        this.setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 8; i++) {
            NBTTagCompound comp = new NBTTagCompound();
            comp.setInteger("strength", i);
            for (int j = 0; j <= 4; j++) {
                ItemStack stack = new ItemStack(this, 1, j);
                stack.setTagCompound((NBTTagCompound) comp.copy());
                list.add(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("wet", Constants.NBT.TAG_BYTE) && stack.getTagCompound().getBoolean("wet")) {
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("general.wet"));
        }
        
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

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("strength", Constants.NBT.TAG_INT)) {
            int strength = stack.getTagCompound().getInteger("strength") + 1;
            tooltip.add(StatCollector.translateToLocal("tile.mining_tnt.name_strength") + " " + StatCollector.translateToLocal("enchantment.level." + strength));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("wet", Constants.NBT.TAG_BYTE) && stack.getTagCompound().getBoolean("wet")) return stack;
        if (!player.capabilities.isCreativeMode) --stack.stackSize;
        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote) world.spawnEntityInWorld(new EntityThrowableMiningTnt(world, player, stack.getTagCompound().getInteger("strength"), stack.getItemDamage()));
        return stack;
    }
}
