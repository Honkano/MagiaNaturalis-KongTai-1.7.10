package com.github.elenterius.magianaturalis.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.github.elenterius.magianaturalis.block.banner.CustomBannerBlock;
import com.github.elenterius.magianaturalis.block.banner.CustomBannerBlockEntity;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlock;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestType;
import com.github.elenterius.magianaturalis.block.geopylon.GeoPylonBlock;
import com.github.elenterius.magianaturalis.block.geopylon.GeoPylonBlockEntity;
import com.github.elenterius.magianaturalis.block.table.TranscribingTableBlock;
import com.github.elenterius.magianaturalis.block.table.TranscribingTableBlockEntity;
import com.github.elenterius.magianaturalis.client.render.RenderUtil;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockEntityRenderer implements ISimpleBlockRenderingHandler {

    private static final ArcaneChestBlockEntity CHEST = new ArcaneChestBlockEntity();
    private static final TranscribingTableBlockEntity TABLE = new TranscribingTableBlockEntity();
    private static final CustomBannerBlockEntity BANNER = new CustomBannerBlockEntity();
    private static final GeoPylonBlockEntity GEO_PYLON = new GeoPylonBlockEntity();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        if (block instanceof TranscribingTableBlock) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(TABLE, 0.0D, 0.0D, 0.0D, 0.0F);
        } else if (block instanceof ArcaneChestBlock) {
            CHEST.setChestType(ArcaneChestType.parseId((byte) metadata));
            TileEntityRendererDispatcher.instance.renderTileEntityAt(CHEST, 0.0D, 0.0D, 0.0D, 0.0F);
        } else if (block instanceof CustomBannerBlock) {
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-1.0F, -0.5F, 0.0F);
            TileEntityRendererDispatcher.instance.renderTileEntityAt(BANNER, 0.0D, 0.0D, 0.0D, 0.0F);
        } else if (block instanceof GeoPylonBlock) {
            GEO_PYLON.idle = true;
            TileEntityRendererDispatcher.instance.renderTileEntityAt(GEO_PYLON, 0.0D, 0.0D, 0.0D, 0.0F);
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return RenderUtil.RENDER_ID_1;
    }

}
