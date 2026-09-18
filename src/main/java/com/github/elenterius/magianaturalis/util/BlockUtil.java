package com.github.elenterius.magianaturalis.util;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import thaumcraft.common.blocks.BlockMagicalLeaves;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.lib.utils.EntityUtils;

public class BlockUtil {

    public static boolean harvestBlock(World world, EntityPlayer player, int x, int y, int z, boolean followItem,
        int abundance, int color) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (block == null || block.getBlockHardness(world, x, y, z) < 0.0F) return false;

        if ((player.capabilities.isCreativeMode || block.canHarvestBlock(player, meta))
            && emulateBlockHarvestByPlayer(world, x, y, z, player)) {
            world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
            block.harvestBlock(world, player, x, y, z, meta);

            if (!player.capabilities.isCreativeMode && !EnchantmentHelper.getSilkTouchModifier(player)
                && abundance > 0) {
                // TODO: Improve for Silverwood & Greatwood
                // block.harvestBlock(world, player, x, y, z, meta);

                if (block instanceof BlockMagicalLeaves) {
                    for (int i = 0; i < abundance + 1; i++) block.dropBlockAsItem(world, x, y, z, meta, 0);
                } else block
                    .dropBlockAsItem(world, x, y, z, meta, EnchantmentHelper.getFortuneModifier(player) + abundance); // player
                                                                                                                      // will
                                                                                                                      // be
                                                                                                                      // zero
                                                                                                                      // in
                                                                                                                      // ForgeEvent
                                                                                                                      // &
                                                                                                                      // drops
                                                                                                                      // will
                                                                                                                      // happen
                                                                                                                      // unnoticed
                                                                                                                      // from
                                                                                                                      // stats
                                                                                                                      // etc
            }

            if (followItem) {
                ArrayList<Entity> entities = EntityUtils
                    .getEntitiesInRange(world, x + 0.5D, y + 0.5D, z + 0.5D, player, EntityItem.class, 2.0D);
                if (!entities.isEmpty()) {
                    for (Entity entity : entities) if (!entity.isDead && entity instanceof EntityItem
                        && entity.ticksExisted == 0
                        && !(entity instanceof EntityFollowingItem)) {
                            EntityFollowingItem followingItem = new EntityFollowingItem(
                                world,
                                entity.posX,
                                entity.posY,
                                entity.posZ,
                                ((EntityItem) entity).getEntityItem()
                                    .copy(),
                                player,
                                color);
                            followingItem.motionX = entity.motionX;
                            followingItem.motionY = entity.motionY;
                            followingItem.motionZ = entity.motionZ;
                            world.spawnEntityInWorld(followingItem);
                            entity.setDead();
                        }
                }
            }
            return true;
        }
        return false;
    }

    public static boolean emulateBlockHarvestByPlayer(World world, int x, int y, int z, EntityPlayer player) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.air) return false;

        int meta = world.getBlockMetadata(x, y, z);
        block.onBlockHarvested(world, x, y, z, meta, player);
        if (block.removedByPlayer(world, player, x, y, z, false)) {
            block.onBlockDestroyedByPlayer(world, x, y, z, meta);
        }
        return true;
    }

}
