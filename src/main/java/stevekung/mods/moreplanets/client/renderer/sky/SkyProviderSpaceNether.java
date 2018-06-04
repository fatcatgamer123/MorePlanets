package stevekung.mods.moreplanets.client.renderer.sky;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import stevekung.mods.moreplanets.client.renderer.sky.SkyProviderBaseMP;

public class SkyProviderSpaceNether extends SkyProviderBaseMP
{
    private static final ResourceLocation SUN = new ResourceLocation("galacticraftcore:textures/gui/planets/orbitalsun.png");
    private static final ResourceLocation MERCURY = new ResourceLocation("galacticraftcore:textures/gui/celestialbodies/mercury.png");

    public SkyProviderSpaceNether(IGalacticraftWorldProvider provider)
    {
        super();
        this.sunSize = 17.5F * provider.getSolarSize();
    }

    @Override
    protected void renderPlanetInSky(float partialTicks, WorldClient world, Minecraft mc)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        float scale;

        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F, 1.0F);
        scale = 19.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(-scale, 99.9D, -scale).endVertex();
        worldrenderer.pos(scale, 99.9D, -scale).endVertex();
        worldrenderer.pos(scale, 99.9D, scale).endVertex();
        worldrenderer.pos(-scale, 99.9D, scale).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Sirius
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 1);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 1.0F);

        // Some blanking to conceal the stars
        scale = this.sunSize / 10.5F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(-scale, 99.9D, -scale).endVertex();
        worldrenderer.pos(scale, 99.9D, -scale).endVertex();
        worldrenderer.pos(scale, 99.9D, scale).endVertex();
        worldrenderer.pos(-scale, 99.9D, scale).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        scale = this.sunSize + 6.0F;
        mc.getTextureManager().bindTexture(SkyProviderSpaceNether.SUN);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-scale, 100.0D, -scale).tex(0.0D, 0.0D).endVertex();
        worldrenderer.pos(scale, 100.0D, -scale).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(scale, 100.0D, scale).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(-scale, 100.0D, scale).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.pushMatrix();

        scale = 3.0F;
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
        GlStateManager.rotate(50.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200F, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
        GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        mc.getTextureManager().bindTexture(SkyProviderSpaceNether.MERCURY);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-scale, 100.0D, -scale).tex(0.0D, 0.0D).endVertex();
        worldrenderer.pos(scale, 100.0D, -scale).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(scale, 100.0D, scale).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(-scale, 100.0D, scale).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    @Override
    protected double[] getMaxStarCount()
    {
        return new double[] { 50000D, 150D, 100D };
    }

    @Override
    protected float[] getStarBrightness()
    {
        return new float[] { 0.35F, 0.35F };
    }

    @Override
    protected boolean useDefaultStarBrightness()
    {
        return false;
    }

    @Override
    protected float getRainStrength(World world, float partialTicks)
    {
        return 0.0F;
    }
}