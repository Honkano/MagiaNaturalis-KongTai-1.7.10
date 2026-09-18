package com.github.elenterius.magianaturalis.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import com.github.elenterius.magianaturalis.entity.EntityPechCustom;

public class AIFollowCreature extends EntityAIBase {

    private final EntityPechCustom taskOwner;
    private final Class<? extends EntityLivingBase> targetEntityClass;

    private EntityLiving closestLivingEntity;
    private int followTime;

    public AIFollowCreature(EntityPechCustom entityPech, Class<? extends EntityLivingBase> clazz) {
        taskOwner = entityPech;
        targetEntityClass = clazz;

        // TODO: set correct mute value
        setMutexBits(1);
    }

    public boolean shouldExecute() {
        if (taskOwner.getRNG()
            .nextInt(400) != 0) {
            return false;
        }

        List<?> list = taskOwner.worldObj
            .getEntitiesWithinAABB(targetEntityClass, taskOwner.boundingBox.expand(6.0D, 3.0D, 6.0D));

        if (list.isEmpty()) {
            return false;
        }

        closestLivingEntity = (EntityLiving) list.get(0);
        return true;
    }

    public boolean continueExecuting() {
        return followTime > 0;
    }

    public void startExecuting() {
        followTime = 1000;
    }

    public void resetTask() {
        closestLivingEntity = null;
        taskOwner.getNavigator()
            .clearPathEntity();
    }

    public void updateTask() {
        --followTime;

        if (closestLivingEntity != null) {
            taskOwner.getLookHelper()
                .setLookPositionWithEntity(closestLivingEntity, 30.0F, 30.0F);
            if (taskOwner.getDistanceSqToEntity(closestLivingEntity) > 4.0D) {
                taskOwner.getNavigator()
                    .tryMoveToEntityLiving(closestLivingEntity, 0.5D);
            } else {
                taskOwner.getNavigator()
                    .clearPathEntity();
            }
        }
    }
}
