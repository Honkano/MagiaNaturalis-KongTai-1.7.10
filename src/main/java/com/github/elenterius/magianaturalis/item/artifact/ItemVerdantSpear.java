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

public class ItemVerdantSpear extends ItemSword implements IRepairable {

    public static final ToolMaterial MATERIAL = EnumHelper.addToolMaterial("MN_SPEAR", 4, 800, 8.0F, 3.5F, 15);

    public IIcon icon;

    public ItemVerdantSpear() {
        super(MATERIAL);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        icon = ir.registerIcon(MagiaNaturalis.rlString("verdant_spear"));
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
