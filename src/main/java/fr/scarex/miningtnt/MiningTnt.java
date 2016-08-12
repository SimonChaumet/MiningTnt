package fr.scarex.miningtnt;

import fr.scarex.miningtnt.block.BlockMiningTnt;
import fr.scarex.miningtnt.block.itemblock.ItemBlockMiningTnt;
import fr.scarex.miningtnt.client.entity.RenderMiningTntPrimed;
import fr.scarex.miningtnt.client.entity.RenderThrowableMiningTnt;
import fr.scarex.miningtnt.entity.EntityMiningTntPrimed;
import fr.scarex.miningtnt.entity.EntityThrowableMiningTnt;
import fr.scarex.miningtnt.item.ItemThrowableMiningTnt;
import fr.scarex.miningtnt.network.PacketMiningExplosion;
import fr.scarex.miningtnt.recipe.MiningTntRecipe;
import fr.scarex.miningtnt.recipe.ThrowableMiningTntRecipe;
import fr.scarex.miningtnt.recipe.WetThrowableMiningTntRecipe;
import fr.scarex.miningtnt.tileentity.TileEntityMiningTnt;
import fr.scarex.miningtnt.world.MiningExplosionTickHandler;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;

/**
 * @author SCAREX
 *
 */
@Mod(modid = MiningTnt.MODID, name = MiningTnt.NAME, version = MiningTnt.VERSION)
public class MiningTnt
{
    public static final String MODID = "miningtnt";
    public static final String NAME = "Mining Tnt";
    public static final String VERSION = "@VERSION@";

    public static BlockMiningTnt BLOCK_MINING_TNT;
    public static ItemThrowableMiningTnt ITEM_THROWABLE_MINING_TNT;

    public static CreativeTabs CREATIVE_TAB = new CreativeTabs(MODID) {
        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(BLOCK_MINING_TNT);
        }
    };
    
    public static SimpleNetworkWrapper NETWORK;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MiningTntConfiguration.load(event.getSuggestedConfigurationFile());

        BLOCK_MINING_TNT = new BlockMiningTnt();
        GameRegistry.registerBlock(BLOCK_MINING_TNT, ItemBlockMiningTnt.class, "mining_tnt");
        GameRegistry.registerTileEntity(TileEntityMiningTnt.class, MiningTnt.MODID + ":mining_tnt");

        ITEM_THROWABLE_MINING_TNT = new ItemThrowableMiningTnt();
        GameRegistry.registerItem(ITEM_THROWABLE_MINING_TNT, "throwable_mining_tnt");

        if (event.getSide().isClient()) {
            ModelResourceLocation blockMiningTntModel = new ModelResourceLocation(MiningTnt.MODID + ":mining_tnt", "normal");
            for (int i = 0; i <= 7; i++) {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_MINING_TNT), i, blockMiningTntModel);
            }
            ModelResourceLocation itemThrowableMiningTnt = new ModelResourceLocation(MiningTnt.MODID + ":throwable_mining_tnt", "inventory");
            for (int i = 0; i <= 7; i++) {
                ModelLoader.setCustomModelResourceLocation(ITEM_THROWABLE_MINING_TNT, i, itemThrowableMiningTnt);
            }
        }

        EntityRegistry.registerModEntity(EntityMiningTntPrimed.class, "miningTntPrimed", 420, this, 40, 1, true);
        EntityRegistry.registerModEntity(EntityThrowableMiningTnt.class, "throwableMiningTnt", 421, this, 40, 1, true);
        if (event.getSide().isClient()) {
            RenderingRegistry.registerEntityRenderingHandler(EntityMiningTntPrimed.class, new RenderMiningTntPrimed.Factory());
            RenderingRegistry.registerEntityRenderingHandler(EntityThrowableMiningTnt.class, new RenderThrowableMiningTnt.Factory());
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        CraftingManager.getInstance().addRecipe(new MiningTntRecipe());
        RecipeSorter.register(MiningTnt.MODID + ":miningTntRecipe", MiningTntRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shaped");

        CraftingManager.getInstance().addRecipe(new ThrowableMiningTntRecipe());
        RecipeSorter.register(MiningTnt.MODID + ":throwableMiningTntRecipe", ThrowableMiningTntRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        
        CraftingManager.getInstance().addRecipe(new WetThrowableMiningTntRecipe());
        RecipeSorter.register(MiningTnt.MODID + ":wetThrowableMiningTntRecipe", WetThrowableMiningTntRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        MinecraftForge.EVENT_BUS.register(new MiningExplosionTickHandler());
        
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MiningTnt.MODID);
        int clientDiscri = 0;
        NETWORK.registerMessage(PacketMiningExplosion.ClientHandler.class, PacketMiningExplosion.class, clientDiscri++, Side.CLIENT);
    }
}
