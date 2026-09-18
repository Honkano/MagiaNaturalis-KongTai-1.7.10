package com.github.elenterius.magianaturalis.util.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import thaumcraft.common.config.ConfigBlocks;

public class BlockMorpher {

    // TODO: REFACTOR ALL THIS STUFF!
    public static boolean setMorphOrRotation(World world, int x, int y, int z, Block block, int meta,
        boolean isPlayerSneaking) {
        boolean IDChange = false;

        if (block == Blocks.log || block == Blocks.log2) {
            if (!isPlayerSneaking) {
                // ChangeWoodType&KeepRotation
                int rot = 0;

                if (meta > 11) {
                    rot = 3;
                } else if (meta > 7) {
                    rot = 2;
                } else if (meta > 3) {
                    rot = 1;
                }

                meta += 1;
                switch (rot) {
                    case 0:
                        if (meta > 3) meta = 0;
                    case 1:
                        if (meta > 7) meta = 4;
                    case 2:
                        if (meta > 11) meta = 8;
                    case 3:
                        if (meta > 15) meta = 12;
                }
            } else {
                // ChangeRotation
                meta += 4;
                if (meta > 15) meta = 0;
            }
        }

        if (block == Blocks.stonebrick) {
            meta += 1;
            if (meta > 3) meta = 0;
        }

        if (block == Blocks.planks) {
            meta += 1;
            if (meta > 5) meta = 0;
        }

        if (block == Blocks.planks) {
            meta += 1;
            if (meta > 5) meta = 0;
        }

        if (block == Blocks.wool || block == Blocks.stained_hardened_clay || block == Blocks.carpet) {
            meta += 1;
            if (meta > 15) meta = 0;
        }

        if (block instanceof BlockStairs) {
            if (!isPlayerSneaking) {
                // ChangeWoodType
                if (block == Blocks.oak_stairs || block == Blocks.spruce_stairs
                    || block == Blocks.birch_stairs
                    || block == Blocks.jungle_stairs
                    || block == Blocks.acacia_stairs
                    || block == Blocks.dark_oak_stairs) {
                    Block oak = Blocks.oak_stairs;
                    Block spruce = Blocks.spruce_stairs;
                    Block birch = Blocks.birch_stairs;
                    Block jungle = Blocks.jungle_stairs;
                    Block acacia = Blocks.acacia_stairs;
                    Block dark = Blocks.dark_oak_stairs;
                    if (block == oak) block = spruce;
                    else if (block == spruce) block = birch;
                    else if (block == birch) block = jungle;
                    else if (block == jungle) block = acacia;
                    else if (block == acacia) block = dark;
                    else block = oak;
                    IDChange = true;
                }

            } else {
                // ChangeRotation
                meta += 1;
                if (meta > 7) meta = 0;
            }

        }

        if (block instanceof BlockSlab) {
            if (!isPlayerSneaking) {
                // SmoothStoneSlab&SmoothSandstoneSlab
                if (block == Blocks.stone_slab) {
                    switch (meta) {
                        case 0:
                            meta = 8;
                            break;
                        case 1:
                            meta = 9;
                            break;
                        case 8:
                            meta = 0;
                            break;
                        case 9:
                            meta = 1;
                            break;
                    }
                }
                if (block == Blocks.wooden_slab) {
                    // ChangeWoodType&KeepLocation
                    int loc = 0;
                    if (meta > 3) {
                        loc = 1;
                    }
                    meta += 1;
                    switch (loc) {
                        case 0:
                            if (meta > 3) meta = 0;
                        case 1:
                            if (meta > 12) meta = 9;
                    }
                }
            } else {
                // ChangeSlabLocation
                if (meta < 9) meta += 8;
                else if (meta > 8) meta -= 8;
            }
        }

        if (block == Blocks.sandstone || block == Blocks.quartz_block) {
            if (!isPlayerSneaking) {
                // ChangeCarvingType
                meta += 1;
                if (meta > 2) meta = 0;
            } else if (block == Blocks.quartz_block) {
                // RotatePillar
                if (meta > 1) meta += 1;
                if (meta > 4) meta = 2;
            }
        }

        if (block == ConfigBlocks.blockMagicalLog) {
            meta += 4;
            if (meta > 13) meta -= 16;
        }

        if (block == Blocks.cobblestone || block == Blocks.cobblestone_wall
            || block == Blocks.dirt
            || block == Blocks.mossy_cobblestone
            || block == Blocks.grass) {
            IDChange = true;
            Block cobble = Blocks.cobblestone;
            Block cobbleM = Blocks.mossy_cobblestone;
            Block wall = Blocks.cobblestone_wall;
            Block dirt = Blocks.dirt;
            Block grass = Blocks.grass;

            if (block == cobble) block = cobbleM;
            else if (block == cobbleM) block = cobble;

            if (block == dirt) block = grass;
            else if (block == grass) block = dirt;

            if (block == wall) {
                meta += 1;
                if (meta > 1) meta = 0;
                IDChange = false;
            }
        }

        if (block != world.getBlock(x, y, z) || meta != world.getBlockMetadata(x, y, z)) {
            if (!IDChange) {
                world.setBlockMetadataWithNotify(x, y, z, meta, -2);
            } else {
                // Only Called by Dirt/Grass & Cobble/MossyCobble & Stairs
                world.setBlock(x, y, z, block, meta, -2);
            }
            return true;
        }
        return false;
    }

}
