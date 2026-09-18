package com.github.elenterius.magianaturalis.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIDoorInteract;

public class AIBreakDoor extends EntityAIDoorInteract {

    private int destroyTicks;
    private int destroyed = -1;

    public AIBreakDoor(EntityLiving entity) {
        super(entity);
    }

    public boolean shouldExecute() {
        return !super.shouldExecute() ? false
            : (!theEntity.worldObj.getGameRules()
                .getGameRuleBooleanValue("mobGriefing") ? false
                    : !field_151504_e.func_150015_f(theEntity.worldObj, entityPosX, entityPosY, entityPosZ));
    }

    public void startExecuting() {
        super.startExecuting();
        destroyTicks = 0;
    }

    public boolean continueExecuting() {
        double d0 = theEntity.getDistanceSq(entityPosX, entityPosY, entityPosZ);
        return destroyTicks <= 200
            && !field_151504_e.func_150015_f(theEntity.worldObj, entityPosX, entityPosY, entityPosZ)
            && d0 < 4.0D;
    }

    public void resetTask() {
        super.resetTask();
        theEntity.worldObj
            .destroyBlockInWorldPartially(theEntity.getEntityId(), entityPosX, entityPosY, entityPosZ, -1);
    }

    public void updateTask() {
        super.updateTask();

        if (theEntity.getRNG()
            .nextInt(20) == 0) theEntity.worldObj.playAuxSFX(1010, entityPosX, entityPosY, entityPosZ, 0);

        ++destroyTicks;
        int n = (int) ((float) destroyTicks / 200.0F * 10.0F);

        if (n != destroyed) {
            theEntity.worldObj
                .destroyBlockInWorldPartially(theEntity.getEntityId(), entityPosX, entityPosY, entityPosZ, n);
            destroyed = n;
        }

        if (destroyTicks == 200) {
            theEntity.worldObj.setBlockToAir(entityPosX, entityPosY, entityPosZ);
            theEntity.worldObj.playAuxSFX(1012, entityPosX, entityPosY, entityPosZ, 0);
            theEntity.worldObj
                .playAuxSFX(2001, entityPosX, entityPosY, entityPosZ, Block.getIdFromBlock(field_151504_e));
        }
    }

}
