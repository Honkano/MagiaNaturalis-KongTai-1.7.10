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
import com.github.elenterius.magianaturalis.client.model.armor.ModelArmorWaterGod;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

/**
 * 【水神铠甲】
 *
 * 目前只做基础版：
 * - 符文护盾 5 点
 * - Vis 魔力减免 6%
 * - 默认防御效果
 * - OBJ 自定义模型
 * 特殊效果之后再补。
 */
public class ItemWaterGodArmor extends ItemArmor implements IRepairable, IRunicArmor, IVisDiscountGear {

    // 4 个部位的物品图标
    public IIcon iconHelm;
    public IIcon iconChest;
    public IIcon iconLegs;
    public IIcon iconBoots;

    // 自定义模型缓存
    @SideOnly(Side.CLIENT)
    private ModelBiped[] models;

    // 可调数值
    private static final boolean ENABLE_VIS_DISCOUNT = true;
    private static final int VIS_DISCOUNT = 6; // Vis 减免 6%
    private static final int RUNIC_CHARGE = 5; // 符文护盾 5 点

    public ItemWaterGodArmor(ArmorMaterial material, int renderIndex, int armorType) {
        super(material, renderIndex, armorType);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    // ==================================================
    // 【注册物品图标】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        iconHelm = ir.registerIcon(MagiaNaturalis.rlString("water_god_helm"));
        iconChest = ir.registerIcon(MagiaNaturalis.rlString("water_god_chest"));
        iconLegs = ir.registerIcon(MagiaNaturalis.rlString("water_god_legs"));
        iconBoots = ir.registerIcon(MagiaNaturalis.rlString("water_god_boots"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (armorType == 0) return iconHelm;
        if (armorType == 1) return iconChest;
        if (armorType == 2) return iconLegs;
        return iconBoots;
    }

    // ==================================================
    // 【模型贴图】展开贴图（备用，用 OBJ 时基本用不到）
    // ==================================================
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (slot == 2) {
            return MagiaNaturalis.rlString("textures/models/armor/WaterGodArmor1.png");
        }
        return MagiaNaturalis.rlString("textures/models/armor/WaterGodArmor0.png");
    }

    // ==================================================
    // 【自定义 OBJ 模型】核心！覆盖原版模型
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
        if (models == null) {
            models = new ModelBiped[4];
        }
        if (models[slot] == null) {
            models[slot] = new ModelArmorWaterGod(slot);
        }
        return models[slot];
    }

    // ==================================================
    // 【稀有度】紫色史诗
    // ==================================================
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack stack2) {
        return super.getIsRepairable(stack, stack2);
    }

    // ==================================================
    // 【符文护盾】
    // ==================================================
    @Override
    public int getRunicCharge(ItemStack stack) {
        return RUNIC_CHARGE;
    }

    // ==================================================
    // 【Vis 魔力减免】
    // ==================================================
    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        if (!ENABLE_VIS_DISCOUNT) return 0;
        return VIS_DISCOUNT;
    }

    // ==================================================
    // 【物品提示】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        if (ENABLE_VIS_DISCOUNT) {
            list.add(
                EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tc.visdiscount")
                    + ": "
                    + getVisDiscount(stack, player, null)
                    + "%");
        }
        super.addInformation(stack, player, list, advanced);
    }
}
