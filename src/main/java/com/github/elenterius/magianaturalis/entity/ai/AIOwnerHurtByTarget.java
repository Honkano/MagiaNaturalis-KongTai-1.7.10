package com.github.elenterius.magianaturalis.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import com.github.elenterius.magianaturalis.entity.EntityOwnableCreature;

public class AIOwnerHurtByTarget extends EntityAITarget {

    EntityOwnableCreature taskOwner;
    EntityLivingBase ownerTarget;
    private int revengeTimer;

    public AIOwnerHurtByTarget(EntityOwnableCreature entity) {
        super(entity, false);
        this.taskOwner = entity;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entityOwner = this.taskOwner.getOwner();

        if (entityOwner == null) {
            return false;
        } else {
            this.ownerTarget = entityOwner.getAITarget();
            int i = entityOwner.func_142015_aE();
            return i != this.revengeTimer && this.isSuitableTarget(this.ownerTarget, false)
                && this.taskOwner.canAttack(this.ownerTarget, entityOwner);
        }
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.ownerTarget);
        EntityLivingBase owner = this.taskOwner.getOwner();

        if (owner != null) {
            this.revengeTimer = owner.func_142015_aE();
        }

        super.startExecuting();
    }

}
