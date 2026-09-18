package com.github.elenterius.magianaturalis.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.block.jar.PrisonJarBlock;
import com.github.elenterius.magianaturalis.client.render.RenderUtil;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import thaumcraft.client.renderers.block.BlockRenderer;

public class BlockJarRenderer extends BlockRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        IIcon i1 = ((PrisonJarBlock) block).iconJarTop;
        IIcon i2 = ((PrisonJarBlock) block).iconJarSide;

        block.setBlockBounds(W3, 0.0F, W3, W13, W12, W13);
        renderer.setRenderBoundsFromBlock(block);
        drawFaces(renderer, block, ((PrisonJarBlock) block).iconJarBottom, i1, i2, i2, i2, i2, true);
        block.setBlockBounds(W5, W12, W5, W11, W14, W11);
        renderer.setRenderBoundsFromBlock(block);
        drawFaces(renderer, block, ((PrisonJarBlock) block).iconJarBottom, i1, i2, i2, i2, i2, true);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        setBrightness(world, x, y, z, block);

        block.setBlockBounds(W3, 0.0F, W3, W13, W12, W13);
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
        block.setBlockBounds(W5, W12, W5, W11, W14, W11);
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);

        renderer.clearOverrideBlockTexture();
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return RenderUtil.RENDER_ID_2;
    }
}
