package com.github.elenterius.magianaturalis.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;

import baubles.api.BaublesApi;

/**
 * 【面具工具类】
 *
 * 负责从 Baubles 项链栏里找到面具，并读出当前态度。
 */
public class MaskHelper {

    // ==================== 旧面具 meta ====================
    public static final int MASK_GRINNING_DEVIL = 0;
    public static final int MASK_ANGRY_GHOST = 1;
    public static final int MASK_SIPPING_FIEND = 2;

    // ==================== 态度 face 索引 ====================
    public static final int FACE_NO_ATTITUDE = 0;
    public static final int FACE_FRIENDLY = 1;
    public static final int FACE_SOLEMN = 2;
    public static final int FACE_HIDING = 3;

    // ==================================================
    // 【判断】玩家是否戴着指定 meta 的面具
    // ==================================================
    public static boolean hasMask(EntityPlayer player, int meta) {
        if (player == null) return false;
        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles == null) return false;
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemMask) {
                    if (stack.getItemDamage() == meta) return true;
                }
            }
        } catch (Exception e) {
            // 忽略
        }
        return false;
    }

    // ==================================================
    // 【获取】玩家当前戴着的态度面具 ItemStack
    // 没有则返回 null
    // ==================================================
    public static ItemStack getAttitudeMask(EntityPlayer player) {
        if (player == null) return null;
        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles == null) return null;
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemMask
                    && stack.getItemDamage() == ItemMask.META_ATTITUDE) {
                    return stack;
                }
            }
        } catch (Exception e) {
            // 忽略
        }
        return null;
    }

    // ==================================================
    // 【获取】玩家当前的态度 face
    // 没戴态度面具 → -1
    // ==================================================
    public static int getAttitudeFace(EntityPlayer player) {
        ItemStack mask = getAttitudeMask(player);
        if (mask == null) return -1;
        return ItemMask.getAttitudeFace(mask);
    }

    public static void markDirty(EntityPlayer player) {
        if (player == null) return;
        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles != null) baubles.markDirty();
        } catch (Exception e) {}
    }

    /** 获取原始面具 ItemStack */
    public static ItemStack getPrimalMask(EntityPlayer player) {
        if (player == null) return null;
        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles == null) return null;
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemMask
                    && stack.getItemDamage() == ItemMask.META_PRIMAL) {
                    return stack;
                }
            }
        } catch (Exception e) {}
        return null;
    }

    /** 玩家当前原始面具的 face，没戴返回 -1 */
    public static int getPrimalFace(EntityPlayer player) {
        ItemStack mask = getPrimalMask(player);
        return mask == null ? -1 : ItemMask.getPrimalFace(mask);
    }

}
