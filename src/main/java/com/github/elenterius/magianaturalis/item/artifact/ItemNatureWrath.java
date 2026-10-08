package com.github.elenterius.magianaturalis.item.artifact;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.EnumHelper;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;

public class ItemNatureWrath extends ItemSword implements IRepairable {

    public static final ToolMaterial MATERIAL = EnumHelper.addToolMaterial("MN_WRATH", 4, 1500, 6.0F, 5.0F, 15);

    public IIcon icon;

    public ItemNatureWrath() {
        super(MATERIAL);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        icon = ir.registerIcon(MagiaNaturalis.rlString("nature_wrath"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return icon;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }
}
