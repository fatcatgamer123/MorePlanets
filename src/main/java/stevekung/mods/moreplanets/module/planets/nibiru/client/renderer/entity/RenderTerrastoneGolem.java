package stevekung.mods.moreplanets.module.planets.nibiru.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.module.planets.nibiru.client.model.ModelTerrastoneGolem;
import stevekung.mods.moreplanets.module.planets.nibiru.entity.EntityTerrastoneGolem;

@SideOnly(Side.CLIENT)
public class RenderTerrastoneGolem extends RenderLiving<EntityTerrastoneGolem>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("moreplanets:textures/entity/terrastone_golem.png");

    public RenderTerrastoneGolem(RenderManager manager)
    {
        super(manager, new ModelTerrastoneGolem(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTerrastoneGolem entity)
    {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityTerrastoneGolem entity, float partialTicks)
    {
        GlStateManager.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    protected void rotateCorpse(EntityTerrastoneGolem entity, float pitch, float yaw, float partialTicks)
    {
        super.rotateCorpse(entity, pitch, yaw, partialTicks);

        if (entity.limbSwingAmount >= 0.01D)
        {
            float f1 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
        }
    }
}