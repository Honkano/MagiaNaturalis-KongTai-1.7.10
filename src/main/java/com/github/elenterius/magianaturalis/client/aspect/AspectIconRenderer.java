package com.github.elenterius.magianaturalis.client.aspect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

@SideOnly(Side.CLIENT)
public final class AspectIconRenderer {

    private AspectIconRenderer() {}

    /**
     * 渲染单个要素图标。
     *
     * 注意：调用者负责用 glPushAttrib / glPopAttrib 包裹，
     * 这里只负责开关必要的 GL 状态，不负责恢复。
     */
    public static void renderAspect(Aspect aspect, int x, int y, int size, float alpha) {
        if (aspect == null || size <= 0 || alpha <= 0.0F) return;

        int color = aspect.getColor();
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        ResourceLocation image = aspect.getImage();
        Minecraft.getMinecraft().renderEngine.bindTexture(image);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertexWithUV(x, y + size, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(x + size, y + size, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(x + size, y, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(x, y, 0.0D, 0.0D, 0.0D);
        tessellator.draw();

        // 只重置颜色，其他 GL 状态交给调用者的 PopAttrib 恢复
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
