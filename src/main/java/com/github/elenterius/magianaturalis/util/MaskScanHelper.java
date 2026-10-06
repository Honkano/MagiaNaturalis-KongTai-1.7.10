package com.github.elenterius.magianaturalis.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.research.IScanEventHandler;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketScannedToServer;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;

public final class MaskScanHelper {

    private MaskScanHelper() {}

    public static boolean tryScan(EntityPlayer player) {
        if (player == null || !player.worldObj.isRemote) return false;

        ScanResult scan = doScan(player);
        if (scan == null) return false;

        if (ScanManager.completeScan(player, scan, "@")) {
            PacketHandler.INSTANCE.sendToServer(
                new PacketScannedToServer(scan, player, "@"));

            spawnScanParticles(player, scan);

            // ★★★ 元始脸副作用：扫描扣饥饿 + 扣血 ★★★
            int food = player.getFoodStats().getFoodLevel();
            player.getFoodStats().setFoodLevel(Math.max(0, food - 4));

            float newHealth = player.getHealth() - 4.0F;
            if (newHealth > 0) {
                player.setHealth(newHealth);
            }

            return true;
        }
        return false;
    }

    private static void spawnScanParticles(EntityPlayer p, ScanResult scan) {
        World w = p.worldObj;

        try {
            if (scan.type == 2 && scan.entity != null) {
                // 实体：在眼睛高度画 3 波
                for (int i = 0; i < 3; i++) {
                    Thaumcraft.proxy.blockRunes(w,
                        scan.entity.posX - 0.5D,
                        scan.entity.posY + scan.entity.getEyeHeight() / 2.0F,
                        scan.entity.posZ - 0.5D,
                        0.3F + w.rand.nextFloat() * 0.7F, 0.0F,
                        0.3F + w.rand.nextFloat() * 0.7F,
                        40, 0.03F);
                }
            } else {
                MovingObjectPosition mop = EntityUtils.getMovingObjectPositionFromPlayer(w, p, true);
                if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    // 方块：画 3 波
                    for (int i = 0; i < 3; i++) {
                        Thaumcraft.proxy.blockRunes(w,
                            mop.blockX, mop.blockY + 0.25D, mop.blockZ,
                            0.3F + w.rand.nextFloat() * 0.7F, 0.0F,
                            0.3F + w.rand.nextFloat() * 0.7F,
                            40, 0.03F);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ScanResult doScan(EntityPlayer p) {
        World world = p.worldObj;

        Entity pointed = EntityUtils.getPointedEntity(world, p, 0.5D, 10.0D, 0.0F, true);
        if (pointed != null) {
            ScanResult sr = new ScanResult((byte) 2, 0, 0, pointed, "");
            return ScanManager.isValidScanTarget(p, sr, "@") ? sr : null;
        }

        MovingObjectPosition mop = EntityUtils.getMovingObjectPositionFromPlayer(world, p, true);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            TileEntity te = world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
            if (te instanceof INode) {
                ScanResult sr = new ScanResult((byte) 3, 0, 0, null,
                    "NODE" + ((INode) te).getId());
                return ScanManager.isValidScanTarget(p, sr, "@") ? sr : null;
            }
            Block b = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
            if (b != Blocks.air) {
                int md = b.getDamageValue(world, mop.blockX, mop.blockY, mop.blockZ);
                ItemStack is = b.getPickBlock(mop, world, mop.blockX, mop.blockY, mop.blockZ);
                ScanResult sr;
                try {
                    if (is == null) is = BlockUtils.createStackedBlock(b, md);
                } catch (Exception e) {}
                try {
                    if (is == null) {
                        sr = new ScanResult((byte) 1, Block.getIdFromBlock(b), md, null, "");
                    } else {
                        sr = new ScanResult((byte) 1, Item.getIdFromItem(is.getItem()),
                            is.getItemDamage(), null, "");
                    }
                } catch (Exception e) { return null; }
                return ScanManager.isValidScanTarget(p, sr, "@") ? sr : null;
            }
        }

        for (IScanEventHandler h : ThaumcraftApi.scanEventhandlers) {
            ScanResult sr = h.scanPhenomena(null, world, p);
            if (sr != null) return sr;
        }
        return null;
    }
}