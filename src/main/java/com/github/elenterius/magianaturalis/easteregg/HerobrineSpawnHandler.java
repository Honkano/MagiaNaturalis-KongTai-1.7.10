package com.github.elenterius.magianaturalis.easteregg;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import thaumcraft.common.lib.research.PlayerKnowledge;

public class HerobrineSpawnHandler {

    private static final Random RAND = new Random();

    /** 每游戏日掷一次（24000 tick = 20 分钟） */
    private static final int BASE_INTERVAL = 24000;

    /** 玩家周围生成距离 */
    private static final double MIN_DIST = 24.0D;
    private static final double MAX_DIST = 48.0D;

    /** 全世界最多同时存在 1 个 */
    private static final int MAX_ALIVE = 1;

    /** 两次生成之间的最小现实时间：30 分钟 */
    private static final long MIN_REAL_INTERVAL_MS = 30L * 60L * 1000L;

    private static long lastSpawnTime = 0L;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        World world = event.world;
        if (world == null || world.isRemote) return;

        // 1) 每游戏日掷一次
        if (world.getTotalWorldTime() % BASE_INTERVAL != 0) return;

        // 2) 全局现实冷却：30 分钟
        if (System.currentTimeMillis() - lastSpawnTime < MIN_REAL_INTERVAL_MS) return;

        // 3) 世界里已经有凝视者就不生成
        if (countWatchers(world) >= MAX_ALIVE) return;

        // 4) 随机挑一个玩家
        @SuppressWarnings("unchecked")
        List<EntityPlayer> players = world.playerEntities;
        if (players.isEmpty()) return;
        EntityPlayer target = players.get(RAND.nextInt(players.size()));

        // 5) 掷骰子
        double chance = calculateChance(target, world);
        if (RAND.nextDouble() > chance) return;

        // 6) 生成
        if (spawnNearPlayer(world, target)) {
            lastSpawnTime = System.currentTimeMillis();
            MagiaNaturalis.LOGGER.info(
                "[HerobrineEasterEgg] Spawned watcher near {} in dim {}",
                target.getCommandSenderName(),
                world.provider.dimensionId);
        }
    }

    /**
     * 1.7.10 没有 world.getEntities(Class, predicate)，
     * 手动遍历 loadedEntityList 过滤。
     */
    @SuppressWarnings("unchecked")
    private int countWatchers(World world) {
        int count = 0;
        List<Entity> list = world.loadedEntityList;
        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);
            if (e instanceof EntityHerobrineWatcher && !e.isDead) {
                count++;
            }
        }
        return count;
    }

    private double calculateChance(EntityPlayer player, World world) {
        double chance = world.isDaytime() ? 0.005D : 0.03D;

        if (world.isThundering()) chance *= 2.0D;

        int warp = getPlayerWarp(player);
        if (warp >= 1000) {
            chance *= 5.0D;
        } else if (warp >= 500) {
            chance *= 2.0D;
        }

        return Math.min(chance, 0.25D);
    }

    private int getPlayerWarp(EntityPlayer player) {
        try {
            PlayerKnowledge knowledge = MagiaNaturalis.proxyTC4.getPlayerKnowledge();
            if (knowledge == null) return 0;
            return knowledge.getWarpTotal(player.getCommandSenderName());
        } catch (Throwable t) {
            return 0;
        }
    }

    private boolean spawnNearPlayer(World world, EntityPlayer player) {
        double angle = RAND.nextDouble() * Math.PI * 2;
        double dist = MIN_DIST + RAND.nextDouble() * (MAX_DIST - MIN_DIST);

        double spawnX = player.posX + Math.cos(angle) * dist;
        double spawnZ = player.posZ + Math.sin(angle) * dist;

        int spawnY = world.getTopSolidOrLiquidBlock((int) spawnX, (int) spawnZ);
        if (spawnY <= 0) return false;

        EntityHerobrineWatcher watcher = new EntityHerobrineWatcher(world);
        watcher.setLocationAndAngles(spawnX, spawnY, spawnZ, 0F, 0F);

        return world.spawnEntityInWorld(watcher);
    }
}