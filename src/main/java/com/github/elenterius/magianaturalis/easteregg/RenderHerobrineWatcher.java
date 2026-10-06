package com.github.elenterius.magianaturalis.easteregg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderHerobrineWatcher extends RenderLiving {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        "magianaturalis:textures/entity/herobrine.png");

    public RenderHerobrineWatcher() {
        // ← 这里换成自定义模型
        super(new ModelHerobrineWatcher(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player != null && entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;

            double dx = player.posX - living.posX;
            double dz = player.posZ - living.posZ;
            float targetYaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;

            living.rotationYaw = targetYaw;
            living.prevRotationYaw = targetYaw;
            living.rotationYawHead = targetYaw;
            living.prevRotationYawHead = targetYaw;
            living.renderYawOffset = targetYaw;
            living.prevRenderYawOffset = targetYaw;
            living.rotationPitch = 0.0F;
            living.prevRotationPitch = 0.0F;
        }

        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected void renderModel(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks,
        float netHeadYaw, float headPitch, float scale) {

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

        super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GL11.glPopAttrib();
    }
}