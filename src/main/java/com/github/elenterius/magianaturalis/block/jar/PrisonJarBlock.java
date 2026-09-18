package com.github.elenterius.magianaturalis.block.jar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.client.render.RenderUtil;
import com.github.elenterius.magianaturalis.util.NBTUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.blocks.CustomStepSound;

public class PrisonJarBlock extends BlockContainer {

    public IIcon iconJarSide;
    public IIcon iconJarTop;
    public IIcon iconJarBottom;

    private NBTTagCompound nbtCacheEntity;

    public PrisonJarBlock() {
        super(Material.glass);
        setHardness(0.3F);
        setStepSound(new CustomStepSound("jar", 1.0F, 1.0F));
        setLightLevel(0.66F);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        iconJarSide = ir.registerIcon(MagiaNaturalis.rlString("prison_jar_side"));
        iconJarTop = ir.registerIcon("thaumcraft:jar_top");
        iconJarBottom = ir.registerIcon("thaumcraft:jar_bottom");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (side) {
            case 0:
                return iconJarBottom;
            case 1:
                return iconJarTop;
            default:
                return iconJarSide;
        }
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
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public int getRenderType() {
        return RenderUtil.RENDER_ID_2;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(4) == 0) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof PrisonJarBlockEntity) {
                MagiaNaturalis.proxyTC4.blockSparkle(world, x, y, z, 0xFFD700, 1);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        PrisonJarBlockEntity jar = (PrisonJarBlockEntity) world.getTileEntity(x, y, z);
        if (jar != null) {
            EntityPlayer player = (EntityPlayer) entity;

            NBTTagCompound data = NBTUtil.getOrCreate(stack);
            if (data.hasKey("entity")) jar.setEntityData(data);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof PrisonJarBlockEntity) {
            PrisonJarBlockEntity jarPrison = (PrisonJarBlockEntity) tile;
            nbtCacheEntity = jarPrison.getEntityDataPrimitive();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        if (nbtCacheEntity != null) {
            ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
            ItemStack stack = new ItemStack(this, 1, 0);
            stack.stackTagCompound = nbtCacheEntity;
            drops.add(stack);
            nbtCacheEntity = null;
            return drops;
        }
        return super.getDrops(world, x, y, z, metadata, fortune);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new PrisonJarBlockEntity();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.1875F, 0.0F, 0.1875F, 0.8125F, 0.75F, 0.8125F);
        super.setBlockBoundsBasedOnState(world, x, y, z);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aaBB, List list,
        Entity entity) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aaBB, list, entity);
    }

}
