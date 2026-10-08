package com.github.elenterius.magianaturalis.easteregg;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class ModelHerobrineWatcher extends ModelBiped {

    public ModelHerobrineWatcher() {
        // 1.7.10 默认皮肤尺寸是 64x32，不要写 64x64
        super(0.0F, 0.0F, 64, 32);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scale, Entity entity) {

        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

        this.bipedHead.rotateAngleY = 0.0F;
        this.bipedHead.rotateAngleX = 0.0F;
        this.bipedHead.rotateAngleZ = 0.0F;
    }
}
