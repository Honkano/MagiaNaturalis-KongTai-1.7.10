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


        // ======================================================================
        // 【揭示之护目镜 · 饰品版】注魔配方
        // 
        // 核心物品：高级揭示护目镜
        // 外围材料：元始珍珠 + 世界盐 + 元始魔力×2 + 六大碎片
        // 不稳定度：2（微乎其微）
        // ======================================================================
        aspects = new AspectList()
            .add(Aspect.SENSES, 64)
            .add(Aspect.AURA, 32)
            .add(Aspect.MAGIC, 32)
            .add(Aspect.LIGHT, 32)
            .add(Aspect.DARKNESS, 16)
            .add(Aspect.EXCHANGE, 16);
        recipe = new ItemStack[] {
            new ItemStack(ConfigItems.itemEldritchObject, 1, 3),  // 元始珍珠 ×1
            new ItemStack(ConfigItems.itemResource, 1, 14),       // 世界盐 ×1
            new ItemStack(ConfigItems.itemResource, 1, 15),       // 元始魔力 ×2
            new ItemStack(ConfigItems.itemResource, 1, 15),
            Shard.AIR.createItem(),
            Shard.FIRE.createItem(),
            Shard.WATER.createItem(),
            Shard.EARTH.createItem(),
            Shard.ORDER.createItem(),
            Shard.ENTROPY.createItem()
        };
        RECIPES.put(
            "RevealingGoggles",
            registerInfusionRecipe(
                MNResearch.REVEALING_GOGGLES.getId(),
                new ItemStack(MNItems.revealingGoggles),
                2,                                             // 不稳定度（微乎其微）
                aspects,
                new ItemStack(MNItems.spectacles),             // 核心：高级揭示护目镜
                recipe));

        // ======================================================================
        // 【三张面具】注魔配方
        // 对照原版神秘时代 MASKGRINNINGDEVIL / MASKANGRYGHOST / MASKSIPPINGFIEND
        // 中心物品从「神秘要塞头盔」改为「凡人护身符」
        // ======================================================================

        // ---- 狞笑恶魔面具（Meta 0）：核心为凡人护身符 ----
        aspects = new AspectList().add(Aspect.MIND, 64)
            .add(Aspect.HEAL, 64)
            .add(Aspect.ARMOR, 16);
        recipe = new ItemStack[] {
            new ItemStack(Items.dye, 1, 0),
            new ItemStack(Items.iron_ingot),
            new ItemStack(Items.leather),
            new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2),
            new ItemStack(ConfigItems.itemZombieBrain),
            new ItemStack(Items.iron_ingot)
        };
        RECIPES.put(
            "MaskGrinningDevil",
            registerInfusionRecipe(
                MNResearch.MASK_GRINNING_DEVIL.getId(),
                new ItemStack(MNItems.mask, 1, 0),
                8,
                aspects,
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0),
                recipe));

        // ---- 暴怒幽魂面具（Meta 1）：核心为凡人护身符 ----
        aspects = new AspectList().add(Aspect.ENTROPY, 64)
            .add(Aspect.DEATH, 64)
            .add(Aspect.ARMOR, 16);
        recipe = new ItemStack[] {
            new ItemStack(Items.dye, 1, 15),
            new ItemStack(Items.iron_ingot),
            new ItemStack(Items.leather),
            new ItemStack(Items.poisonous_potato),
            new ItemStack(Items.skull, 1, 1),
            new ItemStack(Items.iron_ingot)
        };
        RECIPES.put(
            "MaskAngryGhost",
            registerInfusionRecipe(
                MNResearch.MASK_ANGRY_GHOST.getId(),
                new ItemStack(MNItems.mask, 1, 1),
                8,
                aspects,
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0),
                recipe));

        // ---- 嗜血邪妖面具（Meta 2）：核心为凡人护身符 ----
        aspects = new AspectList().add(Aspect.UNDEAD, 64)
            .add(Aspect.LIFE, 64)
            .add(Aspect.ARMOR, 16);
        recipe = new ItemStack[] {
            new ItemStack(Items.dye, 1, 1),
            new ItemStack(Items.iron_ingot),
            new ItemStack(Items.leather),
            new ItemStack(Items.ghast_tear),
            new ItemStack(Items.milk_bucket),
            new ItemStack(Items.iron_ingot)
        };
        RECIPES.put(
            "MaskSippingFiend",
            registerInfusionRecipe(
                MNResearch.MASK_SIPPING_FIEND.getId(),
                new ItemStack(MNItems.mask, 1, 2),
                8,
                aspects,
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0),
                recipe));



        // ======================================================================
        // 【态度面具】注魔配方
        // 核心：凡人护身符
        // 不引用三张面具，只用神秘时代材料
        // 不稳定度 20（最危险）
        // ======================================================================
        aspects = new AspectList()
            .add(Aspect.MIND, 64)
            .add(Aspect.SENSES, 64)
            .add(Aspect.SOUL, 32)
            .add(Aspect.MAGIC, 32)
            .add(Aspect.DARKNESS, 32);
        recipe = new ItemStack[] {
            new ItemStack(ConfigItems.itemEldritchObject, 1, 3),   // 元始珍珠
            new ItemStack(ConfigItems.itemResource, 1, 14),        // 世界盐
            new ItemStack(ConfigItems.itemResource, 1, 15),        // 元始魔力 ×2
            new ItemStack(ConfigItems.itemResource, 1, 15),
            new ItemStack(ConfigItems.itemZombieBrain),            // 僵尸脑（心）
            Shard.AIR.createItem(),
            Shard.FIRE.createItem(),
            Shard.WATER.createItem(),
            Shard.EARTH.createItem(),
            Shard.ORDER.createItem(),
            Shard.ENTROPY.createItem()
        };
        RECIPES.put(
            "MaskAttitude",
            registerInfusionRecipe(
                MNResearch.MASK_ATTITUDE.getId(),
                new ItemStack(MNItems.mask, 1, 3),
                20,                                                // 最危险
                aspects,
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0), // 核心：凡人护身符
                recipe));


        // ======================================================================
        // 【恶毒旅行箱系列】注魔配方：链式升级
        // 升级链：邪恶(0) → 阴险(1) → 污染(3) → 恶魔(2)
        // 每个配方对应独立研究，核心物品为链条上一级的箱子宝宝
        // ======================================================================

        // ---- 邪恶箱子宝宝（Meta 0）：核心为原版旅行箱刷怪器 ----
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

        // ---- 阴险箱子宝宝（Meta 1）：核心为邪恶箱子宝宝（Meta 0）----
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
                MNResearch.SINISTER_TRUNK.getId(),
                new ItemStack(MNItems.evilTrunkSpawner, 1, 1),
                6,
                aspects,
                new ItemStack(MNItems.evilTrunkSpawner, 1, 0), // ← 核心：邪恶箱子宝宝
                recipe));

        // ---- 污染箱子宝宝（Meta 3）：核心为阴险箱子宝宝（Meta 1）----
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
                MNResearch.TAINTED_TRUNK.getId(),
                new ItemStack(MNItems.evilTrunkSpawner, 1, 3),
                6,
                aspects,
                new ItemStack(MNItems.evilTrunkSpawner, 1, 1), // ← 核心：阴险箱子宝宝
                recipe));

        // ---- 恶魔箱子宝宝（Meta 2）：核心为污染箱子宝宝（Meta 3）----
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
                MNResearch.DEMONIC_TRUNK.getId(),
                new ItemStack(MNItems.evilTrunkSpawner, 1, 2),
                6,
                aspects,
                new ItemStack(MNItems.evilTrunkSpawner, 1, 3), // ← 核心：污染箱子宝宝
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




    // =========================================================================================
    // =========================================================================================
    // 【小白都能看懂：Thaumcraft 配方注册完全说明 · 对照 ThaumcraftApi 源码精修版】
    // =========================================================================================
    // =========================================================================================
    //
    // 本说明对照以下源码逐字核对：
    //   thaumcraft/api/ThaumcraftApi.java
    //   thaumcraft/api/ThaumcraftApiHelper.java
    //   thaumcraft/api/crafting/ShapedArcaneRecipe.java
    //   thaumcraft/api/crafting/ShapelessArcaneRecipe.java
    //   thaumcraft/api/crafting/CrucibleRecipe.java
    //   thaumcraft/api/crafting/InfusionRecipe.java
    //   thaumcraft/api/crafting/InfusionEnchantmentRecipe.java
    //
    // ============================
    // 零、配方和研究页面的关系
    // ============================
    // 研究节点（ResearchItem） = “告诉玩家存在这个东西”
    // 配方（Recipe）           = “告诉玩家怎么做这个东西”
    // 研究页面（ResearchPage） = “把配方显示在魔导手册里”
    //
    // 三者关系：
    //   1. 先注册配方 → 游戏知道怎么做这个东西
    //   2. 再注册研究节点 → 玩家解锁后能看到这个知识
    //   3. 在研究节点的 setPages(...) 里塞 ResearchPage(配方)
    //      → 玩家在魔导手册里看到“怎么做”
    //
    // 关键：研究key 和 配方key 必须完全一致，否则手册里配方不显示。
    //
    // ============================
    // 一、五个注册方法（对照 ThaumcraftApi.java 逐字核对）
    // ============================
    //
    // -----------------------------------------------------------------------------------------
    // 【1】注册有序奥术配方
    // -----------------------------------------------------------------------------------------
    // 源码方法签名：
    //
    //   public static ShapedArcaneRecipe addArcaneCraftingRecipe(
    //       String research, ItemStack result, AspectList aspects, Object ... recipe)
    //
    // 参数说明：
    //   ① research：研究key，String
    //   ② result：输出，ItemStack ⚠️ 只接受 ItemStack！
    //   ③ aspects：魔力（vis），AspectList
    //   ④ recipe：网格 + 字符对应表，Object...
    //
    // 返回：ShapedArcaneRecipe（创建出来的配方对象）
    //
    // 用法：
    //
    // ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe(
    //     "revealing_goggles",
    //     new ItemStack(MNItems.revealingGoggles),
    //     new AspectList()
    //         .add(Aspect.AIR, 10)
    //         .add(Aspect.ORDER, 5),
    //     "ABA",
    //     " C ",
    //     "   ",
    //     'A', new ItemStack(ConfigItems.itemResource, 1, 14),
    //     'B', new ItemStack(ConfigItems.itemResource, 1, 15),
    //     'C', new ItemStack(Items.diamond));
    //
    // ⚠️ 重要：这个方法只接受 ItemStack 作为 result。
    //    虽然 ShapedArcaneRecipe 构造函数有 Block / Item 的重载，
    //    但 ThaumcraftApi.addArcaneCraftingRecipe 只接受 ItemStack。
    //    如果你想传 Block 或 Item，要先 new ItemStack(block) 包一层。
    //
    // -----------------------------------------------------------------------------------------
    // 【2】注册无序奥术配方
    // -----------------------------------------------------------------------------------------
    // 源码方法签名：
    //
    //   public static ShapelessArcaneRecipe addShapelessArcaneCraftingRecipe(
    //       String research, ItemStack result, AspectList aspects, Object ... recipe)
    //
    // ⚠️ 方法名是 addShapelessArcaneCraftingRecipe，不是 addShapelessArcaneRecipe！
    //    我之前写错过，这次修正。
    //
    // 参数说明：
    //   ① research：研究key，String
    //   ② result：输出，ItemStack
    //   ③ aspects：魔力（vis），AspectList
    //   ④ recipe：材料列表，Object...
    //
    // 用法：
    //
    // ShapelessArcaneRecipe recipe = ThaumcraftApi.addShapelessArcaneCraftingRecipe(
    //     "revealing_goggles",
    //     new ItemStack(MNItems.revealingGoggles),
    //     new AspectList().add(Aspect.WATER, 20),
    //     new ItemStack(ConfigItems.itemResource, 1, 14),
    //     new ItemStack(ConfigItems.itemResource, 1, 15),
    //     new ItemStack(Items.diamond));
    //
    // -----------------------------------------------------------------------------------------
    // 【3】注册坩埚配方
    // -----------------------------------------------------------------------------------------
    // 源码方法签名：
    //
    //   public static CrucibleRecipe addCrucibleRecipe(
    //       String key, ItemStack result, Object catalyst, AspectList tags)
    //
    // 参数说明：
    //   ① key：研究key，String
    //   ② result：输出，ItemStack
    //   ③ catalyst：催化剂，Object
    //       可以是：ItemStack / String（矿物词典名）/ ArrayList<ItemStack>
    //   ④ tags：源质（essentia），AspectList
    //
    // 用法：
    //
    // CrucibleRecipe recipe = ThaumcraftApi.addCrucibleRecipe(
    //     "revealing_goggles",
    //     new ItemStack(MNItems.revealingGoggles),
    //     new ItemStack(ConfigItems.itemResource, 1, 14),
    //     new AspectList()
    //         .add(Aspect.MAGIC, 4)
    //         .add(Aspect.SENSES, 2));
    //
    // 矿物词典写法：
    //
    // ThaumcraftApi.addCrucibleRecipe(
    //     "revealing_goggles",
    //     new ItemStack(MNItems.revealingGoggles),
    //     "ingotIron",   // 矿物词典名，源码内部自动转成 OreDictionary.getOres
    //     new AspectList().add(Aspect.MAGIC, 4));
    //
    // -----------------------------------------------------------------------------------------
    // 【4】注册注魔配方
    // -----------------------------------------------------------------------------------------
    // 源码方法签名：
    //
    //   public static InfusionRecipe addInfusionCraftingRecipe(
    //       String research, Object result, int instability,
    //       AspectList aspects, ItemStack input, ItemStack[] recipe)
    //
    // ⚠️ 源码内部会检查 result：
    //      if (!(result instanceof ItemStack || result instanceof Object[])) return null;
    //    也就是说 result 只能是 ItemStack 或 Object[]，传其他类型直接返回 null。
    //
    // 参数说明：
    //   ① research：研究key，String
    //   ② result：输出，Object（ItemStack 或 Object[]）
    //   ③ instability：不稳定度，int（0~很大，数值越大越容易出乱子）
    //   ④ aspects：源质（essentia），AspectList
    //   ⑤ input：中央基座上的物品，ItemStack
    //   ⑥ recipe：周围基座上的材料，ItemStack[]
    //
    // 用法：
    //
    // InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe(
    //     "revealing_goggles",
    //     new ItemStack(MNItems.revealingGoggles),
    //     2,
    //     new AspectList()
    //         .add(Aspect.SENSES, 16)
    //         .add(Aspect.AURA, 8)
    //         .add(Aspect.MAGIC, 8),
    //     new ItemStack(ConfigItems.itemGoggles),
    //     new ItemStack[] {
    //         new ItemStack(ConfigItems.itemResource, 1, 14),
    //         new ItemStack(ConfigItems.itemResource, 1, 14),
    //         new ItemStack(Items.diamond),
    //         new ItemStack(Items.diamond)
    //     });
    //
    // -----------------------------------------------------------------------------------------
    // 【5】注册注魔附魔
    // -----------------------------------------------------------------------------------------
    // 源码方法签名：
    //
    //   public static InfusionEnchantmentRecipe addInfusionEnchantmentRecipe(
    //       String research, Enchantment enchantment, int instability,
    //       AspectList aspects, ItemStack[] recipe)
    //
    // ⚠️ 第三个参数是 instability（不稳定度），不是附魔等级！
    //    附魔等级由 Enchantment 自身决定。
    //
    // 参数说明：
    //   ① research：研究key，String
    //   ② enchantment：附魔，Enchantment
    //   ③ instability：不稳定度，int
    //   ④ aspects：源质（essentia），AspectList
    //   ⑤ recipe：附魔材料，ItemStack[]
    //
    // 用法：
    //
    // InfusionEnchantmentRecipe recipe = ThaumcraftApi.addInfusionEnchantmentRecipe(
    //     "revealing_goggles",
    //     Enchantment.sharpness,
    //     1,
    //     new AspectList().add(Aspect.WEAPON, 8),
    //     new ItemStack[] {
    //         new ItemStack(Items.flint),
    //         new ItemStack(Items.flint)
    //     });
    //
    // ============================
    // 二、五个配方类的真实构造函数（对照 crafting 包源码）
    // ============================
    //
    // -----------------------------------------------------------------------------------------
    // 【ShapedArcaneRecipe】有序奥术合成
    // -----------------------------------------------------------------------------------------
    // 构造函数（三个重载）：
    //
    //   public ShapedArcaneRecipe(String research, Block     result, AspectList aspects, Object... recipe)
    //   public ShapedArcaneRecipe(String research, Item      result, AspectList aspects, Object... recipe)
    //   public ShapedArcaneRecipe(String research, ItemStack result, AspectList aspects, Object... recipe)
    //
    // 注意：虽然构造函数支持 Block / Item，但 ThaumcraftApi.addArcaneCraftingRecipe
    //      只接受 ItemStack。所以你通过 API 注册时，只能用 ItemStack 版本。
    //
    // 源码细节：
    //   - aspects 是魔力（vis），不是源质
    //   - recipe 是 Object...，可以传 Object[] 或直接展开
    //   - 网格字符串可以传 String[] 或连续多个 String
    //   - 支持镜像合成（mirrored 默认 true），可用 setMirrored(false) 关闭
    //   - matches() 会检查玩家是否完成了对应研究
    //
    // -----------------------------------------------------------------------------------------
    // 【ShapelessArcaneRecipe】无序奥术合成
    // -----------------------------------------------------------------------------------------
    // 构造函数（三个重载）：
    //
    //   public ShapelessArcaneRecipe(String research, Block     result, AspectList aspects, Object... recipe)
    //   public ShapelessArcaneRecipe(String research, Item      result, AspectList aspects, Object... recipe)
    //   public ShapelessArcaneRecipe(String research, ItemStack result, AspectList aspects, Object... recipe)
    //
    // -----------------------------------------------------------------------------------------
    // 【CrucibleRecipe】坩埚配方
    // -----------------------------------------------------------------------------------------
    // 构造函数（只有一个）：
    //
    //   public CrucibleRecipe(String researchKey, ItemStack result, Object cat, AspectList tags)
    //
    // 源码细节：
    //   - cat 是 Object，可以是 ItemStack / String（矿物词典）/ ArrayList<ItemStack>
    //   - tags 是源质（essentia）
    //   - 有 hash 字段用于快速比对
    //   - 没有 getResearch() 方法，但 key 字段就是研究key
    //
    // -----------------------------------------------------------------------------------------
    // 【InfusionRecipe】注魔配方
    // -----------------------------------------------------------------------------------------
    // 构造函数（只有一个）：
    //
    //   public InfusionRecipe(String research, Object output, int inst,
    //                         AspectList aspects2, ItemStack input, ItemStack[] recipe)
    //
    // 源码细节：
    //   - output 是 Object，可以是 ItemStack 或 ItemStack[]
    //   - inst 是不稳定度，0 最稳
    //   - aspects2 是源质（essentia）
    //   - input 是中央基座上的物品
    //   - recipe 是周围基座上的材料数组
    //   - matches() 会检查研究、中心物品、周围材料
    //   - areItemStacksEqual() 支持矿物词典（fuzzy 匹配）
    //
    // -----------------------------------------------------------------------------------------
    // 【InfusionEnchantmentRecipe】注魔附魔
    // -----------------------------------------------------------------------------------------
    // 构造函数（只有一个）：
    //
    //   public InfusionEnchantmentRecipe(String research, Enchantment input, int inst,
    //                                    AspectList aspects2, ItemStack[] recipe)
    //
    // 源码细节：
    //   - input 是 Enchantment 对象，不是 ItemStack
    //   - inst 是不稳定度，不是附魔等级！
    //   - recipeXP 自动计算：input.getMinEnchantability(1) / 3，最小为 1
    //   - matches() 会检查研究、附魔兼容性、已有附魔等级
    //   - calcInstability() 会根据中心物品已有附魔等级增加不稳定度
    //   - calcXP() 会根据附魔等级计算经验消耗
    //   - getEssentiaMod() 会根据其他附魔等级增加源质消耗（每级 +10%）
    //
    // ============================
    // 三、研究页面怎么用配方？
    // ============================
    //
    // 在 ResearchItem 的 setPages(...) 里，直接用 new ResearchPage(配方)：
    //
    // new ResearchPage((IRecipe) recipe)              → 原版工作台页面
    // new ResearchPage((IArcaneRecipe) recipe)        → 奥术工作台页面
    // new ResearchPage((CrucibleRecipe) recipe)       → 坩埚页面
    // new ResearchPage((InfusionRecipe) recipe)       → 注魔页面
    // new ResearchPage((InfusionEnchantmentRecipe) r) → 注魔附魔页面
    // new ResearchPage((List) recipes)                → 多个配方页面
    // new ResearchPage(ItemStack input)               → 冶炼页面
    // new ResearchPage(ResourceLocation image, String caption) → 图片页面
    //
    // ResearchPage 的构造函数会根据你传进去的对象类型，自动判断页面类型。
    // 所以你必须传正确的类型，不能强转。
    //
    // ============================
    // 四、速查表（对照 ThaumcraftApi 源码）
    // ============================
    //
    // 【注册有序奥术配方】
    //   ThaumcraftApi.addArcaneCraftingRecipe(
    //       String research, ItemStack result, AspectList aspects, Object... recipe)
    //   返回：ShapedArcaneRecipe
    //
    // 【注册无序奥术配方】
    //   ThaumcraftApi.addShapelessArcaneCraftingRecipe(
    //       String research, ItemStack result, AspectList aspects, Object... recipe)
    //   返回：ShapelessArcaneRecipe
    //
    // 【注册坩埚配方】
    //   ThaumcraftApi.addCrucibleRecipe(
    //       String key, ItemStack result, Object catalyst, AspectList tags)
    //   返回：CrucibleRecipe
    //
    // 【注册注魔配方】
    //   ThaumcraftApi.addInfusionCraftingRecipe(
    //       String research, Object result, int instability,
    //       AspectList aspects, ItemStack input, ItemStack[] recipe)
    //   返回：InfusionRecipe
    //
    // 【注册注魔附魔】
    //   ThaumcraftApi.addInfusionEnchantmentRecipe(
    //       String research, Enchantment enchantment, int instability,
    //       AspectList aspects, ItemStack[] recipe)
    //   返回：InfusionEnchantmentRecipe
    //
    // ============================
    // 五、魔力 vs 源质：别搞混
    // ============================
    //
    // 奥术工作台（ShapedArcaneRecipe / ShapelessArcaneRecipe）消耗的是：
    //   → 魔力（vis）
    //   → 从玩家手持的法杖里扣
    //
    // 坩埚（CrucibleRecipe）消耗的是：
    //   → 源质（essentia）
    //   → 从坩埚里累积的要素里扣
    //
    // 注魔（InfusionRecipe / InfusionEnchantmentRecipe）消耗的是：
    //   → 源质（essentia）
    //   → 从附近的罐子/源质库/管道里扣
    //
    // 两者都是 AspectList，但语义完全不同。写错了配方会失效或者扣错资源。
    //
    // ============================
    // 六、最容易犯的错
    // ============================
    //
    // 1. 研究key和配方key不一致
    //    错：register("revealing_goggles", ...)
    //        ThaumcraftApi.addInfusionCraftingRecipe("revealing_goggles2", recipe);
    //    对：两边都写 "revealing_goggles"
    //    后果：配方在手册里不显示
    //
    // 2. 无序奥术配方方法名写错
    //    错：ThaumcraftApi.addShapelessArcaneRecipe(...)
    //    对：ThaumcraftApi.addShapelessArcaneCraftingRecipe(...)
    //    注意：方法名里必须有 Crafting 这个词
    //
    // 3. 把 Block / Item 直接传给 addArcaneCraftingRecipe
    //    错：ThaumcraftApi.addArcaneCraftingRecipe("key", Blocks.stone, aspects, ...)
    //    对：ThaumcraftApi.addArcaneCraftingRecipe("key", new ItemStack(Blocks.stone), aspects, ...)
    //    原因：API 只接受 ItemStack，不接受 Block / Item
    //
    // 4. 把非 ItemStack / 非 Object[] 传给 addInfusionCraftingRecipe
    //    错：ThaumcraftApi.addInfusionCraftingRecipe("key", "some_string", ...)
    //    对：ThaumcraftApi.addInfusionCraftingRecipe("key", new ItemStack(...), ...)
    //    原因：源码内部会检查 result instanceof ItemStack || result instanceof Object[]
    //
    // 5. 把注魔附魔的第三个参数当成等级
    //    错：new InfusionEnchantmentRecipe("key", Enchantment.sharpness, 5, ...)
    //        // 以为 5 是等级
    //    对：第三个参数是不稳定度，等级由 Enchantment 自身决定
    //
    // 6. ResearchPage 传错类型
    //    错：new ResearchPage((IRecipe) MNRecipes.getInfusionRecipe("..."))
    //    对：new ResearchPage(MNRecipes.getInfusionRecipe("..."))
    //    后果：页面显示成原版工作台，或者直接报错
    //
    // 7. 忘了把配方放进 MNRecipes 的 Map
    //    错：只调用 ThaumcraftApi.addXxxRecipe(...)
    //    对：addXxxRecipe(...) + map.put("Key", recipe)
    //    后果：游戏能合成，但手册页面显示为空
    //
    // 8. 魔力写成源质
    //    奥术工作台 → 魔力（vis）
    //    坩埚/注魔 → 源质（essentia）
    //
    // 9. 忘了注册原版工作台配方
    //    ThaumcraftCraftingManager.createFakeRecipe(...) 只用于【显示】
    //    要真的能合成，必须另外用 GameRegistry.addRecipe(...) 注册
    //
    // ============================
    // 七、推荐流程
    // ============================
    //
    // 1. 用 ThaumcraftApi.addXxxRecipe("key", ...) 注册配方
    //    → 它会返回配方对象，同时把配方加入 craftingRecipes 列表
    // 2. 把返回的配方对象放进 MNRecipes 的 Map（给研究页面用）
    // 3. 写 ResearchItem，setPages(...) 里塞 ResearchPage(recipe)
    // 4. 加语言文件（研究名、研究简介、页面文本）
    // 5. 进游戏测试：能不能合成、手册里能不能看到、配方显示对不对
    //
    // =========================================================================================
    // =========================================================================================

    