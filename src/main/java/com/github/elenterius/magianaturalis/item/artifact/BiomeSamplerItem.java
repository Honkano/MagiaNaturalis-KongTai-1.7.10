package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.Constants.NBT;

import org.apache.commons.lang3.text.WordUtils;

import com.github.elenterius.magianaturalis.block.geopylon.GeoPylonBlockEntity;
import com.github.elenterius.magianaturalis.util.NBTUtil;
import com.github.elenterius.magianaturalis.util.Platform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.world.biomes.BiomeHandler;

public class BiomeSamplerItem extends Item {

    public static final String ASPECTS_TAG_KEY = "aspects";
    public static final String BIOME_ID_TAG_KEY = "biome_id";
    public static final String BIOME_NAME_TAG_KEY = "biome_name";
    public static final String BIOME_COLOR_TAG_KEY = "biome_color";

    protected IIcon iconOverlay;

    public BiomeSamplerItem() {
        super();
        setMaxStackSize(1);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister icon) {
        itemIcon = icon.registerIcon(iconString + "_base");
        iconOverlay = icon.registerIcon(iconString + "_overlay");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String translated = Platform.translate(getUnlocalizedName(stack) + ".name");

        String biomeName = NBTUtil.getOrCreate(stack)
            .getString(BIOME_NAME_TAG_KEY);
        if (!biomeName.isEmpty()) {
            return biomeName.trim() + " " + translated;
        }

        return translated;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);

        String[] aspects = getAspects(stack);
        if (aspects == null) {
            list.add(EnumChatFormatting.DARK_GRAY + Platform.translate("hint.magianaturalis.empty"));
            return;
        }

        if (GuiScreen.isCtrlKeyDown()) {
            for (String aspect : aspects) {
                if (aspect != null) list.add(aspect);
            }
        } else {
            list.add(EnumChatFormatting.DARK_GRAY + Platform.translate("hint.magianaturalis.ctrl"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int renderPass) {
        return renderPass == 0 ? itemIcon : iconOverlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int i) {
        if (i == 0) return 0xFF_FFFFFF;
        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        return data.hasKey(BIOME_COLOR_TAG_KEY) ? data.getInteger(BIOME_COLOR_TAG_KEY) : 0xFF_FFFFFF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7,
        float par8, float par9, float par10) {
        if (Platform.isClient()) return false;

        if (player.isSneaking()) {
            BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
            if (biome == null) return false;

            NBTTagCompound data = NBTUtil.getOrCreate(stack);
            data.setString(BIOME_NAME_TAG_KEY, biome.biomeName + " ");
            data.setInteger(BIOME_ID_TAG_KEY, biome.biomeID);
            data.setInteger(BIOME_COLOR_TAG_KEY, biome.color);

            BiomeDictionary.Type[] newTypes = BiomeDictionary.getTypesForBiome(biome);
            NBTTagList nbttaglist = new NBTTagList();
            NBTTagCompound tempData = new NBTTagCompound();

            for (Type newType : newTypes) {
                if (newType != null) {
                    if (newType != Type.MAGICAL) // Because Thaumcraft adds null aspect for magical biome type
                    {
                        Aspect aspect = (Aspect) BiomeHandler.biomeInfo.get(newType)
                            .get(1);
                        if (aspect != null) {
                            int aura = (int) BiomeHandler.biomeInfo.get(newType)
                                .get(0);
                            tempData.setShort(aspect.getTag(), (short) Math.round(aura * 2F / 100F));
                            nbttaglist.appendTag(tempData);
                        }
                    } else {
                        tempData.setShort(Aspect.MAGIC.getTag(), (short) 2);
                        nbttaglist.appendTag(tempData);
                    }
                }
            }

            data.setTag(ASPECTS_TAG_KEY, nbttaglist);
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof GeoPylonBlockEntity) {
                GeoPylonBlockEntity geo = (GeoPylonBlockEntity) tile;
                geo.cachedBiome = BiomeGenBase.getBiome(
                    NBTUtil.getOrCreate(stack)
                        .getInteger(BIOME_ID_TAG_KEY));
                world.markBlockForUpdate(x, y, z);
                return true;
            }
        }

        return false;
    }

    public String[] getAspects(ItemStack stack) {
        if (stack == null) return null;

        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        if (!data.hasKey(ASPECTS_TAG_KEY)) return null;

        NBTTagList nbttaglist = data.getTagList(ASPECTS_TAG_KEY, NBT.TAG_COMPOUND);

        String[] aspects = new String[nbttaglist.tagCount()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound tempData = nbttaglist.getCompoundTagAt(i);
            Set<?> keys = tempData.func_150296_c();
            int j = 0;
            for (Object ob : keys) {
                if (ob instanceof String) {
                    if (j >= nbttaglist.tagCount()) break;
                    String aspectTag = (String) ob;

                    String color = Aspect.getAspect(aspectTag)
                        .getChatcolor();
                    if (color == null) color = "5";
                    aspects[j++] = String.format(
                        "%dx %s%s",
                        tempData.getShort(aspectTag),
                        '§' + color,
                        WordUtils.capitalizeFully(aspectTag));
                }
            }
        }

        return aspects;
    }

}
