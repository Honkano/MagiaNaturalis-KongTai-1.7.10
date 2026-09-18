package com.github.elenterius.magianaturalis.client.render.entity.breeder;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

public class TaintBreederModel extends ModelBase {

    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer rearEnd0;
    public ModelRenderer rearEnd1;
    public ModelRenderer leg8;
    public ModelRenderer leg6;
    public ModelRenderer leg4;
    public ModelRenderer leg2;
    public ModelRenderer leg7;
    public ModelRenderer leg5;
    public ModelRenderer leg3;
    public ModelRenderer leg1;
    public ModelRenderer headClaw;
    public ModelRenderer headClaw2;
    public ModelRenderer eye0;
    public ModelRenderer eye1;

    public TaintBreederModel() {
        textureWidth = 64;
        textureHeight = 64;

        head = new ModelRenderer(this, 32, 4);
        head.addBox(-2.5F, -4F, -5F, 5, 6, 6);
        head.setRotationPoint(0F, 20F, -3F);
        head.setTextureSize(64, 64);
        setRotation(head, 0.0698132F, 0F, 0F);

        body = new ModelRenderer(this, 0, 0);
        body.addBox(-3F, -3F, -3F, 6, 6, 8);
        body.setRotationPoint(0F, 20F, 0F);
        body.setTextureSize(64, 64);
        setRotation(body, 0.1396263F, 0F, 0F);

        rearEnd0 = new ModelRenderer(this, 0, 14);
        rearEnd0.addBox(-5F, -4F, 0F, 10, 9, 12);
        rearEnd0.setRotationPoint(0F, 19F, 3F);
        rearEnd0.setTextureSize(64, 64);
        setRotation(rearEnd0, 0.3490659F, 0F, 0F);

        leg8 = new ModelRenderer(this, 21, 0);
        leg8.addBox(-1F, -1F, -1F, 16, 2, 2);
        leg8.setRotationPoint(4F, 20F, -1F);
        leg8.setTextureSize(64, 64);

        leg6 = new ModelRenderer(this, 21, 0);
        leg6.addBox(-1F, -1F, -1F, 16, 2, 2);
        leg6.setRotationPoint(4F, 20F, 0F);
        leg6.setTextureSize(64, 64);

        leg4 = new ModelRenderer(this, 21, 0);
        leg4.addBox(-1F, -1F, -1F, 16, 2, 2);
        leg4.setRotationPoint(4F, 20F, 1F);
        leg4.setTextureSize(64, 64);

        leg2 = new ModelRenderer(this, 21, 0);
        leg2.addBox(-1F, -1F, -1F, 16, 2, 2);
        leg2.setRotationPoint(4F, 20F, 2F);
        leg2.setTextureSize(64, 64);

        leg7 = new ModelRenderer(this, 21, 0);
        leg7.mirror = true;
        leg7.addBox(-15F, -1F, -1F, 16, 2, 2);
        leg7.setRotationPoint(-4F, 20F, -1F);
        leg7.setTextureSize(64, 64);

        leg5 = new ModelRenderer(this, 21, 0);
        leg5.mirror = true;
        leg5.addBox(-15F, -1F, -1F, 16, 2, 2);
        leg5.setRotationPoint(-4F, 20F, 0F);
        leg5.setTextureSize(64, 64);

        leg3 = new ModelRenderer(this, 21, 0);
        leg3.mirror = true;
        leg3.addBox(-15F, -1F, -1F, 16, 2, 2);
        leg3.setRotationPoint(-4F, 20F, 1F);
        leg3.setTextureSize(64, 64);

        leg1 = new ModelRenderer(this, 21, 0);
        leg1.mirror = true;
        leg1.addBox(-15F, -1F, -1F, 16, 2, 2);
        leg1.setRotationPoint(-4F, 20F, 2F);
        leg1.setTextureSize(64, 64);

        headClaw = new ModelRenderer(this, 32, 16);
        headClaw.addBox(-1.5F, -2.5F, -11F, 3, 2, 7);
        headClaw.setRotationPoint(0F, 20F, -3F);
        headClaw.setTextureSize(64, 64);
        setRotation(headClaw, -0.0194155F, 0F, 0F);

        headClaw2 = new ModelRenderer(this, 44, 26);
        headClaw2.mirror = true;
        headClaw2.addBox(-1.5F, -0.5F, -11F, 3, 2, 7);
        headClaw2.setRotationPoint(0F, 20F, -3F);
        headClaw2.setTextureSize(64, 64);
        setRotation(headClaw2, 0.0400703F, 0F, 0F);

        eye0 = new ModelRenderer(this, 0, 35);
        eye0.mirror = true;
        eye0.addBox(-3.5F, -5F, -3F, 2, 4, 2);
        eye0.setRotationPoint(0F, 20F, -3F);
        eye0.setTextureSize(64, 64);
        setRotation(eye0, 0.0698132F, 0F, 0F);

        eye1 = new ModelRenderer(this, 0, 35);
        eye1.addBox(1.5F, -5F, -3F, 2, 4, 2);
        eye1.setRotationPoint(0F, 20F, -3F);
        eye1.setTextureSize(64, 64);
        setRotation(eye1, 0.0698132F, 0F, 0F);

        rearEnd1 = new ModelRenderer(this, 8, 35);
        rearEnd1.addBox(-3.5F, -3F, 12F, 7, 7, 2);
        rearEnd1.setRotationPoint(0F, 19F, 3F);
        rearEnd1.setTextureSize(64, 64);
        setRotation(rearEnd1, 0.3490659F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float prevLimbSwing, float rotationTicks, float rotationYaw,
        float rotationPitch, float scale) {
        GL11.glPushMatrix();
        setRotationAngles(limbSwing, prevLimbSwing, rotationTicks, rotationYaw, rotationPitch, scale, entity);
        GL11.glTranslatef(0F, -0.25F, 0F);
        head.render(scale);
        body.render(scale);
        leg8.render(scale);
        leg6.render(scale);
        leg4.render(scale);
        leg2.render(scale);
        leg7.render(scale);
        leg5.render(scale);
        leg3.render(scale);
        leg1.render(scale);
        headClaw.render(scale);
        headClaw2.render(scale);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        eye0.render(scale);
        eye1.render(scale);

        EntityLiving entityLiving = (EntityLiving) entity;
        float health = Math.max(entityLiving.getMaxHealth() - entityLiving.getHealth(), 1) * 0.5F;
        float bob = MathHelper.sin(rotationTicks) * 0.0133F * health;
        rearEnd0.rotateAngleY = bob;
        rearEnd1.rotateAngleY = bob;
        rearEnd0.rotateAngleX = 0.3490659F + bob;
        rearEnd1.rotateAngleX = 0.3490659F + bob;

        headClaw.rotateAngleX = -0.0194155F + bob * 0.5F;
        headClaw2.rotateAngleX = 0.0194155F - bob * 0.25F;

        rearEnd0.render(scale);
        rearEnd1.render(scale);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float limbSwing, float prevLimbSwing, float rotationTicks, float rotationYaw,
        float rotationPitch, float scale, Entity entity) {
        headClaw.rotateAngleY = headClaw2.rotateAngleY = eye0.rotateAngleY = eye1.rotateAngleY = head.rotateAngleY = rotationYaw
            / (180F / (float) Math.PI);
        float f7 = ((float) Math.PI / 4F);
        leg1.rotateAngleZ = -f7;
        leg2.rotateAngleZ = f7;
        leg3.rotateAngleZ = -f7 * 0.74F;
        leg4.rotateAngleZ = f7 * 0.74F;
        leg5.rotateAngleZ = -f7 * 0.74F;
        leg6.rotateAngleZ = f7 * 0.74F;
        leg7.rotateAngleZ = -f7;
        leg8.rotateAngleZ = f7;
        float f8 = -0.0F;
        float f9 = 0.3926991F;
        leg1.rotateAngleY = f9 * 2.0F + f8;
        leg2.rotateAngleY = -f9 * 2.0F - f8;
        leg3.rotateAngleY = f9 * 1.0F + f8;
        leg4.rotateAngleY = -f9 * 1.0F - f8;
        leg5.rotateAngleY = -f9 * 1.0F + f8;
        leg6.rotateAngleY = f9 * 1.0F - f8;
        leg7.rotateAngleY = -f9 * 2.0F + f8;
        leg8.rotateAngleY = f9 * 2.0F - f8;
        float f10 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * prevLimbSwing;
        float f11 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * prevLimbSwing;
        float f12 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI / 2F)) * 0.4F) * prevLimbSwing;
        float f13 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI * 3F / 2F)) * 0.4F) * prevLimbSwing;
        float f14 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * prevLimbSwing;
        float f15 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * prevLimbSwing;
        float f16 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.4F) * prevLimbSwing;
        float f17 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 3F / 2F)) * 0.4F) * prevLimbSwing;
        leg1.rotateAngleY += f10;
        leg2.rotateAngleY += -f10;
        leg3.rotateAngleY += f11;
        leg4.rotateAngleY += -f11;
        leg5.rotateAngleY += f12;
        leg6.rotateAngleY += -f12;
        leg7.rotateAngleY += f13;
        leg8.rotateAngleY += -f13;
        leg1.rotateAngleZ += f14;
        leg2.rotateAngleZ += -f14;
        leg3.rotateAngleZ += f15;
        leg4.rotateAngleZ += -f15;
        leg5.rotateAngleZ += f16;
        leg6.rotateAngleZ += -f16;
        leg7.rotateAngleZ += f17;
        leg8.rotateAngleZ += -f17;
    }
}
