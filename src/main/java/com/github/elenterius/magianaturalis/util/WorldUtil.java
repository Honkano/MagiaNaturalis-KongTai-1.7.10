package com.github.elenterius.magianaturalis.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;

import com.github.elenterius.magianaturalis.network.PacketHandler;
import com.github.elenterius.magianaturalis.network.packet.PacketBiomeChange;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil.Shape;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import thaumcraft.common.items.wands.ItemWandCasting;

public class WorldUtil {

    public static List<WorldCoord> plot2DPlane(EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int size) {
        List<WorldCoord> blocks = WorldUtil.plotHelper(
            new WorldCoord(x, y, z),
            new WorldCoord(x, y, z),
            ForgeDirection.getOrientation(side),
            hitX,
            hitY,
            hitZ,
            size);
        WorldCoord a = new WorldCoord(x, y, z);
        WorldCoord b = new WorldCoord(x, y, z);

        if (blocks.size() == 2) {
            a = blocks.get(0);
            b = blocks.get(1);
        }

        return WorldUtil.plot3DCubeArea(player, world, a, b, x, y, z);
    }

    public static List<WorldCoord> plot2DPlaneExtension(EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int size) {
        ForgeDirection face = ForgeDirection.getOrientation(side);
        int id = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        switch (face) {
            case DOWN:
            case UP:
                face = ForgeDirection.getOrientation(Direction.directionToFacing[id]);
                break;

            case EAST:
            case WEST:
                face = ForgeDirection.getOrientation(Direction.directionToFacing[id]);
                if (face == ForgeDirection.EAST || face == ForgeDirection.WEST) face = ForgeDirection.DOWN;
                break;

            case NORTH:
            case SOUTH:
                face = ForgeDirection.getOrientation(Direction.directionToFacing[id]);
                if (face == ForgeDirection.NORTH || face == ForgeDirection.SOUTH) face = ForgeDirection.DOWN;
                break;

            case UNKNOWN:
                break;
            default:
                break;
        }

        List<WorldCoord> blocks = WorldUtil
            .plotHelper(new WorldCoord(x, y, z), new WorldCoord(x, y, z), face, hitX, hitY, hitZ, size);
        WorldCoord a = new WorldCoord(x, y, z);
        WorldCoord b = new WorldCoord(x, y, z);

        if (blocks.size() == 2) {
            a = blocks.get(0);
            b = blocks.get(1);
        }

        return WorldUtil.plot3DCubeArea(player, world, a, b, x, y, z);
    }

    public static List<WorldCoord> plot3DCubeArea(EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int size) {
        ForgeDirection face = ForgeDirection.getOrientation(side);
        List<WorldCoord> blocks = WorldUtil
            .plotHelper(new WorldCoord(x, y, z), new WorldCoord(x, y, z), face, hitX, hitY, hitZ, size);
        WorldCoord a = new WorldCoord(x, y, z);
        WorldCoord b = new WorldCoord(x, y, z);

        if (blocks.size() == 2) {
            a = blocks.get(0);
            b = blocks.get(1);
        }

        a.add(face.getOpposite(), size - 1);

        return WorldUtil.plot3DCubeArea(player, world, a, b, x, y, z);
    }

    public static List<WorldCoord> plot3DCubeArea(EntityPlayer player, World world, WorldCoord P1, WorldCoord P2,
        int ox, int oy, int oz) {
        List<WorldCoord> blocks = new ArrayList<>();

        if (world == null) return blocks;

        Block block1 = world.getBlock(P1.x, P1.y, P1.z);
        Block block2 = world.getBlock(P2.x, P2.y, P2.z);

        if (block1 == null || block2 == null) return blocks;
        if ((player == null) || player instanceof FakePlayer) return blocks;

        ItemStack equippedItemStack = player.getCurrentEquippedItem();
        if (equippedItemStack != null && equippedItemStack.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) equippedItemStack.getItem();
            ItemStack focusStack = wand.getFocusItem(equippedItemStack);

            // Block oBlock = world.getBlock(ox, oy, oz);
            // int oBD = world.getBlockMetadata(ox, oy, oz);

            // Mode lmeta = BuilderFocusUtil.getMeta(focusStack);
            Shape lshape = BuilderFocusUtil.getShape(focusStack);
            int lsize = BuilderFocusUtil.getSize(focusStack);

            int disX = Math.abs(P1.x - P2.x);
            int disY = Math.abs(P1.y - P2.y);
            int disZ = Math.abs(P1.z - P2.z);

            int minX = Math.min(P1.x, P2.x);
            int minY = Math.min(P1.y, P2.y);
            int minZ = Math.min(P1.z, P2.z);

            for (int x = 0; x <= disX; x++) {
                for (int y = 0; y <= disY; y++) {
                    for (int z = 0; z <= disZ; z++) {
                        int tempX = minX + x;
                        int tempY = minY + y;
                        int tempZ = minZ + z;

                        Block lb = world.getBlock(tempX, tempY, tempZ);
                        if (lb.canPlaceBlockAt(world, tempX, tempY, tempZ)) {
                            if (lshape == Shape.SPHERE) {
                                if (lsize % 2 == 0) lsize++;

                                double d0 = Math.abs(x - lsize / 2) + Math.abs(y - lsize / 2) + Math.abs(z - lsize / 2);
                                if (d0 <= lsize / 2) {
                                    blocks.add(new WorldCoord(tempX, tempY, tempZ));
                                }
                            } else {
                                blocks.add(new WorldCoord(tempX, tempY, tempZ));
                            }
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static List<WorldCoord> plotVeinArea(EntityPlayer player, World world, int x, int y, int z, int size) {
        List<WorldCoord> blocklist = new ArrayList<>();

        if (world == null) return blocklist;

        if (player == null || player instanceof FakePlayer) return blocklist;

        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (block == null) return blocklist;

        int blocks = 0;
        List<WorldCoord> next = new LinkedList<>();
        next.add(new WorldCoord(x, y, z));

        while (blocks < size && !next.isEmpty()) {
            List<WorldCoord> temp = next;
            next = new LinkedList<>();

            for (WorldCoord wc : temp) {
                Block lblock = world.getBlock(wc.x, wc.y, wc.z);
                int lmeta = world.getBlockMetadata(wc.x, wc.y, wc.z);

                if (world.canMineBlock(player, wc.x, wc.y, wc.z) && lblock.getMaterial() != Material.air
                    && lblock.getBlockHardness(world, wc.x, wc.y, wc.z) >= 0) if (lblock == block && lmeta == meta) {
                        blocks++;
                        WorldCoord tempWC = new WorldCoord(wc.x, wc.y, wc.z);
                        if (!blocklist.contains(tempWC)) blocklist.add(tempWC);

                        next.add(new WorldCoord(wc.x + 1, wc.y, wc.z));
                        next.add(new WorldCoord(wc.x - 1, wc.y, wc.z));
                        next.add(new WorldCoord(wc.x, wc.y + 1, wc.z));
                        next.add(new WorldCoord(wc.x, wc.y - 1, wc.z));
                        next.add(new WorldCoord(wc.x, wc.y, wc.z + 1));
                        next.add(new WorldCoord(wc.x, wc.y, wc.z - 1));

                        next.add(new WorldCoord(wc.x + 1, wc.y - 1, wc.z + 1));
                        next.add(new WorldCoord(wc.x + 1, wc.y - 1, wc.z - 1));
                        next.add(new WorldCoord(wc.x - 1, wc.y - 1, wc.z - 1));
                        next.add(new WorldCoord(wc.x - 1, wc.y - 1, wc.z + 1));

                        next.add(new WorldCoord(wc.x + 1, wc.y, wc.z + 1));
                        next.add(new WorldCoord(wc.x + 1, wc.y, wc.z - 1));
                        next.add(new WorldCoord(wc.x - 1, wc.y, wc.z - 1));
                        next.add(new WorldCoord(wc.x - 1, wc.y, wc.z + 1));

                        next.add(new WorldCoord(wc.x + 1, wc.y + 1, wc.z + 1));
                        next.add(new WorldCoord(wc.x + 1, wc.y + 1, wc.z - 1));
                        next.add(new WorldCoord(wc.x - 1, wc.y + 1, wc.z - 1));
                        next.add(new WorldCoord(wc.x - 1, wc.y + 1, wc.z + 1));
                    }
            }

        }
        return blocklist;
    }

    public static boolean checkAAB(World world, AxisAlignedBB bounds, int x, int y, int z) {
        if (bounds == null) return true;
        return world.checkNoEntityCollision(bounds.getOffsetBoundingBox(x, y, z));
    }

    public static MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player,
        double blockReachDistance, boolean bool) {
        float f = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f
            + (double) (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight());
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-pitch * 0.017453292F);
        float f6 = MathHelper.sin(-pitch * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;

        double d3 = blockReachDistance;
        if (d3 <= 0) d3 = 5.0D;

        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return world.func_147447_a(vec3, vec31, bool, !bool, false);
    }

    public static void setBiomeAt(World world, int x, int z, BiomeGenBase biome) {
        if (biome == null) return;
        // 【新增】检查 biomeID 是否在合法范围（0-255）
        if (biome.biomeID < 0 || biome.biomeID > 255) return;
        // 【新增】检查客户端是否注册了这个生物群系，防止写入“不存在”的 ID
        if (BiomeGenBase.getBiome(biome.biomeID) == null) return;

        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        byte[] biomeArray = chunk.getBiomeArray();
        biomeArray[((z & 0xF) << 4 | x & 0xF)] = ((byte) (biome.biomeID & 0xFF));
        chunk.setBiomeArray(biomeArray);

        if (Platform.isServer()) {
            PacketHandler.network.sendToAllAround(
                new PacketBiomeChange.BiomeChangeMessage(x, z, (short) biome.biomeID),
                new TargetPoint(world.provider.dimensionId, x, world.getTopSolidOrLiquidBlock(x, z), z, 32D));
        }
    }

    public static boolean resetBiomeAt(World world, int x, int z) {
        BiomeGenBase[] defaultBiome = world.getWorldChunkManager()
            .loadBlockGeneratorData(null, x, z, 1, 1);
        if (defaultBiome != null && defaultBiome[0] != null) {
            if (world.getBiomeGenForCoords(x, z) != defaultBiome[0]) {
                WorldUtil.setBiomeAt(world, x, z, defaultBiome[0]);
                return true;
            }
        }
        return false;
    }

    public static void setBiomeAtChunk(World world, Chunk chunk, BiomeGenBase biome) {
        if (biome == null) return;
        byte[] biomeArray = chunk.getBiomeArray();
        Arrays.fill(biomeArray, ((byte) (biome.biomeID & 0xFF)));
        chunk.setBiomeArray(biomeArray);
    }

    private static List<WorldCoord> plotHelper(WorldCoord P1, WorldCoord P2, ForgeDirection side, float hitX,
        float hitY, float hitZ, int size) {
        List<WorldCoord> blocks = new ArrayList<>();
        int minmax = 0;
        if (size % 2 == 0) {
            minmax = size / 2;
            int X = hitX <= 0.5f ? -1 : 1;
            int Y = hitY <= 0.5f ? -1 : 1;
            int Z = hitZ <= 0.5f ? -1 : 1;
            switch (side) {
                case UP:
                case DOWN:
                    if (X > 0 && Z > 0) {
                        P1.add(minmax, 0, minmax);
                        P2.subtract(minmax - X, 0, minmax - Z);
                    }

                    if (Z > 0 && X < 0) {
                        P1.add(minmax + X, 0, minmax);
                        P2.subtract(minmax, 0, minmax - Z);
                    }

                    if (Z < 0 && X > 0) {
                        P1.add(minmax, 0, minmax + Z);
                        P2.subtract(minmax - X, 0, minmax);
                    }

                    if (X < 0 && Z < 0) {
                        P1.add(minmax + X, 0, minmax + Z);
                        P2.subtract(minmax, 0, minmax);
                    }
                    break;
                case NORTH:
                case SOUTH:
                    if (X > 0 && Y > 0) {
                        P1.add(minmax, minmax, 0);
                        P2.subtract(minmax - X, minmax - Y, 0);
                    }
                    if (Y > 0 && X < 0) {
                        P1.add(minmax + X, minmax, 0);
                        P2.subtract(minmax, minmax - Y, 0);
                    }
                    if (Y < 0 && X > 0) {
                        P1.add(minmax, minmax + Y, 0);
                        P2.subtract(minmax - X, minmax, 0);
                    }
                    if (X < 0 && Y < 0) {
                        P1.add(minmax + X, minmax + Y, 0);
                        P2.subtract(minmax, minmax, 0);
                    }
                    break;
                case EAST:
                case WEST:
                    if (Z > 0 && Y > 0) {
                        P1.add(0, minmax, minmax);
                        P2.subtract(0, minmax - Y, minmax - Z);
                    }
                    if (Y > 0 && Z < 0) {
                        P1.add(0, minmax, minmax + Z);
                        P2.subtract(0, minmax - Y, minmax);
                    }
                    if (Y < 0 && Z > 0) {
                        P1.add(0, minmax + Y, minmax);
                        P2.subtract(0, minmax, minmax - Z);
                    }
                    if (Z < 0 && Y < 0) {
                        P1.add(0, minmax + Y, minmax + Z);
                        P2.subtract(0, minmax, minmax);
                    }
                case UNKNOWN:
                    break;
                default:
                    break;
            }
        } else {
            minmax = (size - 1) / 2;
            switch (side) {
                case DOWN:
                case UP:
                    P1.add(minmax, 0, minmax);
                    P2.subtract(minmax, 0, minmax);
                    break;
                case EAST:
                case WEST:
                    P1.add(0, minmax, minmax);
                    P2.subtract(0, minmax, minmax);
                    break;
                case NORTH:
                case SOUTH:
                    P1.add(minmax, minmax, 0);
                    P2.subtract(minmax, minmax, 0);
                    break;
                default:
                    break;
            }
        }
        blocks.add(P1);
        blocks.add(P2);
        return blocks;
    }

}
