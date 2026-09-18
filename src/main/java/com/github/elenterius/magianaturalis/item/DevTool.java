package com.github.elenterius.magianaturalis.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.block.table.TranscribingTableBlockEntity;
import com.github.elenterius.magianaturalis.item.artifact.ResearchLogItem;
import com.github.elenterius.magianaturalis.util.Platform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileBanner;
import thaumcraft.common.tiles.TileOwned;

public class DevTool extends Item {

    private byte modes;

    public DevTool() {
        super();
        modes = 2;
        maxStackSize = 1;

        setTextureName(MagiaNaturalis.rlString("research_log"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);
        list.add(EnumChatFormatting.DARK_PURPLE + "Modus Operandi: " + stack.getItemDamage());

        if (stack.getItemDamage() == 0) list.add(EnumChatFormatting.DARK_GRAY + "Do Nothing");
        else if (stack.getItemDamage() == 1) list.add(EnumChatFormatting.DARK_GRAY + "Debug Certain Tiles");
        else if (stack.getItemDamage() == 2) list.add(EnumChatFormatting.DARK_GRAY + "Remove Temp Warp");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Platform.isClient()) return stack;

        // Change DEV Tool Modes!!!
        if (player.isSneaking()) {
            stack.setItemDamage(stack.getItemDamage() + 1);
            if (stack.getItemDamage() > modes) stack.setItemDamage(0);
            return stack;
        }

        if (stack.getItemDamage() == 2) {
            ThaumcraftApiHelper.addStickyWarpToPlayer(player, -100);
            ThaumcraftApiHelper.addWarpToPlayer(player, -100, true);
        }
        if (stack.getItemDamage() == 0) {
            MagiaNaturalis.LOGGER.info("Item: {}", player.inventory.getStackInSlot(0));
            MagiaNaturalis.LOGGER.info("NBT: {}", player.inventory.getStackInSlot(0).stackTagCompound);
        }

        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7,
        float par8, float par9, float par10) {
        if (Platform.isClient()) return false;

        // DO DEV MAGIC HERE !!!
        if (stack.getItemDamage() == 1) {
            TileEntity tile = world.getTileEntity(x, y, z);

            if (tile == null) {
                return false;
            }

            if (tile instanceof TileOwned) {
                TileOwned owned = (TileOwned) tile;
                MagiaNaturalis.LOGGER.info(
                    String.format(
                        "%nX= %d, Y= %d, Z= %d%nOwner: %s%nAccessList: %s",
                        x,
                        y,
                        z,
                        owned.owner,
                        owned.accessList.toString()));
                return true;
            } else if (tile instanceof ArcaneChestBlockEntity) {
                ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) tile;
                MagiaNaturalis.LOGGER.info(
                    String.format(
                        "%nX= %d, Y= %d, Z= %d%nOwner UUID: %s%nPlayer UUID: %s%nAccessList: %s",
                        x,
                        y,
                        z,
                        chest.getOwner()
                            .toString(),
                        player.getGameProfile()
                            .getId(),
                        chest.accessList.toString()));
                return true;
            } else if (tile instanceof TranscribingTableBlockEntity) {
                TranscribingTableBlockEntity table = (TranscribingTableBlockEntity) tile;
                ItemStack stack2 = table.getStackInSlot(0);
                if (stack2.getItem() instanceof ResearchLogItem) {
                    ResearchLogItem log = (ResearchLogItem) stack2.getItem();
                    int i = 6;
                    if (log.getResearchPoint(stack2, Aspect.AIR) < 64) i--;
                    if (log.getResearchPoint(stack2, Aspect.EARTH) < 64) i--;
                    if (log.getResearchPoint(stack2, Aspect.WATER) < 64) i--;
                    if (log.getResearchPoint(stack2, Aspect.FIRE) < 64) i--;
                    if (log.getResearchPoint(stack2, Aspect.ORDER) < 64) i--;
                    if (log.getResearchPoint(stack2, Aspect.ENTROPY) < 64) i--;
                    MagiaNaturalis.LOGGER.info("ResearchLog {} of 6 RP Maxed", i);
                    return true;
                }
            } else if (tile instanceof INode) {
                INode node = (INode) tile;
                if (player.isSneaking()) {
                    node.setNodeType(NodeType.PURE);
                    node.setNodeModifier(NodeModifier.BRIGHT);

                    node.setNodeVisBase(Aspect.FIRE, (short) 1000);
                    node.setNodeVisBase(Aspect.AIR, (short) 1000);
                    node.setNodeVisBase(Aspect.WATER, (short) 1000);
                    node.setNodeVisBase(Aspect.EARTH, (short) 1000);
                    node.setNodeVisBase(Aspect.ENTROPY, (short) 1000);
                    node.setNodeVisBase(Aspect.ORDER, (short) 1000);

                    AspectList aspects = node.getAspects();
                    aspects.merge(Aspect.FIRE, 1000);
                    aspects.merge(Aspect.AIR, 1000);
                    aspects.merge(Aspect.WATER, 1000);
                    aspects.merge(Aspect.EARTH, 1000);
                    aspects.merge(Aspect.ENTROPY, 1000);
                    aspects.merge(Aspect.ORDER, 1000);
                    node.setAspects(aspects);

                    world.markBlockForUpdate(x, y, z);
                }
                MagiaNaturalis.LOGGER.info(
                    "AspectBase: {}",
                    node.getAspectsBase()
                        .visSize());
                return true;
            } else if (tile instanceof TileBanner) {
                MagiaNaturalis.LOGGER.info("Block: {}", world.getBlock(x, y, z));
            }
        }
        // else if(stack.getItemDamage() == 2)
        // {
        // String name = "Lord_Cerberus";
        // UUID uuid = Platform.generateOfflineUUIDforName(name);
        // GameProfile gameprofile = Platform.findGameProfileByName(name);
        // System.out.printf("%n%s UUID: %s", name + "Offline", uuid.toString());
        // System.out.printf("%n%s GameProfile: %s%n", gameprofile.getName(), gameprofile.toString());
        // return true;
        // }

        return false;
    }

}
