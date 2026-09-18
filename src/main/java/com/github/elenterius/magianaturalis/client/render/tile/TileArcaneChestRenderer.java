package com.github.elenterius.magianaturalis.client.render.tile;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.client.render.RenderUtil;

public class TileArcaneChestRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation rl_gw = new ResourceLocation(
        MagiaNaturalis.MOD_ID,
        "textures/models/" + "chest_greatwood.png");
    private static final ResourceLocation rl_sw = new ResourceLocation(
        MagiaNaturalis.MOD_ID,
        "textures/models/" + "chest_silverwood.png");
    private final ModelChest chestModel = new ModelChest();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        renderTileEntityAt((ArcaneChestBlockEntity) tile, x, y, z, partialTicks);
    }

    public void renderTileEntityAt(ArcaneChestBlockEntity tile, double x, double y, double z, float partialTicks) {
        int metadata = !tile.hasWorldObj() ? 0 : tile.getBlockMetadata();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        switch (tile.getChestType()) {
            case GREAT_WOOD:
                RenderUtil.bindTexture(rl_gw);
                break;
            case SILVER_WOOD:
                RenderUtil.bindTexture(rl_sw);
                break;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslated(x, y + 1.0d, z + 1.0d);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

        float facingAngle = 0f;
        switch (metadata) {
            case 2:
                facingAngle = 180f;
                break;
            case 3:
                facingAngle = 0f;
                break;
            case 4:
                facingAngle = 90f;
                break;
            case 5:
                facingAngle = -90f;
                break;
        }

        GL11.glRotatef(facingAngle, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        float lidAngle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        chestModel.chestLid.rotateAngleX = (-(lidAngle * 3.141593F / 2.0F));

        chestModel.renderAll();

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

}
