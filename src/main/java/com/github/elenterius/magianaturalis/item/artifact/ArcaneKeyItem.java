package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;
import java.util.UUID;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.util.NBTUtil;
import com.github.elenterius.magianaturalis.util.Platform;
import com.github.elenterius.magianaturalis.util.access.UserAccess;
import com.github.elenterius.magianaturalis.util.access.UserAccessUtil;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.TileOwned;

public class ArcaneKeyItem extends Item {

    // TODO: IMPLEMENT OFFLINE PLAYER ADDING - WITH ANVIL? HOW WILL THAT WORK?
    public ArcaneKeyItem() {
        super();
        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);
        list.add(
            EnumChatFormatting.DARK_PURPLE + Platform.translate("flavor.magianaturalis.key." + stack.getItemDamage()));

        NBTTagCompound data = NBTUtil.getOrCreate(stack);

        if (data.hasKey("owner") && data.hasKey("ownerNick")) {
            list.add("");
            list.add("Soulbound to " + data.getString("ownerNick"));
        }
        if (data.hasKey("forger") && data.hasKey("forgerNick")) {
            if (!data.hasKey("owner") && !data.hasKey("ownerNick")) {
                list.add("");
            }
            list.add("Forged by " + data.getString("forgerNick"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(this, 1, 0)); // This Key grants Access for the Bound Player to any Tile owned by the Key
                                             // Forger
        list.add(new ItemStack(this, 1, 1)); // This Key adds a list of players to the access list of the Tile owned by
                                             // the Bound Player or Key Forger
    }

    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.uncommon;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getTagCompound() != null && (stack.getTagCompound()
            .hasKey("forger")
            || stack.getTagCompound()
                .hasKey("owner"));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getItemDamage();
    }

    public UUID getBoundPlayer(ItemStack stack) {
        if (stack == null) return null;
        return UUID.fromString(
            NBTUtil.getOrCreate(stack)
                .getString("owner"));
    }

    public boolean setBoundPlayer(ItemStack stack, GameProfile gameprofile) {
        if (stack == null) return false;
        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        data.setString(
            "owner",
            gameprofile.getId()
                .toString());
        data.setString("ownerNick", gameprofile.getName());
        return true;
    }

    public UUID getKeyForger(ItemStack stack) {
        if (stack == null) return null;
        return UUID.fromString(
            NBTUtil.getOrCreate(stack)
                .getString("forger"));
    }

    public boolean setKeyForger(ItemStack stack, GameProfile gameprofile) {
        if (stack == null) return false;
        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        data.setString(
            "forger",
            gameprofile.getId()
                .toString());
        data.setString("forgerNick", gameprofile.getName());
        return true;
    }

    public byte getAccessLevel(ItemStack stack) {
        if (stack == null) return 0;
        return NBTUtil.getOrCreate(stack)
            .getByte("accessLevel");
    }

    public boolean setAccessLevel(ItemStack stack, byte level) {
        if (stack == null) return false;
        NBTUtil.getOrCreate(stack)
            .setByte("accessLevel", level);
        return true;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        setKeyForger(stack, player.getGameProfile());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Platform.isServer() && player.isSneaking()) {
            NBTTagCompound data = NBTUtil.getOrCreate(stack);
            if (!data.hasKey("forger")) {
                setKeyForger(stack, player.getGameProfile());
                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.DARK_PURPLE + String.format(
                            Platform.translate("chat.magianaturalis.key.owner"),
                            player.getGameProfile()
                                .getName())));
            } else if (!data.hasKey("owner")) {
                setBoundPlayer(stack, player.getGameProfile());
                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.DARK_PURPLE + String.format(
                            Platform.translate("chat.magianaturalis.key.boundplayer.1"),
                            player.getGameProfile()
                                .getName())));
            }
            return stack;
        }
        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (Platform.isServer()) {
            if (stack.getItemDamage() == 0) // This Key grants Access for the Bound Player to any Tile owned by the Key
                                            // Forger
            {
                NBTTagCompound data = NBTUtil.getOrCreate(stack);
                if (!data.hasKey("forger")) {
                    // setKeyForger(stack, player.getGameProfile());
                    return false;
                }

                if (data.hasKey("owner")) {
                    if (!(UUID.fromString(data.getString("owner"))
                        .equals(getBoundPlayer(stack)))) {
                        return false;
                    }
                }

                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile == null) return false;

                boolean bool = false;
                if (tile instanceof ArcaneChestBlockEntity) {
                    bool = ((ArcaneChestBlockEntity) tile).getOwner()
                        .equals(getKeyForger(stack));
                } else if (tile instanceof TileOwned) {
                    if (!data.hasKey("forgerNick")) return false;
                    bool = ((TileOwned) tile).owner.equals(
                        stack.getTagCompound()
                            .getString("forgerNick"));
                }

                if (bool) UserAccessUtil.addPlayerToAccessList(player, getAccessLevel(stack), world, x, y, z);
                return bool;
            } else if (stack.getItemDamage() == 1) // This Key adds a list of players to the access list of the Tile
                                                   // owned by the Bound Player or Key Forger
            {
                NBTTagCompound data = NBTUtil.getOrCreate(stack);
                boolean hasAccess = false;

                if (data.hasKey("owner")) {
                    if (!(UUID.fromString(data.getString("owner"))
                        .equals(getBoundPlayer(stack)))) {
                        hasAccess = false;
                    }
                }

                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile == null) return false;

                if (tile instanceof ArcaneChestBlockEntity) {
                    ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) tile;
                    if (player.getGameProfile()
                        .getId()
                        .equals(chest.getOwner())) {
                        hasAccess = true;
                    } else {
                        hasAccess = chest.accessList.contains(new UserAccess(player.getUniqueID(), (byte) 1));
                    }
                } else if (tile instanceof TileOwned) {
                    TileOwned owned = (TileOwned) tile;
                    if (player.getGameProfile()
                        .getName()
                        .equals(owned.owner)) {
                        hasAccess = true;
                    } else {
                        hasAccess = owned.accessList.contains(
                            1 + player.getGameProfile()
                                .getName());
                    }
                }
                if (!hasAccess) return false;

                if (!data.hasKey("AccessList")) return false;
                NBTTagList accessList = data.getTagList("AccessList", NBT.TAG_COMPOUND);
                for (int i = 0; i < accessList.tagCount(); i++) {
                    NBTTagCompound tempData = accessList.getCompoundTagAt(i);
                    UserAccessUtil.addPlayerToAccessList(
                        new GameProfile(UUID.fromString(tempData.getString("UUID")), tempData.getString("Nick")),
                        tempData.getByte("ArcaneChestType"),
                        world,
                        x,
                        y,
                        z);
                }
                return true;
            }
        }
        return false;
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (stack.getItemDamage() == 0) {
            if (!(entity instanceof EntityPlayer)) return false;
            EntityPlayer player2 = (EntityPlayer) entity;

            if (setBoundPlayer(stack, player2.getGameProfile())) {
                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.DARK_PURPLE + String.format(
                            Platform.translate("chat.magianaturalis.key.boundplayer"),
                            player2.getGameProfile()
                                .getName())));
            }
        } else if (stack.getItemDamage() == 1) {
            if (!(entity instanceof EntityPlayer)) return false;
            EntityPlayer player2 = (EntityPlayer) entity;

            NBTTagCompound data = NBTUtil.getOrCreate(stack);
            NBTTagList accessList;

            if (!data.hasKey("AccessList")) accessList = new NBTTagList();
            else accessList = data.getTagList("AccessList", NBT.TAG_COMPOUND);

            NBTTagCompound tempData = new NBTTagCompound();
            tempData.setString(
                "UUID",
                player2.getGameProfile()
                    .getId()
                    .toString());
            tempData.setString(
                "Nick",
                player2.getGameProfile()
                    .getName());
            tempData.setByte("ArcaneChestType", data.getByte("AccessLevel"));
            accessList.appendTag(tempData);
            data.setTag("AccessList", accessList);

            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.DARK_PURPLE + String.format(
                        Platform.translate("chat.magianaturalis.key.accesslist"),
                        player2.getGameProfile()
                            .getName())));
        }
        return false;
    }

}
