package com.github.elenterius.magianaturalis.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.block.jar.PrisonJarBlockEntity;

public class TileJarPrisonRenderer extends TileEntitySpecialRenderer {

    public static final float ENTITY_SCALE = 0.21875F;

    // private final ModelJar jarModel = new ModelJar();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        renderTileEntityAt((PrisonJarBlockEntity) tile, x, y, z, f);
    }

    public void renderTileEntityAt(PrisonJarBlockEntity tile, double x, double y, double z, float f) {
        Entity entity = tile.getCachedEntity();
        if (entity == null) return;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glTranslatef(0.5F, 0.1F, 0.5F);

        if (Minecraft.getMinecraft().renderViewEntity != null
            && Minecraft.getMinecraft().renderViewEntity.getDistance(tile.xCoord, tile.yCoord, tile.zCoord) < 4.5D) {
            double dx = Minecraft.getMinecraft().renderViewEntity.posX - (tile.xCoord + 0.5D);
            double dz = Minecraft.getMinecraft().renderViewEntity.posZ - (tile.zCoord + 0.5D);

            float f2 = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
        } else {
            GL11.glRotatef(entity.ticksExisted++, 0F, 1F, 0F);
        }

        GL11.glScalef(ENTITY_SCALE, ENTITY_SCALE, ENTITY_SCALE);

        RenderManager.instance.renderEntityWithPosYaw(entity, 0D, 0D, 0D, 0F, 0F);
        GL11.glPopMatrix();
    }

}
