package com.github.elenterius.magianaturalis.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.block.banner.CustomBannerBlockEntity;
import com.github.elenterius.magianaturalis.client.render.RenderUtil;

import thaumcraft.client.renderers.models.ModelBanner;

public class TileBannerCustomRenderer extends TileEntitySpecialRenderer {

    private ModelBanner model = new ModelBanner();
    private static final ResourceLocation rlBanner = new ResourceLocation(
        MagiaNaturalis.MOD_ID,
        "textures/models/" + "banner_mn.png");

    @Override
    public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f1) {
        this.renderTileEntityAt((CustomBannerBlockEntity) entity, x, y, z, f1);
    }

    public void renderTileEntityAt(CustomBannerBlockEntity banner, double x, double y, double z, float f1) {
        boolean flag = banner.getWorldObj() != null;
        long k = flag ? banner.getWorldObj()
            .getTotalWorldTime() : 0L;

        GL11.glPushMatrix();
        RenderUtil.bindTexture(rlBanner);

        GL11.glTranslatef((float) (x + 0.5F), (float) (y + 1.5F), (float) (z + 0.5F));
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (banner.getWorldObj() != null) {
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(banner.getFacing() * 360 / 16.0F, 0.0F, 1.0F, 0.0F);
        }

        if (!banner.getWall()) this.model.renderPole();
        else GL11.glTranslated(0.0D, 0.0D, -0.4125D);

        this.model.renderBeam();
        this.model.renderTabs();
        float f3 = banner.xCoord * 7 + banner.yCoord * 9 + banner.zCoord * 13 + (float) k + f1;
        this.model.Banner.rotateAngleX = ((0.005F + 0.005F * MathHelper.cos(f3 * 3.141593F * 0.02F)) * 3.141593F);
        this.model.renderBanner();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
