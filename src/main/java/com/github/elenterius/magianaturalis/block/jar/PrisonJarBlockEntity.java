package com.github.elenterius.magianaturalis.block.jar;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.init.MNConfig;
import com.github.elenterius.magianaturalis.util.NBTUtil;
import com.github.elenterius.magianaturalis.util.Platform;

import thaumcraft.api.wands.IWandable;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintacle;
import thaumcraft.common.tiles.TileJar;

public class PrisonJarBlockEntity extends TileJar implements IWandable {

    protected static final String ENTITY_TAG_KEY = "entity";

    private NBTTagCompound entityData = new NBTTagCompound();
    private Entity cachedEntity;

    public Entity getCachedEntity() {
        return cachedEntity;
    }

    private void setEntityForCache(NBTTagCompound data) {
        cachedEntity = null;
        if (data == null) return;

        if (data.hasKey(ENTITY_TAG_KEY)) {
            cachedEntity = EntityList.createEntityFromNBT(data.getCompoundTag(ENTITY_TAG_KEY), getWorldObj());

            if (cachedEntity != null && cachedEntity instanceof EntityTaintacle) cachedEntity.ticksExisted = 30;
        }
    }

    /**
     * 判定一个实体能不能被封进罐子。
     *
     * 判定顺序：
     * 1. 空 / 玩家 / 非 EntityCreature → 拒绝
     * 2. 黑名单命中 → 拒绝
     * 3. 白名单命中 → 允许（覆盖一切）
     * 4. 原版 Boss → 拒绝
     * 5. 其他 → 允许
     *
     * 白名单可以覆盖 Boss 判定，让玩家能封末影龙。
     */
    public static boolean canCapture(EntityLivingBase entity) {
        if (entity == null) return false;
        if (entity instanceof EntityPlayer) return false;
        if (!(entity instanceof EntityCreature)) return false;

        String id = EntityList.getEntityString(entity);
        if (id == null) return false;

        // 1. 黑名单优先
        if (MNConfig.jarBlacklist != null) {
            for (String blocked : MNConfig.jarBlacklist) {
                if (blocked != null && blocked.equalsIgnoreCase(id)) return false;
            }
        }

        // 2. 白名单覆盖一切（包括 Boss）
        if (MNConfig.jarWhitelist != null) {
            for (String allowed : MNConfig.jarWhitelist) {
                if (allowed != null && allowed.equalsIgnoreCase(id)) return true;
            }
        }

        // 3. 默认规则：原版 Boss 拒绝
        if (entity instanceof IBossDisplayData) return false;

        return true;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound data) {
        data.setTag(ENTITY_TAG_KEY, entityData);
    }

    @Override
    public void readCustomNBT(NBTTagCompound data) {
        entityData = data.getCompoundTag(ENTITY_TAG_KEY);
        setEntityForCache(entityData);
    }

    public boolean saveEntityToNBT(EntityLivingBase entity) {
        if (Platform.isClient()) return false;
        if (!canCapture(entity)) return false;

        if (!entity.writeMountToNBT(entityData)) return false;
        entity.setDead();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    public boolean hasEntityInside() {
        return entityData != null && entityData.hasKey(ENTITY_TAG_KEY);
    }

    public NBTTagCompound getEntityData() {
        return hasEntityInside() ? null : entityData.getCompoundTag(ENTITY_TAG_KEY);
    }

    public void setEntityData(NBTTagCompound data) {
        entityData = data;
        setEntityForCache(entityData);
    }

    public NBTTagCompound getEntityDataPrimitive() {
        return entityData;
    }

    public void releaseFromContainer() {
        if (Platform.isServer()) {
            if (NBTUtil.spawnEntityFromNBT(
                entityData.getCompoundTag(ENTITY_TAG_KEY),
                worldObj,
                xCoord + 0.5D,
                yCoord,
                zCoord + 0.5D)) {
                entityData = null;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side,
        int md) {
        if (hasEntityInside()) {
            if (Platform.isServer()) {
                world.setBlockToAir(x, y, z);
                releaseFromContainer();
            }

            worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(ConfigBlocks.blockJar) + 61440);
            player.worldObj.playSound(
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                "random.glass",
                1.0F,
                0.9F + player.worldObj.rand.nextFloat() * 0.2F,
                false);
            player.swingItem();
            return 0;
        }
        return -1;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
        // do nothing
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
        // do nothing
    }
}
