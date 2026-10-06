package com.github.elenterius.magianaturalis;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.elenterius.magianaturalis.api.Aspects;
import com.github.elenterius.magianaturalis.init.CommonSetup;
import com.github.elenterius.magianaturalis.network.PacketHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import thaumcraft.common.Thaumcraft;

@Mod(
    name = "Magia Naturalis",
    modid = MagiaNaturalis.MOD_ID,
    version = MagiaNaturalis.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:Thaumcraft;required-after:NotEnoughItems")
public class MagiaNaturalis {

    public static final String MOD_ID = "magianaturalis";
    public static final String VERSION = "1.0.0";

    public static final String COMMON_PROXY = "com.github.elenterius.magianaturalis.init.CommonSetup";
    public static final String CLIENT_PROXY = "com.github.elenterius.magianaturalis.init.client.ClientSetup";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static MagiaNaturalis instance;

    @SidedProxy(clientSide = MagiaNaturalis.CLIENT_PROXY, serverSide = MagiaNaturalis.COMMON_PROXY)
    public static CommonSetup proxy;
    public static thaumcraft.common.CommonProxy proxyTC4;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxyTC4 = Thaumcraft.proxy;

        // ★ 触发 Aspects 类加载，自动注册三个自创要素到 TC4
        Aspects.init();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.initPackets();
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    public static String translationKey(String name) {
        return MagiaNaturalis.MOD_ID + "." + name;
    }

    public static String rlString(String path) {
        return MagiaNaturalis.MOD_ID + ":" + path;
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MagiaNaturalis.MOD_ID, path);
    }

    public static void lightning(World world, double sx, double sy, double sz, double ex, double ey, double ez, int dur,
        float curve, int speed, int type) {
        proxy.lightning(world, sx, sy, sz, ex, ey, ez, dur, curve, speed, type);
    }

}