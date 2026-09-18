package com.github.elenterius.magianaturalis.util.alchemy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;

public class EntityMorpher {

    public static boolean setPhenoMorph(World world, Entity pointedEntity) {
        if (pointedEntity instanceof EntityLivingBase) {
            if (pointedEntity instanceof EntityHorse) {
                if (!world.isRemote) {
                    EntityHorse horse = (EntityHorse) pointedEntity;
                    int k = world.rand.nextInt(7);
                    int l = world.rand.nextInt(5);
                    int i = k | l << 8;
                    horse.setHorseVariant(i);
                }
                return true;
            }
            if (pointedEntity instanceof EntityOcelot) {
                if (!world.isRemote) {
                    EntityOcelot cat = (EntityOcelot) pointedEntity;
                    int skin = cat.getTameSkin();
                    skin += 1;
                    if (skin > 3) skin = 0;
                    cat.setTameSkin(skin);
                }
                return true;
            }
            if (pointedEntity instanceof EntitySheep) {
                if (!world.isRemote) {
                    EntitySheep sheep = (EntitySheep) pointedEntity;
                    int color = sheep.getFleeceColor();
                    color += 1;
                    if (color > 15) color = 0;
                    sheep.setFleeceColor(color);
                }
                return true;
            }
        }
        return false;
    }

}
