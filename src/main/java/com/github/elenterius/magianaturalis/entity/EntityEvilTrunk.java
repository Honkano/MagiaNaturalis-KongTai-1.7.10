package com.github.elenterius.magianaturalis.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.entity.ai.AIFollowJumpOwner;
import com.github.elenterius.magianaturalis.entity.ai.AILeapAtTarget;
import com.github.elenterius.magianaturalis.entity.ai.AIOwnerHurtByTarget;
import com.github.elenterius.magianaturalis.entity.ai.AIOwnerHurtTarget;
import com.github.elenterius.magianaturalis.init.MNItems;
import com.github.elenterius.magianaturalis.inventory.InventoryEvilTrunk;
import com.github.elenterius.magianaturalis.util.Platform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.entities.golems.ItemGolemBell;

public class EntityEvilTrunk extends EntityOwnableCreature {

    protected static final int TYPE_DATA_ID = 23;
    protected static final int UPGRADE_DATA_ID = 24;
    protected static final int IS_OPEN_DATA_ID = 25;

    protected static final byte SKULL_ROT_UPDATE_ID = 10;
    protected static final byte SHOW_HEARTS_UPDATE_ID = 11;

    public InventoryEvilTrunk inventory = new InventoryEvilTrunk(this, 36);

    public float skullrot;
    protected int eatDelay;

    public EntityEvilTrunk(World world, int type) {
        this(world);
        setType((byte) type);
    }

    public EntityEvilTrunk(World world) {
        super(world);
        setSize(0.8F, 0.8F);
        skullrot = 0F;
        preventEntitySpawning = true;
        isImmuneToFire = true;

        // if(getTrunkType() == 3)
        // ADD FLIGHT

        tasks.addTask(3, new AILeapAtTarget(this));
        tasks.addTask(4, new EntityAIAttackOnCollide(this, 0.6D, true));
        tasks.addTask(5, new AIFollowJumpOwner(this));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(6, new EntityAILookIdle(this));

        targetTasks.addTask(1, new AIOwnerHurtByTarget(this));
        targetTasks.addTask(2, new AIOwnerHurtTarget(this));
        targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(TYPE_DATA_ID, (byte) 0);
        dataWatcher.addObject(IS_OPEN_DATA_ID, (byte) 0);
        dataWatcher.addObject(UPGRADE_DATA_ID, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(75.0D);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
    }

    public byte getType() {
        return dataWatcher.getWatchableObjectByte(TYPE_DATA_ID);
    }

    public void setType(byte id) {
        dataWatcher.updateObject(TYPE_DATA_ID, id);
    }

    public boolean isOpen() {
        return dataWatcher.getWatchableObjectByte(IS_OPEN_DATA_ID) == 1;
    }

    public void setOpen(boolean flag) {
        dataWatcher.updateObject(IS_OPEN_DATA_ID, (byte) (flag ? 1 : 0));
    }

    public byte getUpgrade() {
        return dataWatcher.getWatchableObjectByte(UPGRADE_DATA_ID);
    }

    public void setUpgrade(byte i) {
        dataWatcher.updateObject(UPGRADE_DATA_ID, i);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        data.setByte("TrunkType", getType());
        data.setTag("Inventory", inventory.writeToNBT(new NBTTagList()));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        setType(data.getByte("TrunkType"));

        NBTTagList nbttaglist = data.getTagList("Inventory", 10);
        inventory.readFromNBT(nbttaglist);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    protected void updateAITick() {
        if (eatDelay > 0) eatDelay -= 1;
        fallDistance = 0.0F;

        if (getOwner() != null) {
            if (getAttackTarget() != null && !getAttackTarget().isDead && getAttackTarget() != getOwner()) {
                faceEntity(getAttackTarget(), 10F, 20F);
                if ((attackTime <= 0) && (getDistanceToEntity(getAttackTarget()) < 1.5D)
                    && (getAttackTarget().boundingBox.maxY > boundingBox.minY)
                    && (getAttackTarget().boundingBox.minY < boundingBox.maxY)) {
                    attackTime = (10 + worldObj.rand.nextInt(5));
                    getAttackTarget().attackEntityFrom(
                        DamageSource.causeMobDamage(this),
                        (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                    worldObj.setEntityState(this, SKULL_ROT_UPDATE_ID);
                    worldObj.playSoundAtEntity(this, "mob.blaze.hit", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
                }

            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damage, float amount) {
        if (isEntityInvulnerable()) {
            return false;
        } else {
            if (damage == DamageSource.lava) return false;
            if (damage == DamageSource.inFire) return false;
            if (damage == DamageSource.onFire) return false;
            if (damage == DamageSource.drown) return false;
            return super.attackEntityFrom(damage, amount);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canAttack(EntityLivingBase attacker, EntityLivingBase owner) {
        if (!(attacker instanceof EntityCreeper) && !(attacker instanceof EntityGhast)) {
            if (attacker instanceof EntityWolf) {
                if (((EntityWolf) attacker).isTamed() && ((EntityWolf) attacker).getOwner() == owner) return false;
            }
            if (attacker instanceof EntityOwnableCreature) {
                if (((EntityOwnableCreature) attacker).getOwner() == owner) return false;
            }

            return attacker instanceof EntityPlayer && owner instanceof EntityPlayer
                && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) attacker) ? false
                    : !(attacker instanceof EntityHorse) || !((EntityHorse) attacker).isTame();
        } else {
            return false;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            if (!onGround) {
                if ((motionY < 0.0D) && (!inWater)) skullrot += 0.015F;
            }

            if ((onGround) || (inWater)) {
                if (!isOpen()) {
                    skullrot -= 0.1F;
                    if (skullrot < 0.0F) skullrot = 0.0F;
                }
            }

            if (isOpen()) skullrot += 0.035F;
            if (skullrot > (isOpen() ? 0.5F : 0.2F)) skullrot = (isOpen() ? 0.5F : 0.2F);
        } else if ((getHealth() < getMaxHealth()) && (ticksExisted % 50 == 0)) {
            heal(1.0F);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack stack = player.getCurrentEquippedItem();

        if (stack != null && stack.getItem() instanceof ItemFood && getHealth() < getMaxHealth()) {
            ItemFood itemfood = (ItemFood) stack.getItem();
            stack.stackSize -= 1;
            heal(itemfood.func_150905_g(stack));

            if (getHealth() == getMaxHealth())
                worldObj.playSoundAtEntity(this, "random.burp", 0.5F, worldObj.rand.nextFloat() * 0.5F + 0.5F);
            else worldObj.playSoundAtEntity(this, "random.eat", 0.5F, worldObj.rand.nextFloat() * 0.5F + 0.5F);

            worldObj.setEntityState(this, SHOW_HEARTS_UPDATE_ID);
            skullrot = 0.15F;

            if (stack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }

            return true;
        }

        if (Platform.isServer() && !player.isSneaking() && isOwner(player)) {
            player.openGui(MagiaNaturalis.instance, 3, worldObj, getEntityId(), 0, 0);
            return true;
        }

        return false;
    }

    public boolean handleLeftClick(EntityPlayer player) {
        ItemStack stack = player.getCurrentEquippedItem();

        if (!isDead && isOwner(player) && stack != null && stack.getItem() instanceof ItemGolemBell) {
            if (Platform.isClient()) {
                spawnExplosionParticle();
            } else if (Platform.isServer()) {
                ItemStack drop = new ItemStack(MNItems.evilTrunkSpawner, 1, getType());

                if (!player.isSneaking()) {
                    if (inventory.hasItems()) {
                        drop.setTagInfo("inventory", inventory.writeToNBT(new NBTTagList()));
                    }
                } else {
                    inventory.dropAllItems();
                }

                entityDropItem(drop, 0.5F);

                worldObj.playSoundAtEntity(this, "thaumcraft:zap", 0.5F, 1F);

                setDead();
            }
            return true;
        }

        return false;
    }

    @Override
    protected String getHurtSound() {
        return Blocks.log2.stepSound.getStepResourcePath();
    }

    @Override
    protected String getDeathSound() {
        return "random.break";
    }

    void showHeartsOrSmokeFX(boolean flag) {
        String s = "heart";
        int amount = 1;
        if (!flag) {
            s = "explode";
            amount = 7;
        }
        for (int i = 0; i < amount; i++) {
            double d = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            worldObj.spawnParticle(
                s,
                posX + rand.nextFloat() * width * 2.0F - width,
                posY + 0.5D + rand.nextFloat() * height,
                posZ + rand.nextFloat() * width * 2.0F - width,
                d,
                d1,
                d2);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == SKULL_ROT_UPDATE_ID) {
            skullrot = 0.15F;
        } else if (id == SHOW_HEARTS_UPDATE_ID) {
            skullrot = 0.15F;
            showHeartsOrSmokeFX(true);
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!worldObj.isRemote) {
            inventory.dropAllItems();
        }
        super.onDeath(damageSource);
    }

}
