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

    /** 白名单：明确允许被封进罐子的实体 ID（优先级最高，可覆盖 Boss 判定） */
    public static String[] jarWhitelist = new String[0];

    /** 黑名单：明确禁止被封进罐子的实体 ID（比白名单优先级低，但比默认规则高） */
    public static String[] jarBlacklist = new String[] { "EnderDragon", "WitherBoss" };

    public static void init(File file) {
        config = new Configuration(file);
        config.load();

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

        if (config.hasChanged()) config.save();
    }
}
