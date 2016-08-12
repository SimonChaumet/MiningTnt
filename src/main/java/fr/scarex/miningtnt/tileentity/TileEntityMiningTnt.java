package fr.scarex.miningtnt.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * @author SCAREX
 *
 */
public class TileEntityMiningTnt extends TileEntity
{
    protected int strength;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.strength = compound.getInteger("strength");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("strength", this.strength);
    }

    /**
     * @param strength
     *            the strength to set
     */
    public void setStrength(int strength) {
        this.strength = strength;
    }

    /**
     * @return the strength
     */
    public int getStrength() {
        return strength;
    }
}
