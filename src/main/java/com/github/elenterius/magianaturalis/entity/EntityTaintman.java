package com.github.elenterius.magianaturalis.entity;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.world.World;

public class EntityTaintman extends EntityEnderman {

    public EntityTaintman(World world) {
        super(world);
        setSize(0.6F, 1.4F);
    }

    @Override
    public void onLivingUpdate() {
        // super.onLivingUpdate();
    }

}
