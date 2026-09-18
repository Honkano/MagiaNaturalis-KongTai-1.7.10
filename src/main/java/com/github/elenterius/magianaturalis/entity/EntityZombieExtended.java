package com.github.elenterius.magianaturalis.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.entity.ai.AIBreakDoor;
import com.github.elenterius.magianaturalis.entity.ai.EntityAIOwnerTarget;

public class EntityZombieExtended extends EntityZombie implements IEntityOwnable {

    public EntityZombieExtended(World world) {
        super(world);
        tasks.taskEntries.clear();
        getNavigator().setAvoidSun(false);
        // getNavigator().setBreakDoors(true);

        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new AIBreakDoor(this));
        tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
        tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityLiving.class, 1.0D, true));
        tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        tasks.addTask(5, new EntityAIMoveThroughVillage(this, 1.0D, false));
        tasks.addTask(6, new EntityAIWander(this, 1.0D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAIOwnerTarget(this, false));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(17, "");
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(
            new AttributeModifier("Random Knockback Resistance", this.rand.nextDouble() * 0.05000000074505806D, 0));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!worldObj.isRemote && (getAttackTarget() == null || getAttackTarget().isDead || ticksExisted > 300)) {
            attackEntityFrom(DamageSource.outOfWorld, 10.0F);
        }
    }

    @Override
    public void onKillEntity(EntityLivingBase entity) {
        super.onKillEntity(entity);

        if (entity instanceof EntityVillager) {
            if (rand.nextBoolean()) return;

            EntityZombieExtended entityZombie = new EntityZombieExtended(worldObj);
            entityZombie.copyLocationAndAnglesFrom(entity);
            worldObj.removeEntity(entity);
            entityZombie.setVillager(true);
            worldObj.spawnEntityInWorld(entityZombie);
            worldObj.playAuxSFXAtEntity(null, 1016, (int) posX, (int) posY, (int) posZ, 0);
        }
    }

    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        if (func_152113_b() == null) data.setString("Owner", "");
        else data.setString("Owner", func_152113_b());
    }

    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        String name = data.getString("Owner");

        if (name.length() > 0) setOwner(name);
    }

    @Override
    protected Item getDropItem() {
        return null;
    }

    @Override
    protected void dropRareDrop(int n) {
        // Removes Drops
    }

    @Override
    public boolean interact(EntityPlayer player) {
        return false;
    }

    public void setOwner(String name) {
        dataWatcher.updateObject(17, name);
    }

    public EntityLivingBase getOwnerEntity() {
        return worldObj.getPlayerEntityByName(func_152113_b());
    }

    @Override
    public Entity getOwner() {
        return getOwnerEntity();
    }

    public void setExperienceValue(int n) {
        experienceValue = n;
    }

    @Override
    public String func_152113_b() {
        return dataWatcher.getWatchableObjectString(17);
    }

}
