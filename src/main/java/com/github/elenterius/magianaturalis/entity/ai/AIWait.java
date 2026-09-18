package com.github.elenterius.magianaturalis.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import com.github.elenterius.magianaturalis.entity.EntityOwnableCreature;

public class AIWait extends EntityAIBase {

    private EntityOwnableCreature taskOwner;

    public AIWait(EntityOwnableCreature entity) {
        taskOwner = entity;
        setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        if (taskOwner.isInWater()) {
            return false;
        } else if (!taskOwner.onGround) {
            return false;
        } else {
            EntityLivingBase creatureOwner = taskOwner.getOwner();
            if (creatureOwner == null) return true;
            if (taskOwner.getDistanceSqToEntity(creatureOwner) < 144.0D && creatureOwner.getAITarget() != null)
                return false;
            return taskOwner.isWaiting();
        }
    }

    @Override
    public void startExecuting() {
        taskOwner.getNavigator()
            .clearPathEntity();
        taskOwner.setWaiting(true);
    }

    @Override
    public void resetTask() {
        taskOwner.setWaiting(false);
    }

}
