package fr.scarex.miningtnt.network;

import java.util.List;

import com.google.common.collect.Lists;

import fr.scarex.miningtnt.world.MiningExplosion;
import fr.scarex.miningtnt.world.MiningExplosionTickHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author SCAREX
 *
 */
public class PacketMiningExplosion implements IMessage
{
    private double posX;
    private double posY;
    private double posZ;
    private float strength;
    private boolean disabled;
    private List<BlockPos> affectedBlockPositions;

    public PacketMiningExplosion() {}

    public PacketMiningExplosion(double posX, double posY, double posZ, float strength, boolean disabled, List<BlockPos> affectedBlockPositions) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.strength = strength;
        this.affectedBlockPositions = affectedBlockPositions;
        this.disabled = disabled;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posX = buf.readFloat();
        this.posY = buf.readFloat();
        this.posZ = buf.readFloat();
        this.strength = buf.readFloat();
        this.disabled = buf.readBoolean();

        int i = buf.readInt();
        affectedBlockPositions = Lists.newArrayListWithCapacity(i);
        int j = (int) this.posX;
        int k = (int) this.posY;
        int l = (int) this.posZ;
        for (int i1 = 0; i1 < i; i1++) {
            int j1 = buf.readByte() + j;
            int k1 = buf.readByte() + k;
            int l1 = buf.readByte() + l;
            this.affectedBlockPositions.add(new BlockPos(j1, k1, l1));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat((float) this.posX);
        buf.writeFloat((float) this.posY);
        buf.writeFloat((float) this.posZ);
        buf.writeFloat(this.strength);
        buf.writeBoolean(this.disabled);

        buf.writeInt(this.affectedBlockPositions.size());
        int relativeX = (int) this.posX;
        int relativeY = (int) this.posY;
        int relativeZ = (int) this.posZ;

        for (BlockPos blockpos : this.affectedBlockPositions) {
            int x = blockpos.getX() - relativeX;
            int y = blockpos.getY() - relativeY;
            int z = blockpos.getZ() - relativeZ;
            buf.writeByte(x);
            buf.writeByte(y);
            buf.writeByte(z);
        }
    }
    
    public static class ClientHandler implements IMessageHandler<PacketMiningExplosion, IMessage>
    {
        @Override
        public IMessage onMessage(PacketMiningExplosion message, MessageContext ctx) {
            MiningExplosion explosion = new MiningExplosion(FMLClientHandler.instance().getWorldClient(), null, message.posX, message.posY, message.posZ, message.strength, message.disabled, message.affectedBlockPositions);
            explosion.doExplosionB(true);
            MiningExplosionTickHandler.registerMiningExplosion(explosion);
            return null;
        }
    }
}
