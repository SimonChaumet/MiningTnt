package fr.scarex.miningtnt.entity;

import java.util.List;
import java.util.UUID;

import fr.scarex.miningtnt.MiningTnt;
import fr.scarex.miningtnt.MiningTntConfiguration;
import fr.scarex.miningtnt.world.MiningExplosion;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author SCAREX
 *
 */
public class EntityThrowableMiningTnt extends Entity implements IProjectile, IEntityAdditionalSpawnData
{
    protected int xTile = -1;
    protected int yTile = -1;
    protected int zTile = -1;
    protected Block blockIn;
    private int dataIn;
    private boolean inGround;
    protected Entity shooter;
    protected int ticksInGround;
    protected int ticksInAir;
    protected int fuse = -1;
    protected int strength;
    protected int fortune;
    protected boolean disabled = false;

    public EntityThrowableMiningTnt(World world) {
        super(world);
        this.renderDistanceWeight = 10.0D;
        this.setSize(0.1F, 0.1F);
    }

    public EntityThrowableMiningTnt(World world, double x, double y, double z) {
        super(world);
        this.renderDistanceWeight = 10.0D;
        this.setSize(0.1F, 0.1F);
        this.setPosition(x, y, z);
    }

    public EntityThrowableMiningTnt(World world, Entity shooter, int strength, int fortune) {
        super(world);
        this.strength = strength;
        this.fortune = fortune;
        this.renderDistanceWeight = 10.0D;
        this.shooter = shooter;

        this.setSize(0.1F, 0.1F);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double) shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI));
        this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI));
        this.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
    }

    @Override
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt_double(x * x + y * y + z * z);
        x = x / (double) f;
        y = y / (double) f;
        z = z / (double) f;
        x = x + this.rand.nextGaussian() * (double) (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) inaccuracy;
        y = y + this.rand.nextGaussian() * (double) (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) inaccuracy;
        z = z + this.rand.nextGaussian() * (double) (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) inaccuracy;
        x = x * (double) velocity;
        y = y * (double) velocity;
        z = z * (double) velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f1 = MathHelper.sqrt_double(x * x + z * z);
        this.prevRotationYaw = this.rotationYaw = (float) (MathHelper.atan2(x, z) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (MathHelper.atan2(y, (double) f1) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(x * x + z * z);
            this.prevRotationYaw = this.rotationYaw = (float) (MathHelper.atan2(x, z) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (MathHelper.atan2(y, (double) f) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
        IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (block.getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState(this.worldObj, blockpos);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBox(this.worldObj, blockpos, iblockstate);

            if (axisalignedbb != null && axisalignedbb.isVecInside(new Vec3(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.inGround) {
            int j = block.getMetaFromState(iblockstate);

            if (block == this.blockIn && j == this.dataIn) {
                ++this.ticksInGround;
            } else {
                this.inGround = false;
                this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
                this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            ++this.ticksInAir;
            Vec3 vec31 = new Vec3(this.posX, this.posY, this.posZ);
            Vec3 vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec31, vec3, false, true, false);
            vec31 = new Vec3(this.posX, this.posY, this.posZ);
            vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null) vec3 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);

            Entity entity = null;
            List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;

            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = (Entity) list.get(i);

                if (entity1.canBeCollidedWith() && (entity1 != this.shooter || this.ticksInAir >= 5)) {
                    float f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().expand((double) f1, (double) f1, (double) f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

                    if (movingobjectposition1 != null) {
                        double d1 = vec31.squareDistanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) movingobjectposition = new MovingObjectPosition(entity);

            if (movingobjectposition != null) {
                if (movingobjectposition.entityHit != null) {
                    this.setPosition(movingobjectposition.entityHit.posX, movingobjectposition.entityHit.posY, movingobjectposition.entityHit.posZ);
                } else {
                    this.fuse = 0;
                    BlockPos blockpos1 = movingobjectposition.getBlockPos();
                    this.xTile = blockpos1.getX();
                    this.yTile = blockpos1.getY();
                    this.zTile = blockpos1.getZ();
                    IBlockState iblockstate1 = this.worldObj.getBlockState(blockpos1);
                    this.blockIn = iblockstate1.getBlock();
                    this.dataIn = this.blockIn.getMetaFromState(iblockstate1);
                    this.motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - this.posX));
                    this.motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - this.posY));
                    this.motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
                    float f5 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / (double) f5 * 0.05000000074505806D;
                    this.posY -= this.motionY / (double) f5 * 0.05000000074505806D;
                    this.posZ -= this.motionZ / (double) f5 * 0.05000000074505806D;
                    this.inGround = true;

                    if (this.blockIn.getMaterial() != Material.air) {
                        this.blockIn.onEntityCollidedWithBlock(this.worldObj, blockpos1, iblockstate1, this);
                    }
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;

            float f4 = 0.99F;
            float f6 = 0.05F;

            if (this.isInWater()) {
                this.setDisabled(true);
                for (int i1 = 0; i1 < 4; ++i1) {
                    float f8 = 0.25F;
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double) f8, this.posY - this.motionY * (double) f8, this.posZ - this.motionZ * (double) f8, this.motionX, this.motionY, this.motionZ, new int[0]);
                }

                f4 = 0.6F;
            }

            if (this.isWet()) {
                this.extinguish();
            }

            this.motionX *= (double) f4;
            this.motionY *= (double) f4;
            this.motionZ *= (double) f4;
            this.motionY -= (double) f6;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }

        if (this.fuse == 80) this.explode();
        if (this.fuse >= 0) this.fuse++;
        if (this.ticksInGround >= 1200) this.setDead();
    }

    public void explode() {
        if (!this.isDisabled()) this.setDead();
        if (!this.worldObj.isRemote && MiningTntConfiguration.explosionStrength[this.strength] > 0) MiningExplosion.createExplosion(this.worldObj, this, this.posX, this.posY + (double) (this.height / 16.0F), this.posZ, MiningTntConfiguration.explosionStrength[this.strength], this.fortune, true, this.isDisabled());
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.worldObj.isRemote && this.isDisabled()) {
            ItemStack stack = new ItemStack(MiningTnt.ITEM_THROWABLE_MINING_TNT, 1, this.fortune);
            NBTTagCompound compItem = new NBTTagCompound();
            compItem.setInteger("strength", this.strength);
            compItem.setBoolean("wet", true);
            stack.setTagCompound(compItem);
            if (player.inventory.addItemStackToInventory(stack)) {
                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("xTile", (short) this.xTile);
        tagCompound.setShort("yTile", (short) this.yTile);
        tagCompound.setShort("zTile", (short) this.zTile);
        tagCompound.setShort("life", (short) this.ticksInGround);
        ResourceLocation resourcelocation = (ResourceLocation) Block.blockRegistry.getNameForObject(this.blockIn);
        tagCompound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
        tagCompound.setByte("inData", (byte) this.dataIn);
        tagCompound.setBoolean("inGround", this.inGround);
        tagCompound.setLong("shooterUUIDMost", this.shooter.getUniqueID().getMostSignificantBits());
        tagCompound.setLong("shooterUUIDLeast", this.shooter.getUniqueID().getLeastSignificantBits());
        tagCompound.setInteger("fortune", this.fortune);
        tagCompound.setInteger("fuse", this.fuse);
        tagCompound.setInteger("strength", this.strength);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.xTile = tagCompund.getShort("xTile");
        this.yTile = tagCompund.getShort("yTile");
        this.zTile = tagCompund.getShort("zTile");
        this.ticksInGround = tagCompund.getShort("life");

        if (tagCompund.hasKey("inTile", 8)) this.blockIn = Block.getBlockFromName(tagCompund.getString("inTile"));

        this.dataIn = tagCompund.getByte("inData") & 255;
        this.inGround = tagCompund.getBoolean("inGround");
        this.shooter = getEntityByUUID(this.worldObj, new UUID(tagCompund.getLong("shooterUUIDMost"), tagCompund.getLong("shooterUUIDLeast")));
        this.fortune = tagCompund.getInteger("fortune");
        this.fuse = tagCompund.getInteger("fortune");
        this.strength = tagCompund.getInteger("strength");
    }

    public static Entity getEntityByUUID(World world, UUID uuid) {
        for (Entity e : world.loadedEntityList) {
            if (e.getUniqueID().equals(uuid)) return e;
        }
        return null;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    public boolean canAttackWithItem() {
        return false;
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

    @Override
    protected void entityInit() {}

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @param disabled
     *            the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
