package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.client.render.model.gear.ModelNaturalRobe;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

public class ItemNaturalRobe extends ItemArmor implements IRepairable, IRunicArmor, IVisDiscountGear {

    public IIcon iconHelm;
    public IIcon iconChest;
    public IIcon iconLegs;
    public IIcon iconBoots;

    // 自定义 3D 模型（和血腥教皇一样）
    ModelBiped model1 = null; // 全尺寸
    ModelBiped model2 = null; // 半尺寸（胸甲用）
    ModelBiped model = null;

    public ItemNaturalRobe(ArmorMaterial material, int renderIndex, int armorType) {
        super(material, renderIndex, armorType);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        iconHelm = ir.registerIcon(MagiaNaturalis.rlString("natural_robe_helm"));
        iconChest = ir.registerIcon(MagiaNaturalis.rlString("natural_robe_chest"));
        iconLegs = ir.registerIcon(MagiaNaturalis.rlString("natural_robe_legs"));
        iconBoots = ir.registerIcon(MagiaNaturalis.rlString("natural_robe_boots"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (armorType == 0) return iconHelm;
        if (armorType == 1) return iconChest;
        if (armorType == 2) return iconLegs;
        return iconBoots;
    }

    /**
     * 血腥教皇三件套（头/胸/腿）共用一张图，
     * 靴子单独一张。
     */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        // 靴子单独一张
        if (armorType == 3) {
            return MagiaNaturalis.rlString("textures/models/armor/natural_robe_layer_2.png");
        }
        // 头盔/胸甲/护腿共用血腥教皇那张
        return MagiaNaturalis.rlString("textures/models/armor/natural_robe_layer_1.png");
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
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
        return 5;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(
            EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tc.visdiscount")
                + ": "
                + getVisDiscount(stack, player, null)
                + "%");
        super.addInformation(stack, player, list, advanced);
    }

    /**
     * 自定义 3D 模型。
     * 靴子返回 null → 走原版标准模型 + 独立贴图。
     * 三件套用 ModelNaturalRobe。
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        // 靴子走标准模型
        if (armorType == 3) return null;

        if (model1 == null) model1 = new ModelNaturalRobe(1.0F);
        if (model2 == null) model2 = new ModelNaturalRobe(0.5F);

        if (armorType != 1 && armorType != 3) {
            model = model2;
        } else {
            model = model1;
        }

        if (model != null) {
            model.bipedHead.showModel = armorSlot == 0;
            model.bipedHeadwear.showModel = armorSlot == 0;
            model.bipedBody.showModel = armorSlot == 1 || armorSlot == 2;
            model.bipedRightArm.showModel = armorSlot == 1;
            model.bipedLeftArm.showModel = armorSlot == 1;
            model.bipedRightLeg.showModel = armorSlot == 2;
            model.bipedLeftLeg.showModel = armorSlot == 2;
            model.isSneak = entityLiving.isSneaking();
            model.isRiding = entityLiving.isRiding();
            model.isChild = entityLiving.isChild();
            model.aimedBow = false;
            model.heldItemRight = entityLiving.getHeldItem() != null ? 1 : 0;

            if (entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).getItemInUseDuration() > 0) {
                EnumAction action = ((EntityPlayer) entityLiving).getItemInUse()
                    .getItemUseAction();
                if (action == EnumAction.block) {
                    model.heldItemRight = 3;
                } else if (action == EnumAction.bow) {
                    model.aimedBow = true;
                }
            }
        }

        return model;
    }
}
