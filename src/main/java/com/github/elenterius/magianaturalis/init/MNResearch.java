package com.github.elenterius.magianaturalis.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestType;
import com.github.elenterius.magianaturalis.research.NamespacedResearchItem;
import com.github.elenterius.magianaturalis.research.ResearchItemProxy;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public class MNResearch {

    private static final Map<DeferredHolder<ResearchItem>, Function<ResourceLocation, ResearchItem>> RESEARCH_REGISTRY = new HashMap<>();

    public static final DeferredHolder<ResearchItem> INTRO = register(
        "intro",
        key -> new NamespacedResearchItem(key, 0, 0, 0, new ItemStack(ConfigItems.itemResource, 1, 15)).setSpecial()
        .setRound()
        .setAutoUnlock()
        .setPages(
            createTextResearchPage(key, 1),
            createTextResearchPage(key, 2),
            createTextResearchPage(key, 3)));

    public static final DeferredHolder<ResearchItem> CARPENTRY = register(
        "carpentry",
        key -> new NamespacedResearchItem(key, -2, 2, 0, new ItemStack(MNBlocks.arcaneWood, 1, 4)).setRound()
            .setAutoUnlock()
            .setPages(
                createTextResearchPage(key, 1),
                new ResearchPage(MNRecipes.getRecipe("GreatwoodOrn")),
                new ResearchPage(MNRecipes.getRecipe("PlankSilverwood")),
                new ResearchPage(MNRecipes.getRecipe("GreatwoodGoldOrn1")),
                new ResearchPage(MNRecipes.getRecipe("GreatwoodGoldOrn2")),
                new ResearchPage(MNRecipes.getRecipe("GreatwoodGoldTrim"))));

    public static final DeferredHolder<ResearchItem> RESEARCH_LOG = register(
        "research_log",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.MIND, 3)
                .add(Aspect.VOID, 3)
                .add(Aspect.ORDER, 3),
            -1,
            -2,
            0,
            new ItemStack(MNItems.researchLog)).setRound()
                .setPages(createTextResearchPage(key, 1), new ResearchPage(MNRecipes.getArcaneRecipe("ResearchLog")))
                .setParentsHidden("DECONSTRUCTOR"));

    public static final DeferredHolder<ResearchItem> TRANSCRIBING_TABLE = register(
        "transcribing_table",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.MIND, 3)
                .add(Aspect.VOID, 3)
                .add(Aspect.ORDER, 3),
            -2,
            -4,
            0,
            new ItemStack(MNBlocks.transcribingTable))
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getArcaneRecipe("TranscribingTable")),
                    new ResearchPage(createTranscribingTableStructurePage()),
                    createTextResearchPage(key, 2))
                .setParents(RESEARCH_LOG.getId()));

    public static final DeferredHolder<ResearchItem> GOGGLES_PROXY = register(
        "tc_goggles",
        key -> ResearchItemProxy.createNamespaced(key, "GOGGLES", -4, 1));

    // ==================================================
    // 【修改】高级揭示护目镜：加第二页描述
    // 页面顺序：page1 → page2 → 合成配方
    // ==================================================
    public static final DeferredHolder<ResearchItem> SPECTACLES = register(
        "spectacles",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SENSES, 3)
                .add(Aspect.AURA, 3)
                .add(Aspect.MAGIC, 3),
            -6,
            0,
            1,
            new ItemStack(MNItems.spectacles))
                .setPages(
                    createTextResearchPage(key, 1),
                    createTextResearchPage(key, 2),
                    new ResearchPage(MNRecipes.getArcaneRecipe("Spectacles")))
                .setSecondary()
                .setParents(GOGGLES_PROXY.getId()));

    // ==================================================
    // 【修改】混沌护目镜：加第二页描述
    // 页面顺序：page1 → page2 → 注魔配方
    // ==================================================
    public static final DeferredHolder<ResearchItem> DARK_GOGGLES = register("dark_goggles", key -> {
        ResearchItem research = new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SENSES, 6)
                .add(Aspect.AURA, 3)
                .add(Aspect.MAGIC, 3)
                .add(Aspect.DARKNESS, 4),
            -7,
            2,
            2,
            new ItemStack(MNItems.gogglesDark))
                .setPages(
                    createTextResearchPage(key, 1),
                    createTextResearchPage(key, 2),
                    new ResearchPage(MNRecipes.getInfusionRecipe("DarkGoggles")))
                .setParents(GOGGLES_PROXY.getId())
                .setParentsHidden(
                    SPECTACLES.getId()
                        .toString());

        ThaumcraftApi.addWarpToResearch(research.key, 1);
        return research;
    });

    public static final DeferredHolder<ResearchItem> WARDED_ARCANA_PROXY = register(
        "tc_warded_arcana",
        key -> ResearchItemProxy.createNamespaced(key, "WARDEDARCANA", -5, -2));

    public static final DeferredHolder<ResearchItem> ARCANE_KEYS = register(
        "arcane_keys",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.TOOL, 4)
                .add(Aspect.MIND, 3)
                .add(Aspect.MECHANISM, 3),
            -4,
            -3,
            3,
            new ItemStack(MNItems.arcaneKey))
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getArcaneRecipe("ThaumiumKey1")),
                    createTextResearchPage(key, 2),
                    new ResearchPage(MNRecipes.getArcaneRecipe("ThaumiumKey2")))
                .setParents(WARDED_ARCANA_PROXY.getId()));

    // ==================================================
    // 【修改】神秘箱子：加第二页描述
    // 页面顺序：page1 → page2 → 宏伟之木箱子配方 → 银木箱子配方
    // ==================================================
    public static final DeferredHolder<ResearchItem> ARCANE_CHEST = register("arcane_chest", key -> {
        ItemStack icon = new ItemStack(MNBlocks.arcaneChest, 1, ArcaneChestType.GREAT_WOOD.id());
        return new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.VOID, 4)
                .add(Aspect.MIND, 3)
                .add(Aspect.MECHANISM, 3)
                .add(Aspect.ARMOR, 3),
            -7,
            -3,
            3,
            icon)
                .setPages(
                    createTextResearchPage(key, 1),
                    createTextResearchPage(key, 2),
                    new ResearchPage(MNRecipes.getArcaneRecipe("ArcaneChest1")),
                    new ResearchPage(MNRecipes.getArcaneRecipe("ArcaneChest2")))
                .setParents(WARDED_ARCANA_PROXY.getId());
    });

    public static final DeferredHolder<ResearchItem> EQUAL_TRADE_FOCUS_PROXY = register(
        "tc_focus_trade",
        key -> ResearchItemProxy.createNamespaced(key, "FOCUSTRADE", 2, -4));

    public static final DeferredHolder<ResearchItem> CONSTRUCTION_FOCUS = register(
        "construction_focus",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.MAGIC, 3)
                .add(Aspect.CRAFT, 6)
                .add(Aspect.ORDER, 2)
                .add(Aspect.EARTH, 2),
            4,
            -5,
            2,
            new ItemStack(MNItems.focusBuild))
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("ConstructionFocus")))
                .setParents(EQUAL_TRADE_FOCUS_PROXY.getId()));

    public static final DeferredHolder<ResearchItem> REVENANT_FOCUS = register("revenant_focus", key -> {
        ResearchItem research = new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.TRAVEL, 3)
                .add(Aspect.BEAST, 6)
                .add(Aspect.UNDEAD, 3)
                .add(Aspect.MAGIC, 3),
            3,
            -7,
            2,
            new ItemStack(MNItems.focusRevenant))
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("RevenantFocus")))
                .setHidden()
                .setEntityTriggers("Zombie")
                .setAspectTriggers(Aspect.UNDEAD)
                .setParentsHidden("BASICTHAUMATURGY", "INFUSION");

        ThaumcraftApi.addWarpToResearch(research.key, 2);
        ThaumcraftApi.addWarpToItem(new ItemStack(MNItems.focusRevenant), 1);

        return research;
    });

    public static final DeferredHolder<ResearchItem> CRUCIBLE_PROXY = register(
        "tc_crucible",
        key -> ResearchItemProxy.createNamespaced(key, "CRUCIBLE", 3, -1));

    public static final DeferredHolder<ResearchItem> MUTATION_STONE = register(
        "mutation_stone",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.MAGIC, 3)
                .add(Aspect.EXCHANGE, 4)
                .add(Aspect.EARTH, 2),
            4,
            -3,
            2,
            new ItemStack(MNItems.alchemicalStone, 1, 0))
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("StonePheno")),
                    new ResearchPage(MNRecipes.getRecipes("WoodConversion")),
                    new ResearchPage(MNRecipes.getRecipes("ColorConversion")))
                .setSecondary()
                .setParents(EQUAL_TRADE_FOCUS_PROXY.getId(), CRUCIBLE_PROXY.getId()));

    public static final DeferredHolder<ResearchItem> QUICKSILVER_STONE = register(
        "quicksilver_stone",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SENSES, 3)
                .add(Aspect.EXCHANGE, 4)
                .add(Aspect.AURA, 2),
            6,
            -1,
            2,
            new ItemStack(MNItems.alchemicalStone, 1, 1))
                .setPages(createTextResearchPage(key, 1), new ResearchPage(MNRecipes.getInfusionRecipe("StoneQuick")))
                .setParents(CRUCIBLE_PROXY.getId()));

    public static final DeferredHolder<ResearchItem> FOCUS_POUCH_PROXY = register(
        "tc_focus_pouch",
        key -> ResearchItemProxy.createNamespaced(key, "FOCUSPOUCH", 4, 2));

    public static final DeferredHolder<ResearchItem> ENDER_POUCH = register(
        "ender_pouch",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.ELDRITCH, 3)
                .add(Aspect.VOID, 3),
            6,
            3,
            2,
            new ItemStack(MNItems.focusPouchEnder))
                .setPages(createTextResearchPage(key, 1), new ResearchPage(MNRecipes.getInfusionRecipe("EnderPouch")))
                .setParents(FOCUS_POUCH_PROXY.getId())
                .setRound());

    public static final DeferredHolder<ResearchItem> TRAVEL_TRUNK_PROXY = register(
        "tc_travel_trunk",
        key -> ResearchItemProxy.createNamespaced(key, "TRAVELTRUNK", 1, 3));

    // ==================================================
    // 【拆分】恶毒旅行箱系列：链式升级阵型（按升级顺序排列）
    // 升级链：邪恶(0) → 阴险(1) → 污染(3) → 恶魔(2)
    // 坐标采用偶数递增：x = 2, 4, 6, 8；y = 5, 7, 9, 11
    // 全部为难度20，禁忌知识（需解锁前置才可见）
    // ==================================================
    public static final DeferredHolder<ResearchItem> EVIL_TRUNK = register("evil_trunk", key -> {
        ResearchItem research = new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SOUL, 3)
                .add(Aspect.BEAST, 3)
                .add(Aspect.TAINT, 3),
            2,
            5,
            2, // 基础节点保持原难度
            new ItemStack(MNItems.evilTrunkSpawner, 1, 0)) // Meta 0：邪恶箱子宝宝
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("CorruptedTrunk")))
                .setParents(TRAVEL_TRUNK_PROXY.getId());

        ThaumcraftApi.addWarpToResearch(research.key, 1);
        return research;
    });

    public static final DeferredHolder<ResearchItem> SINISTER_TRUNK = register("sinister_trunk", key -> {
        ResearchItem research = new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SOUL, 3)
                .add(Aspect.BEAST, 3)
                .add(Aspect.TAINT, 3),
            4,
            7,
            20, // 【修改】难度上升到20
            new ItemStack(MNItems.evilTrunkSpawner, 1, 1)) // Meta 1：阴险箱子宝宝
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("SinisterTrunk")))
                .setParents(EVIL_TRUNK.getId()) // 前置：邪恶箱子宝宝
                .setConcealed(); // 禁忌知识：不解锁前置看不见

        ThaumcraftApi.addWarpToResearch(research.key, 1);
        return research;
    });

    // 【注意】污染（Meta 3）在升级链中排在恶魔（Meta 2）之前
    public static final DeferredHolder<ResearchItem> TAINTED_TRUNK = register("tainted_trunk", key -> {
        ResearchItem research = new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SOUL, 3)
                .add(Aspect.BEAST, 3)
                .add(Aspect.TAINT, 3),
            6,
            9,
            20, // 【修改】难度上升到20
            new ItemStack(MNItems.evilTrunkSpawner, 1, 3)) // Meta 3：污染箱子宝宝
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("TaintedTrunk")))
                .setParents(SINISTER_TRUNK.getId()) // 前置：阴险箱子宝宝
                .setConcealed(); // 禁忌知识：不解锁前置看不见

        ThaumcraftApi.addWarpToResearch(research.key, 1);
        return research;
    });

    // 【注意】恶魔（Meta 2）是升级链的最后一环，吃污染（Meta 3）
    public static final DeferredHolder<ResearchItem> DEMONIC_TRUNK = register("demonic_trunk", key -> {
        ResearchItem research = new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SOUL, 3)
                .add(Aspect.BEAST, 3)
                .add(Aspect.TAINT, 3),
            8,
            11,
            20, // 【修改】难度上升到20
            new ItemStack(MNItems.evilTrunkSpawner, 1, 2)) // Meta 2：恶魔箱子宝宝
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("DemonicTrunk")))
                .setParents(TAINTED_TRUNK.getId()) // 前置：污染箱子宝宝
                .setConcealed(); // 禁忌知识：不解锁前置看不见

        ThaumcraftApi.addWarpToResearch(research.key, 1);
        return research;
    });

    public static final DeferredHolder<ResearchItem> PRISON_JAR = register(
        "prison_jar",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.TRAP, 6)
                .add(Aspect.GREED, 3)
                .add(Aspect.EXCHANGE, 3)
                .add(Aspect.MOTION, 3),
            -1,
            4,
            3,
            new ItemStack(MNBlocks.jarPrison))
                .setPages(createTextResearchPage(key, 1), new ResearchPage(MNRecipes.getInfusionRecipe("JarPrison")))
                .setParentsHidden("JARLABEL"));

    public static final DeferredHolder<ResearchItem> SICKLES = register(
        "sickles",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.TOOL, 3)
                .add(Aspect.CROP, 3)
                .add(Aspect.HARVEST, 3),
            -4,
            3,
            1,
            new ItemStack(MNItems.sickleThaumium))
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getRecipe("ThaumiumSickle")),
                    new ResearchPage(MNRecipes.getRecipe("VoidSickle")))
                .setParentsHidden("THAUMIUM")
                .setSecondary());

    public static final DeferredHolder<ResearchItem> SICKLE_OF_ABUNDANCE = register(
        "sickle_of_abundance",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.TOOL, 3)
                .add(Aspect.CROP, 3)
                .add(Aspect.HARVEST, 3)
                .add(Aspect.GREED, 6),
            -5,
            5,
            2,
            new ItemStack(MNItems.sickleElemental))
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("ElementalSickle")))
                .setParents(SICKLES.getId())
                .setParentsHidden("INFUSION"));

    public static final DeferredHolder<ResearchItem> GEO_OCCULTISM = register(
        "geo_occultism",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.AURA, 4)
                .add(Aspect.EXCHANGE, 3)
                .add(Aspect.WEATHER, 3)
                .add(Aspect.MAGIC, 6)
                .add(Aspect.EARTH, 2)
                .add(Aspect.AIR, 2),
            0,
            -5,
            0,
            new ItemStack(MNBlocks.geoPylon))
                .setPages(
                    new ResearchPage("research.magianaturalis.geo_occultism.page.1"),
                    new ResearchPage(MNRecipes.getInfusionRecipe("GeoPylon")),
                    new ResearchPage(createGeoPylonStructurePage()),
                    new ResearchPage("research.magianaturalis.geo_occultism.page.2"),
                    new ResearchPage("research.magianaturalis.geo_occultism.page.3"),
                    new ResearchPage(MNRecipes.getArcaneRecipe("BiomeReport")))
                .setParentsHidden(
                    "INFUSION",
                    MUTATION_STONE.getId()
                        .toString()));

    static void register() {
        registerCategories();
        registerAllResearch();
        registerItemAspects();
    }

    private static void registerAllResearch() {
        RESEARCH_REGISTRY.forEach((registryObject, factory) -> {
            ResearchItem researchItem = factory.apply(registryObject.getId());
            researchItem.registerResearchItem();
            registryObject.bind(researchItem);
        });
        RESEARCH_REGISTRY.clear();
    }

    private static DeferredHolder<ResearchItem> register(String name,
        Function<ResourceLocation, ResearchItem> factory) {
        DeferredHolder<ResearchItem> registryObject = new DeferredHolder<>(MagiaNaturalis.rl(name));
        RESEARCH_REGISTRY.putIfAbsent(registryObject, factory);
        return registryObject;
    }

    private static void registerItemAspects() {
        ThaumcraftApi.registerObjectTag(
            new ItemStack(MNItems.sickleElemental),
            new AspectList().add(Aspect.HARVEST, 6)
                .add(Aspect.TOOL, 2)
                .add(Aspect.GREED, 6)
                .add(Aspect.CRYSTAL, 6)
                .add(Aspect.MAGIC, 3)
                .add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(MNItems.sickleThaumium),
            new AspectList().add(Aspect.HARVEST, 4)
                .add(Aspect.TOOL, 2)
                .add(Aspect.METAL, 9)
                .add(Aspect.MAGIC, 3)
                .add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(MNItems.gogglesDark),
            new AspectList().add(Aspect.ARMOR, 4)
                .add(Aspect.SENSES, 7)
                .add(Aspect.DARKNESS, 5)
                .add(Aspect.ENTROPY, 4)
                .add(Aspect.BEAST, 3));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(MNItems.researchLog),
            new AspectList().add(Aspect.MIND, 8)
                .add(Aspect.VOID, 6)
                .add(Aspect.MAGIC, 3)
                .add(Aspect.CLOTH, 3));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(MNItems.spectacles),
            new AspectList().add(Aspect.ARMOR, 4)
                .add(Aspect.SENSES, 5)
                .add(Aspect.CLOTH, 3)
                .add(Aspect.GREED, 3));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(MNItems.herobrinesScythe),
            new AspectList().add(Aspect.WEAPON, 32)
                .add(Aspect.DEATH, 16)
                .add(Aspect.ELDRITCH, 16)
                .add(Aspect.ENERGY, 8)
                .add(Aspect.SOUL, 8)
                .add(Aspect.MAGIC, 4)
                .add(Aspect.AURA, 4));
    }

    private static void registerCategories() {
        ResearchCategories.registerCategory(
            MagiaNaturalis.MOD_ID,
            MagiaNaturalis.rl("textures/items/research_log.png"),
            MagiaNaturalis.rl("textures/gui/background.png"));
    }

    private static ResearchPage createTextResearchPage(ResourceLocation key, int pageNumber) {
        return new ResearchPage(
            String.format("research.%s.%s.page.%d", key.getResourceDomain(), key.getResourcePath(), pageNumber));
    }

    private static List<Object> createGeoPylonStructurePage() {
        final int dx = 3, dy = 5, dz = 3; // 原来是 dy = 4，现在中间加一层空层，变成 5

        List<Object> structure = new ArrayList<>();
        structure.add(new AspectList());
        structure.add(Integer.valueOf(dx));
        structure.add(Integer.valueOf(dy));
        structure.add(Integer.valueOf(dz));

        ItemStack hole = new ItemStack(ConfigBlocks.blockHole, 1, 15);

        List<ItemStack> blocks = new ArrayList<>();
        int cx = dx / 2;
        int cz = dz / 2;

        for (int y = 0; y < dy; y++) {
            for (int x = 0; x < dx; x++) {
                for (int z = 0; z < dz; z++) {
                    boolean center = (x == cx && z == cz);
                    ItemStack stack = hole;

                    if (center) {
                        if (y == 0) {
                            // 研究页第 1 层：最上面，中心地标塔
                            stack = new ItemStack(MNBlocks.geoPylon);
                        } else if (y >= 2) {
                            // 研究页第 3、4、5 层：中心神秘石
                            stack = new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 0);
                        }
                        // y == 1：中心也保持 hole，所以这一层全是 3×3 隙间
                    }

                    blocks.add(stack);
                }
            }
        }
        structure.add(blocks);

        return structure;
    }

    /**
     * 抄录台的多方块结构展示页。
     *
     * 底层结构（5×5 平面）：
     * b j b j b z=0
     * j j j j j z=1
     * b j a j b z=2
     * j j j j j z=3
     * b j b j b z=4
     * 其中 a = 抄录台、b = 解构工作台、j = 隙间
     *
     * 判定规则：
     * (x=2, z=2) → a 抄录台
     * x、z 都是偶数 → b 解构工作台
     * 其他 → j 隙间
     *
     * 本版本只画一层（dy=1），不加上层隙间，图会更大更清楚。
     *
     * ⚠️【重要】TC4 结构页的 y 轴是"反着来"的！
     * y = 0 → 研究页里显示在【最上面】
     * y = dy-1 → 研究页里显示在【最下面】
     * 跟 MC 世界坐标（y=0 是底、y 越大越高）完全相反。
     */
    private static List<Object> createTranscribingTableStructurePage() {
        // ==================================================
        // 1) 结构尺寸
        // dy = 1 → 只画一层
        // ==================================================
        final int dx = 5;
        final int dy = 1; // ← 只留一层
        final int dz = 5;

        // ==================================================
        // 2) 塞进 TC4 要的 List 格式
        // ==================================================
        List<Object> structure = new ArrayList<>();
        structure.add(new AspectList()); // [0] 源质消耗（空）
        structure.add(Integer.valueOf(dx)); // [1] dx = 5
        structure.add(Integer.valueOf(dy)); // [2] dy = 1
        structure.add(Integer.valueOf(dz)); // [3] dz = 5

        // ==================================================
        // 3) 循环遍历每一格
        // dy=1，所以 y 只会是 0（视觉最底层）
        // 不需要 else 分支——只有一层
        // ==================================================
        List<ItemStack> blocks = new ArrayList<>();
        for (int y = 0; y < dy; y++) {
            for (int x = 0; x < dx; x++) {
                for (int z = 0; z < dz; z++) {

                    if (x == 2 && z == 2) {
                        // 正中心 → a 抄录台
                        blocks.add(new ItemStack(MNBlocks.transcribingTable));
                    } else if (x % 2 == 0 && z % 2 == 0) {
                        // x、z 都是偶数 → b 解构工作台
                        blocks.add(new ItemStack(ConfigBlocks.blockTable, 1, 14));
                    } else {
                        // 其他 → j 隙间
                        blocks.add(new ItemStack(ConfigBlocks.blockHole, 1, 15));
                    }
                }
            }
        }
        structure.add(blocks); // [4] 方块列表

        return structure;
    }


    // ==================================================
    // 【揭示之护目镜（饰品版）】
    // 
    // 位置：远离高级揭示护目镜，避免拥挤
    // 前置：高级揭示护目镜 + 注魔台
    // 玩法：难度 4 的连连看
    // 不是禁忌知识，不加扭曲值
    //
    // 机制说明：
    // - 玩家完成【高级揭示护目镜】和【注魔台】后，节点自动亮起
    // - 点击节点会消耗墨水和纸，生成研究笔记
    // - 玩连连看完成笔记
    // - 右键完成的笔记，解锁研究
    // - 不需要扫描任何东西，不设置 setHidden / setLost
    // ==================================================
    public static final DeferredHolder<ResearchItem> REVEALING_GOGGLES = register(
        "revealing_goggles",
        key -> new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SENSES, 4)
                .add(Aspect.AURA, 3)
                .add(Aspect.MAGIC, 3)
                .add(Aspect.DARKNESS, 2),
            -8,   // x 坐标，保持你原来的
            0,    // y 坐标，保持你原来的
            4,    // 难度 4
            new ItemStack(MNItems.revealingGoggles))
                .setPages(
                    createTextResearchPage(key, 1),
                    createTextResearchPage(key, 2),
                    createTextResearchPage(key, 3),
                    new ResearchPage(MNRecipes.getInfusionRecipe("RevealingGoggles")))
                .setParents(SPECTACLES.getId())   // 可见前置：高级揭示护目镜，会画线
                .setParentsHidden("INFUSION")     // 隐藏前置：必须完成注魔台，不画线
                .setConcealed()                   // 前置没完成时看不见
                .setSpecial());                   // 尖刺边框，不加扭曲
                


        // ==================================================
        // 【面具学 · 总成】
        // 前置：窥秘之眼镜（饰品版）
        // 图标：textures/misc/humanus.png
        // 页面：哲学故事 + 图片 + 总述
        // ==================================================
        public static final DeferredHolder<ResearchItem> MASK_THEORY = register(
            "mask_theory",
            key -> new NamespacedResearchItem(
                key,
                new AspectList().add(Aspect.MIND, 8)
                    .add(Aspect.SENSES, 6)
                    .add(Aspect.MAGIC, 4)
                    .add(Aspect.DARKNESS, 4)
                    .add(Aspect.SOUL, 4),
                -10,
                0,
                3,
                MagiaNaturalis.rl("textures/misc/humanus.png"))
                    .setPages(
                        createTextResearchPage(key, 1),
                        createTextResearchPage(key, 2),   // ★ 用文本页，图片写在语言文件里
                        createTextResearchPage(key, 3),  
                        createTextResearchPage(key, 4))
                        
                    .setParents(REVEALING_GOGGLES.getId())
                    .setConcealed()
                    .setSpecial());

        // ==================================================
        // 【狞笑恶魔面具】
        // 前置：面具学
        // 效果：削弱扭曲
        // ==================================================
        public static final DeferredHolder<ResearchItem> MASK_GRINNING_DEVIL = register("mask_grinning_devil", key -> {
            ResearchItem research = new NamespacedResearchItem(
                key,
                new AspectList().add(Aspect.MIND, 6)
                    .add(Aspect.SENSES, 4)
                    .add(Aspect.DARKNESS, 3)
                    .add(Aspect.HEAL, 2),
                -13,
                -1,
                2,
                new ItemStack(MNItems.mask, 1, 0))
                    .setPages(
                        createTextResearchPage(key, 1),
                        new ResearchPage(MNRecipes.getInfusionRecipe("MaskGrinningDevil")))
                    .setParents(MASK_THEORY.getId())
                    .setConcealed();

            ThaumcraftApi.addWarpToResearch(research.key, 2);
            return research;
        });

        // ==================================================
        // 【暴怒幽魂面具】
        // 前置：面具学
        // 效果：反弹凋零
        // ==================================================
        public static final DeferredHolder<ResearchItem> MASK_ANGRY_GHOST = register("mask_angry_ghost", key -> {
            ResearchItem research = new NamespacedResearchItem(
                key,
                new AspectList().add(Aspect.ENTROPY, 6)
                    .add(Aspect.DEATH, 6)
                    .add(Aspect.WEAPON, 4)
                    .add(Aspect.ARMOR, 3),
                -13,
                -3,
                2,
                new ItemStack(MNItems.mask, 1, 1))
                    .setPages(
                        createTextResearchPage(key, 1),
                        new ResearchPage(MNRecipes.getInfusionRecipe("MaskAngryGhost")))
                    .setParents(MASK_THEORY.getId())
                    .setConcealed();

            ThaumcraftApi.addWarpToResearch(research.key, 2);
            return research;
        });

        // ==================================================
        // 【嗜血邪妖面具】
        // 前置：面具学
        // 效果：窃取生命
        // ==================================================
        public static final DeferredHolder<ResearchItem> MASK_SIPPING_FIEND = register("mask_sipping_fiend", key -> {
            ResearchItem research = new NamespacedResearchItem(
                key,
                new AspectList().add(Aspect.UNDEAD, 6)
                    .add(Aspect.LIFE, 6)
                    .add(Aspect.HUNGER, 4)
                    .add(Aspect.SOUL, 4),
                -13,
                1,
                2,
                new ItemStack(MNItems.mask, 1, 2))
                    .setPages(
                        createTextResearchPage(key, 1),
                        new ResearchPage(MNRecipes.getInfusionRecipe("MaskSippingFiend")))
                    .setParents(MASK_THEORY.getId())
                    .setConcealed();

            ThaumcraftApi.addWarpToResearch(research.key, 2);
            return research;
        });


        // ==================================================
        // 【态度面具】
        // 前置：狞笑恶魔 / 暴怒幽魂 / 嗜血邪妖（三张都要完成）
        // 效果：四态切换（无态度 / 友善 / 威严 / 隐匿）
        // 禁忌知识：解开后 +20 扭曲
        // ==================================================
        public static final DeferredHolder<ResearchItem> MASK_ATTITUDE = register("mask_attitude", key -> {
            ResearchItem research = new NamespacedResearchItem(
                key,
                new AspectList().add(Aspect.MIND, 16)
                    .add(Aspect.SENSES, 12)
                    .add(Aspect.SOUL, 8)
                    .add(Aspect.MAGIC, 8)
                    .add(Aspect.DARKNESS, 8),
                -15, -1, 3,                                 // 三张面具下方
                new ItemStack(MNItems.mask, 1, 3))          // meta 3 = 态度面具
                    .setPages(
                        createTextResearchPage(key, 1),     // 哲学/神秘学介绍
                        createTextResearchPage(key, 2),     // 无态度 一句话
                        createTextResearchPage(key, 3),     // 友善   一句话
                        createTextResearchPage(key, 4),     // 威严   一句话
                        createTextResearchPage(key, 5),     // 隐匿   一句话
                        new ResearchPage(MNRecipes.getInfusionRecipe("MaskAttitude")))  // 注魔配方
                    .setParents(
                        MASK_GRINNING_DEVIL.getId(),
                        MASK_ANGRY_GHOST.getId(),
                        MASK_SIPPING_FIEND.getId())
                    .setConcealed()
                    .setSpecial();

            ThaumcraftApi.addWarpToResearch(research.key, 20);  // 20 点扭曲
            return research;
        });



    public static final DeferredHolder<ResearchItem> HEROBRINES_SCYTHE = register("herobrines_scythe", key -> {
        ResearchItem research = new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.ELDRITCH, 16)
                .add(Aspect.DEATH, 16)
                .add(Aspect.WEAPON, 16)
                .add(Aspect.AURA, 8)
                .add(Aspect.ENERGY, 6),
            -5,
            8,
            4,
            new ItemStack(MNItems.herobrinesScythe))
                .setPages(
                    createTextResearchPage(key, 0),
                    createTextResearchPage(key, 1),
                    createTextResearchPage(key, 2),
                    createTextResearchPage(key, 3),
                    createTextResearchPage(key, 4),
                    createTextResearchPage(key, 5),
                    createTextResearchPage(key, 6),
                    createTextResearchPage(key, 7),
                    new ResearchPage(MNRecipes.getInfusionRecipe("HerobrinesScythe")))
                .setParents(SICKLE_OF_ABUNDANCE.getId())   // 富饶镰刀：画线 + 必须完成
                .setParentsHidden("INFUSION")               // 注魔台：不画线 + 必须完成
                .setConcealed()
                .setSpecial();

        ThaumcraftApi.addWarpToResearch(research.key, 4);
        return research;
    });




}




    // =========================================================================================
    // =========================================================================================
    // 【补充一】ResearchItem 构造函数有三个重载
    // =========================================================================================
    // 你项目里用的是 NamespacedResearchItem，但它的父类 ResearchItem 有三个构造函数：
    //
    //   ResearchItem(String key, String category)
    //       → 最简构造，用于桩研究 / 虚拟研究
    //       → 没有 tags、没有坐标、没有图标，自动 setVirtual()
    //       → 原版例子：
    //           new ResearchItem("CAP_iron", "THAUMATURGY").setAutoUnlock().registerResearchItem();
    //
    //   ResearchItem(String key, String category, AspectList tags,
    //                int col, int row, int complex, ResourceLocation icon)
    //       → 图标用图片资源
    //       → 原版例子：
    //           new ResearchItem("ASPECTS", "BASICS", new AspectList(),
    //                            0, 0, 0,
    //                            new ResourceLocation("thaumcraft", "textures/misc/r_aspects.png"))
    //               .setStub().setRound().setAutoUnlock().registerResearchItem();
    //
    //   ResearchItem(String key, String category, AspectList tags,
    //                int col, int row, int complex, ItemStack icon)
    //       → 图标用物品
    //       → 原版例子：
    //           new ResearchItem("GOGGLES", "ARTIFICE",
    //                            new AspectList().add(Aspect.SENSES, 3).add(Aspect.AURA, 3),
    //                            4, 1, 1,
    //                            new ItemStack(ConfigItems.itemGoggles))
    //               .setPages(...)
    //               .setParents(new String[]{"THAUMOMETER"})
    //               .setConcealed()
    //               .registerResearchItem();
    //
    // ⚠️ 重要：complexity 在构造函数里会被强制限制：
    //       if (complexity < 1) this.complexity = 1;
    //       if (complexity > 3) this.complexity = 3;
    //    所以原版难度最大就是 3，写 4 会被改成 3。
    //    你项目里写 4，是否真的生效，取决于 NamespacedResearchItem 有没有重写这个逻辑。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充二】图标的两种写法
    // =========================================================================================
    // 图标可以是：
    //   1. ItemStack：
    //        new ItemStack(MNItems.revealingGoggles)
    //        new ItemStack(ConfigBlocks.blockStoneDevice, 1, 5)   // 带 meta
    //        new ItemStack(Items.diamond)
    //
    //   2. ResourceLocation 图片：
    //        new ResourceLocation("thaumcraft", "textures/misc/r_nodes.png")
    //        格式：new ResourceLocation(模组ID, "textures/xxx/yyy.png")
    //
    // 区别：
    //   - ItemStack 图标会显示物品本身，鼠标悬停显示物品名
    //   - ResourceLocation 图标显示一张贴图，通常用于“概念性”研究
    //
    // 你项目里目前都是 ItemStack，没问题。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充三】研究名和研究简介的本地化键（⚠️ 重要！）
    // =========================================================================================
    // 原版 ResearchItem.java 里：
    //
    //   public String getName() {
    //       return StatCollector.translateToLocal("tc.research_name." + key);
    //   }
    //
    //   public String getText() {
    //       return StatCollector.translateToLocal("tc.research_text." + key);
    //   }
    //
    // 也就是说，原版的本地化键格式是：
    //   tc.research_name.KEY=研究名
    //   tc.research_text.KEY=研究简介
    //
    // 但你的语言文件里用的是：
    //   research.magianaturalis.revealing_goggles.name=窥秘眼镜
    //   research.magianaturalis.revealing_goggles.text=真相在颈不在顶
    //
    // 这说明你的 NamespacedResearchItem 一定重写了 getName() 和 getText()，
    // 改成了 "research.<modid>.<key>.name" 和 "research.<modid>.<key>.text" 的格式。
    //
    // ⚠️ 所以你在写语言文件时，要跟着你自己的格式来：
    //   research.magianaturalis.研究key.name=研究名
    //   research.magianaturalis.研究key.text=研究简介
    //   research.magianaturalis.研究key.page.1=第一页
    //   research.magianaturalis.研究key.page.2=第二页
    //   ...
    //
    // 而不是原版的 tc.research_name.KEY。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充四】研究页面的本地化键
    // =========================================================================================
    // 你的 createTextResearchPage(key, n) 大概是这么实现的：
    //
    //   private static ResearchPage createTextResearchPage(ResourceLocation key, int pageNumber) {
    //       return new ResearchPage(
    //           String.format("research.%s.%s.page.%d",
    //               key.getResourceDomain(), key.getResourcePath(), pageNumber));
    //   }
    //
    // 所以 key = magianaturalis:revealing_goggles 时，它会去找：
    //   research.magianaturalis.revealing_goggles.page.1
    //   research.magianaturalis.revealing_goggles.page.2
    //   research.magianaturalis.revealing_goggles.page.3
    //
    // ⚠️ 注意：key 里的 ":" 会被 getResourceDomain / getResourcePath 拆开，
    //    所以语言文件里不要写 ":"，要写 "."。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充五】研究节点的注册顺序（⚠️ 非常重要的坑）
    // =========================================================================================
    // 正确的注册顺序：
    //
    //   1. 注册研究分类
    //      ResearchCategories.registerCategory(MOD_ID, 图标, 背景)
    //
    //   2. 注册配方
    //      ThaumcraftApi.addArcaneCraftingRecipe(...)
    //      ThaumcraftApi.addCrucibleRecipe(...)
    //      ThaumcraftApi.addInfusionCraftingRecipe(...)
    //      ...
    //
    //   3. 注册研究节点
    //      researchItem.registerResearchItem();
    //      → 内部会调用 ResearchCategories.addResearch(this)
    //      → 所以分类必须已经注册，否则研究加不进去
    //
    //   4. 注册物品/方块/实体/源质标签
    //      ThaumcraftApi.registerObjectTag(...)
    //      ThaumcraftApi.registerEntityTag(...)
    //
    // ⚠️ 顺序错了会出问题：
    //    - 分类没注册就注册研究 → 研究丢失
    //    - 配方没注册就注册研究 → 手册里配方页空白
    //    - 研究没注册就加扭曲 → 扭曲可能不生效
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充六】研究 key 的唯一性
    // =========================================================================================
    // 每个研究节点的 key 必须全局唯一。
    //
    // 原版 ResearchCategories.addResearch(ResearchItem) 内部：
    //   如果分类不存在 → 报错
    //   如果 key 已存在 → 直接覆盖
    //
    // 也就是说，key 写重复了不会报错，但后面的会覆盖前面的。
    // 你以为你注册了两个研究，结果只有一个生效。
    //
    // 所以：
    //   - 别用 "GOGGLES" 这种原版已经用过的 key
    //   - 你项目里的 "revealing_goggles"、"spectacles" 都是安全的
    //   - 建议所有 key 都加个前缀（比如全部小写 + 下划线），避免撞车
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充七】setSiblings 自动解锁机制
    // =========================================================================================
    // setSiblings(...) 表示：当前研究完成后，会自动解锁这些兄弟研究。
    //
    // 原版例子：
    //   (new ResearchItem("DISTILESSENTIA", "ALCHEMY", ...))
    //       .setSiblings(new String[]{"JARLABEL"})
    //       .setParents(new String[]{"NITOR", "ALUMENTUM"})
    //       .registerResearchItem();
    //
    // 意思就是：解锁蒸馏器研究后，会自动解锁罐子标签研究。
    //
    // ⚠️ 注意：
    //   - siblings 是“完成后自动解锁”，不是“必须完成”
    //   - 它通常用于“隐藏配方”，比如原版的各种杖端杖柄组合
    //   - 如果你想让玩家必须手动研究，就不要用 siblings
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充八】研究点直接购买机制（setSecondary 和难度）
    // =========================================================================================
    // 原版 ResearchManager.findMatchingResearch() 里有一段：
    //
    //   boolean secondary = ri.isSecondary() && Config.researchDifficulty == 0
    //                       || Config.researchDifficulty == -1;
    //
    //   if (!secondary && !ri.isHidden() && !ri.isLost()
    //       && !ri.isAutoUnlock() && !ri.isVirtual() && !ri.isStub()) {
    //       allValidResearch.add(ri);
    //   }
    //
    // 翻译一下：
    //   - Config.researchDifficulty == -1 → 和平模式，所有研究自动完成
    //   - Config.researchDifficulty == 0  → 普通模式，次要研究可以用研究点买
    //   - Config.researchDifficulty > 0   → 困难模式，次要研究也不能买
    //
    // 所以：
    //   - 如果你游戏里设置了“和平模式”，研究节点会直接亮，不需要连连看
    //   - 如果你设置了“普通模式”，加了 setSecondary() 的研究可以用研究点买
    //   - 如果你设置了“困难模式”，所有研究都必须连连看
    //
    // ⚠️ 你之前说“揭示之护目镜被研究点买走”，很可能是：
    //   - 你游戏难度设成了“普通模式”
    //   - 或者你的某个附属模组改写了研究完成逻辑
    //   - 不是你代码的问题
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充九】扭曲值 Warp 的真实计算（对照 ResearchManager 源码）
    // =========================================================================================
    // 你在研究节点上调用：
    //
    //   ThaumcraftApi.addWarpToResearch("研究KEY", 数值);
    //
    // 它不会立刻加扭曲，而是在玩家完成研究时，由 ResearchManager 来处理：
    //
    //   public void completeResearch(EntityPlayer player, String key) {
    //       if (completeResearchUnsaved(player.getCommandSenderName(), key)) {
    //           int warp = ThaumcraftApi.getWarp(key);
    //           if (warp > 0 && !Config.wuss && !player.worldObj.isRemote) {
    //               if (warp > 1) {
    //                   int w2 = warp / 2;
    //                   if (warp - w2 > 0) {
    //                       Thaumcraft.addWarpToPlayer(player, warp - w2, false);  // 永久
    //                   }
    //                   if (w2 > 0) {
    //                       Thaumcraft.addStickyWarpToPlayer(player, w2);          // 粘性
    //                   }
    //               } else {
    //                   Thaumcraft.addWarpToPlayer(player, warp, false);           // 永久
    //               }
    //           }
    //       }
    //   }
    //
    // 翻译一下：
    //   - warp == 0    → 不加扭曲
    //   - warp == 1    → 加 1 点永久扭曲
    //   - warp == 2    → 加 1 点永久 + 1 点粘性
    //   - warp == 3    → 加 2 点永久 + 1 点粘性
    //   - warp == 4    → 加 2 点永久 + 2 点粘性
    //   - warp == 10   → 加 5 点永久 + 5 点粘性
    //   - Config.wuss  → 和平模式，不加任何扭曲
    //
    // 永久扭曲：不能自然消退
    // 粘性扭曲：可以缓慢消退
    // 临时扭曲：不来自研究，来自事件
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十】研究笔记的颜色取决于“最高源质”
    // =========================================================================================
    // 原版 ResearchItem.getResearchPrimaryTag()：
    //
    //   public Aspect getResearchPrimaryTag() {
    //       Aspect aspect = null;
    //       int highest = 0;
    //       if (tags != null)
    //           for (Aspect tag : tags.getAspects()) {
    //               if (tags.getAmount(tag) > highest) {
    //                   aspect = tag;
    //                   highest = tags.getAmount(tag);
    //               }
    //           }
    //       return aspect;
    //   }
    //
    // 它返回你 tags 里数值最高的那个源质。
    // 这个源质决定了：
    //   - 研究笔记的颜色（ResearchManager.createNote 里 setInteger("color", ...)）
    //   - 研究节点边框的颜色
    //
    // ⚠️ 所以如果你不希望笔记颜色太怪，tags 里让主要源质的数值最高。
    //    比如 revealing_goggles 里 Aspect.SENSES 是 4，其他是 3，那颜色就是 SENSES 的颜色。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十一】研究笔记的六边形大小取决于 complexity
    // =========================================================================================
    // 原版 ResearchManager.createNote()：
    //
    //   int radius = 1 + Math.min(3, rr.getComplexity());
    //
    // 翻译一下：
    //   complexity == 0  → radius = 1
    //   complexity == 1  → radius = 2
    //   complexity == 2  → radius = 3
    //   complexity == 3  → radius = 4
    //   complexity == 4  → radius = 4（因为 Math.min(3, 4) = 3）
    //   complexity == 10 → radius = 4
    //   complexity == 20 → radius = 4
    //
    // ⚠️ 也就是说，原版里 complexity 超过 3 的部分会被忽略！
    //    你写 4 或者 20，实际半径都是 4，不会更大。
    //    如果你想要 20 真的有 20 的效果，必须改 ResearchManager.createNote() 里的 Math.min。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十二】研究节点能不能被“扫描发现”
    // =========================================================================================
    // 只有加了以下之一的研究节点，才能被扫描发现：
    //
    //   .setHidden()      → 扫描 / 知识碎片都能发现
    //   .setLost()        → 只能扫描发现，不能用知识碎片
    //
    // 并且还要配以下之一：
    //
    //   .setItemTriggers(new ItemStack(...))     → 扫描指定物品
    //   .setEntityTriggers("实体ID")              → 扫描指定实体
    //   .setAspectTriggers(Aspect.XXX)            → 扫描带指定源质的物品
    //
    // 如果只 setHidden 但不设 triggers：
    //   → 扫描任何东西都不会发现它
    //   → 它就成了“永远无法发现”的节点（除非用 @ 前缀强制解锁）
    //
    // ⚠️ 你明确说过不要扫描触发，所以：
    //    - 不要加 setHidden()
    //    - 不要加 setLost()
    //    - 不要加 setItemTriggers / setEntityTriggers / setAspectTriggers
    //    前置完成 → 亮起 → 点击拿笔记 → 连连看 → 右键解锁，这就是你要的。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十三】研究前缀 "@" 是什么意思
    // =========================================================================================
    // 原版 ResearchManager.isResearchComplete()：
    //
    //   if (!key.startsWith("@") && ResearchCategories.getResearch(key) == null) {
    //       return false;
    //   }
    //
    // 翻译一下：
    //   - key 前面带 "@" → 表示“线索”，不是完整研究
    //   - key 不带 "@"   → 必须是已注册的研究
    //
    // "@" 前缀通常用于：
    //   - 扫描发现隐藏研究时的“临时线索”
    //   - 知识碎片给出的提示
    //   - 玩家还没有真正完成研究，但已经“知道了有这么个东西”
    //
    // 你在写研究时一般不会用到 "@"，除非你想做“线索系统”。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十四】研究完成后的自动保存
    // =========================================================================================
    // 原版 ResearchManager.completeResearch() 在完成后会调用：
    //
    //   scheduleSave(player);
    //
    // 但你项目里的 scheduleSave 是空的：
    //
    //   public static void scheduleSave(EntityPlayer player) {
    //       if (!player.worldObj.isRemote) {
    //           ;
    //       }
    //   }
    //
    // 也就是说，TC4 原版依赖玩家退出时整体保存，不是即时保存。
    // 你不用担心“研究完成没保存”，退出游戏时会被一起保存。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十五】完整的小白速查表
    // =========================================================================================
    // 想注册一个“前置完成 → 亮起 → 点击拿笔记 → 连连看 → 右键解锁”的研究：
    //
    // 1. 写 register：
    //    public static final DeferredHolder<ResearchItem> XXX = register(
    //        "研究key",
    //        key -> new NamespacedResearchItem(
    //            key,
    //            new AspectList().add(Aspect.XXX, 数值),
    //            x, y, 难度,
    //            new ItemStack(图标物品))
    //                .setPages(
    //                    createTextResearchPage(key, 1),
    //                    new ResearchPage(配方))
    //                .setParents(可见前置)          // 会画线
    //                .setParentsHidden("隐藏前置")  // 不画线
    //                .setConcealed()                // 前置没完成看不见
    //                .setSpecial());                // 尖刺边框
    //
    // 2. 语言文件：
    //    research.magianaturalis.研究key.name=研究名
    //    research.magianaturalis.研究key.text=研究简介
    //    research.magianaturalis.研究key.page.1=第一页文字
    //
    // 3. 配方先注册：
    //    ThaumcraftApi.addXxxRecipe("研究key", 配方);
    //    并且要放进 MNRecipes 的 Map
    //
    // 4. 不要加：
    //    setHidden() / setLost() / setSecondary()
    //    setItemTriggers() / setEntityTriggers() / setAspectTriggers()
    //
    // 5. 想加扭曲才加：
    //    ThaumcraftApi.addWarpToResearch(research.key, 数值);
    //
    // =========================================================================================
    // =========================================================================================

    // =========================================================================================
    // =========================================================================================
    // 【补充十六】ResearchPage 的 ALL 页面类型（对照 ResearchPage.java 源码）
    // =========================================================================================
    //
    // ResearchPage.java 里定义的 PageType 枚举有 11 种：
    //
    //   TEXT                    纯文本页
    //   TEXT_CONCEALED          条件文本页（完成某研究后才显示）
    //   IMAGE                   图片页
    //   CRUCIBLE_CRAFTING       坩埚配方页
    //   ARCANE_CRAFTING         奥术工作台配方页
    //   ASPECTS                 源质页
    //   NORMAL_CRAFTING         原版工作台配方页
    //   INFUSION_CRAFTING       注魔配方页
    //   COMPOUND_CRAFTING       复合页（多个配方 / 多方块结构）
    //   INFUSION_ENCHANTMENT    注魔附魔页
    //   SMELTING                冶炼页
    //
    // 你写 new ResearchPage(对象) 时，源码会根据对象的类型自动判断页面类型。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十七】每种 ResearchPage 的构造方法（对照源码）
    // =========================================================================================
    //
    // 【纯文本页】
    //   new ResearchPage("本地化KEY")
    //   new ResearchPage("tc.research_page.XXX.1")
    //   → 显示一段文本，文本从语言文件里读
    //
    // 【条件文本页】
    //   new ResearchPage("某研究KEY", "本地化KEY")
    //   → 只有当玩家完成了 "某研究KEY" 时，这个页面才显示
    //   → 原版例子：
    //       new ResearchPage("UPGRADEAIR", "tc.research_page.COREUSE.3")
    //   → 效果：完成了风之升级研究后，核心使用页才会出现“风之升级解锁”那一段
    //
    // 【图片页】
    //   new ResearchPage(ResourceLocation 图片, String 说明文本)
    //   → 原版例子：
    //       new ResearchPage(new ResourceLocation("thaumcraft", "textures/misc/r_mask0.png"),
    //                        "面具说明")
    //   → 显示一张图片 + 一段说明
    //
    // 【坩埚配方页】
    //   new ResearchPage(CrucibleRecipe)
    //   new ResearchPage(CrucibleRecipe[])
    //   → 显示坩埚配方
    //
    // 【奥术工作台配方页】
    //   new ResearchPage(IArcaneRecipe)
    //   new ResearchPage(IArcaneRecipe[])
    //   → 显示奥术工作台配方
    //
    // 【原版工作台配方页】
    //   new ResearchPage(IRecipe)
    //   new ResearchPage(IRecipe[])
    //   → 显示原版工作台配方
    //
    // 【注魔配方页】
    //   new ResearchPage(InfusionRecipe)
    //   new ResearchPage(InfusionRecipe[])
    //   → 显示注魔配方
    //
    // 【注魔附魔页】
    //   new ResearchPage(InfusionEnchantmentRecipe)
    //   → 显示注魔附魔配方
    //
    // 【冶炼页】
    //   new ResearchPage(ItemStack input)
    //   → 显示“把 input 放进熔炉冶炼后得到什么”
    //   → 原版例子：
    //       new ResearchPage(new ItemStack(ConfigItems.itemShard, 1, 6))
    //   → 效果：显示平衡碎片冶炼后得到世界盐
    //   → ⚠️ 注意：这个 ItemStack 是【输入】，不是输出！
    //     输出由 FurnaceRecipes.smelting().getSmeltingResult(input) 自动取
    //
    // 【复合页 / 多方块结构页】
    //   new ResearchPage(List recipe)
    //   → 这是一个"万能页"，根据 List 里塞什么来决定显示什么
    //   → 可以塞多个配方（自动分页显示）
    //   → 也可以塞多方块结构（你自己代码里就是这么用的）
    //
    // 【源质页】
    //   new ResearchPage(AspectList)
    //   → 显示一组源质和数量
    //   → 很少直接用，通常是内部使用
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十八】多方块结构页的格式（对照你自己代码）
    // =========================================================================================
    // 你的 createGeoPylonStructurePage() 和 createTranscribingTableStructurePage()
    // 返回的都是 List<Object>，格式是 TC4 规定的"多方块结构"格式：
    //
    // List<Object> structure = new ArrayList<>();
    // structure.add(new AspectList());     // [0] 源质消耗（通常留空）
    // structure.add(Integer dx);           // [1] 结构宽（x 方向）
    // structure.add(Integer dy);           // [2] 结构高（y 方向）
    // structure.add(Integer dz);           // [3] 结构深（z 方向）
    // structure.add(List<ItemStack> blocks);// [4] 方块列表（按顺序排）
    //
    // 关键点：
    //   - 方块列表的顺序是：先 y 循环，再 x 循环，再 z 循环
    //     也就是：
    //       for (int y = 0; y < dy; y++)
    //         for (int x = 0; x < dx; x++)
    //           for (int z = 0; z < dz; z++)
    //             blocks.add(该位置的方块);
    //
    //   - ⚠️ TC4 结构页的 y 轴是【反】的！
    //     y = 0 显示在【最上面】，y = dy-1 显示在【最下面】
    //     和 MC 世界坐标正好相反（MC 里 y=0 是底部）
    //
    //   - 每格方块如果是 "隙间"（ConfigBlocks.blockHole），表示"这一格是空的"
    //     它不会显示任何东西，只是占位
    //
    //   - 你的 createTranscribingTableStructurePage() 里 dy=1，只画一层
    //     这是最清楚的做法，推荐
    //
    // 完整例子（你的代码）：
    //
    // List<Object> structure = new ArrayList<>();
    // structure.add(new AspectList());   // [0] 源质消耗空
    // structure.add(5);                   // [1] dx = 5
    // structure.add(1);                   // [2] dy = 1
    // structure.add(5);                   // [3] dz = 5
    //
    // List<ItemStack> blocks = new ArrayList<>();
    // for (int y = 0; y < 1; y++) {
    //     for (int x = 0; x < 5; x++) {
    //         for (int z = 0; z < 5; z++) {
    //             if (x == 2 && z == 2) {
    //                 blocks.add(new ItemStack(MNBlocks.transcribingTable));
    //             } else if (x % 2 == 0 && z % 2 == 0) {
    //                 blocks.add(new ItemStack(ConfigBlocks.blockTable, 1, 14));
    //             } else {
    //                 blocks.add(new ItemStack(ConfigBlocks.blockHole, 1, 15));
    //             }
    //         }
    //     }
    // }
    // structure.add(blocks);              // [4] 方块列表
    //
    // 然后在研究节点里：
    // new ResearchPage(structure)
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充十九】原版工作台配方页怎么写
    // =========================================================================================
    // 原版工作台配方有两种来源：
    //
    //   1. 真的注册到原版工作台的配方（GameRegistry.addRecipe）
    //   2. TC4 提供的 createFakeRecipe，只用于研究页显示
    //
    // 如果你只想"在手册里显示"，不想真的能合成：
    //
    // ShapedRecipes fake = ThaumcraftCraftingManager.createFakeRecipe(
    //     new ItemStack(MNItems.revealingGoggles),
    //     new Object[] {
    //         "ABA",
    //         "BCB",
    //         "ABA",
    //         'A', new ItemStack(Items.gold_ingot),
    //         'B', new ItemStack(Items.diamond),
    //         'C', new ItemStack(ConfigItems.itemGoggles)
    //     });
    //
    // 然后在研究节点里：
    // new ResearchPage(fake)
    //
    // 如果你想让玩家真的能用原版工作台合成，要另外注册：
    //
    // GameRegistry.addRecipe(
    //     new ShapedOreRecipe(
    //         new ItemStack(MNItems.revealingGoggles),
    //         new Object[] {
    //             "ABA",
    //             "BCB",
    //             "ABA",
    //             'A', "ingotGold",
    //             'B', "gemDiamond",
    //             'C', new ItemStack(ConfigItems.itemGoggles)
    //         }));
    //
    // 两个都做了，才是"能合成 + 手册里能看到"。
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充二十】冶炼页怎么写
    // =========================================================================================
    // 冶炼页就是：
    //
    // new ResearchPage(new ItemStack(输入物品))
    //
    // ⚠️ 这里的 ItemStack 是【输入】，不是输出！
    //   输出由源码内部自动查：
    //       FurnaceRecipes.smelting().getSmeltingResult(input)
    //
    // 原版例子：
    //
    // new ResearchPage(new ItemStack(ConfigItems.itemShard, 1, 6))
    //   → 输入是平衡碎片（meta 6）
    //   → 输出自动查 = 世界盐
    //   → 页面显示：平衡碎片 → 世界盐
    //
    // 如果你要加自定义冶炼配方，先注册到原版：
    //
    // GameRegistry.addSmelting(
    //     new ItemStack(输入物品),
    //     new ItemStack(输出物品),
    //     经验值);
    //
    // 然后在研究页里：
    // new ResearchPage(new ItemStack(输入物品))
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充二十一】多配方页怎么写
    // =========================================================================================
    // 如果你想在一个研究节点里显示多个配方（比如一堆工具），
    // 有两种做法：
    //
    // 【做法一】每个配方一个页面
    // .setPages(
    //     createTextResearchPage(key, 1),
    //     new ResearchPage(配方1),
    //     new ResearchPage(配方2),
    //     new ResearchPage(配方3))
    //
    // 效果：三个配方页，玩家翻三页看
    //
    // 【做法二】一个复合页塞多个配方
    // ArrayList<IArcaneRecipe> recipes = new ArrayList<>();
    // recipes.add(配方1);
    // recipes.add(配方2);
    // recipes.add(配方3);
    //
    // .setPages(
    //     createTextResearchPage(key, 1),
    //     new ResearchPage(recipes))
    //
    // 效果：一个复合页，自动分页显示三个配方
    //
    // 原版例子：
    //
    // ArrayList scer = new ArrayList();
    // scer.add(权杖配方1);
    // scer.add(权杖配方2);
    // scer.add(权杖配方3);
    //
    // (new ResearchItem("SCEPTRE", "THAUMATURGY", ...))
    //     .setPages(new ResearchPage[] {
    //         new ResearchPage("tc.research_page.SCEPTRE.1"),
    //         new ResearchPage((IArcaneRecipe[])scer.toArray(new IArcaneRecipe[0]))
    //     })
    //     .registerResearchItem();
    //
    // 你项目里也用到了这个：
    // new ResearchPage(MNRecipes.getRecipes("WoodConversion"))
    // new ResearchPage(MNRecipes.getRecipes("ColorConversion"))
    //
    // =========================================================================================
    // =========================================================================================
    // 【补充二十二】完整小白速查表：研究节点 + 配方
    // =========================================================================================
    //
    // 想做一个"前置完成 → 亮起 → 点击拿笔记 → 连连看 → 解锁 → 能合成"的东西：
    //
    // 【第 1 步】注册分类（如果还没注册）
    //   ResearchCategories.registerCategory(
    //       MagiaNaturalis.MOD_ID,
    //       MagiaNaturalis.rl("textures/items/research_log.png"),
    //       MagiaNaturalis.rl("textures/gui/background.png"));
    //
    // 【第 2 步】注册配方（游戏要知道怎么做）
    //   ThaumcraftApi.addArcaneCraftingRecipe("研究key", 配方);
    //   ThaumcraftApi.addShapelessArcaneCraftingRecipe("研究key", 配方);
    //   ThaumcraftApi.addCrucibleRecipe("研究key", 配方);
    //   ThaumcraftApi.addInfusionCraftingRecipe("研究key", 配方);
    //   ThaumcraftApi.addInfusionEnchantmentRecipe("研究key", 配方);
    //   原版工作台配方用 GameRegistry.addRecipe(...)
    //   冶炼配方用 GameRegistry.addSmelting(...)
    //
    // 【第 3 步】把配方放进 MNRecipes 的 Map（研究页面要取）
    //   infusionRecipes.put("配方名", 配方);
    //   arcaneRecipes.put("配方名", 配方);
    //   crucibleRecipes.put("配方名", 配方);
    //
    // 【第 4 步】注册研究节点（游戏要知道有这个知识）
    //   public static final DeferredHolder<ResearchItem> XXX = register(
    //       "研究key",
    //       key -> new NamespacedResearchItem(
    //           key,
    //           new AspectList().add(Aspect.XXX, 数值),
    //           x, y, 难度,
    //           new ItemStack(图标))
    //               .setPages(
    //                   createTextResearchPage(key, 1),                    // 文字页
    //                   new ResearchPage(MNRecipes.getXxxRecipe("配方名"))) // 配方页
    //               .setParents(可见前置)          // 会画线
    //               .setParentsHidden("隐藏前置")  // 不画线
    //               .setConcealed()                // 前置没完成看不见
    //               .setSpecial());                // 尖刺边框
    //
    // 【第 5 步】加语言文件
    //   research.magianaturalis.研究key.name=研究名
    //   research.magianaturalis.研究key.text=研究简介
    //   research.magianaturalis.研究key.page.1=第一页文字
    //   research.magianaturalis.研究key.page.2=第二页文字
    //   ...
    //
    // 【第 6 步】研究页面里塞配方
    //   配方类型              写法
    //   ------------------    -----------------------------------------
    //   原版工作台            new ResearchPage((IRecipe) 配方)
    //   奥术工作台            new ResearchPage((IArcaneRecipe) 配方)
    //   坩埚                  new ResearchPage((CrucibleRecipe) 配方)
    //   注魔                  new ResearchPage((InfusionRecipe) 配方)
    //   注魔附魔              new ResearchPage((InfusionEnchantmentRecipe) 配方)
    //   冶炼                  new ResearchPage(new ItemStack(输入))
    //   多方块结构            new ResearchPage(List)
    //   多配方复合页          new ResearchPage(List)
    //   图片                  new ResearchPage(ResourceLocation, String)
    //   纯文本                new ResearchPage("本地化KEY")
    //   条件文本              new ResearchPage("触发研究KEY", "本地化KEY")
    //
    // 【第 7 步】想加扭曲才加
    //   ThaumcraftApi.addWarpToResearch(research.key, 数值);
    //
    // 【第 8 步】测试
    //   - 能合成吗？        → 配方注册成功
    //   - 手册里能看到吗？  → 研究节点注册成功
    //   - 配方页显示对吗？  → ResearchPage 类型传对了
    //   - 前置要求生效吗？  → setParents / setParentsHidden 对了
    //   - 颜色难看吗？      → tags 里最高源质决定颜色
    //
    // =========================================================================================
    // =========================================================================================