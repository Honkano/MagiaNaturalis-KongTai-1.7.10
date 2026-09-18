package com.github.elenterius.magianaturalis.init;

import java.util.function.Supplier;

import net.minecraft.item.Item;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.item.ItemHerobrinesScythe;
import com.github.elenterius.magianaturalis.item.alchemy.AlchemicalStoneItem;
import com.github.elenterius.magianaturalis.item.artifact.*;
import com.github.elenterius.magianaturalis.item.baubles.FocusEnderPouchItem;
import com.github.elenterius.magianaturalis.item.focus.BuilderFocusItem;
import com.github.elenterius.magianaturalis.item.focus.RevenantFocusItem;

import cpw.mods.fml.common.registry.GameRegistry;

public class MNItems {

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
    public static Item herobrinesScythe; // 1. 在这里声明

    // public static Item devTool;
    // public static Item biomeDevTool;

    public static void initItems() {
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

        // 2. 在这里注册（注意名字用的是下划线格式）
        herobrinesScythe = registerItem("herobrines_scythe", ItemHerobrinesScythe::new);

        // devTool = registerItem("devTool", DevTool::new);
        // biomeDevTool = registerItem("biomeDevTool", BiomeDevTool::new);
    }

    private static <T extends Item> T registerItem(String name, Supplier<T> factory) {
        T item = factory.get();
        item.setUnlocalizedName(MagiaNaturalis.translationKey(name));
        item.setTextureName(MagiaNaturalis.rlString(name));
        item.setCreativeTab(MNCreativeTabs.MAIN);

        GameRegistry.registerItem(item, name);

        return item;
    }

}
