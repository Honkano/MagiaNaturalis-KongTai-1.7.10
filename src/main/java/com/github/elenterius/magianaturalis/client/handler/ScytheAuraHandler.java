package com.github.elenterius.magianaturalis.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.init.MNItems;
import com.github.elenterius.magianaturalis.item.ItemHerobrinesScythe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.client.lib.UtilsFX;

/**
 * 白瞳者之镰背后的渲染：
 * 1. 5 张光环贴图，按 Tier 切换
 * 2. 【新】满级时在玩家背后站立一个 Herobrine 影子
 *
 * 挂载位置：ClientSetup.init 里 register(new ScytheAuraHandler())
 * 不需要额外注册。
 */
public class ScytheAuraHandler {

    // ==============================================================
    // 5 个等级对应的光环贴图。索引 = Tier 0~4。
    // 对应文件：assets/magianaturalis/textures/misc/
    // ==============================================================
    private static final ResourceLocation[] AURA_TEXTURES = new ResourceLocation[] {
        new ResourceLocation("magianaturalis:textures/misc/Dark.png"),
        new ResourceLocation("magianaturalis:textures/misc/Dark1.png"),
        new ResourceLocation("magianaturalis:textures/misc/Dark2.png"),
        new ResourceLocation("magianaturalis:textures/misc/Dark3.png"),
        new ResourceLocation("magianaturalis:textures/misc/Dark4.png") };

    // ==============================================================
    // 【新】Herobrine 影子皮肤。
    // 放到 assets/magianaturalis/textures/entity/herobrine.png
    // 就是 MC 通用玩家皮肤格式（64x32 或 64x64 都行）
    // ==============================================================
    private static final ResourceLocation HEROBRINE_TEXTURE = new ResourceLocation(
        "magianaturalis:textures/entity/herobrine.png");

    /** 每级光环的整体缩放 */
    private static final float[] AURA_SCALE = { 0.85F, 0.95F, 1.05F, 1.15F, 1.30F };

    /** 每级光环的透明度 */
    private static final float[] AURA_ALPHA = { 0.55F, 0.65F, 0.72F, 0.80F, 0.90F };

    /** 每级的旋转速度倍率 */
    private static final float[] AURA_SPIN = { 0.6F, 0.9F, 1.2F, 1.6F, 2.0F };

    /** 复用同一个 ModelBiped，避免每帧 new */
    @SideOnly(Side.CLIENT)
    private ModelBiped herobrineModel;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != MNItems.herobrinesScythe) return;

        // 先画光环
        renderCircleBehindPlayer(player, event.partialRenderTick, held);

        // Tier 1 及以上就出现 Herobrine，按等级越来越实
        int tier = ItemHerobrinesScythe.getTier(held);
        if (tier >= 1) {
            renderHerobrineBehind(player, event.partialRenderTick, tier);
        }
    }

    // ==================================================
    // 光环渲染
    // ==================================================
    private void renderCircleBehindPlayer(EntityPlayer player, float partialTicks, ItemStack held) {
        int tier = ItemHerobrinesScythe.getTier(held);
        if (tier < 0 || tier >= AURA_TEXTURES.length) tier = 0;

        ResourceLocation texture = AURA_TEXTURES[tier];
        float scale = AURA_SCALE[tier];
        float alpha = AURA_ALPHA[tier];
        float spin = AURA_SPIN[tier];

        Tessellator t = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glRotatef(180 - player.rotationYaw, 0, 1, 0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTranslated(
            0,
            (player != Minecraft.getMinecraft().thePlayer ? 1.62F : 0F) - player.getDefaultEyeHeight()
                + (player.isSneaking() ? 0.0625 : 0),
            0.8D);

        GL11.glRotatef(90, 1, 0, 0);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        float r, g, b;
        switch (tier) {
            case 0:
                r = 0.75F;
                g = 0.15F;
                b = 0.15F;
                break;
            case 1:
                r = 0.90F;
                g = 0.12F;
                b = 0.12F;
                break;
            case 2:
                r = 1.00F;
                g = 0.10F;
                b = 0.10F;
                break;
            case 3:
                r = 1.00F;
                g = 0.06F;
                b = 0.06F;
                break;
            case 4:
                r = 1.00F;
                g = 0.03F;
                b = 0.03F;
                break;
            default:
                r = 1F;
                g = 0.1F;
                b = 0.1F;
        }
        GL11.glColor4f(r, g, b, alpha);

        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef((player.ticksExisted + partialTicks) * spin, 0F, 1F, 0F);

        UtilsFX.bindTexture(texture);

        t.startDrawingQuads();
        t.addVertexWithUV(-1, 0, -1, 0, 0);
        t.addVertexWithUV(-1, 0, 1, 0, 1);
        t.addVertexWithUV(1, 0, 1, 1, 1);
        t.addVertexWithUV(1, 0, -1, 1, 0);
        t.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

    // ==================================================
    // 【新】Herobrine 影子渲染
    // ==================================================
    /**
     * 在玩家身后站立一个静止的 Herobrine 模型。
     * 位置：比光环更上、更后。
     * 位置想调就改下面两个数字：
     * Y 方向 → OFFSET_Y
     * Z 方向 → OFFSET_Z
     */
    private void renderHerobrineBehind(EntityPlayer player, float partialTicks, int tier) {

        if (herobrineModel == null) {
            herobrineModel = new ModelBiped();
        }

        // ---- 按 Tier 决定"实不实" ----
        // 索引 = Tier 1~4
        // 缩放：0.7 → 1.0（Tier 4 和玩家一样大）
        // 透明度：0.45 → 1.0（Tier 4 完全不透明）
        final float[] TIER_SCALE = { 0.50F, 0.80F, 1.00F, 1.50F };
        final float[] TIER_ALPHA = { 0.45F, 0.55F, 0.70F, 1.00F };

        int idx = Math.max(0, Math.min(3, tier - 1)); // tier 1~4 → 下标 0~3
        float scale = TIER_SCALE[idx];
        float alpha = TIER_ALPHA[idx];

        // ---- 位置微调 ----
        final double OFFSET_Y = 1.5D;
        final double OFFSET_Z = 1.4D;

        GL11.glPushMatrix();

        GL11.glRotatef(180 - player.rotationYaw, 0, 1, 0);

        GL11.glTranslated(
            0,
            (player != Minecraft.getMinecraft().thePlayer ? 1.62F : 0F) - player.getDefaultEyeHeight()
                + (player.isSneaking() ? 0.0625 : 0)
                + OFFSET_Y,
            OFFSET_Z);

        GL11.glRotatef(180, 0, 1, 0);
        GL11.glRotatef(180, 1, 0, 0);

        // 按 Tier 缩放
        GL11.glScalef(scale, scale, scale);

        // 自发光
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // 按 Tier 决定透明度。Tier 4 的 alpha=1.0 就是完全不透明
        GL11.glColor4f(1F, 1F, 1F, alpha);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(HEROBRINE_TEXTURE);

        herobrineModel.render(player, 0F, 0F, 0F, 0F, 0F, 0.0625F);

        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glPopMatrix();
    }
}
