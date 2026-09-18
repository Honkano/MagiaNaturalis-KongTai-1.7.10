package com.github.elenterius.magianaturalis.client.render.entity.trunk;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.client.render.RenderUtil;
import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;

public class DemonicTrunkModel extends ModelBase {

    public ModelRenderer chestSkull;
    public ModelRenderer chestJaw;
    public ModelRenderer chestTooth1;
    public ModelRenderer chestToothTop1;
    public ModelRenderer chestTooth2;
    public ModelRenderer chestToothTop2;
    public ModelRenderer chestTooth3;
    public ModelRenderer chestTooth4;
    public ModelRenderer chestHorn1;
    public ModelRenderer chestHorn2;
    public ModelRenderer chestHorn3;
    public ModelRenderer chestHorn4;
    public ModelRenderer chestHornTop1;
    public ModelRenderer chestHornTop2;
    public ModelRenderer chestRightWing;
    public ModelRenderer chestOuterRightWing;
    public ModelRenderer chestLeftWing;
    public ModelRenderer chestOuterLeftWing;

    public DemonicTrunkModel() {
        chestSkull = new ModelRenderer(this, 0, 19).setTextureSize(128, 64);
        chestSkull.addBox(0F, -10F, -14F, 14, 10, 14);
        chestSkull.setRotationPoint(1F, 11F, 15F);

        chestJaw = new ModelRenderer(this, 0, 0).setTextureSize(128, 64);
        chestJaw.addBox(0F, 0F, -14F, 14, 5, 14);
        chestJaw.setRotationPoint(1F, 11F, 15F);

        chestTooth1 = new ModelRenderer(this, 7, 0).setTextureSize(128, 64);
        chestTooth1.addBox(-1F, -1F, -14.5F, 2, 3, 1);
        chestTooth1.setRotationPoint(13F, 11F, 15F);

        chestToothTop1 = new ModelRenderer(this, 9, 0).setTextureSize(128, 64);
        chestToothTop1.addBox(0F, -2F, -14.5F, 1, 1, 1);
        chestToothTop1.setRotationPoint(13F, 11F, 15F);

        chestTooth2 = new ModelRenderer(this, 0, 0).setTextureSize(128, 64);
        chestTooth2.addBox(-1F, -1F, -14.5F, 2, 3, 1);
        chestTooth2.setRotationPoint(3F, 11F, 15F);

        chestToothTop2 = new ModelRenderer(this, 0, 0).setTextureSize(128, 64);
        chestToothTop2.addBox(-1F, -2F, -14.5F, 1, 1, 1);
        chestToothTop2.setRotationPoint(3F, 11F, 15F);

        chestTooth3 = new ModelRenderer(this, 0, 0).setTextureSize(128, 64);
        chestTooth3.addBox(-0.7F, -1F, -14.5F, 2, 3, 1);
        chestTooth3.setRotationPoint(6F, 11F, 15F);

        chestTooth4 = new ModelRenderer(this, 7, 0).setTextureSize(128, 64);
        chestTooth4.addBox(-1.25F, -1F, -14.5F, 2, 3, 1);
        chestTooth4.setRotationPoint(10F, 11F, 15F);

        chestHorn1 = new ModelRenderer(this, 0, 5).setTextureSize(128, 64);
        chestHorn1.addBox(1F, -12F, -13F, 2, 2, 2);
        chestHorn1.setRotationPoint(1F, 11F, 15F);

        chestHorn2 = new ModelRenderer(this, 0, 5).setTextureSize(128, 64);
        chestHorn2.addBox(2F, -14F, -13F, 2, 2, 2);
        chestHorn2.setRotationPoint(1F, 11F, 15F);

        chestHorn3 = new ModelRenderer(this, 0, 5).setTextureSize(128, 64);
        chestHorn3.addBox(11F, -12F, -13F, 2, 2, 2);
        chestHorn3.setRotationPoint(1F, 11F, 15F);

        chestHorn4 = new ModelRenderer(this, 0, 5).setTextureSize(128, 64);
        chestHorn4.addBox(10F, -14F, -13F, 2, 2, 2);
        chestHorn4.setRotationPoint(1F, 11F, 15F);

        chestHornTop1 = new ModelRenderer(this, 3, 6).setTextureSize(128, 64);
        chestHornTop1.addBox(10F, -15F, -12F, 1, 1, 1);
        chestHornTop1.setRotationPoint(1F, 11F, 15F);

        chestHornTop2 = new ModelRenderer(this, 3, 6).setTextureSize(128, 64);
        chestHornTop2.addBox(3F, -15F, -12F, 1, 1, 1);
        chestHornTop2.setRotationPoint(1F, 11F, 15F);

        chestRightWing = new ModelRenderer(this, 56, 0).setTextureSize(128, 64);
        chestRightWing.addBox(0F, -12F, -0.5F, 10, 16, 1);
        chestRightWing.setRotationPoint(15F, 11F, 9F);

        chestOuterRightWing = new ModelRenderer(this, 56, 17).setTextureSize(128, 64);
        chestOuterRightWing.addBox(0F, -10F, -0.5F, 8, 13, 1);
        chestOuterRightWing.setRotationPoint(25F, 11F, 9F);

        chestLeftWing = new ModelRenderer(this, 56, 0).setTextureSize(128, 64);
        chestLeftWing.mirror = true;
        chestLeftWing.addBox(-10F, -12F, -0.5F, 10, 16, 1);
        chestLeftWing.setRotationPoint(1F, 11F, 9F);

        chestOuterLeftWing = new ModelRenderer(this, 56, 17).setTextureSize(128, 64);
        chestOuterLeftWing.mirror = true;
        chestOuterLeftWing.addBox(-8F, -10F, -0.5F, 8, 13, 1);
        chestOuterLeftWing.setRotationPoint(-9F, 11F, 9F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, 0.5F, -0.5F);

        if (entity != null) {
            float rotation = 1.0F - ((EntityEvilTrunk) entity).skullrot;
            rotation = 1.0F - rotation * rotation * rotation;
            rotation = (-(rotation * RenderUtil.PI_f / 2.0F));

            chestSkull.rotateAngleX = rotation;
            chestHorn1.rotateAngleX = rotation;
            chestHorn2.rotateAngleX = rotation;
            chestHorn3.rotateAngleX = rotation;
            chestHorn4.rotateAngleX = rotation;
            chestHornTop1.rotateAngleX = rotation;
            chestHornTop2.rotateAngleX = rotation;

            chestRightWing.rotateAngleX = rotation;
            chestOuterRightWing.rotateAngleX = rotation;
            chestLeftWing.rotateAngleX = rotation;
            chestOuterLeftWing.rotateAngleX = rotation;
        }

        chestRightWing.rotateAngleY = MathHelper.cos(f2 * 0.5F) * RenderUtil.PI_f * 0.25F;
        chestLeftWing.rotateAngleY = -chestRightWing.rotateAngleY;
        float X = 10f * MathHelper.cos(chestRightWing.rotateAngleY);
        float Z = 10f * MathHelper.sin(chestRightWing.rotateAngleY);
        chestOuterRightWing.setRotationPoint(15F + X, 11F, 9F - Z);
        chestOuterLeftWing.setRotationPoint(1F - X, 11F, 9F - Z);
        chestOuterRightWing.rotateAngleY = chestRightWing.rotateAngleY * 1.9F;
        chestOuterLeftWing.rotateAngleY = -chestRightWing.rotateAngleY * 1.9F;

        float scale = 0.0625F;
        chestSkull.render(scale);
        chestJaw.render(scale);
        chestTooth1.render(scale);
        chestToothTop1.render(scale);
        chestTooth2.render(scale);
        chestToothTop2.render(scale);
        chestTooth3.render(scale);
        chestTooth4.render(scale);
        chestHorn1.render(scale);
        chestHorn2.render(scale);
        chestHorn3.render(scale);
        chestHorn4.render(scale);
        chestHornTop1.render(scale);
        chestHornTop2.render(scale);
        chestRightWing.render(scale);
        chestOuterRightWing.render(scale);
        chestLeftWing.render(scale);
        chestOuterLeftWing.render(scale);

        GL11.glPopMatrix();
    }
}
