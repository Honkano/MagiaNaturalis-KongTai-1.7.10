package com.github.elenterius.magianaturalis.entity.taint;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.github.elenterius.magianaturalis.util.Platform;

import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityTaintSpider;

public class EntityTaintBreeder extends EntitySpider implements ITaintedMob {

    private byte breedTime = 8;
    private byte broodTimer;

    public EntityTaintBreeder(World world) {
        super(world);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(42.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.600000011920929D);
    }

    public boolean getCanSpawnHere() {
        BiomeGenBase biome = worldObj
            .getBiomeGenForCoords(MathHelper.floor_double(posX), MathHelper.floor_double(posZ));
        if (biome != null) return biome.biomeID == Config.biomeTaintID && super.getCanSpawnHere();
        return false;
    }

    protected Entity findPlayerToAttack() {
        return worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
    }

    public boolean isPotionApplicable(PotionEffect potionEffect) {
        return potionEffect.getPotionID() != Config.potionTaintPoisonID && super.isPotionApplicable(potionEffect);
    }

    public void onLivingUpdate() {
        if (Platform.isServer() && ticksExisted % 20 == 0) {
            if (getHealth() < getMaxHealth() * 0.8F) {
                if (broodTimer++ % breedTime == 0 && findPlayerToAttack() != null) {
                    for (int i = worldObj.rand.nextInt(2); i >= 0; i--) {
                        EntityTaintSpider breedling = new EntityTaintSpider(worldObj);
                        breedling.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
                        int level = worldObj.difficultySetting == EnumDifficulty.HARD ? 2 : 3;
                        breedling.addPotionEffect(new PotionEffect(Potion.jump.id, 144, level));
                        breedling.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 144, level - 2));
                        worldObj.spawnEntityInWorld(breedling);
                    }
                }
            }
        }

        super.onLivingUpdate();
    }

    protected Item getDropItem() {
        return ConfigItems.itemResource;
    }

    protected void dropFewItems(boolean bool, int chance) {
        if (worldObj.rand.nextInt(6) == 0) if (worldObj.rand.nextBoolean()) {
            entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 11), height / 2.0F);
        } else {
            entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 12), height / 2.0F);
        }
    }

}
