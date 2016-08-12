package fr.scarex.miningtnt;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * @author SCAREX
 *
 */
public final class MiningTntConfiguration
{
    public static Configuration CONFIG;
    public static String CATEGORY;
    public static int[] explosionStrength = new int[] {
            4, 6, 10, 14, 18, 22, 24,
            26 };
    public static int[] explosionDurability = new int[] {
            30, 50, 80, 120, 200, 400, 800, 1561
    };
    public static int blockExplodedPerTick = 1200;

    public static void load(File f) {
        CONFIG = new Configuration(f);
        syncConfig();
    }

    public static void syncConfig() {
        CATEGORY = CONFIG.CATEGORY_GENERAL;
        CONFIG.load();

        explosionStrength = CONFIG.get(CATEGORY, "explosionStrength", explosionStrength, "Set the explosion strength for each size", 0, 40, true, 8).getIntList();
        explosionDurability = CONFIG.get(CATEGORY, "explosionDurability", explosionDurability, "Set the durability consumed when crafted", 0, 2000, true, 8).getIntList();
        blockExplodedPerTick = CONFIG.getInt(CATEGORY, "blockExplodedPerTick", blockExplodedPerTick, 1, 100000, "Set the number of blocks exploded each tick");

        if (CONFIG.hasChanged()) CONFIG.save();
    }
}
