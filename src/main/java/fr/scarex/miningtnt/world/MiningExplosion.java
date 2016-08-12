package fr.scarex.miningtnt.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import fr.scarex.miningtnt.MiningTnt;
import fr.scarex.miningtnt.MiningTntConfiguration;
import fr.scarex.miningtnt.block.IStackedBlock;
import fr.scarex.miningtnt.network.PacketMiningExplosion;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * @author SCAREX
 *
 */
public class MiningExplosion extends Explosion
{
    protected int fortune;
    protected boolean spawnparticles;
    protected boolean disabled = false;

    public MiningExplosion(World worldIn, Entity p_i45753_2_, double p_i45753_3_, double p_i45753_5_, double p_i45753_7_, float p_i45753_9_, boolean p_i45753_10_, boolean p_i45753_11_, List<BlockPos> p_i45753_12_) {
        super(worldIn, p_i45753_2_, p_i45753_3_, p_i45753_5_, p_i45753_7_, p_i45753_9_, p_i45753_10_, p_i45753_11_, p_i45753_12_);
    }

    public MiningExplosion(World worldIn, Entity p_i45754_2_, double p_i45754_3_, double p_i45754_5_, double p_i45754_7_, float size, boolean p_i45754_10_, boolean p_i45754_11_, int fortune, boolean disabled) {
        super(worldIn, p_i45754_2_, p_i45754_3_, p_i45754_5_, p_i45754_7_, size, p_i45754_10_, p_i45754_11_);
        this.fortune = fortune;
        this.disabled = disabled;
    }

    public MiningExplosion(World worldIn, Entity p_i45752_2_, double p_i45752_3_, double p_i45752_5_, double p_i45752_7_, float p_i45752_9_, boolean disabled, List<BlockPos> p_i45752_10_) {
        super(worldIn, p_i45752_2_, p_i45752_3_, p_i45752_5_, p_i45752_7_, p_i45752_9_, p_i45752_10_);
        this.disabled = disabled;
    }

    public void doExplosionA() {
        if (this.isDisabled()) return;
        Set<BlockPos> set = Sets.<BlockPos>newHashSet();
        for (int j = 0; j < 32; ++j) {
            for (int k = 0; k < 32; ++k) {
                for (int l = 0; l < 32; ++l) {
                    if (j == 0 || j == 31 || k == 0 || k == 31 || l == 0 || l == 31) {
                        double d0 = (double) ((float) j / 31.0F * 2.0F - 1.0F);
                        double d1 = (double) ((float) k / 31.0F * 2.0F - 1.0F);
                        double d2 = (double) ((float) l / 31.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.1F);
                        double d4 = this.explosionX;
                        double d6 = this.explosionY;
                        double d8 = this.explosionZ;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

                            if (iblockstate.getBlock().getMaterial() != Material.air) {
                                float f2 = this.exploder != null ? this.exploder.getExplosionResistance(this, this.worldObj, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(worldObj, blockpos, (Entity) null, this);
                                f -= (f2 + 0.3F) * 0.08F;
                            }

                            if (f > 0.0F && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, blockpos, iblockstate, f))) {
                                set.add(blockpos);
                            }

                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }
        this.affectedBlockPositions.addAll(set);
        Collections.sort(this.affectedBlockPositions, new BlockPosComparator(new BlockPos(this.explosionX, this.explosionY, this.explosionZ)));
        float f3 = this.explosionSize * 2.0F;
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, new ArrayList<Entity>(), f3);
    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
        if (this.isDisabled()) {
            this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.fizz", 0.4F, 2.0F + this.worldObj.rand.nextFloat() * 0.4F);
            this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.explosionX, this.explosionY, this.explosionZ, 0, 0, 0, new int[0]);
        } else {
            this.spawnparticles = spawnParticles;
            this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

            if (this.explosionSize >= 2.0F && this.isSmoking) {
                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
            } else {
                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
            }

            if (this.isFlaming) {
                for (BlockPos blockpos1 : this.affectedBlockPositions) {
                    if (this.worldObj.getBlockState(blockpos1).getBlock().getMaterial() == Material.air && this.worldObj.getBlockState(blockpos1.down()).getBlock().isFullBlock() && this.explosionRNG.nextInt(3) == 0) {
                        this.worldObj.setBlockState(blockpos1, Blocks.fire.getDefaultState());
                    }
                }
            }
        }
    }

    public boolean updateExplosionB() {
        if (this.isDisabled()) return true;
        if (this.isSmoking) {
            Iterator<BlockPos> ite = this.affectedBlockPositions.iterator();
            int i = 0;
            while (ite.hasNext() && i < MiningTntConfiguration.blockExplodedPerTick) {
                i++;
                BlockPos blockpos = ite.next();
                Block block = this.worldObj.getBlockState(blockpos).getBlock();

                if (this.spawnparticles && this.worldObj.rand.nextInt(4) == 0) {
                    double d0 = (double) ((float) blockpos.getX() + this.worldObj.rand.nextFloat());
                    double d1 = (double) ((float) blockpos.getY() + this.worldObj.rand.nextFloat());
                    double d2 = (double) ((float) blockpos.getZ() + this.worldObj.rand.nextFloat());
                    double d3 = d0 - this.explosionX;
                    double d4 = d1 - this.explosionY;
                    double d5 = d2 - this.explosionZ;
                    double d6 = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 = d3 / d6;
                    d4 = d4 / d6;
                    d5 = d5 / d6;
                    double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
                    d7 = d7 * (double) (this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                    d3 = d3 * d7;
                    d4 = d4 * d7;
                    d5 = d5 * d7;
                    this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5, new int[0]);
                    this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
                }

                if (block.getMaterial() != Material.air) {
                    if (block.canDropFromExplosion(this)) {
                        IBlockState state = this.worldObj.getBlockState(blockpos);
                        if (this.fortune >= 4) {
                            if (block.canSilkHarvest(this.worldObj, blockpos, state, this.exploder instanceof EntityPlayer ? (EntityPlayer) this.exploder : null)) {
                                List<ItemStack> items = new ArrayList<ItemStack>();
                                ItemStack stack = null;
                                if (block instanceof IStackedBlock) stack = ((IStackedBlock) block).createNewStackedBlock(state);
                                if (stack == null) {
                                    Item item = Item.getItemFromBlock(block);
                                    if (item != null) stack = new ItemStack(item, 1, item.getHasSubtypes() ? block.getMetaFromState(state) : 0);
                                }
                                items.add(stack);
                                ForgeEventFactory.fireBlockHarvesting(items, this.worldObj, blockpos, state, 0, 1F, true, null);

                                for (ItemStack cstack : items) {
                                    if (cstack != null) Block.spawnAsEntity(this.worldObj, blockpos, cstack);
                                }
                            }
                        } else {
                            block.dropBlockAsItem(this.worldObj, blockpos, this.worldObj.getBlockState(blockpos), this.fortune);
                        }
                    }

                    block.onBlockExploded(this.worldObj, blockpos, this);
                }
                ite.remove();
            }
        }
        return this.affectedBlockPositions.isEmpty();
    }

    /**
     * @return the fortune
     */
    public int getFortune() {
        return fortune;
    }

    /**
     * @param fortune
     *            the fortune to set
     */
    public void setFortune(int fortune) {
        this.fortune = fortune;
    }

    /**
     * @return the spawnparticles
     */
    public boolean isSpawnparticles() {
        return spawnparticles;
    }

    /**
     * @param spawnparticles
     *            the spawnparticles to set
     */
    public void setSpawnparticles(boolean spawnparticles) {
        this.spawnparticles = spawnparticles;
    }

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

    public static MiningExplosion createExplosion(World world, Entity entityIn, double x, double y, double z, float strength, int fortune, boolean isSmoking, boolean disabled) {
        return newExplosion(world, entityIn, x, y, z, strength, fortune, false, isSmoking, disabled);
    }

    public static MiningExplosion newExplosion(World world, Entity entityIn, double x, double y, double z, float strength, int fortune, boolean isFlaming, boolean isSmoking, boolean disabled) {
        MiningExplosion explosion = new MiningExplosion(world, entityIn, x, y, z, strength, isFlaming, isSmoking, fortune, disabled);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return explosion;
        explosion.doExplosionA();
        explosion.doExplosionB(false);
        if (!world.isRemote) {
            for (EntityPlayer entityplayer : world.playerEntities) {
                if (entityplayer.getDistanceSq(x, y, z) < 4096.0D) MiningTnt.NETWORK.sendTo(new PacketMiningExplosion(explosion.explosionX, explosion.explosionY, explosion.explosionZ, explosion.explosionSize, explosion.isDisabled(), explosion.getAffectedBlockPositions()), (EntityPlayerMP) entityplayer);
            }
        }
        MiningExplosionTickHandler.registerMiningExplosion(explosion);
        return explosion;
    }

    public static class BlockPosComparator implements Comparator<BlockPos>
    {
        public final BlockPos expPos;

        public BlockPosComparator(BlockPos expPos) {
            this.expPos = expPos;
        }

        @Override
        public int compare(BlockPos bp1, BlockPos bp2) {
            int i1 = MathHelper.floor_double(bp1.distanceSq(expPos));
            int i2 = MathHelper.floor_double(bp2.distanceSq(expPos));
            return i1 == i2 ? 0 : (i1 > i2 ? 1 : -1);
        }
    }
}
