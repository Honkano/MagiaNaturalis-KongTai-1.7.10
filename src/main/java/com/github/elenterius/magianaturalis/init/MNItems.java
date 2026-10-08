package com.github.elenterius.magianaturalis.init;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.item.ItemHerobrinesScythe;
import com.github.elenterius.magianaturalis.item.ItemNaturalTablet;
import com.github.elenterius.magianaturalis.item.alchemy.AlchemicalStoneItem;
import com.github.elenterius.magianaturalis.item.artifact.*;
import com.github.elenterius.magianaturalis.item.baubles.FocusEnderPouchItem;
import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.item.baubles.ItemRevealingGoggles;
import com.github.elenterius.magianaturalis.item.focus.BuilderFocusItem;
import com.github.elenterius.magianaturalis.item.focus.RevenantFocusItem;

import cpw.mods.fml.common.registry.GameRegistry;

public class MNItems {

    // ==================================================
    // 【物品字段声明】所有物品都先在这里声明
    // ==================================================
    public static Item researchLog;
    public static Item biomeReport;
    public static Item alchemicalStone;
    public static Item arcaneKey;
    public static Item gogglesDark;
    public static Item spectacles;
    public static Item focusBuild;
    public static Item focusRevenant;
    public static Item evilTrunkSpawner;
    public static Item focusPouchEnder;
    public static Item sickleThaumium;
    public static Item sickleElemental;
    public static Item voidSickle;
    public static Item herobrinesScythe;
    public static Item natureVerdict;
    public static Item greatwoodTablet;
    public static Item silverwoodTablet;
    public static Item natureWrath;
    public static Item verdantSpear;
    public static Item tideVerdict;
    public static Item revealingGoggles;

    // ==================================================
    // 【神装版：自然法袍】★ 小飞鱼之前教的，不动
    //
    // 参数说明：
    // 1. 名字 —— 唯一标识，不能和其他材质重名
    // 2. 耐久系数 —— 数字越大，装备越耐用
    // 3. 四部位防御值 —— {头盔, 胸甲, 护腿, 靴子}
    // 4. 附魔性 —— 数字越大，越容易附魔到好属性
    // ==================================================
    public static final ItemArmor.ArmorMaterial NATURAL_ROBE_MATERIAL = EnumHelper.addArmorMaterial(
        "NATURAL_ROBE", // ★ 唯一名字（不能重名）
        40, // ★ 耐久系数
        new int[] { 4, 9, 7, 4 }, // ★ 四部位防御值
        30 // ★ 附魔性
    );

    public static int NATURAL_ROBE_RENDER_INDEX = 0;

    public static Item naturalRobeHelm;
    public static Item naturalRobeChest;
    public static Item naturalRobeLegs;
    public static Item naturalRobeBoots;

    // ==================================================
    // 【普通版：银木套】★ 你现在做的
    //
    // 参数说明：
    // 1. 名字 —— 唯一标识，不能和其他材质重名
    // 2. 耐久系数 —— 数字越大，装备越耐用
    // 3. 四部位防御值 —— {头盔, 胸甲, 护腿, 靴子}
    // 4. 附魔性 —— 数字越大，越容易附魔到好属性
    //
    // 参考值（照抄即可）：
    // 皮革：耐久系数 5，防御 {1,3,2,1}，附魔性 15
    // 金： 耐久系数 7，防御 {2,5,3,1}，附魔性 25
    // 锁链：耐久系数 15，防御 {2,5,4,1}，附魔性 12
    // 铁： 耐久系数 15，防御 {2,6,5,2}，附魔性 9
    // 钻石：耐久系数 33，防御 {3,8,6,3}，附魔性 10
    //
    // 当前设定：耐久 44，防御 {6,10,8,6}，附魔性 10
    // ==================================================
    public static final ItemArmor.ArmorMaterial NATURAL_WOOD_MATERIAL = EnumHelper.addArmorMaterial(
        "NATURAL_WOOD_ARMOR", // ★ 唯一名字（不能重名）
        44, // ★ 耐久系数
        new int[] { 6, 10, 8, 6 }, // ★ 四部位防御值
        10 // ★ 附魔性
    );

    public static int NATURAL_WOOD_RENDER_INDEX = 0;

    public static Item naturalWoodHelm;
    public static Item naturalWoodChest;
    public static Item naturalWoodLegs;
    public static Item naturalWoodBoots;

    // ==================================================
    // 【宏伟之木普通版材质】
    // ==================================================
    public static final ItemArmor.ArmorMaterial GREATWOOD_MATERIAL = EnumHelper
        .addArmorMaterial("GREATWOOD", 25, new int[] { 3, 7, 5, 3 }, 15);

    public static int GREATWOOD_RENDER_INDEX = 0;
    public static Item greatwoodHelm;
    public static Item greatwoodChest;
    public static Item greatwoodLegs;
    public static Item greatwoodBoots;

    // ==================================================
    // 【宏伟之木升级版材质】
    // ==================================================
    public static final ItemArmor.ArmorMaterial GREATWOOD_ADVANCED_MATERIAL = EnumHelper
        .addArmorMaterial("GREATWOOD_ADVANCED", 40, new int[] { 4, 9, 7, 4 }, 25);

    public static int GREATWOOD_ADVANCED_RENDER_INDEX = 0;
    public static Item greatwoodAdvancedHelm;
    public static Item greatwoodAdvancedChest;
    public static Item greatwoodAdvancedLegs;
    public static Item greatwoodAdvancedBoots;

    // ==================================================
    // 【水神铠甲材质】
    // ==================================================
    public static final ItemArmor.ArmorMaterial WATER_GOD_MATERIAL = EnumHelper.addArmorMaterial(
        "WATER_GOD", // 唯一名字
        45, // 耐久系数
        new int[] { 5, 10, 8, 5 }, // 四部位防御值
        30 // 附魔性
    );

    public static int WATER_GOD_RENDER_INDEX = 0;
    public static Item waterGodHelm;
    public static Item waterGodChest;
    public static Item waterGodLegs;
    public static Item waterGodBoots;

    // ==================================================
    // 【面具】
    // ==================================================
    public static Item mask;

    // ==================================================
    // 【物品注册】所有物品都在这里创建并注册
    // ==================================================
    public static void initItems() {

        // --------------------------------------------------
        // 【原有物品】用 registerItem 工具方法注册
        // --------------------------------------------------
        researchLog = registerItem("research_log", ResearchLogItem::new);
        biomeReport = registerItem("biome_sampler", BiomeSamplerItem::new);
        alchemicalStone = registerItem("alchemical_stone", AlchemicalStoneItem::new);
        arcaneKey = registerItem("thaumium_key", ArcaneKeyItem::new);
        gogglesDark = registerItem("dark_crystal_goggles", DarkCrystalGogglesItem::new);
        spectacles = registerItem("spectacles", SpectaclesItem::new);
        focusBuild = registerItem("builder_focus", BuilderFocusItem::new);
        focusRevenant = registerItem("revenant_focus", RevenantFocusItem::new);
        evilTrunkSpawner = registerItem("evil_trunk", EvilTrunkSpawnerItem::new);
        focusPouchEnder = registerItem("focus_ender_pouch", FocusEnderPouchItem::new);
        sickleThaumium = registerItem("thaumium_sickle", ThaumiumSickleItem::new);
        sickleElemental = registerItem("elemental_sickle", ElementalSickleItem::new);
        voidSickle = registerItem("void_sickle", VoidSickleItem::new);
        revealingGoggles = registerItem("revealing_goggles", ItemRevealingGoggles::new);
        herobrinesScythe = registerItem("herobrines_scythe", ItemHerobrinesScythe::new);

        // 两个石板共用同一个类，只是底座贴图不同
        greatwoodTablet = registerItem("greatwood_tablet", () -> new ItemNaturalTablet("greatwood_tablet"));
        silverwoodTablet = registerItem("silverwood_tablet", () -> new ItemNaturalTablet("silverwood_tablet"));

        // --------------------------------------------------
        // 【神装版：自然法袍】★ 小飞鱼之前教的，不动
        //
        // armorType 对应关系（MC 硬编码，不要改）：
        // 0 = 头盔
        // 1 = 胸甲
        // 2 = 护腿
        // 3 = 靴子
        // --------------------------------------------------
        naturalRobeHelm = new ItemNaturalRobe(NATURAL_ROBE_MATERIAL, NATURAL_ROBE_RENDER_INDEX, 0);
        naturalRobeChest = new ItemNaturalRobe(NATURAL_ROBE_MATERIAL, NATURAL_ROBE_RENDER_INDEX, 1);
        naturalRobeLegs = new ItemNaturalRobe(NATURAL_ROBE_MATERIAL, NATURAL_ROBE_RENDER_INDEX, 2);
        naturalRobeBoots = new ItemNaturalRobe(NATURAL_ROBE_MATERIAL, NATURAL_ROBE_RENDER_INDEX, 3);

        naturalRobeHelm.setUnlocalizedName("magianaturalis.natural_robe_helm");
        naturalRobeChest.setUnlocalizedName("magianaturalis.natural_robe_chest");
        naturalRobeLegs.setUnlocalizedName("magianaturalis.natural_robe_legs");
        naturalRobeBoots.setUnlocalizedName("magianaturalis.natural_robe_boots");

        GameRegistry.registerItem(naturalRobeHelm, "magianaturalis.natural_robe_helm");
        GameRegistry.registerItem(naturalRobeChest, "magianaturalis.natural_robe_chest");
        GameRegistry.registerItem(naturalRobeLegs, "magianaturalis.natural_robe_legs");
        GameRegistry.registerItem(naturalRobeBoots, "magianaturalis.natural_robe_boots");

        // --------------------------------------------------
        // 【普通版：银木套】★ 你现在做的
        //
        // armorType 对应关系（MC 硬编码，不要改）：
        // 0 = 头盔
        // 1 = 胸甲
        // 2 = 护腿
        // 3 = 靴子
        //
        // ⚠️ 关键：setUnlocalizedName 里"不带 item."前缀
        // 但语言文件里"要带 item."前缀
        // 这是 1.7.10 的机制，别搞错！
        // --------------------------------------------------
        naturalWoodHelm = new ItemNaturalWoodArmor(NATURAL_WOOD_MATERIAL, NATURAL_WOOD_RENDER_INDEX, 0);
        naturalWoodChest = new ItemNaturalWoodArmor(NATURAL_WOOD_MATERIAL, NATURAL_WOOD_RENDER_INDEX, 1);
        naturalWoodLegs = new ItemNaturalWoodArmor(NATURAL_WOOD_MATERIAL, NATURAL_WOOD_RENDER_INDEX, 2);
        naturalWoodBoots = new ItemNaturalWoodArmor(NATURAL_WOOD_MATERIAL, NATURAL_WOOD_RENDER_INDEX, 3);

        naturalWoodHelm.setUnlocalizedName("magianaturalis.natural_wood_helm");
        naturalWoodChest.setUnlocalizedName("magianaturalis.natural_wood_chest");
        naturalWoodLegs.setUnlocalizedName("magianaturalis.natural_wood_legs");
        naturalWoodBoots.setUnlocalizedName("magianaturalis.natural_wood_boots");

        GameRegistry.registerItem(naturalWoodHelm, "magianaturalis.natural_wood_helm");
        GameRegistry.registerItem(naturalWoodChest, "magianaturalis.natural_wood_chest");
        GameRegistry.registerItem(naturalWoodLegs, "magianaturalis.natural_wood_legs");
        GameRegistry.registerItem(naturalWoodBoots, "magianaturalis.natural_wood_boots");

        // --------------------------------------------------
        // 【宏伟之木普通版】四件套
        // --------------------------------------------------
        greatwoodHelm = new ItemGreatwoodArmor(GREATWOOD_MATERIAL, GREATWOOD_RENDER_INDEX, 0);
        greatwoodChest = new ItemGreatwoodArmor(GREATWOOD_MATERIAL, GREATWOOD_RENDER_INDEX, 1);
        greatwoodLegs = new ItemGreatwoodArmor(GREATWOOD_MATERIAL, GREATWOOD_RENDER_INDEX, 2);
        greatwoodBoots = new ItemGreatwoodArmor(GREATWOOD_MATERIAL, GREATWOOD_RENDER_INDEX, 3);

        greatwoodHelm.setUnlocalizedName("magianaturalis.greatwood_helm");
        greatwoodChest.setUnlocalizedName("magianaturalis.greatwood_chest");
        greatwoodLegs.setUnlocalizedName("magianaturalis.greatwood_legs");
        greatwoodBoots.setUnlocalizedName("magianaturalis.greatwood_boots");

        GameRegistry.registerItem(greatwoodHelm, "magianaturalis.greatwood_helm");
        GameRegistry.registerItem(greatwoodChest, "magianaturalis.greatwood_chest");
        GameRegistry.registerItem(greatwoodLegs, "magianaturalis.greatwood_legs");
        GameRegistry.registerItem(greatwoodBoots, "magianaturalis.greatwood_boots");

        // --------------------------------------------------
        // 【宏伟之木升级版】四件套
        // --------------------------------------------------
        greatwoodAdvancedHelm = new ItemGreatwoodAdvancedArmor(
            GREATWOOD_ADVANCED_MATERIAL,
            GREATWOOD_ADVANCED_RENDER_INDEX,
            0);
        greatwoodAdvancedChest = new ItemGreatwoodAdvancedArmor(
            GREATWOOD_ADVANCED_MATERIAL,
            GREATWOOD_ADVANCED_RENDER_INDEX,
            1);
        greatwoodAdvancedLegs = new ItemGreatwoodAdvancedArmor(
            GREATWOOD_ADVANCED_MATERIAL,
            GREATWOOD_ADVANCED_RENDER_INDEX,
            2);
        greatwoodAdvancedBoots = new ItemGreatwoodAdvancedArmor(
            GREATWOOD_ADVANCED_MATERIAL,
            GREATWOOD_ADVANCED_RENDER_INDEX,
            3);

        greatwoodAdvancedHelm.setUnlocalizedName("magianaturalis.greatwood_advanced_helm");
        greatwoodAdvancedChest.setUnlocalizedName("magianaturalis.greatwood_advanced_chest");
        greatwoodAdvancedLegs.setUnlocalizedName("magianaturalis.greatwood_advanced_legs");
        greatwoodAdvancedBoots.setUnlocalizedName("magianaturalis.greatwood_advanced_boots");

        GameRegistry.registerItem(greatwoodAdvancedHelm, "magianaturalis.greatwood_advanced_helm");
        GameRegistry.registerItem(greatwoodAdvancedChest, "magianaturalis.greatwood_advanced_chest");
        GameRegistry.registerItem(greatwoodAdvancedLegs, "magianaturalis.greatwood_advanced_legs");
        GameRegistry.registerItem(greatwoodAdvancedBoots, "magianaturalis.greatwood_advanced_boots");

        // --------------------------------------------------
        // 【水神铠甲】四件套
        // --------------------------------------------------
        waterGodHelm = new ItemWaterGodArmor(WATER_GOD_MATERIAL, WATER_GOD_RENDER_INDEX, 0);
        waterGodChest = new ItemWaterGodArmor(WATER_GOD_MATERIAL, WATER_GOD_RENDER_INDEX, 1);
        waterGodLegs = new ItemWaterGodArmor(WATER_GOD_MATERIAL, WATER_GOD_RENDER_INDEX, 2);
        waterGodBoots = new ItemWaterGodArmor(WATER_GOD_MATERIAL, WATER_GOD_RENDER_INDEX, 3);

        waterGodHelm.setUnlocalizedName("magianaturalis.water_god_helm");
        waterGodChest.setUnlocalizedName("magianaturalis.water_god_chest");
        waterGodLegs.setUnlocalizedName("magianaturalis.water_god_legs");
        waterGodBoots.setUnlocalizedName("magianaturalis.water_god_boots");

        GameRegistry.registerItem(waterGodHelm, "magianaturalis.water_god_helm");
        GameRegistry.registerItem(waterGodChest, "magianaturalis.water_god_chest");
        GameRegistry.registerItem(waterGodLegs, "magianaturalis.water_god_legs");
        GameRegistry.registerItem(waterGodBoots, "magianaturalis.water_god_boots");

        // --------------------------------------------------
        // 【武器】三把新武器
        // --------------------------------------------------
        natureVerdict = new ItemNatureVerdict();
        natureVerdict.setUnlocalizedName("magianaturalis.nature_verdict");
        GameRegistry.registerItem(natureVerdict, "magianaturalis.nature_verdict");

        natureWrath = new ItemNatureWrath();
        natureWrath.setUnlocalizedName("magianaturalis.nature_wrath");
        GameRegistry.registerItem(natureWrath, "magianaturalis.nature_wrath");

        verdantSpear = new ItemVerdantSpear();
        verdantSpear.setUnlocalizedName("magianaturalis.verdant_spear");
        GameRegistry.registerItem(verdantSpear, "magianaturalis.verdant_spear");

        tideVerdict = new ItemTideVerdict();
        tideVerdict.setUnlocalizedName("magianaturalis.tide_verdict");
        GameRegistry.registerItem(tideVerdict, "magianaturalis.tide_verdict");
        // --------------------------------------------------
        // 【面具】一个物品，三个 meta
        // --------------------------------------------------
        mask = new ItemMask();
        mask.setUnlocalizedName("magianaturalis.mask");
        GameRegistry.registerItem(mask, "magianaturalis.mask");

    }

    // ==================================================
    // 【工具方法】registerItem
    //
    // 自动帮你做三件事：
    // 1. setUnlocalizedName
    // 2. setTextureName
    // 3. 加到创造标签页
    // 4. 注册到游戏
    // ==================================================
    private static <T extends Item> T registerItem(String name, Supplier<T> factory) {
        T item = factory.get();
        item.setUnlocalizedName(MagiaNaturalis.translationKey(name));
        item.setTextureName(MagiaNaturalis.rlString(name));
        item.setCreativeTab(MNCreativeTabs.MAIN);

        GameRegistry.registerItem(item, name);

        return item;
    }
}
