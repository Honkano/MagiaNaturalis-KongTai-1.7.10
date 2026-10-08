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

public class ItemTideVerdict extends ItemSword implements IRepairable {

    public static final ToolMaterial MATERIAL = EnumHelper.addToolMaterial("MN_TIDE", 4, 1500, 8.0F, 4.0F, 20);

    public IIcon icon;

    public ItemTideVerdict() {
        super(MATERIAL);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        icon = ir.registerIcon(MagiaNaturalis.rlString("tide_verdict"));
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
