package com.github.elenterius.magianaturalis.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import com.github.elenterius.magianaturalis.entity.EntityZombieExtended;

public class EntityAIOwnerTarget extends EntityAITarget {

    private EntityLivingBase targetEntity;
    private EntityZombieExtended entityZE;

    public EntityAIOwnerTarget(EntityCreature creature, boolean bool) {
        super(creature, bool);
        targetEntity = taskOwner.getAttackTarget();
        entityZE = (EntityZombieExtended) taskOwner;
    }

    @Override
    public boolean shouldExecute() {
        if (targetEntity == null) return false;
        if (!targetEntity.isDead) return true;
        return false;
    }

    public void startExecuting() {
        if (taskOwner.getAttackTarget() != null && taskOwner.getAttackTarget().isDead) taskOwner.setDead();

        if (taskOwner.getAttackTarget() == null && entityZE.getAITarget() != null
            && !entityZE.getOwnerEntity()
                .getAITarget().isDead)
            if (entityZE.getOwnerEntity()
                .getAITarget() instanceof EntityLivingBase && entityZE.canEntityBeSeen(
                    entityZE.getOwnerEntity()
                        .getAITarget()))
                taskOwner.setAttackTarget(
                    entityZE.getOwnerEntity()
                        .getAITarget());

        taskOwner.setAttackTarget(targetEntity);
        super.startExecuting();
    }

    public void updateTask() {
        if (taskOwner.getAttackTarget() != null && taskOwner.getAttackTarget().isDead) taskOwner.setDead();

        if (taskOwner.getAttackTarget() == null && entityZE.getAITarget() != null
            && !entityZE.getOwnerEntity()
                .getAITarget().isDead)
            if (entityZE.getOwnerEntity()
                .getAITarget() instanceof EntityLivingBase && entityZE.canEntityBeSeen(
                    entityZE.getOwnerEntity()
                        .getAITarget()))
                taskOwner.setAttackTarget(
                    entityZE.getOwnerEntity()
                        .getAITarget());
    }
}
