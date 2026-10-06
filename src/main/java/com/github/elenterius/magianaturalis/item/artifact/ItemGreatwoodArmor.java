package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.client.model.armor.ModelArmorGreatwood;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

/**
 * 【宏伟之木套 · 普通版】
 *
 * 参考 Botania 的 Manasteel 套装。
 * 用自定义模型，穿上去有植物魔法那种造型。
 */
public class ItemGreatwoodArmor extends ItemArmor
    implements IRepairable, IRunicArmor, IVisDiscountGear {

    public IIcon iconHelm;
    public IIcon iconChest;
    public IIcon iconLegs;
    public IIcon iconBoots;

    @SideOnly(Side.CLIENT)
    private ModelBiped[] models;

    private static final boolean ENABLE_VIS_DISCOUNT = true;
    private static final int VIS_DISCOUNT = 5;

    public ItemGreatwoodArmor(ArmorMaterial material, int renderIndex, int armorType) {
        super(material, renderIndex, armorType);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        iconHelm  = ir.registerIcon(MagiaNaturalis.rlString("greatwood_helm"));
        iconChest = ir.registerIcon(MagiaNaturalis.rlString("greatwood_chest"));
        iconLegs  = ir.registerIcon(MagiaNaturalis.rlString("greatwood_legs"));
        iconBoots = ir.registerIcon(MagiaNaturalis.rlString("greatwood_boots"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (armorType == 0) return iconHelm;
        if (armorType == 1) return iconChest;
        if (armorType == 2) return iconLegs;
        return iconBoots;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return MagiaNaturalis.rlString("textures/models/armor/greatwood_model.png");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
        if (models == null) {
            models = new ModelBiped[4];
        }

        if (models[slot] == null) {
            models[slot] = new ModelArmorGreatwood(slot);
        }

        return models[slot];
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack stack2) {
        return super.getIsRepairable(stack, stack2);
    }

    @Override
    public int getRunicCharge(ItemStack stack) {
        return 0;
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        if (!ENABLE_VIS_DISCOUNT) return 0;
        return VIS_DISCOUNT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        if (ENABLE_VIS_DISCOUNT) {
            list.add(
                EnumChatFormatting.DARK_PURPLE
                    + StatCollector.translateToLocal("tc.visdiscount")
                    + ": "
                    + getVisDiscount(stack, player, null)
                    + "%");
        }
        super.addInformation(stack, player, list, advanced);
    }
}