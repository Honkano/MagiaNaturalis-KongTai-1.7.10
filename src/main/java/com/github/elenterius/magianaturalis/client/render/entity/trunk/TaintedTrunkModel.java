package com.github.elenterius.magianaturalis.client.render.entity.trunk;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.client.render.RenderUtil;
import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;

public class TaintedTrunkModel extends ModelBase {

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
    public ModelRenderer chestHornTop;

    public TaintedTrunkModel() {
        chestSkull = new ModelRenderer(this, 0, 19).setTextureSize(64, 64);
        chestSkull.addBox(0F, -10F, -14F, 14, 10, 14);
        chestSkull.setRotationPoint(1F, 11F, 15F);

        chestJaw = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
        chestJaw.addBox(0F, 0F, -14F, 14, 5, 14);
        chestJaw.setRotationPoint(1F, 11F, 15F);

        chestTooth1 = new ModelRenderer(this, 7, 0).setTextureSize(64, 64);
        chestTooth1.addBox(-1F, -1F, -14.5F, 2, 3, 1);
        chestTooth1.setRotationPoint(13F, 11F, 15F);

        chestToothTop1 = new ModelRenderer(this, 9, 0).setTextureSize(64, 64);
        chestToothTop1.addBox(0F, -2F, -14.5F, 1, 1, 1);
        chestToothTop1.setRotationPoint(13F, 11F, 15F);

        chestTooth2 = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
        chestTooth2.addBox(-1F, -1F, -14.5F, 2, 3, 1);
        chestTooth2.setRotationPoint(3F, 11F, 15F);

        chestToothTop2 = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
        chestToothTop2.addBox(-1F, -2F, -14.5F, 1, 1, 1);
        chestToothTop2.setRotationPoint(3F, 11F, 15F);

        chestTooth3 = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
        chestTooth3.addBox(-0.7F, -1F, -14.5F, 2, 3, 1);
        chestTooth3.setRotationPoint(6F, 11F, 15F);

        chestTooth4 = new ModelRenderer(this, 7, 0).setTextureSize(64, 64);
        chestTooth4.addBox(-1.25F, -1F, -14.5F, 2, 3, 1);
        chestTooth4.setRotationPoint(10F, 11F, 15F);

        chestHorn1 = new ModelRenderer(this, 0, 5).setTextureSize(64, 64);
        chestHorn1.addBox(6F, -12F, -13F, 2, 2, 2);
        chestHorn1.setRotationPoint(1F, 11F, 15F);

        chestHorn2 = new ModelRenderer(this, 0, 5).setTextureSize(64, 64);
        chestHorn2.addBox(6F, -14F, -13.4F, 2, 2, 2);
        chestHorn2.setRotationPoint(1F, 11F, 15F);

        chestHornTop = new ModelRenderer(this, 3, 6).setTextureSize(64, 64);
        chestHornTop.addBox(6.5F, -16F, -13.2F, 1, 2, 1);
        chestHornTop.setRotationPoint(1F, 11F, 15F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, 0.5F, -0.5F);

        if (entity != null) {
            float rotation = 1.0F - ((EntityEvilTrunk) entity).skullrot;
            rotation = 1.0F - rotation * rotation * rotation;
            chestSkull.rotateAngleX = (-(rotation * RenderUtil.PI_f / 2.0F));
            chestHornTop.rotateAngleX = chestHorn2.rotateAngleX = chestHorn1.rotateAngleX = chestSkull.rotateAngleX;
        }

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
        chestHornTop.render(scale);
        GL11.glPopMatrix();
    }

}
