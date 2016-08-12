package fr.scarex.miningtnt.entity;

import fr.scarex.miningtnt.MiningTntConfiguration;
import fr.scarex.miningtnt.world.MiningExplosion;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * @author SCAREX
 *
 */
public class EntityMiningTntPrimed extends Entity implements IEntityAdditionalSpawnData
{
    public int fuse;
    private EntityLivingBase tntPlacedBy;
    protected int strength;
    protected int fortune;

    public EntityMiningTntPrimed(World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
    }

    public EntityMiningTntPrimed(World worldIn, double x, double y, double z, EntityLivingBase placer, int strength, int fortune) {
        this(worldIn);
        this.setPosition(x, y, z);
        float f = (float) (Math.random() * Math.PI * 2.0D);
        this.motionX = (double) (-((float) Math.sin((double) f)) * 0.02F);
        this.motionY = 0.20000000298023224D;
        this.motionZ = (double) (-((float) Math.cos((double) f)) * 0.02F);
        this.fuse = 80;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.tntPlacedBy = placer;
        this.strength = strength;
        this.fortune = fortune;
    }

    protected void entityInit() {}

    protected boolean canTriggerWalking() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }

        if (this.fuse-- <= 0) {
            this.setDead();

            if (!this.worldObj.isRemote) {
                this.explode();
            }
        } else {
            this.handleWaterMovement();
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    private void explode() {
        if (MiningTntConfiguration.explosionStrength[this.strength] > 0) MiningExplosion.createExplosion(this.worldObj, this, this.posX, this.posY + (double) (this.height / 16.0F), this.posZ, MiningTntConfiguration.explosionStrength[this.strength], this.fortune, true, false);
    }

    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("Fuse", (byte) this.fuse);
    }

    protected void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.fuse = tagCompund.getByte("Fuse");
    }

    public EntityLivingBase getTntPlacedBy() {
        return this.tntPlacedBy;
    }

    public float getEyeHeight() {
        return 0.0F;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.fuse);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.fuse = additionalData.readInt();
    }
}
