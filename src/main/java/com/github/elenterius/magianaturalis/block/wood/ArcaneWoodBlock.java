package com.github.elenterius.magianaturalis.block.wood;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArcaneWoodBlock extends Block {

    // TODO: FANCY GREATWOOD & SILVERWOOD SIGN?
    private final IIcon[] icon = new IIcon[7];

    public ArcaneWoodBlock() {
        super(Material.wood);
        setHardness(2.0F);
        setResistance(5.0F);
        setStepSound(soundTypeWood);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        icon[0] = ir.registerIcon(MagiaNaturalis.rlString("greatwood_planks"));
        icon[1] = ir.registerIcon(MagiaNaturalis.rlString("greatwood_orn"));
        icon[2] = ir.registerIcon(MagiaNaturalis.rlString("silverwood_planks_1"));
        icon[3] = ir.registerIcon(MagiaNaturalis.rlString("silverwood_planks_2"));
        icon[4] = ir.registerIcon(MagiaNaturalis.rlString("greatwood_gold_orn"));
        icon[5] = ir.registerIcon(MagiaNaturalis.rlString("greatwood_gold_orn2"));
        icon[6] = ir.registerIcon(MagiaNaturalis.rlString("greatwood_gold_trim"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta > 6) {
            meta = 0;
        }

        if (meta == 6) {
            return side == 1 || side == 0 ? icon[0] : icon[meta];
        }

        return icon[meta];
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
        list.add(new ItemStack(item, 1, 4));
        list.add(new ItemStack(item, 1, 5));
        list.add(new ItemStack(item, 1, 6));
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int meta = stack.getItemDamage();
        if (meta < 0 || meta > 6) {
            meta = 0;
        }
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

}
