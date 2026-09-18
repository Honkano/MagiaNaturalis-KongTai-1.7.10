package com.github.elenterius.magianaturalis.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import com.github.elenterius.magianaturalis.entity.ai.AIFollowCreature;
import com.github.elenterius.magianaturalis.entity.ai.AIInteractPlayer;
import com.github.elenterius.magianaturalis.entity.ai.AILookAtPlayer;

import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityPech;

public class EntityPechCustom extends EntityMob {

    private static final String[] PECH_TYPE_NAME = new String[] { "gambler", "verdant", "alchemist" };

    public boolean isInteracting = false;
    private double damageBonus = 2.5D;

    public EntityPechCustom(World world) {
        this(world, 0);
    }

    public EntityPechCustom(World world, int type) {
        super(world);

        setPechType(type);

        setSize(0.6F, 1.8F);
        getNavigator().setBreakDoors(true);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));

        tasks.addTask(1, new AIInteractPlayer(this));
        tasks.addTask(1, new AILookAtPlayer(this));
        // tasks.addTask(3, new AIPechItemEntityGoto(this));

        tasks.addTask(2, new EntityAIMoveIndoors(this));
        tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        tasks.addTask(4, new EntityAIOpenDoor(this, true));
        tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.5D));
        tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));

        tasks.addTask(7, new AIFollowCreature(this, EntityAgeable.class));

        tasks.addTask(9, new EntityAIWander(this, 0.6D));
        tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        tasks.addTask(10, new EntityAIWatchClosest2(this, EntityPechCustom.class, 5.0F, 0.02F));
        tasks.addTask(11, new EntityAIWatchClosest(this, EntityPech.class, 5.0F, 0.02F));
        tasks.addTask(12, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        tasks.addTask(13, new EntityAILookIdle(this));

        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }

    public boolean isAIEnabled() {
        return true;
    }

    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(13, 0);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D + damageBonus);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
    }

    public String getEntityName() {
        if (hasCustomNameTag()) {
            return getCustomNameTag();
        }
        return StatCollector.translateToLocal("entity.pechCustom." + PECH_TYPE_NAME[getPechType()] + ".name");
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData) {
        par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
        setPechType(rand.nextInt(3));
        return par1EntityLivingData;
    }

    public boolean getCanSpawnHere() {
        BiomeGenBase biome = worldObj
            .getBiomeGenForCoords(MathHelper.floor_double(posX), MathHelper.floor_double(posZ));
        boolean magicBiome = false;
        if (biome != null) {
            magicBiome = (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL))
                && (biome.biomeID != Config.biomeTaintID);
        }
        return (magicBiome) && (super.getCanSpawnHere());
    }

    public boolean interact(EntityPlayer player) {
        // Add Condition here + link to gui
        return false;
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    protected boolean canDespawn() {
        if (hasCustomNameTag()) {
            return false;
        }
        return true;
    }

    public void setPechType(int id) {
        dataWatcher.updateObject(13, id);
    }

    public int getPechType() {
        return dataWatcher.getWatchableObjectInt(13);
    }

    protected void dropFewItems(boolean flag, int i) {
        Aspect[] aspects = (Aspect[]) Aspect.aspects.values()
            .toArray(new Aspect[0]);
        for (int j = 0; j < 1 + i; j++) if (rand.nextBoolean()) {
            ItemStack stack = new ItemStack(ConfigItems.itemManaBean);
            ((IEssentiaContainerItem) stack.getItem())
                .setAspects(stack, new AspectList().add(aspects[rand.nextInt(aspects.length)], 1));
            entityDropItem(stack, 1.5F);
        }
    }

    protected void dropRareDrop(int par1) {
        // Rare Drop Knowledge Framgment
        // TODO: Add Loaded Dice for Gambling
        ItemStack stack = ItemApi.getItem("itemResource", 9);
        entityDropItem(stack, 1.5F);
    }

    public int getTalkInterval() {
        return 120;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    protected String getLivingSound() {
        return "thaumcraft:pech_idle";
    }

    protected String getHurtSound() {
        return "thaumcraft:pech_hit";
    }

    protected String getDeathSound() {
        return "thaumcraft:pech_death";
    }

    public Entity getInteractionTarget() {
        return null;
    }

}
