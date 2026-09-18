package com.github.elenterius.magianaturalis.item;

import java.io.*;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.util.Platform;

import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.world.biomes.BiomeHandler;

public class BiomeDevTool extends Item {

    public BiomeDevTool() {
        super();
        maxStackSize = 1;

        setTextureName(MagiaNaturalis.rlString("research_log"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);
        list.add(EnumChatFormatting.DARK_PURPLE + "Last Biome: " + stack.getItemDamage());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Platform.isClient()) return stack;
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7,
        float par8, float par9, float par10) {
        if (Platform.isClient()) return false;
        boolean ignore = false;

        if (ignore) {
            MagiaNaturalis.LOGGER.info(world.getBlock(x, y, z));
            MagiaNaturalis.LOGGER.info(world.getBlockMetadata(x, y, z));
            return false;
        }

        String divider = "----------------------------------------------------";

        File mcDir = (File) FMLInjectionData.data()[6];
        File modsDir = new File(mcDir, "magia_naturalis");
        if (!modsDir.exists()) modsDir.mkdirs();

        File exportFile = new File(mcDir, "magia_naturalis/" + "Biome_Types-Aspects.txt");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile)))) {
            String aspectName = "";
            String types = "";
            int aura = 0;

            Aspect aspect;
            for (BiomeDictionary.Type type : BiomeDictionary.Type.values()) {
                types = "[" + type.toString() + "]";
                aspect = (Aspect) BiomeHandler.biomeInfo.get(type)
                    .get(1);
                aura = (int) BiomeHandler.biomeInfo.get(type)
                    .get(0);
                aura = Math.round(aura * 2F / 100F);

                if (aspect != null) {
                    aspectName = "[" + aspect.getName() + "]";
                } else {
                    aspectName = type == BiomeDictionary.Type.MAGICAL ? "[" + Aspect.MAGIC.getName() + "]" : "[NULL]";
                }

                String info = String.format("%-16s :\t%dx\t%s%n", types, aura, aspectName);
                writer.write(info);
            }
        } catch (IOException e) {
            MagiaNaturalis.LOGGER.error("Failed to dump {}", exportFile.getPath(), e);
        }

        exportFile = new File(mcDir, "magia_naturalis/" + "Biome_Composition.txt");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile)))) {
            BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
            for (BiomeGenBase biome : biomes) {
                if (biome != null) {
                    String aspectName = "";
                    String types = "";
                    int aura = 0;
                    StringBuilder aspectCost = new StringBuilder();

                    Aspect aspect;
                    for (BiomeDictionary.Type type : BiomeDictionary.getTypesForBiome(biome)) {
                        types = "[" + type.toString() + "]";
                        aspect = (Aspect) BiomeHandler.biomeInfo.get(type)
                            .get(1);
                        aura = (int) BiomeHandler.biomeInfo.get(type)
                            .get(0);
                        aura = Math.round(aura * 2F / 100F);

                        if (aspect != null) {
                            aspectName = "[" + aspect.getName() + "]";
                        } else {
                            aspectName = type == BiomeDictionary.Type.MAGICAL ? "[" + Aspect.MAGIC.getName() + "]"
                                : "[NULL]";
                        }

                        aspectCost.append(String.format("%-16s :\t%dx\t%s%n", types, aura, aspectName));
                    }

                    String info = String.format(divider + "%nBiome: %s%n%s", biome.biomeName, aspectCost);
                    writer.write(info);
                }
            }
        } catch (IOException e) {
            MagiaNaturalis.LOGGER.error("Failed to dump {}", exportFile.getPath(), e);
        }

        return false;
    }

}
