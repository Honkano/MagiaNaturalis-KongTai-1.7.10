package com.github.elenterius.magianaturalis.util.access;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.init.MNBlocks;
import com.github.elenterius.magianaturalis.util.Platform;
import com.mojang.authlib.GameProfile;

import thaumcraft.common.blocks.BlockArcaneDoor;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileOwned;

public final class UserAccessUtil {

    private UserAccessUtil() {}

    /**
     * Returns A byte representing on what table the operation was successful. Is without Chat Message or Sound-Effect.
     * 0 = Failed; 1 = Arcane Chest; 2 = Arcane Door; 3 = TileOwned
     */
    public static byte addPlayerToAccessList(GameProfile gameProfile, byte accesLevel, World world, int x, int y,
        int z) {
        Block block = world.getBlock(x, y, z);
        if (block == null) return 0; // fail-safe
        int metadata = world.getBlockMetadata(x, y, z);

        if (block == MNBlocks.arcaneChest) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile == null) return 0;
            if (tile instanceof ArcaneChestBlockEntity) {
                ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) tile;
                if (!chest.getOwner()
                    .equals(gameProfile.getId())) {
                    if (!chest.accessList.contains(new UserAccess(gameProfile.getId(), (byte) 1))
                        && !chest.accessList.contains(new UserAccess(gameProfile.getId(), (byte) 2))
                        && !chest.accessList.contains(new UserAccess(gameProfile.getId(), (byte) 0))) {
                        chest.accessList.add(new UserAccess(gameProfile.getId(), accesLevel));
                        world.markBlockForUpdate(x, y, z);
                        return 1;
                    }
                }
            }
        } else if (block == ConfigBlocks.blockArcaneDoor) {
            TileEntity[] tiles = new TileEntity[2];
            tiles[0] = world.getTileEntity(x, y, z);
            int offset = 1;
            int magic = ((BlockArcaneDoor) block).getFullMetadata(world, x, y, z);
            if ((magic & 0x8) != 0) {
                offset = -1;
            } // 8 is top and 0 is bottom
            tiles[1] = world.getTileEntity(x, y + offset, z);
            for (byte b = 0; b < 2; b++) {
                if (tiles[b] != null && tiles[b] instanceof TileOwned) {
                    TileOwned owned = (TileOwned) tiles[b];
                    if (!owned.owner.equals(gameProfile.getName())
                        && !owned.accessList.contains("0" + gameProfile.getName())
                        && !owned.accessList.contains("1" + gameProfile.getName())) {
                        switch (accesLevel) {
                            case 2:
                                accesLevel = 1;
                                break;
                            case 1:
                                accesLevel = 0;
                                break;
                        }
                        owned.accessList.add(accesLevel + gameProfile.getName());
                    }
                }
            }
            world.markBlockForUpdate(x, y, z);
            world.markBlockForUpdate(x, y + offset, z);
            return 2;
        } else if ((block == ConfigBlocks.blockWoodenDevice && (metadata == 2 || metadata == 3))
            || block != ConfigBlocks.blockWoodenDevice) {
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile == null) return -1;
                if (tile instanceof TileOwned) {
                    TileOwned owned = (TileOwned) tile;
                    if (!owned.owner.equals(gameProfile.getName())
                        && !owned.accessList.contains("0" + gameProfile.getName())
                        && !owned.accessList.contains("1" + gameProfile.getName())) {
                        switch (accesLevel) {
                            case 2:
                                accesLevel = 1;
                                break;
                            case 1:
                                accesLevel = 0;
                                break;
                        }
                        owned.accessList.add(accesLevel + gameProfile.getName());
                        world.markBlockForUpdate(x, y, z);
                        return 3;
                    }
                }
            }
        return 0;
    }

    /**
     * Adds a player and displays a chat message and plays the TC key sound effect when the operation was successful.
     */
    public static boolean addPlayerToAccessList(EntityPlayer player, byte accessLevel, World world, int x, int y,
        int z) {
        byte result = UserAccessUtil.addPlayerToAccessList(player.getGameProfile(), accessLevel, world, x, y, z);
        if (result-- > 0) {
            world.playSoundEffect(x, y, z, "thaumcraft:key", 1.0F, 0.9F);
            String[] word = { "chest", "door", "misc" };
            player.addChatMessage(
                new ChatComponentText(
                    new StringBuilder().append(EnumChatFormatting.DARK_PURPLE)
                        .append(Platform.translate("chat.magianaturalis.key.access." + word[result]))
                        .toString()));
            return true;
        }
        return false;
    }

}
