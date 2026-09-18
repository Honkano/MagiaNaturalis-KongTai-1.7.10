package com.github.elenterius.magianaturalis.init;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.block.banner.CustomBannerBlock;
import com.github.elenterius.magianaturalis.block.banner.CustomBannerBlockEntity;
import com.github.elenterius.magianaturalis.block.banner.CustomBannerBlockItem;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlock;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockItem;
import com.github.elenterius.magianaturalis.block.geopylon.GeoPylonBlock;
import com.github.elenterius.magianaturalis.block.geopylon.GeoPylonBlockEntity;
import com.github.elenterius.magianaturalis.block.jar.PrisonJarBlock;
import com.github.elenterius.magianaturalis.block.jar.PrisonJarBlockEntity;
import com.github.elenterius.magianaturalis.block.jar.PrisonJarBlockItem;
import com.github.elenterius.magianaturalis.block.table.TranscribingTableBlock;
import com.github.elenterius.magianaturalis.block.table.TranscribingTableBlockEntity;
import com.github.elenterius.magianaturalis.block.wood.ArcaneWoodBlock;
import com.github.elenterius.magianaturalis.block.wood.ArcaneWoodBlockItem;

import cpw.mods.fml.common.registry.GameRegistry;

public class MNBlocks {

    public static Block transcribingTable;
    public static Block arcaneChest;
    public static Block jarPrison;
    public static Block arcaneWood;
    public static Block banner;
    public static Block geoPylon;

    public static void initBlocks() {
        transcribingTable = registerBlock("transcribing_table", TranscribingTableBlock::new);
        arcaneChest = registerBlock("arcane_chest", ArcaneChestBlockItem.class, ArcaneChestBlock::new);
        jarPrison = registerBlock("prison_jar", PrisonJarBlockItem.class, PrisonJarBlock::new);
        arcaneWood = registerBlock("arcane_wood", ArcaneWoodBlockItem.class, ArcaneWoodBlock::new);
        banner = registerBlock("banner", CustomBannerBlockItem.class, CustomBannerBlock::new);
        geoPylon = registerBlock("geo_pylon", GeoPylonBlock::new);

        registerOreDict();
    }

    private static <T extends Block, V extends ItemBlock> T registerBlock(String name, Class<V> blockItem,
        Supplier<T> factory) {
        T block = factory.get();
        block.setBlockName(MagiaNaturalis.translationKey(name));
        block.setCreativeTab(MNCreativeTabs.MAIN);

        GameRegistry.registerBlock(block, blockItem, name);

        return block;
    }

    private static <T extends Block> T registerBlock(String name, Supplier<T> factory) {
        T block = factory.get();
        block.setBlockName(MagiaNaturalis.translationKey(name));
        block.setCreativeTab(MNCreativeTabs.MAIN);

        GameRegistry.registerBlock(block, name);

        return block;
    }

    private static void registerOreDict() {
        OreDictionary.registerOre("plankWood", new ItemStack(MNBlocks.arcaneWood, 1, 0));
        OreDictionary.registerOre("plankWood", new ItemStack(MNBlocks.arcaneWood, 1, 1));
        OreDictionary.registerOre("plankWood", new ItemStack(MNBlocks.arcaneWood, 1, 2));
    }

    public static void initTileEntities() {
        GameRegistry
            .registerTileEntity(TranscribingTableBlockEntity.class, MagiaNaturalis.rlString("transcribing_table"));
        GameRegistry.registerTileEntity(ArcaneChestBlockEntity.class, MagiaNaturalis.rlString("arcane_chest"));
        GameRegistry.registerTileEntity(PrisonJarBlockEntity.class, MagiaNaturalis.rlString("prison_jar"));
        GameRegistry.registerTileEntity(CustomBannerBlockEntity.class, MagiaNaturalis.rlString("banner"));
        GameRegistry.registerTileEntity(GeoPylonBlockEntity.class, MagiaNaturalis.rlString("geo_pylon"));
    }

}
