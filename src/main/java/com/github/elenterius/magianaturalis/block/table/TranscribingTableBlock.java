package com.github.elenterius.magianaturalis.block.table;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.client.render.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class TranscribingTableBlock extends BlockContainer {

    public TranscribingTableBlock() {
        super(Material.wood);
        setHardness(2.5F);
        setResistance(10.0F);
        setStepSound(soundTypeWood);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        blockIcon = ir.registerIcon("thaumcraft:woodplain");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TranscribingTableBlockEntity();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return RenderUtil.RENDER_ID_1;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(3) == 0) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null && tile instanceof TranscribingTableBlockEntity
                && ((IInventory) tile).getStackInSlot(0) != null) {
                int xOffset = rand.nextInt(2) - rand.nextInt(2);
                int zOffset = rand.nextInt(2) - rand.nextInt(2);
                xOffset += xOffset;
                zOffset += zOffset;

                if (xOffset != 0 || zOffset != 0) {
                    tile = world.getTileEntity(x + xOffset, y, z + zOffset);
                    if (tile != null && tile instanceof TileDeconstructionTable
                        && world.getBlock(x + xOffset, y, z + zOffset) == ConfigBlocks.blockTable) {
                        if (((TileDeconstructionTable) tile).aspect != null)
                            world.spawnParticle("enchantmenttable", x + 0.5D, y + 2.0D, z + 0.5D, xOffset, -1, zOffset);
                    }
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        InventoryUtils.dropItems(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float f0, float f1,
        float f3) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if ((tileEntity instanceof TranscribingTableBlockEntity)) {
            player.openGui(MagiaNaturalis.instance, 1, world, x, y, z);
            return true;
        }

        return false;
    }

}
