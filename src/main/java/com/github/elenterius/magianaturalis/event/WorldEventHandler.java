package com.github.elenterius.magianaturalis.event;

import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class WorldEventHandler {

    private WorldEventHandler() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
    }

    @SubscribeEvent
    public void onEntitySpawn(final EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityCreeper) {
            ((EntityCreeper) event.entity).tasks.addTask(
                3,
                new EntityAIAvoidEntity(((EntityCreeper) event.entity), EntityEvilTrunk.class, 6.0F, 1.0D, 1.2D));
        }
    }

}
