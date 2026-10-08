package com.github.elenterius.magianaturalis.api;

import net.minecraft.util.ResourceLocation;

import thaumcraft.api.aspects.Aspect;

/**
 * 自然魔法的自创要素。
 *
 * 命名规则：短、独特、无下划线、拉丁/神话风。
 * 显示名走 TC4 内置的 WordUtils.capitalizeFully(tag)，所以 tag 本身就是显示名。
 * 中文/其他语言走 tc.aspect.<tag> 语言文件。
 */
public final class Aspects {

    private Aspects() {}

    // ========== 地球：大地母亲 ==========
    public static final Aspect GAIA = new Aspect(
        "gaia",
        0x3A7DC9,
        new Aspect[] { Aspect.EARTH, Aspect.MIND },
        new ResourceLocation("magianaturalis", "textures/aspects/gaia.png"),
        1);

    // ========== 自然：本模组的核心 ==========
    public static final Aspect NATURA = new Aspect(
        "natura",
        0x00BCD4,
        new Aspect[] { Aspect.MIND, Aspect.TREE },
        new ResourceLocation("magianaturalis", "textures/aspects/natura.png"),
        1);

    // ========== 花：花之女神 ==========
    public static final Aspect FLORA = new Aspect(
        "flora",
        0xFF6B9D,
        new Aspect[] { Aspect.SENSES, Aspect.MIND },
        new ResourceLocation("magianaturalis", "textures/aspects/flora.png"),
        1);

    /**
     * 所有自定义要素。以后加新要素，在这里 add 一个就行。
     * Mixin 会自动遍历这个数组，把玩家已发现的追加到研究页。
     */
    public static final Aspect[] ALL_CUSTOM = { GAIA, NATURA, FLORA };

    public static void init() {
        // 空方法，仅为了让 JVM 加载这个类
    }
}
