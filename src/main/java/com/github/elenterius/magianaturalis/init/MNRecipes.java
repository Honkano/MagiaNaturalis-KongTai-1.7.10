package com.github.elenterius.magianaturalis.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.github.elenterius.magianaturalis.block.chest.ArcaneChestType;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public final class MNRecipes {

    private static final HashMap<String, Object> RECIPES = new HashMap<>();

    private MNRecipes() {}

    static void register() {
        registerWorkbenchRecipes();
        registerArcaneRecipes();
        registerInfusionRecipes();
    }

    public static IRecipe getRecipe(String key) {
        return (IRecipe) RECIPES.get(key);
    }

    public static IRecipe[] getRecipes(String key) {
        return (IRecipe[]) RECIPES.get(key);
    }

    public static IArcaneRecipe getArcaneRecipe(String key) {
        return (IArcaneRecipe) RECIPES.get(key);
    }

    public static InfusionRecipe getInfusionRecipe(String key) {
        return (InfusionRecipe) RECIPES.get(key);
    }

    private static IRecipe addRecipe(IRecipe recipe) {
        // noinspection unchecked
        CraftingManager.getInstance()
            .getRecipeList()
            .add(recipe);
        return recipe;
    }

    private static IRecipe addShapedRecipe(ItemStack stackResult, Object... params) {
        return CraftingManager.getInstance()
            .addRecipe(stackResult, params);
    }

    private static IRecipe addShapedOreDictRecipe(ItemStack stackResult, Object... params) {
        IRecipe recipe = new ShapedOreRecipe(stackResult, params);
        return addRecipe(recipe);
    }

    private static IRecipe addShapelessRecipe(ItemStack stackResult, ItemStack... ingredients) {
        ArrayList<ItemStack> list = new ArrayList<>();
        Collections.addAll(list, ingredients);
        IRecipe recipe = new ShapelessRecipes(stackResult, list);
        return addRecipe(recipe);
    }

    static void registerWorkbenchRecipes() {
        RECIPES.put(
            "ThaumiumSickle",
            addShapedOreDictRecipe(
                new ItemStack(MNItems.sickleThaumium, 1),
                " I ",
                "  I",
                "SI ",
                'I',
                "ingotThaumium",
                'S',
                "stickWood"));
        RECIPES.put(
            "VoidSickle",
            addShapedOreDictRecipe(
                new ItemStack(MNItems.voidSickle, 1),
                " I ",
                "  I",
                "SI ",
                'I',
                "ingotVoid",
                'S',
                "stickWood"));

        RECIPES.put(
            "GreatwoodSlab",
            new IRecipe[] {
                addShapedRecipe(
                    new ItemStack(ConfigBlocks.blockSlabWood, 6, 0),
                    "WWW",
                    'W',
                    new ItemStack(MNBlocks.arcaneWood, 1, 0)),
                addShapedRecipe(
                    new ItemStack(ConfigBlocks.blockSlabWood, 6, 0),
                    "WWW",
                    'W',
                    new ItemStack(MNBlocks.arcaneWood, 1, 1)) });
        RECIPES.put(
            "SilverwoodSlab",
            new IRecipe[] {
                addShapedRecipe(
                    new ItemStack(ConfigBlocks.blockSlabWood, 6, 1),
                    "WWW",
                    'W',
                    new ItemStack(MNBlocks.arcaneWood, 1, 2)),
                addShapedRecipe(
                    new ItemStack(ConfigBlocks.blockSlabWood, 6, 1),
                    "WWW",
                    'W',
                    new ItemStack(MNBlocks.arcaneWood, 1, 3)) });

        RECIPES.put(
            "PlankSilverwood",
            addShapedRecipe(
                new ItemStack(MNBlocks.arcaneWood, 1, 2),
                "W",
                "W",
                'W',
                new ItemStack(ConfigBlocks.blockSlabWood, 6, 1)));
        RECIPES.put(
            "GreatwoodOrn",
            addShapedRecipe(
                new ItemStack(MNBlocks.arcaneWood, 1, 1),
                "W",
                "W",
                'W',
                new ItemStack(ConfigBlocks.blockSlabWood, 6, 0)));
        RECIPES.put(
            "GreatwoodGoldTrim",
            addShapedRecipe(
                new ItemStack(MNBlocks.arcaneWood, 3, 6),
                "WWW",
                "NNN",
                "WWW",
                'N',
                Items.gold_nugget,
                'W',
                new ItemStack(ConfigBlocks.blockSlabWood, 6, 0)));
        RECIPES.put(
            "GreatwoodGoldOrn1",
            addShapedRecipe(
                new ItemStack(MNBlocks.arcaneWood, 4, 4),
                "NWN",
                "WNW",
                "NWN",
                'N',
                Items.gold_nugget,
                'W',
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6)));
        RECIPES.put(
            "GreatwoodGoldOrn2",
            addShapedRecipe(
                new ItemStack(MNBlocks.arcaneWood, 4, 5),
                "NWN",
                "WIW",
                "NWN",
                'I',
                Items.gold_ingot,
                'N',
                Items.gold_nugget,
                'W',
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6)));

        RECIPES.put(
            "WoodConversion",
            new IRecipe[] {
                addShapelessRecipe(
                    new ItemStack(MNBlocks.arcaneWood, 1, 0),
                    new ItemStack(MNItems.alchemicalStone, 1, 0),
                    new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6)),
                addShapelessRecipe(
                    new ItemStack(MNBlocks.arcaneWood, 1, 1),
                    new ItemStack(MNItems.alchemicalStone, 1, 0),
                    new ItemStack(MNBlocks.arcaneWood, 1, 0)),
                addShapelessRecipe(
                    new ItemStack(MNBlocks.arcaneWood, 1, 2),
                    new ItemStack(MNItems.alchemicalStone, 1, 0),
                    new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7)),
                addShapelessRecipe(
                    new ItemStack(MNBlocks.arcaneWood, 1, 3),
                    new ItemStack(MNItems.alchemicalStone, 1, 0),
                    new ItemStack(MNBlocks.arcaneWood, 1, 2)),
                addShapelessRecipe(
                    new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                    new ItemStack(MNItems.alchemicalStone, 1, 0),
                    new ItemStack(MNBlocks.arcaneWood, 1, 1)),
                addShapelessRecipe(
                    new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7),
                    new ItemStack(MNItems.alchemicalStone, 1, 0),
                    new ItemStack(MNBlocks.arcaneWood, 1, 3)) });

        IRecipe[] recipe = new IRecipe[32];
        for (int meta = 0; meta < 16; meta++) {
            recipe[meta] = addShapelessRecipe(
                new ItemStack(Blocks.stained_hardened_clay, 1, meta),
                new ItemStack(MNItems.alchemicalStone, 1, 0),
                new ItemStack(Blocks.stained_hardened_clay, 1, 15 - meta));
            recipe[31 - meta] = addShapelessRecipe(
                new ItemStack(Blocks.wool, 1, meta),
                new ItemStack(MNItems.alchemicalStone, 1, 0),
                new ItemStack(Blocks.wool, 1, 15 - meta));
        }
        RECIPES.put("ColorConversion", recipe);
    }

    static void registerArcaneRecipes() {
        AspectList aspects;
        Object[] recipe;

        aspects = new AspectList().add(Aspect.ORDER, 4)
            .add(Aspect.ENTROPY, 2)
            .add(Aspect.AIR, 2)
            .add(Aspect.EARTH, 4)
            .add(Aspect.FIRE, 2)
            .add(Aspect.WATER, 2);
        recipe = new Object[] { " L ", "TBS", " L ", 'S', ConfigItems.itemInkwell, 'T', ConfigItems.itemThaumometer,
            'L', Items.string, 'B', Items.book };
        RECIPES.put(
            "BiomeReport",
            registerArcaneRecipe(
                MNResearch.GEO_OCCULTISM.getId(),
                new ItemStack(MNItems.biomeReport, 1),
                aspects,
                recipe));

        aspects = new AspectList().add(Aspect.ORDER, 20)
            .add(Aspect.ENTROPY, 20)
            .add(Aspect.AIR, 20)
            .add(Aspect.EARTH, 20)
            .add(Aspect.FIRE, 20)
            .add(Aspect.WATER, 20);
        recipe = new Object[] { "ALI", "TBW", "OLE", 'A', Shard.AIR.createItem(), 'I', Shard.FIRE.createItem(), 'T',
            Shard.WATER.createItem(), 'W', Shard.EARTH.createItem(), 'O', Shard.ORDER.createItem(), 'E',
            Shard.ENTROPY.createItem(), 'L', Items.leather, 'B', Items.book };
        RECIPES.put(
            "ResearchLog",
            registerArcaneRecipe(
                MNResearch.RESEARCH_LOG.getId(),
                new ItemStack(MNItems.researchLog, 1),
                aspects,
                recipe));

        aspects = new AspectList().add(Aspect.ORDER, 2)
            .add(Aspect.ENTROPY, 2)
            .add(Aspect.AIR, 2)
            .add(Aspect.EARTH, 2)
            .add(Aspect.FIRE, 2)
            .add(Aspect.WATER, 2);
        recipe = new Object[] { "NXI", "N  ", 'I', Resource.THAUMIUM_INGOT.createItem(), 'N',
            ResourceNugget.THAUMIUM_NUGGET.createItem(), 'X', ResourceNugget.IRON_NUGGET.createItem() };
        RECIPES.put(
            "ThaumiumKey1",
            registerArcaneRecipe(
                MNResearch.ARCANE_KEYS.getId(),
                new ItemStack(MNItems.arcaneKey, 2, 0),
                aspects,
                recipe));
        recipe = new Object[] { "NXI", "N  ", 'I', Resource.THAUMIUM_INGOT.createItem(), 'N',
            ResourceNugget.THAUMIUM_NUGGET.createItem(), 'X', Items.gold_nugget };
        RECIPES.put(
            "ThaumiumKey2",
            registerArcaneRecipe(
                MNResearch.ARCANE_KEYS.getId(),
                new ItemStack(MNItems.arcaneKey, 2, 1),
                aspects,
                recipe));

        aspects = new AspectList().add(Aspect.ORDER, 5)
            .add(Aspect.ENTROPY, 5)
            .add(Aspect.AIR, 5)
            .add(Aspect.EARTH, 5)
            .add(Aspect.FIRE, 5)
            .add(Aspect.WATER, 5);
        recipe = new Object[] { "ILI", "TGT", 'I', Items.gold_ingot, 'L', Items.leather, 'T',
            ConfigItems.itemThaumometer, 'G', ConfigItems.itemGoggles };
        RECIPES.put(
            "Spectacles",
            registerArcaneRecipe(MNResearch.SPECTACLES.getId(), new ItemStack(MNItems.spectacles), aspects, recipe));

        aspects = new AspectList().add(Aspect.ORDER, 30)
            .add(Aspect.ENTROPY, 30)
            .add(Aspect.AIR, 30)
            .add(Aspect.EARTH, 30)
            .add(Aspect.FIRE, 30)
            .add(Aspect.WATER, 30);
        recipe = new Object[] { "ASI", "TDW", "OBE", 'A', Shard.AIR.createItem(), 'I', Shard.FIRE.createItem(), 'T',
            Shard.WATER.createItem(), 'W', Shard.EARTH.createItem(), 'O', Shard.ORDER.createItem(), 'E',
            Shard.ENTROPY.createItem(), 'S', ConfigItems.itemInkwell, 'D',
            new ItemStack(ConfigBlocks.blockTable, 1, 14), 'B', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6) };
        RECIPES.put(
            "TranscribingTable",
            registerArcaneRecipe(
                MNResearch.TRANSCRIBING_TABLE.getId(),
                new ItemStack(MNBlocks.transcribingTable),
                aspects,
                recipe));

        aspects = new AspectList().add(Aspect.WATER, 20)
            .add(Aspect.ORDER, 15)
            .add(Aspect.EARTH, 15)
            .add(Aspect.FIRE, 10);
        recipe = new Object[] { "IBI", "WCW", "IWI", 'I', Resource.THAUMIUM_INGOT.createItem(), 'B',
            ConfigItems.itemZombieBrain, 'W', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6), 'C', Blocks.chest };
        ItemStack arcaneChest = new ItemStack(MNBlocks.arcaneChest, 1, ArcaneChestType.GREAT_WOOD.id());
        RECIPES
            .put("ArcaneChest1", registerArcaneRecipe(MNResearch.ARCANE_CHEST.getId(), arcaneChest, aspects, recipe));
        recipe = new Object[] { "IBI", "WCW", "IWI", 'I', Resource.THAUMIUM_INGOT.createItem(), 'B',
            ConfigItems.itemZombieBrain, 'W', new ItemStack(MNBlocks.arcaneWood, 1, 2), 'C', Blocks.chest };
        arcaneChest = new ItemStack(MNBlocks.arcaneChest, 1, ArcaneChestType.SILVER_WOOD.id());
        RECIPES
            .put("ArcaneChest2", registerArcaneRecipe(MNResearch.ARCANE_CHEST.getId(), arcaneChest, aspects, recipe));
    }

    static void registerInfusionRecipes() {
        AspectList aspects;
        ItemStack[] recipe;

        // ======================================================================
        // 【白瞳者之镰】注魔配方
        // 注意：外围物品当前 13 个，超过 TC4 上限 12 个！
        // 请从 recipe 数组中再删掉 1 个物品，配方才能生效。
        // ======================================================================
        aspects = new AspectList()
            // --- 原有要素，保持不变 ---
            .add(Aspect.WEAPON, 828)
            .add(Aspect.ENERGY, 320)
            .add(Aspect.AURA, 64)
            .add(Aspect.ELDRITCH, 666)
            .add(Aspect.DEATH, 640)
            // --- 新增要素：暂定全部 = 640（与死亡相同）---
            .add(Aspect.DARKNESS, 640) // 黑暗
            .add(Aspect.VOID, 640) // 虚空
            .add(Aspect.HEAL, 640) // 治疗（生命类）
            .add(Aspect.HUNGER, 640) // 饥饿
            .add(Aspect.GREED, 640); // 贪婪

        recipe = new ItemStack[] {
            // --- 保留的原有物品（各 1 个）---
            new ItemStack(Items.nether_star), // 下界之星 ×1
                                              // //
                                              // 元始杖芯
                                              // ×1
            new ItemStack(ConfigItems.itemBucketDeath), // 桶装死亡水 ×1

            // --- 新增物品 ---
            new ItemStack(ConfigItems.itemShard, 1, 6), // ❓平衡碎片（暂用 itemShard:6）
            new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 8), // ❓血腥教徒旗帜（暂用 blockWoodenDevice:8）
            new ItemStack(ConfigItems.itemSwordCrimson), // 血腥之刃 ×1
            new ItemStack(ConfigItems.itemResource, 1, 15), // ❓元始魔力（暂用 itemResource:17）
            new ItemStack(ConfigItems.itemEldritchObject, 1, 3), // ❓元始珍珠（暂用 itemEldritchObject:3）
            new ItemStack(ConfigItems.itemFocusPrimal), // 元始法杖核心 ×1
            new ItemStack(ConfigItems.itemPrimalCrusher), // 元始杵 ×1
            new ItemStack(ConfigItems.itemEldritchObject, 1, 0), // ❓邪术之眼 ×1（暂用 itemEldritchObject:0）
            new ItemStack(ConfigItems.itemEldritchObject, 1, 2) // ❓符文石板（暂用 itemEldritchObject:2）

        };

        RECIPES.put(
            "HerobrinesScythe",
            registerInfusionRecipe(
                MNResearch.HEROBRINES_SCYTHE.getId(),
                new ItemStack(MNItems.herobrinesScythe),
                20,
                aspects,
                new ItemStack(MNItems.sickleElemental), // 中间核心：富饶镰刀，不变
                recipe));

        aspects = new AspectList().add(Aspect.WEATHER, 9)
            .add(Aspect.AURA, 16)
            .add(Aspect.EXCHANGE, 8)
            .add(Aspect.MECHANISM, 12);
        recipe = new ItemStack[] { new ItemStack(ConfigItems.itemFocusTrade), new ItemStack(MNItems.alchemicalStone),
            Shard.AIR.createItem(), Shard.FIRE.createItem(), Shard.WATER.createItem(), Shard.EARTH.createItem(),
            Shard.ORDER.createItem(), Shard.ENTROPY.createItem() };
        RECIPES.put(
            "GeoPylon",
            registerInfusionRecipe(
                MNResearch.GEO_OCCULTISM.getId(),
                new ItemStack(MNBlocks.geoPylon),
                8,
                aspects,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 14),
                recipe));

        aspects = new AspectList().add(Aspect.SENSES, 32)
            .add(Aspect.ARMOR, 16)
            .add(Aspect.DARKNESS, 32);
        recipe = new ItemStack[] { Shard.ENTROPY.createItem(), Shard.ENTROPY.createItem(),
            new ItemStack(Items.spider_eye), new ItemStack(Items.spider_eye), new ItemStack(Items.iron_ingot),
            new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
            new ItemStack(ConfigItems.itemZombieBrain) };
        RECIPES.put(
            "DarkGoggles",
            registerInfusionRecipe(
                MNResearch.DARK_GOGGLES.getId(),
                new ItemStack(MNItems.gogglesDark),
                3,
                aspects,
                new ItemStack(ConfigItems.itemGoggles),
                recipe));

        aspects = new AspectList().add(Aspect.GREED, 32)
            .add(Aspect.CROP, 16)
            .add(Aspect.HARVEST, 24)
            .add(Aspect.TOOL, 8);
        ItemStack stackBook = new ItemStack(Items.enchanted_book);
        Items.enchanted_book.addEnchantment(stackBook, new EnchantmentData(Enchantment.fortune.effectId, 2));
        recipe = new ItemStack[] { Shard.ORDER.createItem(), Shard.ENTROPY.createItem(),
            new ItemStack(Items.wheat_seeds), Resource.THAUMIUM_INGOT.createItem(),
            Resource.THAUMIUM_INGOT.createItem(), stackBook };
        RECIPES.put(
            "ElementalSickle",
            registerInfusionRecipe(
                MNResearch.SICKLE_OF_ABUNDANCE.getId(),
                new ItemStack(MNItems.sickleElemental),
                1,
                aspects,
                new ItemStack(MNItems.sickleThaumium),
                recipe));

        aspects = new AspectList().add(Aspect.CRAFT, 32)
            .add(Aspect.TOOL, 16)
            .add(Aspect.EXCHANGE, 8)
            .add(Aspect.MECHANISM, 3);
        recipe = new ItemStack[] { Shard.ORDER.createItem(), Shard.BALANCED.createItem(), Shard.ENTROPY.createItem(),
            new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), new ItemStack(ConfigItems.itemShovelElemental),
            new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), Resource.QUICKSILVER.createItem(),
            new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7) };
        RECIPES.put(
            "ConstructionFocus",
            registerInfusionRecipe(
                MNResearch.CONSTRUCTION_FOCUS.getId(),
                new ItemStack(MNItems.focusBuild),
                5,
                aspects,
                new ItemStack(ConfigItems.itemFocusTrade),
                recipe));

        aspects = (new AspectList()).add(Aspect.UNDEAD, 25)
            .add(Aspect.FLESH, 15)
            .add(Aspect.BEAST, 15)
            .add(Aspect.ENTROPY, 25);
        recipe = new ItemStack[] { new ItemStack(ConfigItems.itemZombieBrain), Shard.EARTH.createItem(),
            new ItemStack(Items.rotten_flesh), Shard.WATER.createItem(), new ItemStack(Items.rotten_flesh),
            Shard.ENTROPY.createItem() };
        RECIPES.put(
            "RevenantFocus",
            registerInfusionRecipe(
                MNResearch.REVENANT_FOCUS.getId(),
                new ItemStack(MNItems.focusRevenant),
                3,
                aspects,
                Resource.QUICKSILVER.createItem(),
                recipe));

        aspects = new AspectList().add(Aspect.ELDRITCH, 8)
            .add(Aspect.VOID, 8)
            .add(Aspect.TRAVEL, 8)
            .add(Aspect.EXCHANGE, 3);
        recipe = new ItemStack[] { new ItemStack(Items.ender_pearl), new ItemStack(Blocks.ender_chest),
            new ItemStack(Items.ender_pearl) };
        RECIPES.put(
            "EnderPouch",
            registerInfusionRecipe(
                MNResearch.ENDER_POUCH.getId(),
                new ItemStack(MNItems.focusPouchEnder),
                1,
                aspects,
                new ItemStack(ConfigItems.itemFocusPouch),
                recipe));

        aspects = new AspectList().add(Aspect.ELDRITCH, 8)
            .add(Aspect.VOID, 8)
            .add(Aspect.ARMOR, 8)
            .add(Aspect.EXCHANGE, 8);
        recipe = new ItemStack[] { new ItemStack(Items.gold_ingot), new ItemStack(Items.ender_pearl),
            new ItemStack(Items.lead), new ItemStack(Items.ender_pearl), };
        RECIPES.put(
            "JarPrison",
            registerInfusionRecipe(
                MNResearch.PRISON_JAR.getId(),
                new ItemStack(MNBlocks.jarPrison),
                2,
                aspects,
                new ItemStack(ConfigBlocks.blockJar),
                recipe));

        aspects = new AspectList().add(Aspect.EXCHANGE, 32);
        recipe = new ItemStack[] { new ItemStack(Items.emerald), new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7),
            new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7) };
        RECIPES.put(
            "StonePheno",
            registerInfusionRecipe(
                MNResearch.MUTATION_STONE.getId(),
                new ItemStack(MNItems.alchemicalStone, 1, 0),
                2,
                aspects,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),
                recipe));

        aspects = new AspectList().add(Aspect.EXCHANGE, 16)
            .add(Aspect.WATER, 16)
            .add(Aspect.MAGIC, 8)
            .add(Aspect.FLESH, 6);
        recipe = new ItemStack[] { new ItemStack(Items.glowstone_dust),
            new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6), new ItemStack(Items.glowstone_dust),
            new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6) };
        RECIPES.put(
            "StoneQuick",
            registerInfusionRecipe(
                MNResearch.QUICKSILVER_STONE.getId(),
                new ItemStack(MNItems.alchemicalStone, 1, 1),
                2,
                aspects,
                Resource.QUICKSILVER.createItem(),
                recipe));

        aspects = new AspectList().add(Aspect.MOTION, 16)
            .add(Aspect.SOUL, 16)
            .add(Aspect.ENTROPY, 16)
            .add(Aspect.FLESH, 6);
        recipe = new ItemStack[] { new ItemStack(Items.gold_ingot), new ItemStack(ConfigItems.itemZombieBrain),
            Shard.BALANCED.createItem(), new ItemStack(Items.iron_ingot), new ItemStack(Items.spider_eye),
            new ItemStack(Items.ender_eye) };
        RECIPES.put(
            "CorruptedTrunk",
            registerInfusionRecipe(
                MNResearch.EVIL_TRUNK.getId(),
                new ItemStack(MNItems.evilTrunkSpawner, 1, 0),
                2,
                aspects,
                new ItemStack(ConfigItems.itemTrunkSpawner),
                recipe));

        aspects = new AspectList().add(Aspect.TAINT, 16)
            .add(Aspect.SOUL, 16)
            .add(Aspect.ENTROPY, 16)
            .add(Aspect.FLESH, 8);
        recipe = new ItemStack[] { Resource.TAINT_TENDRIL.createItem(), new ItemStack(Items.gold_ingot),
            Resource.TAINTED_GOO.createItem(), Resource.TAINT_TENDRIL.createItem(), new ItemStack(Items.ender_eye),
            Resource.TAINTED_GOO.createItem() };
        RECIPES.put(
            "TaintedTrunk",
            registerInfusionRecipe(
                MNResearch.EVIL_TRUNK.getId(),
                new ItemStack(MNItems.evilTrunkSpawner, 1, 3),
                6,
                aspects,
                new ItemStack(MNItems.evilTrunkSpawner),
                recipe));

        aspects = new AspectList().add(Aspect.FIRE, 16)
            .add(Aspect.SOUL, 16)
            .add(Aspect.ENTROPY, 16)
            .add(Aspect.FLESH, 8);
        recipe = new ItemStack[] { new ItemStack(Items.blaze_rod), new ItemStack(Items.gold_ingot),
            new ItemStack(Blocks.nether_brick), new ItemStack(Items.blaze_rod), new ItemStack(Blocks.nether_brick),
            new ItemStack(Blocks.quartz_block) };
        RECIPES.put(
            "DemonicTrunk",
            registerInfusionRecipe(
                MNResearch.EVIL_TRUNK.getId(),
                new ItemStack(MNItems.evilTrunkSpawner, 1, 2),
                6,
                aspects,
                new ItemStack(MNItems.evilTrunkSpawner),
                recipe));

        aspects = new AspectList().add(Aspect.MIND, 16)
            .add(Aspect.SOUL, 16)
            .add(Aspect.ENTROPY, 16)
            .add(Aspect.FLESH, 8);
        recipe = new ItemStack[] { new ItemStack(ConfigItems.itemZombieBrain), new ItemStack(Items.gold_ingot),
            new ItemStack(ConfigBlocks.blockJar, 1, 1), new ItemStack(ConfigItems.itemAmuletVis),
            new ItemStack(ConfigItems.itemZombieBrain), new ItemStack(Items.spider_eye) };
        RECIPES.put(
            "SinisterTrunk",
            registerInfusionRecipe(
                MNResearch.EVIL_TRUNK.getId(),
                new ItemStack(MNItems.evilTrunkSpawner, 1, 1),
                6,
                aspects,
                new ItemStack(MNItems.evilTrunkSpawner),
                recipe));
    }

    private static InfusionRecipe registerInfusionRecipe(ResourceLocation research, Object result, int instability,
        AspectList aspects, ItemStack input, ItemStack[] recipe) {
        return ThaumcraftApi
            .addInfusionCraftingRecipe(research.toString(), result, instability, aspects, input, recipe);
    }

    public static ShapedArcaneRecipe registerArcaneRecipe(ResourceLocation research, ItemStack result,
        AspectList aspects, Object... recipe) {
        return ThaumcraftApi.addArcaneCraftingRecipe(research.toString(), result, aspects, recipe);
    }

    private enum Shard {

        AIR,
        FIRE,
        WATER,
        EARTH,
        ORDER,
        ENTROPY,
        BALANCED;

        public ItemStack createItem() {
            return createItem(1);
        }

        public ItemStack createItem(int count) {
            return new ItemStack(ConfigItems.itemShard, count, ordinal());
        }

    }

    private enum Resource {

        ALUMENTUM(0),
        NITOR(1),
        THAUMIUM_INGOT(2),
        QUICKSILVER(3),
        MAGIC_TALLOW(4),
        // #5 ZOMBIE BRAIN was removed and turned into a standalone item
        AMBER(6),
        ENCHANTED_FABRIC(7),
        VIS_FILTER(8),
        KNOWLEDGE_FRAGMENT(9),
        MIRRORED_GLASS(10),
        TAINTED_GOO(11),
        TAINT_TENDRIL(12),
        JAR_LABEL(13),
        SALIS_MUNDUS(14),
        PRIMAL_CHARM(15),
        VOID_METAL_INGOT(16),
        VOID_SEED(17),
        GOLD_COIN(18);

        private final int metadata;

        Resource(int metadata) {
            this.metadata = metadata;
        }

        public ItemStack createItem() {
            return createItem(1);
        }

        public ItemStack createItem(int count) {
            return new ItemStack(ConfigItems.itemResource, count, metadata);
        }

    }

    private enum ResourceNugget {

        IRON_NUGGET(0),
        COPPER_NUGGER(1),
        TIN_NUGGET(2),
        SILVER_NUGGET(3),
        LEAD_NUGGET(4),
        QUICKSILVER_DROP(5),
        THAUMIUM_NUGGET(6),
        VOID_METAL_NUGGET(7),
        NATIVE_IRON_CLUSTER(16),
        NATIVE_COPPER_CLUSTER(17),
        NATIVE_TIN_CLUSTER(18),
        NATIVE_SILVER_CLUSTER(19),
        NATIVE_LEAD_CLUSTER(20),
        NATIVE_CINNABAR_CLUSTER(21),
        NATIVE_GOLD_CLUSTER(31);

        private final int metadata;

        ResourceNugget(int metadata) {
            this.metadata = metadata;
        }

        public ItemStack createItem() {
            return createItem(1);
        }

        public ItemStack createItem(int count) {
            return new ItemStack(ConfigItems.itemNugget, count, metadata);
        }

    }

}
