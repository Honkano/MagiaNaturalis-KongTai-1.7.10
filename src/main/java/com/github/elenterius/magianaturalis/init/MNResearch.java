package com.github.elenterius.magianaturalis.init;

import java.util.HashMap;
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
import thaumcraft.common.config.ConfigItems;

public class MNResearch {

    private static final Map<DeferredHolder<ResearchItem>, Function<ResourceLocation, ResearchItem>> RESEARCH_REGISTRY = new HashMap<>();

    public static final DeferredHolder<ResearchItem> INTRO = register(
        "intro",
        key -> new NamespacedResearchItem(key, 0, 0, 0, new ItemStack(ConfigItems.itemResource, 1, 15)).setSpecial()
            .setRound()
            .setAutoUnlock()
            .setPages(createTextResearchPage(key, 1)));

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
                    new ResearchPage(MNRecipes.getArcaneRecipe("TranscribingTable")))
                .setParents(RESEARCH_LOG.getId()));

    public static final DeferredHolder<ResearchItem> GOGGLES_PROXY = register(
        "tc_goggles",
        key -> ResearchItemProxy.createNamespaced(key, "GOGGLES", -4, 1));

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
                .setPages(createTextResearchPage(key, 1), new ResearchPage(MNRecipes.getArcaneRecipe("Spectacles")))
                .setSecondary()
                .setParents(GOGGLES_PROXY.getId()));

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
                .setPages(createTextResearchPage(key, 1), new ResearchPage(MNRecipes.getInfusionRecipe("DarkGoggles")))
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

    public static final DeferredHolder<ResearchItem> EVIL_TRUNK = register("evil_trunk", key -> {
        ResearchItem research = new NamespacedResearchItem(
            key,
            new AspectList().add(Aspect.SOUL, 3)
                .add(Aspect.BEAST, 3)
                .add(Aspect.TAINT, 3),
            2,
            5,
            2,
            new ItemStack(MNItems.evilTrunkSpawner))
                .setPages(
                    createTextResearchPage(key, 1),
                    new ResearchPage(MNRecipes.getInfusionRecipe("CorruptedTrunk")),
                    new ResearchPage(MNRecipes.getInfusionRecipe("SinisterTrunk")),
                    new ResearchPage(MNRecipes.getInfusionRecipe("DemonicTrunk")),
                    new ResearchPage(MNRecipes.getInfusionRecipe("TaintedTrunk")))
                .setParents(TRAVEL_TRUNK_PROXY.getId());

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
                    new ResearchPage("research.magianaturalis.geo_occultism.page.2"),
                    new ResearchPage(MNRecipes.getArcaneRecipe("BiomeReport")))
                .setParentsHidden(
                    "INFUSION",
                    MUTATION_STONE.getId()
                        .toString()));

    // research = new NamespacedResearchItem("SARKOLOGIA", 2, 1, 0, new ResourceLocation(ResourceUtil.DOMAIN,
    // "textures/misc/titan.png"));
    // research.setPages(new ResearchPage[] { new ResearchPage("research.magianaturalis.SARKOLOGIA.1") });
    // research.setRound().setAutoUnlock().registerResearchItem();
    //
    // research = new NamespacedResearchItem("MINERALOGY", 2, 2, 0, new ItemStack(Items.quartz, 1, 0));
    // research.setPages(new ResearchPage[] { new ResearchPage("research.magianaturalis.MINERALOGY.1") });
    // research.setRound().setAutoUnlock().registerResearchItem();
    //

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
                    new ResearchPage(MNRecipes.getInfusionRecipe("HerobrinesScythe")))
                .setParents(SICKLE_OF_ABUNDANCE.getId()) // 可见前置：富饶镰刀
                .setParentsHidden("INFUSION") // 隐藏前置：必须解锁注魔！【补上这一行】
                .setConcealed()
                .setSpecial();

        ThaumcraftApi.addWarpToResearch(research.key, 4);
        return research;
    });

}
