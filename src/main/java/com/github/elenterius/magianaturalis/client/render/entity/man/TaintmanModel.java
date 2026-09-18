package com.github.elenterius.magianaturalis.client.render.entity.man;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

/**
 * ModelTaintman - TrinaryBrain
 * Created using Tabula 4.1.1
 */
public class TaintmanModel extends ModelBase {

    public ModelRenderer Body;
    public ModelRenderer Headwear;
    public ModelRenderer spore;
    public ModelRenderer RightArm;
    public ModelRenderer LeftArm;
    public ModelRenderer RightLeg;
    public ModelRenderer LeftLeg;
    public ModelRenderer Head;

    public TaintmanModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Body = new ModelRenderer(this, 32, 0);
        this.Body.setRotationPoint(0.0F, -1.6F, 0.0F);
        this.Body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.Headwear = new ModelRenderer(this, 0, 16);
        this.Headwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Headwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.LeftLeg = new ModelRenderer(this, 56, 0);
        this.LeftLeg.mirror = true;
        this.LeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.LeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 20, 2, 0.0F);
        this.spore = new ModelRenderer(this, 32, 16);
        this.spore.setRotationPoint(0.0F, 7.0F, -2.5F);
        this.spore.addBox(-3.0F, -4.0F, -1.5F, 6, 8, 3, 0.0F);
        this.LeftArm = new ModelRenderer(this, 56, 0);
        this.LeftArm.mirror = true;
        this.LeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
        this.LeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 25, 2, 0.0F);
        this.setRotateAngle(LeftArm, 0.0F, 0.0F, -0.17366026057343578F);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-4.0F, -12.0F, -4.0F, 8, 8, 8, 0.0F);
        this.RightLeg = new ModelRenderer(this, 56, 0);
        this.RightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.RightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 20, 2, 0.0F);
        this.RightArm = new ModelRenderer(this, 56, 0);
        this.RightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
        this.RightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 25, 2, 0.0F);
        this.setRotateAngle(RightArm, 0.0F, 0.0F, 0.17366026057343578F);
        this.Body.addChild(this.Headwear);
        this.Body.addChild(this.LeftLeg);
        this.Body.addChild(this.spore);
        this.Body.addChild(this.LeftArm);
        this.Headwear.addChild(this.Head);
        this.Body.addChild(this.RightLeg);
        this.Body.addChild(this.RightArm);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.Body.offsetX, this.Body.offsetY, this.Body.offsetZ);
        GL11.glTranslatef(this.Body.rotationPointX * f5, this.Body.rotationPointY * f5, this.Body.rotationPointZ * f5);
        GL11.glScaled(0.5D, 0.5D, 0.5D); // 0.8D, 0.8D, 0.8D
        GL11.glTranslatef(-this.Body.offsetX, -this.Body.offsetY + 1.19F, -this.Body.offsetZ); // Ohne 1.19F
        GL11.glTranslatef(
            -this.Body.rotationPointX * f5,
            -this.Body.rotationPointY * f5,
            -this.Body.rotationPointZ * f5);
        this.Body.render(f5);
        GL11.glPopMatrix();
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
