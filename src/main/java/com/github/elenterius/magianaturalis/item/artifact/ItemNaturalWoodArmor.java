package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

/**
 * 【银木套 · 普通版 · 通用装备模板 · 小白版】
 *
 * 这套是"银木套的普通版"。
 * 之前小飞鱼教你做的那个 ItemNaturalRobe 是"银木套的神装版"，两者不要搞混。
 *
 * 使用方法：
 * 1. 复制这个类，改类名（比如 ItemNaturalWoodArmor → ItemGreatwoodArmor）
 * 2. 把里面所有 "natural_wood" 改成你的装备名（比如 "greatwood"）
 * 3. 改代码里那些带 "★" 的注释，告诉你要改什么
 *
 * 接口说明：
 *   IRepairable      —— 可以用铁砧修复（TC4 的接口）
 *   IRunicArmor      —— 可以镶嵌符文（TC4 的符文护甲机制）
 *   IVisDiscountGear —— 减少 Vis 消耗（TC4 的魔力减免）
 */
public class ItemNaturalWoodArmor extends ItemArmor
    implements IRepairable, IRunicArmor, IVisDiscountGear {

    // ==================================================
    // 【贴图图标】4 个部位的物品图标
    //
    // 这 4 个变量用来存"物品栏里的图标"
    // 你不需要手动给它们赋值，registerIcons() 会自动赋值
    // ==================================================
    public IIcon iconHelm;   // 头盔的图标
    public IIcon iconChest;  // 胸甲的图标
    public IIcon iconLegs;   // 护腿的图标
    public IIcon iconBoots;  // 靴子的图标

    // ==================================================
    // 【Vis 折扣开关】★ 你可以改的地方
    //
    // true  = 打开 Vis 折扣，装备会显示"魔力减免: 5%"
    // false = 关闭 Vis 折扣，装备就只是普通防御装备
    //
    // 例子：如果你只想做纯防御装备，改成 false
    // ==================================================
    private static final boolean ENABLE_VIS_DISCOUNT = true;

    // ==================================================
    // 【Vis 折扣数值】★ 你可以改的地方
    //
    // 意思是"穿这件装备，使用 Vis 消耗减少多少百分比"
    //
    // 参考值：
    //   5  = 5%（普通）
    //   10 = 10%（不错）
    //   20 = 20%（很强）
    //
    // 例子：你希望这套装备减少 10% 的 Vis 消耗，就改成 10
    // ==================================================
    private static final int VIS_DISCOUNT = 5;

    // ==================================================
    // 【构造方法】MC 会自动调用，你不用管
    //
    // 参数说明：
    //   material     —— 材质（在 MNItems.java 里定义）
    //   renderIndex  —— 渲染前缀（在 ClientSetup.java 里注册）
    //   armorType    —— 部位（0=头盔，1=胸甲，2=护腿，3=靴子）
    // ==================================================
    public ItemNaturalWoodArmor(ArmorMaterial material, int renderIndex, int armorType) {
        super(material, renderIndex, armorType);
        setCreativeTab(MNCreativeTabs.MAIN); // 加到"神秘自然"创造标签页
    }

    // ==================================================
    // 【注册物品图标】
    //
    // 告诉游戏：这 4 个部位的图标长什么样
    //
    // ★ 你要改的地方：
    //   把 "natural_wood_helm" 改成你的物品图标文件名（不带 .png）
    //   比如你的文件叫 natural_wood_helm.png，就写 "natural_wood_helm"
    //
    // 例子：
    //   iconHelm = ir.registerIcon(MagiaNaturalis.rlString("natural_wood_helm"));
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        iconHelm  = ir.registerIcon(MagiaNaturalis.rlString("natural_wood_helm"));
        iconChest = ir.registerIcon(MagiaNaturalis.rlString("natural_wood_chest"));
        iconLegs  = ir.registerIcon(MagiaNaturalis.rlString("natural_wood_legs"));
        iconBoots = ir.registerIcon(MagiaNaturalis.rlString("natural_wood_boots"));
    }

    // ==================================================
    // 【获取物品图标】
    //
    // 游戏在显示物品时，会调用这个方法问："这个部位的图标是哪个？"
    //
    // armorType 的对应关系（MC 硬编码的）：
    //   0 = 头盔
    //   1 = 胸甲
    //   2 = 护腿
    //   3 = 靴子
    //
    // 你不用改这里，直接照抄
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (armorType == 0) return iconHelm;
        if (armorType == 1) return iconChest;
        if (armorType == 2) return iconLegs;
        return iconBoots;
    }

    // ==================================================
    // 【获取模型贴图】最重要的一步！
    //
    // 告诉游戏：玩家穿上这套装备时，盔甲的外观用哪张贴图
    //
    // MC 的 slot 参数含义：
    //   slot == 0  —— 头盔（用 layer_1）
    //   slot == 1  —— 胸甲（用 layer_1）
    //   slot == 2  —— 护腿（用 layer_2）★ 特殊
    //   slot == 3  —— 靴子（用 layer_1）
    //
    // ★ 你要改的地方：
    //   把 "natural_wood" 改成你的贴图文件名前缀
    //
    // 例子：你的贴图叫 natural_wood_layer_1.png 和 natural_wood_layer_2.png
    //   就把 "natural_wood_layer_1.png" 和 "natural_wood_layer_2.png" 对上
    // ==================================================
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        // 护腿用 layer_2（MC 原版的规定，不要改）
        if (slot == 2) {
            return MagiaNaturalis.rlString("textures/models/armor/natural_wood_layer_2.png");
        }
        // 其他部位（头盔/胸甲/靴子）都用 layer_1
        return MagiaNaturalis.rlString("textures/models/armor/natural_wood_layer_1.png");
    }

    // ==================================================
    // 【稀有度】★ 你可以改的地方
    //
    // 决定物品名字的颜色
    //
    // 可选值：
    //   EnumRarity.common     —— 白色（普通）
    //   EnumRarity.uncommon   —— 黄色（少见）
    //   EnumRarity.rare       —— 青色（稀有）
    //   EnumRarity.epic       —— 紫色（史诗）
    //
    // 例子：你想让这套装备是"传奇"级别的，就改成 EnumRarity.rare
    // ==================================================
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon; // 当前是"少见"（黄色）
    }

    // ==================================================
    // 【可修复性】★ 你可以改的地方
    //
    // 决定这件装备能不能用某种材料在铁砧上修
    //
    // 当前：用默认逻辑（材质匹配才能修）
    //
    // 如果你想用特定材料修，改成：
    //
    //   例子 1：用金锭修
    //   return stack2.isItemEqual(new ItemStack(net.minecraft.init.Items.gold_ingot))
    //       || super.getIsRepairable(stack, stack2);
    //
    //   例子 2：用你模组里的物品修
    //   return stack2.isItemEqual(new ItemStack(MNItems.natureVerdict))
    //       || super.getIsRepairable(stack, stack2);
    // ==================================================
    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack stack2) {
        return super.getIsRepairable(stack, stack2);
    }

    // ==================================================
    // 【符文护甲充电】★ 你可以改的地方
    //
    // TC4 的符文护甲机制：如果返回 >0，这套装备能镶嵌符文
    //
    // 参考值：
    //   0  —— 不能镶符文（普通装备）
    //   5  —— 可以镶 5 个符文（普通）
    //   10 —— 可以镶 10 个符文（很强）
    //
    // 例子：你想做符文版银木套，就改成 10
    // ==================================================
    @Override
    public int getRunicCharge(ItemStack stack) {
        return 0; // 当前不能镶符文
    }

    // ==================================================
    // 【Vis 折扣效果】
    //
    // 这个方法是 TC4 调用它来计算"魔力减免"
    // 不用改，改上面的 ENABLE_VIS_DISCOUNT 和 VIS_DISCOUNT 就行
    //
    // 逻辑：
    //   开关开着 → 返回 VIS_DISCOUNT（有减免）
    //   开关关了 → 返回 0（无减免）
    // ==================================================
    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        if (!ENABLE_VIS_DISCOUNT) return 5; // 开关关闭，返回 0（无减免）
        return VIS_DISCOUNT;                // 开关开着，返回设定值
    }

    // ==================================================
    // 【物品提示信息】
    //
    // 鼠标悬停在物品上时显示的文字
    //
    // 当前：如果开了 Vis 折扣，就显示"魔力减免: 5%"
    //
    // 例子：如果以后你想加别的提示，在这里加
    //   list.add(EnumChatFormatting.GREEN + "自然守护 +5");
    // ==================================================
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