package com.github.elenterius.magianaturalis.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;

import com.github.elenterius.magianaturalis.entity.EntityPechCustom;

public class AIInteractPlayer extends EntityAIBase {

    private EntityPechCustom pech;

    public AIInteractPlayer(EntityPechCustom entityPech) {
        pech = entityPech;
        setMutexBits(5);
    }

    public boolean shouldExecute() {
        if (!pech.isEntityAlive()) {
            return false;
        } else if (pech.isInWater()) {
            return false;
        } else if (!pech.onGround) {
            return false;
        } else if (pech.velocityChanged) {
            return false;
        }

        return pech.isInteracting;
    }

    public void startExecuting() {
        pech.getNavigator()
            .clearPathEntity();
    }

    public void resetTask() {
        pech.isInteracting = false;
    }

}
