package com.github.elenterius.magianaturalis.client.model.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

/**
 * 【水神铠甲 · OBJ 模型】
 *
 * 对照 ASJLib 的 AdvancedArmorModel 源码逐字复刻。
 *
 * 关键三步，少任何一步模型都不跟随玩家动作：
 * ① 设置 isSneak / isChild / isRiding / heldItemRight 等状态字段
 * ② 调用 setRotationAngles 计算所有部位的 rotateAngle
 * ③ 渲染时应用 rotationPoint + rotateAngle + 180° 翻转
 */
public class ModelArmorWaterGod extends ModelBiped {

    private static IModelCustom model;
    private static ResourceLocation texture;

    private static final float DEG = 180f / (float) Math.PI;
    private static final float S = 0.0625f;

    static {
        try {
            model = AdvancedModelLoader.loadModel(new ResourceLocation("magianaturalis", "model/WaterGodArmor.obj"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        texture = new ResourceLocation("magianaturalis", "textures/models/armor/WaterGodArmor.png");
    }

    private final int slot;

    public ModelArmorWaterGod(int slot) {
        this.slot = slot;
    }

    /**
     * 应用某个部位的变换。
     * 对照 AdvancedArmorModel：
     * translate(rotationPoint * scale)
     * rotateZ(rotateAngleZ * 180/π)
     * rotateY(rotateAngleY * 180/π)
     * rotateX(rotateAngleX * 180/π)
     * rotateX(180) ← OBJ 的 Y 轴翻转
     */
    private void applyPartTransform(float px, float py, float pz, float rx, float ry, float rz) {
        GL11.glTranslatef(px * S, py * S, pz * S);
        GL11.glRotatef(rz * DEG, 0f, 0f, 1f);
        GL11.glRotatef(ry * DEG, 0f, 1f, 0f);
        GL11.glRotatef(rx * DEG, 1f, 0f, 0f);
        GL11.glRotatef(180f, 1f, 0f, 0f);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        if (model == null) return;

        // ==================================================
        // ① ★★ 关键：设置动作状态字段
        //
        // 少了这一步，setRotationAngles 里 if (isSneak) 分支进不去，
        // 模型就会像须佐能乎一样僵直。
        // ==================================================
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;

            this.isSneak = living.isSneaking();
            this.isChild = living.isChild();
            this.isRiding = living.isRiding();

            ItemStack held = living.getHeldItem();
            this.heldItemRight = held != null ? 1 : 0;
            this.heldItemLeft = 0;

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                this.aimedBow = false;
                if (held != null && player.getItemInUseCount() > 0) {
                    EnumAction action = held.getItemUseAction();
                    if (action == EnumAction.block) {
                        this.heldItemRight = 3;
                    } else if (action == EnumAction.bow) {
                        this.aimedBow = true;
                    }
                }
            }
        }

        // ==================================================
        // ② ★★ 关键：计算所有部位的动作数据
        // ==================================================
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(texture);

        float objScale = 0.01f;

        switch (slot) {

            // ==================================================
            // 【头盔】
            // ==================================================
            case 0:
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedHead.rotationPointX,
                    bipedHead.rotationPointY,
                    bipedHead.rotationPointZ,
                    bipedHead.rotateAngleX,
                    bipedHead.rotateAngleY,
                    bipedHead.rotateAngleZ);
                GL11.glTranslatef(0f, -0.75f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("Head");
                GL11.glPopMatrix();
                break;

            // ==================================================
            // 【胸甲 + 左右手臂】
            // ==================================================
            case 1:
                // 胸甲
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedBody.rotationPointX,
                    bipedBody.rotationPointY,
                    bipedBody.rotationPointZ,
                    bipedBody.rotateAngleX,
                    bipedBody.rotateAngleY,
                    bipedBody.rotateAngleZ);
                GL11.glTranslatef(0f, -0.75f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("Body");
                GL11.glPopMatrix();

                // 右臂（跟随挥手、走路）
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedRightArm.rotationPointX,
                    bipedRightArm.rotationPointY,
                    bipedRightArm.rotationPointZ,
                    bipedRightArm.rotateAngleX,
                    bipedRightArm.rotateAngleY,
                    bipedRightArm.rotateAngleZ);
                GL11.glTranslatef(0.31f, -0.55f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("ArmO");
                GL11.glPopMatrix();

                // 左臂
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedLeftArm.rotationPointX,
                    bipedLeftArm.rotationPointY,
                    bipedLeftArm.rotationPointZ,
                    bipedLeftArm.rotateAngleX,
                    bipedLeftArm.rotateAngleY,
                    bipedLeftArm.rotateAngleZ);
                GL11.glTranslatef(-0.31f, -0.55f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("ArmT");
                GL11.glPopMatrix();
                break;

            // ==================================================
            // 【腰带 + 左右护腿】
            // ==================================================
            case 2:
                // 腰带
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedBody.rotationPointX,
                    bipedBody.rotationPointY,
                    bipedBody.rotationPointZ,
                    bipedBody.rotateAngleX,
                    bipedBody.rotateAngleY,
                    bipedBody.rotateAngleZ);
                GL11.glTranslatef(0f, -0.73f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("Belt");
                GL11.glPopMatrix();

                // 右护腿（跟随走路）
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedRightLeg.rotationPointX,
                    bipedRightLeg.rotationPointY,
                    bipedRightLeg.rotationPointZ,
                    bipedRightLeg.rotateAngleX,
                    bipedRightLeg.rotateAngleY,
                    bipedRightLeg.rotateAngleZ);
                GL11.glTranslatef(0.125f, 0.01f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("pantsO");
                GL11.glPopMatrix();

                // 左护腿
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedLeftLeg.rotationPointX,
                    bipedLeftLeg.rotationPointY,
                    bipedLeftLeg.rotationPointZ,
                    bipedLeftLeg.rotateAngleX,
                    bipedLeftLeg.rotateAngleY,
                    bipedLeftLeg.rotateAngleZ);
                GL11.glTranslatef(-0.125f, 0.01f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("PantsT");
                GL11.glPopMatrix();
                break;

            // ==================================================
            // 【左右靴子】
            // ==================================================
            case 3:
                // 右靴
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedRightLeg.rotationPointX,
                    bipedRightLeg.rotationPointY,
                    bipedRightLeg.rotationPointZ,
                    bipedRightLeg.rotateAngleX,
                    bipedRightLeg.rotateAngleY,
                    bipedRightLeg.rotateAngleZ);
                GL11.glTranslatef(0.125f, 0f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("BootO");
                GL11.glPopMatrix();

                // 左靴
                GL11.glPushMatrix();
                applyPartTransform(
                    bipedLeftLeg.rotationPointX,
                    bipedLeftLeg.rotationPointY,
                    bipedLeftLeg.rotationPointZ,
                    bipedLeftLeg.rotateAngleX,
                    bipedLeftLeg.rotateAngleY,
                    bipedLeftLeg.rotateAngleZ);
                GL11.glTranslatef(-0.125f, 0f, 0f);
                GL11.glScalef(objScale, objScale, objScale);
                model.renderPart("BootT");
                GL11.glPopMatrix();
                break;
        }
    }
}
