package com.github.elenterius.magianaturalis.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.entity.EntityOwnableCreature;

public class AIFollowJumpOwner extends EntityAIBase {

    World world;
    EntityOwnableCreature taskOwner;
    EntityLivingBase entityOwner;
    private int jumpDelay;
    private boolean move;

    public AIFollowJumpOwner(EntityOwnableCreature taskOwner) {
        this.taskOwner = taskOwner;
        world = taskOwner.worldObj;
        if (world != null) jumpDelay = world.rand.nextInt(20) + 10;
        setMutexBits(3);
    }

    public boolean shouldExecute() {
        if (!taskOwner.isWaiting()) {
            entityOwner = taskOwner.getOwner();

            if (entityOwner == null) return false;
            else if (taskOwner.getDistanceToEntity(entityOwner) > 5.0F) return true;
        }
        return false;
    }

    public boolean continueExecuting() {
        return !taskOwner.isWaiting() && entityOwner != null && taskOwner.getDistanceToEntity(entityOwner) > 5.0F;
    }

    public void updateTask() {
        entityOwner = taskOwner.getOwner();
        if (entityOwner == null) return;

        float distance = taskOwner.getDistanceToEntity(entityOwner);
        if (distance > 20.0F
            || (!taskOwner.canEntityBeSeen(entityOwner) && distance > 8F && entityMotionXZBelow(taskOwner, 0.1D))) {
            int i = MathHelper.floor_double(entityOwner.posX) - 2;
            int j = MathHelper.floor_double(entityOwner.posZ) - 2;
            int k = MathHelper.floor_double(entityOwner.boundingBox.minY);

            for (int l = 0; l <= 4; l++)
                for (int i1 = 0; i1 <= 4; i1++) if (((l < 1) || (i1 < 1) || (l > 3) || (i1 > 3))
                    && (world.isBlockNormalCubeDefault(i + l, k - 1, j + i1, false))
                    && (!world.isBlockNormalCubeDefault(i + l, k, j + i1, false))
                    && (!world.isBlockNormalCubeDefault(i + l, k + 1, j + i1, false))) {
                        world.playSoundEffect(i + l + 0.5F, k, j + i1 + 0.5F, "mob.endermen.portal", 0.1F, 1.0F);
                        taskOwner.setLocationAndAngles(
                            i + l + 0.5F,
                            k,
                            j + i1 + 0.5F,
                            taskOwner.rotationYaw,
                            taskOwner.rotationPitch);
                        taskOwner.setAttackTarget(null);
                    }
        }

        if (distance > 4.8F) {
            taskOwner.faceEntity(entityOwner, 15.0F, 20.0F);
            move = true;
        } else {
            move = false;
        }

        boolean high = true;

        if ((taskOwner.onGround || taskOwner.isInWater()) && jumpDelay-- <= 0 && move) {
            taskOwner.faceEntity(entityOwner, 10.0F, 20.0F);
            jumpDelay = getJumpDelay() / 3;

            double d0 = entityOwner.posX - taskOwner.posX;
            double d1 = entityOwner.posZ - taskOwner.posZ;
            float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
            double d2 = 0.3000000298023224D;
            taskOwner.motionX += d0 / (double) f * 0.5D * 1.25D + taskOwner.motionX * d2;
            taskOwner.motionZ += d1 / (double) f * 0.5D * 1.25D + taskOwner.motionZ * d2;
            taskOwner.motionY = (high ? 0.45D : 0.35D);

            world.playSoundAtEntity(taskOwner, "random.chestclosed", 0.15F, world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    protected boolean entityMotionXZBelow(EntityLiving entity, double threshold) {
        double x = entity.motionX;
        double z = entity.motionZ;
        return Math.sqrt(x * x + z * z) < threshold;
    }

    protected int getJumpDelay() {
        return world.rand.nextInt(20) + 10;
    }

}
