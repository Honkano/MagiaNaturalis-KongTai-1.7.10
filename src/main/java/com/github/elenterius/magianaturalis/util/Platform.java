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
