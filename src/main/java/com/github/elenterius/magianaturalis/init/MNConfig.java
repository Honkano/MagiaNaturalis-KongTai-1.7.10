package com.github.elenterius.magianaturalis.init;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * 模组配置文件。
 *
 * 生成路径：.minecraft/config/magianaturalis.cfg
 *
 * 关于实体 ID：
 * 1.7.10 里每个实体都有唯一注册名，例如：
 * "Zombie"、"Creeper"、"EnderDragon"、"WitherBoss"
 * "Thaumcraft.Boss"、"Thaumcraft.GiantBrainyZombie"
 * 查法：
 * 1. 按 F3 准星指向生物，屏幕右侧会显示 Entity ID
 * 2. 已封罐子的 Tooltip 里会显示 "ID: xxx"
 * 3. /summon 命令按 Tab 会列出所有注册名
 */
public class MNConfig {

    public static Configuration config;

    // ======================================================================
    // ===== 原有：封罐实体黑白名单 =====
    // ======================================================================

    /** 白名单：明确允许被封进罐子的实体 ID（优先级最高，可覆盖 Boss 判定） */
    public static String[] jarWhitelist = new String[0];

    /** 黑名单：明确禁止被封进罐子的实体 ID（比白名单优先级低，但比默认规则高） */
    public static String[] jarBlacklist = new String[] { "EnderDragon", "WitherBoss" };

    // ======================================================================
    // ===== 新增：物品槽要素叠加层 =====
    // ======================================================================

    /** 叠加层缩放比例：0.0625 ~ 1.0 */
    private static float slotOverlayScale = 0.5F;

    /** 叠加层透明度：0.0 ~ 1.0 */
    private static float slotOverlayAlpha = 1.0F;

    /** 叠加层在槽内的位置 */
    private static OverlayPosition slotOverlayPosition = OverlayPosition.TOP_RIGHT;

    // 常量
    private static final float MIN_SCALE = 0.0625F;
    private static final float MAX_SCALE = 1.0F;
    private static final float MIN_ALPHA = 0.0F;
    private static final float MAX_ALPHA = 1.0F;
    private static final String CATEGORY_OVERLAY = "aspect_overlay";

    // ======================================================================
    // init
    // ======================================================================

    public static void init(File file) {
        config = new Configuration(file);
        config.load();

        // ----- 原有 -----
        jarWhitelist = config.getStringList(
            "jarWhitelist",
            "prison_jar",
            jarWhitelist,
            "Entities that CAN be captured, even bosses.\n" + "One entity ID per line.\n" + "Example: Thaumcraft.Boss");

        jarBlacklist = config.getStringList(
            "jarBlacklist",
            "prison_jar",
            jarBlacklist,
            "Entities that CANNOT be captured. Takes priority over whitelist.\n" + "One entity ID per line.\n"
                + "Example: EnderDragon");

        // ----- 新增：物品槽要素叠加层 -----
        slotOverlayScale = config.getFloat(
            "scale",
            CATEGORY_OVERLAY,
            slotOverlayScale,
            MIN_SCALE,
            MAX_SCALE,
            "图标缩放比例，1.0 表示占满整个物品槽");

        slotOverlayAlpha = config.getFloat(
            "alpha",
            CATEGORY_OVERLAY,
            slotOverlayAlpha,
            MIN_ALPHA,
            MAX_ALPHA,
            "叠加层图标的透明度");

        String posName = config.getString(
            "position",
            CATEGORY_OVERLAY,
            slotOverlayPosition.name(),
            "叠加层图标在物品槽内的位置：TOP_LEFT / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT / CENTER",
            new String[] { "TOP_LEFT", "TOP_RIGHT", "BOTTOM_LEFT", "BOTTOM_RIGHT", "CENTER" });

        try {
            slotOverlayPosition = OverlayPosition.valueOf(posName);
        } catch (IllegalArgumentException e) {
            slotOverlayPosition = OverlayPosition.TOP_RIGHT;
        }

        if (config.hasChanged()) config.save();
    }

    // ======================================================================
    // ===== 新增：供渲染代码读取 =====
    // ======================================================================

    /** 叠加层实际像素大小（16 乘以缩放比例） */
    public static int getSlotOverlaySize() {
        return Math.max(1, Math.round(16.0F * slotOverlayScale));
    }

    public static float getSlotOverlayAlpha() {
        return slotOverlayAlpha;
    }

    public static OverlayPosition getSlotOverlayPosition() {
        return slotOverlayPosition;
    }

    // ======================================================================
    // ===== 新增：位置枚举 =====
    // ======================================================================

    public enum OverlayPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CENTER;

        public int getOffsetX(int overlaySize) {
            if (this == TOP_RIGHT || this == BOTTOM_RIGHT) return 16 - overlaySize;
            if (this == CENTER) return (16 - overlaySize) / 2;
            return 0;
        }

        public int getOffsetY(int overlaySize) {
            if (this == BOTTOM_LEFT || this == BOTTOM_RIGHT) return 16 - overlaySize;
            if (this == CENTER) return (16 - overlaySize) / 2;
            return 0;
        }
    }
}