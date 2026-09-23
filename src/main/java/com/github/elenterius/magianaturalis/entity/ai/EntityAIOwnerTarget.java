package com.github.elenterius.magianaturalis.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import com.github.elenterius.magianaturalis.entity.EntityZombieExtended;

public class EntityAIOwnerTarget extends EntityAITarget {

    private EntityZombieExtended entityZE;

    public EntityAIOwnerTarget(EntityCreature creature, boolean bool) {
        super(creature, bool);
        entityZE = (EntityZombieExtended) taskOwner;
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase current = taskOwner.getAttackTarget();
        if (current != null && !current.isDead && !EntityZombieExtended.isFriendlyCreature(current)) {
            return true;
        }

        EntityLivingBase ownerTarget = getOwnerTargetSafely();
        if (ownerTarget != null && !ownerTarget.isDead && taskOwner.canEntityBeSeen(ownerTarget)) {
            return true;
        }

        return false;
    }

    @Override
    public void startExecuting() {
        EntityLivingBase ownerTarget = getOwnerTargetSafely();
        EntityLivingBase current = taskOwner.getAttackTarget();

        if ((current == null || current.isDead) && ownerTarget != null
            && !ownerTarget.isDead
            && taskOwner.canEntityBeSeen(ownerTarget)) {
            taskOwner.setAttackTarget(ownerTarget);
        }

        super.startExecuting();
    }

    @Override
    public void updateTask() {
        EntityLivingBase current = taskOwner.getAttackTarget();

        // 当前目标死了，或者是个友好生物 → 清掉
        if (current != null && (current.isDead || EntityZombieExtended.isFriendlyCreature(current))) {
            taskOwner.setAttackTarget(null);
        }

        if (taskOwner.getAttackTarget() == null) {
            EntityLivingBase ownerTarget = getOwnerTargetSafely();
            if (ownerTarget != null && !ownerTarget.isDead && taskOwner.canEntityBeSeen(ownerTarget)) {
                taskOwner.setAttackTarget(ownerTarget);
            }
        }
    }

    /**
     * 安全读取"主人当前攻击目标"。
     * 过滤掉友好生物，防止小僵尸跟着主人去打动物 / 村民。
     */
    private EntityLivingBase getOwnerTargetSafely() {
        if (entityZE.getOwnerEntity() == null) return null;
        EntityLivingBase target = entityZE.getOwnerEntity()
            .getAITarget();
        if (target != null && EntityZombieExtended.isFriendlyCreature(target)) return null;
        return target;
    }
}
