package com.github.elenterius.magianaturalis.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.util.MaskHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ScanResult;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;

@SideOnly(Side.CLIENT)
public class MaskHUDHandler {

    private static final int FACE_PRIMAL = 0;
    private static int tickCounter = 0;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) return;
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        ItemStack mask = MaskHelper.getPrimalMask(player);
        if (mask == null) return;
        if (ItemMask.getPrimalFace(mask) != FACE_PRIMAL) return;

        // ★ 扫描视线前方（不检查"是否可以扫"，只判断有没有目标）
        ScanResult scan = doScan(player);
        if (scan == null) return;

        // ★ 每 5 tick 生成一次粒子
        tickCounter++;
        if (tickCounter % 5 == 0) {
            spawnParticles(player, scan);
        }

        // ★ 已扫过 → 显示 HUD
        if (!ScanManager.hasBeenScanned(player, scan)) return;

        String name = getScanName(scan);
        AspectList aspects = ScanManager.getScanAspects(scan, player.worldObj);
        if ((name == null || name.isEmpty())
            && (aspects == null || aspects.size() == 0)) return;

        renderHUD(mc, event.resolution, name, aspects);
    }

    // ==================================================
    // 【扫描逻辑】找视线前方目标（不做有效性检查）
    // ==================================================
    private static ScanResult doScan(EntityPlayer p) {
        Entity pointed = EntityUtils.getPointedEntity(p.worldObj, p, 0.5D, 10.0D, 0.0F, true);
        if (pointed != null) {
            return new ScanResult((byte) 2, 0, 0, pointed, "");
        }

        MovingObjectPosition mop = EntityUtils.getMovingObjectPositionFromPlayer(p.worldObj, p, true);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block b = p.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
            if (b == Blocks.air) return null;

            int md = b.getDamageValue(p.worldObj, mop.blockX, mop.blockY, mop.blockZ);
            ItemStack is = b.getPickBlock(mop, p.worldObj, mop.blockX, mop.blockY, mop.blockZ);
            try {
                if (is == null) is = BlockUtils.createStackedBlock(b, md);
            } catch (Exception e) {}

            try {
                if (is == null) {
                    return new ScanResult((byte) 1, Block.getIdFromBlock(b), md, null, "");
                } else {
                    return new ScanResult((byte) 1, Item.getIdFromItem(is.getItem()),
                        is.getItemDamage(), null, "");
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    // ==================================================
    // 【粒子】持续生成符文
    // ==================================================
    private static void spawnParticles(EntityPlayer p, ScanResult scan) {
        try {
            if (scan.type == 2 && scan.entity != null) {
                Thaumcraft.proxy.blockRunes(p.worldObj,
                    scan.entity.posX - 0.5D,
                    scan.entity.posY + scan.entity.height / 2.0D,
                    scan.entity.posZ - 0.5D,
                    0.3F + p.worldObj.rand.nextFloat() * 0.7F, 0.0F,
                    0.3F + p.worldObj.rand.nextFloat() * 0.7F,
                    15, 0.03F);
            } else if (scan.type == 1) {
                MovingObjectPosition mop = EntityUtils.getMovingObjectPositionFromPlayer(p.worldObj, p, true);
                if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    Thaumcraft.proxy.blockRunes(p.worldObj,
                        mop.blockX, mop.blockY + 0.25D, mop.blockZ,
                        0.3F + p.worldObj.rand.nextFloat() * 0.7F, 0.0F,
                        0.3F + p.worldObj.rand.nextFloat() * 0.7F,
                        15, 0.03F);
                }
            }
        } catch (Exception e) {}
    }

    // ==================================================
    // 【目标名字】
    // ==================================================
    private static String getScanName(ScanResult scan) {
        try {
            if (scan.type == 1) {
                ItemStack stack = new ItemStack(Item.getItemById(scan.id), 1, scan.meta);
                if (stack.getItem() != null) return stack.getDisplayName();
            } else if (scan.type == 2 && scan.entity != null) {
                if (scan.entity instanceof EntityItem) {
                    return ((EntityItem) scan.entity).getEntityItem().getDisplayName();
                }
                return scan.entity.getCommandSenderName();
            }
        } catch (Exception e) {}
        return null;
    }

    // ==================================================
    // 【渲染 HUD】名字 + 源质，在屏幕中央上方
    // ==================================================
    private static void renderHUD(Minecraft mc, ScaledResolution res,
                                  String name, AspectList aspects) {
        int midX = res.getScaledWidth() / 2;
        int midY = res.getScaledHeight() / 2;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // --- 名字（在准星上方 30 像素）---
        if (name != null && !name.isEmpty()) {
            FontRenderer fr = mc.fontRenderer;
            int w = fr.getStringWidth(name);
            fr.drawStringWithShadow(name, midX - w / 2, midY - 45, 0xFFFFFF);
        }

        // --- 源质图标（名字下方一排）---
        if (aspects != null && aspects.size() > 0) {
            Aspect[] arr = aspects.getAspectsSorted();
            int count = Math.min(arr.length, 8);
            int spacing = 20;
            int totalW = count * spacing;
            int startX = midX - totalW / 2 + spacing / 2;
            int y = midY - 30;

            for (int i = 0; i < count; i++) {
                Aspect aspect = arr[i];
                if (aspect == null) continue;

                GL11.glPushMatrix();
                // 从"GUI 中心"平移到"图标位置"
                // UtilsFX.drawTag 画的是 16×16 方块
                GL11.glTranslatef(startX + i * spacing - 8, y, 0);
                UtilsFX.drawTag(0, 0, aspect, aspects.getAmount(aspect),
                    0, 0.0D, 1, 1.0F, false);
                GL11.glPopMatrix();
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }
}