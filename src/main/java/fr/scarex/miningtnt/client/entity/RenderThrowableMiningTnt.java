package fr.scarex.miningtnt.client.entity;

import fr.scarex.miningtnt.MiningTnt;
import fr.scarex.miningtnt.entity.EntityThrowableMiningTnt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * @author SCAREX
 *
 */
public class RenderThrowableMiningTnt extends Render<EntityThrowableMiningTnt>
{
    private final RenderItem renderItem;
    
    protected RenderThrowableMiningTnt(RenderManager renderManager, RenderItem renderItem) {
        super(renderManager);
        this.renderItem = renderItem;
    }

    @Override
    public void doRender(EntityThrowableMiningTnt entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        bindTexture(TextureMap.locationBlocksTexture);
        this.renderItem.renderItem(new ItemStack(MiningTnt.ITEM_THROWABLE_MINING_TNT), ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityThrowableMiningTnt entity) {
        return TextureMap.locationBlocksTexture;
    }
    
    public static class Factory implements IRenderFactory<EntityThrowableMiningTnt>
    {
        @Override
        public Render<? super EntityThrowableMiningTnt> createRenderFor(RenderManager manager) {
            return new RenderThrowableMiningTnt(manager, Minecraft.getMinecraft().getRenderItem());
        }
    }
}
