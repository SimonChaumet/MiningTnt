package fr.scarex.miningtnt.block;

import java.util.List;

import fr.scarex.miningtnt.MiningTnt;
import fr.scarex.miningtnt.entity.EntityMiningTntPrimed;
import fr.scarex.miningtnt.tileentity.TileEntityMiningTnt;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * @author SCAREX
 *
 */
public class BlockMiningTnt extends Block implements ITileEntityProvider
{
    public static final PropertyInteger FORTUNE = PropertyInteger.create("fortune", 0, 4);

    public BlockMiningTnt() {
        super(Material.tnt);
        this.setHardness(0F);
        this.setStepSound(soundTypeGrass);
        this.setCreativeTab(MiningTnt.CREATIVE_TAB);
        this.setUnlocalizedName("mining_tnt");
        this.setDefaultState(this.blockState.getBaseState().withProperty(FORTUNE, 0));
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 8; i++) {
            NBTTagCompound comp = new NBTTagCompound();
            NBTTagCompound compBlock = new NBTTagCompound();
            compBlock.setInteger("strength", i);
            comp.setTag("BlockEntityTag", compBlock);
            for (int j = 0; j <= 4; j++) {
                ItemStack stack = new ItemStack(this, 1, j);
                stack.setTagCompound((NBTTagCompound) comp.copy());
                list.add(stack);
            }
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);

        if (worldIn.isBlockPowered(pos)) {
            this.explode(worldIn, pos, state, null);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (worldIn.isBlockPowered(pos)) {
            this.explode(worldIn, pos, state, null);
            worldIn.setBlockToAir(pos);
        }
    }

    public void explode(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase igniter) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileEntityMiningTnt) {
            int strength = ((TileEntityMiningTnt) worldIn.getTileEntity(pos)).getStrength();
            EntityMiningTntPrimed entitytntprimed = new EntityMiningTntPrimed(worldIn, (double) ((float) pos.getX() + 0.5F), (double) pos.getY(), (double) ((float) pos.getZ() + 0.5F), igniter, strength, state.getValue(FORTUNE).intValue());
            worldIn.spawnEntityInWorld(entitytntprimed);
            worldIn.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
        }
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof TileEntityMiningTnt) {
            int strength = ((TileEntityMiningTnt) world.getTileEntity(pos)).getStrength();
            EntityMiningTntPrimed entitytntprimed = new EntityMiningTntPrimed(world, (double) ((float) pos.getX() + 0.5F), (double) pos.getY(), (double) ((float) pos.getZ() + 0.5F), explosion.getExplosivePlacedBy(), strength, world.getBlockState(pos).getValue(FORTUNE).intValue());
            entitytntprimed.fuse = world.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
            world.spawnEntityInWorld(entitytntprimed);
        }
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (playerIn.getCurrentEquippedItem() != null) {
            Item item = playerIn.getCurrentEquippedItem().getItem();

            if (item == Items.flint_and_steel || item == Items.fire_charge) {
                this.explode(worldIn, pos, state, playerIn);
                worldIn.setBlockToAir(pos);

                if (item == Items.flint_and_steel) {
                    playerIn.getCurrentEquippedItem().damageItem(1, playerIn);
                } else if (!playerIn.capabilities.isCreativeMode) {
                    --playerIn.getCurrentEquippedItem().stackSize;
                }

                return true;
            }
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entityIn;

            if (entityarrow.isBurning()) {
                this.explode(worldIn, pos, worldIn.getBlockState(pos), entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) entityarrow.shootingEntity : null);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FORTUNE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((Integer) state.getValue(FORTUNE)).intValue();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {
                FORTUNE });
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(FORTUNE).intValue();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMiningTnt();
    }
}
