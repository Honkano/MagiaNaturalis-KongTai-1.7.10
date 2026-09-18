package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.util.NBTUtil;
import com.github.elenterius.magianaturalis.util.Platform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class ResearchLogItem extends Item {

    public static final Aspect[] RESEARCH_ASPECTS = { Aspect.AIR, Aspect.EARTH, Aspect.WATER, Aspect.FIRE, Aspect.ORDER,
        Aspect.ENTROPY };

    public ResearchLogItem() {
        super();
        maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);

        short airResearch = getResearchPoint(stack, Aspect.AIR);
        short earthResearch = getResearchPoint(stack, Aspect.EARTH);
        short waterResearch = getResearchPoint(stack, Aspect.WATER);
        short fireResearch = getResearchPoint(stack, Aspect.FIRE);
        short orderResearch = getResearchPoint(stack, Aspect.ORDER);
        short entropyResearch = getResearchPoint(stack, Aspect.ENTROPY);

        int totalResearch = airResearch + earthResearch
            + waterResearch
            + fireResearch
            + orderResearch
            + entropyResearch;
        if (totalResearch == 0) {
            list.add(EnumChatFormatting.DARK_GRAY + Platform.translate("hint.magianaturalis.empty"));
            return;
        }

        if (airResearch > 0)
            list.add(String.format("%dx %s%s", airResearch, EnumChatFormatting.YELLOW, Aspect.AIR.getName()));
        if (earthResearch > 0)
            list.add(String.format("%dx %s%s", earthResearch, EnumChatFormatting.GREEN, Aspect.EARTH.getName()));
        if (waterResearch > 0)
            list.add(String.format("%dx %s%s", waterResearch, EnumChatFormatting.AQUA, Aspect.WATER.getName()));
        if (fireResearch > 0)
            list.add(String.format("%dx %s%s", fireResearch, EnumChatFormatting.RED, Aspect.FIRE.getName()));
        if (orderResearch > 0)
            list.add(String.format("%dx %s%s", orderResearch, EnumChatFormatting.WHITE, Aspect.ORDER.getName()));
        if (entropyResearch > 0) list
            .add(String.format("%dx %s%s", entropyResearch, EnumChatFormatting.DARK_GRAY, Aspect.ENTROPY.getName()));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack == null) return null;
        if (Platform.isClient()) return stack;
        if (player.isSneaking()) return stack;

        for (Aspect aspect : RESEARCH_ASPECTS) {
            short amount = NBTUtil.getOrCreate(stack)
                .getShort("rp:" + aspect.getTag());
            if (amount > 0 && MagiaNaturalis.proxyTC4.playerKnowledge
                .addAspectPool(player.getCommandSenderName(), aspect, amount)) {
                ResearchManager.scheduleSave(player);
                PacketHandler.INSTANCE.sendTo(
                    new PacketAspectPool(
                        aspect.getTag(),
                        amount,
                        Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(player.getCommandSenderName(), aspect)),
                    (EntityPlayerMP) player);
                setResearchPoint(stack, aspect, (short) 0);
            }
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7,
        float par8, float par9, float par10) {
        if (Platform.isClient()) return false;
        if (!player.isSneaking()) return false;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileDeconstructionTable) {
            TileDeconstructionTable table = (TileDeconstructionTable) tile;
            if (addResearchPoint(stack, table.aspect, (short) 1)) {
                table.aspect = null;
                world.markBlockForUpdate(x, y, z);
                return true;
            }
        }

        return false;
    }

    public short getResearchPoint(ItemStack stack, Aspect aspect) {
        if (stack != null && aspect != null) {
            return NBTUtil.getOrCreate(stack)
                .getShort("rp:" + aspect.getTag());
        }
        return 0;
    }

    public boolean setResearchPoint(ItemStack stack, Aspect aspect, short size) {
        if (stack != null && aspect != null) {
            if (size > 64) return false;
            if (size < 0) size = 0;
            NBTUtil.getOrCreate(stack)
                .setShort("rp:" + aspect.getTag(), size);
            return true;
        }
        return false;
    }

    public boolean addResearchPoint(ItemStack stack, Aspect aspect, short size) {
        if (stack != null && aspect != null && size > 0) {
            size += NBTUtil.getOrCreate(stack)
                .getShort("rp:" + aspect.getTag());
            if (size > 64) return false;
            NBTUtil.getOrCreate(stack)
                .setShort("rp:" + aspect.getTag(), size);
            return true;
        }
        return false;
    }

}
