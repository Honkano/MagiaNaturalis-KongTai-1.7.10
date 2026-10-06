package com.github.elenterius.magianaturalis.init;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.easteregg.EntityHerobrineWatcher;
import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;
import com.github.elenterius.magianaturalis.entity.EntityZombieExtended;
import com.github.elenterius.magianaturalis.entity.taint.EntityTaintBreeder;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.EntityRegistry;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public final class MNEntities {

    static void register() {
        int id = 0;

        EntityRegistry.registerGlobalEntityID(
            EntityTaintBreeder.class,
            MagiaNaturalis.translationKey("taint_breeder"),
            EntityRegistry.findGlobalUniqueEntityId(),
            0xFFC0FF,
            0x800090);
        EntityRegistry
            .registerModEntity(EntityTaintBreeder.class, "taint_breeder", id++, MagiaNaturalis.instance, 64, 3, false);

        EntityRegistry
            .registerModEntity(EntityEvilTrunk.class, "evil_trunk", id++, MagiaNaturalis.instance, 64, 3, false);
        EntityRegistry.registerModEntity(
            EntityZombieExtended.class,
            "ferocious_revenant",
            id++,
            MagiaNaturalis.instance,
            64,
            3,
            false);

        // ==================================================
        // 【彩蛋】白瞳凝视者
        // id++ 现在是 3，正好是下一个空号
        // ==================================================
        EntityRegistry.registerModEntity(
            EntityHerobrineWatcher.class,
            "herobrine_watcher",
            id++,
            MagiaNaturalis.instance,
            80,    // 追踪距离
            3,     // 更新频率
            false  // 它不动，不需要同步速度
        );
    }

    static void registerThaumcraftChampions() {
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "taint_breeder:1");
    }

    static void addMobSpawns() {
        if (ThaumcraftWorldGenerator.biomeTaint != null) {
            BiomeGenBase biomeTaint = ThaumcraftWorldGenerator.biomeTaint;
            if (((biomeTaint.getSpawnableList(EnumCreatureType.monster) != null ? 1 : 0)
                & (!biomeTaint.getSpawnableList(EnumCreatureType.monster)
                    .isEmpty() ? 1 : 0))
                != 0) {
                EntityRegistry.addSpawn(
                    EntityTaintBreeder.class,
                    30,
                    1,
                    1,
                    EnumCreatureType.monster,
                    new BiomeGenBase[] { biomeTaint });
            } else {
                MagiaNaturalis.LOGGER.error("Failed to add Entity Spawns to Biome: {}", biomeTaint);
            }
        } else {
            MagiaNaturalis.LOGGER.error("Failed to add Entity Spawns to Biome: Taint");
        }
    }

}