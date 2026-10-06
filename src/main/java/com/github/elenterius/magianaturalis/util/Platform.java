package com.github.elenterius.magianaturalis.util;

import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;

public final class Platform {

    public static final UUID NIL_UUID = new UUID(0L, 0L);

    public static boolean isClient() {
        return FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient();
    }

    public static boolean isServer() {
        return FMLCommonHandler.instance()
            .getEffectiveSide()
            .isServer();
    }

    public static World getClientWorld() {
        return MagiaNaturalis.proxyTC4.getClientWorld();
    }

    public static String translate(String str) {
        return StatCollector.translateToLocal(str);
    }

    // ==================================================
    // 【新增】带参数的翻译，用于 %s / %d 占位符
    // ==================================================
    public static String translate(String key, Object... args) {
        return StatCollector.translateToLocalFormatted(key, args);
    }

    // ==================================================
    // 【新增】枚举 → 翻译键 → 翻译文本
    // 例：translateEnum("enum.magianaturalis.shape.", Shape.PLANE_EXTEND)
    //   → 找 "enum.magianaturalis.shape.plane_extend" → "平面延伸"
    // 找不到就返回枚举原名（大写），方便排查
    // ==================================================
    public static String translateEnum(String prefix, Enum<?> value) {
        if (value == null) return "?";
        String key = prefix + value.name()
            .toLowerCase();
        String translated = StatCollector.translateToLocal(key);
        return translated.equals(key) ? value.name() : translated;
    }

    /**
     * Finds the GameProfile for the given Player Name.
     * Only Authenticated Clients/Server can retrieve the online UUID else the UUID will be a offline generated one.
     */
    public static GameProfile findGameProfileByName(String playerName) {
        return MinecraftServer.getServer()
            .func_152358_ax()
            .func_152655_a(playerName);
    }

    public static GameProfile findGameProfileByUUID(UUID uuid) {
        return MinecraftServer.getServer()
            .func_152358_ax()
            .func_152652_a(uuid);
    }

    public static UUID generateOfflineUUIDforName(String playerName) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(Charsets.UTF_8));
    }

}