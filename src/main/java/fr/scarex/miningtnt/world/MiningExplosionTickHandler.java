package fr.scarex.miningtnt.world;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author SCAREX
 *
 */
public class MiningExplosionTickHandler
{
    private static final Object LOCK = new Object();
    private static final List<MiningExplosion> explosionList = Lists.newArrayList();

    public static void registerMiningExplosion(MiningExplosion exp) {
        synchronized (LOCK) {
            explosionList.add(exp);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        synchronized (LOCK) {
            Iterator<MiningExplosion> ite = this.explosionList.iterator();
            while (ite.hasNext()) {
                MiningExplosion exp = ite.next();
                if (exp.updateExplosionB()) ite.remove();
            }
        }
    }
}
