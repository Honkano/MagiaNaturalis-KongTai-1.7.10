package com.github.elenterius.magianaturalis.entity;

import java.util.UUID;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.github.elenterius.magianaturalis.entity.ai.AIWait;
import com.github.elenterius.magianaturalis.util.Platform;
import com.mojang.authlib.GameProfile;

public abstract class EntityOwnableCreature extends EntityCreature implements IEntityOwnable {

    protected static final int IS_WAITING_DATA_ID = 20;
    protected static final int OWNER_UUID_DATA_ID = 21;
    protected static final int OWNER_NAME_DATA_ID = 22;

    protected static final String OWNER_UUID_KEY = "OwnerUUID";
    protected static final String OWNER_NAME_KEY = "OwnerName";
    protected static final String WAITING_KEY = "Waiting";

    protected EntityOwnableCreature(World world) {
        super(world);

        tasks.addTask(2, new AIWait(this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(IS_WAITING_DATA_ID, (byte) 0);
        dataWatcher.addObject(OWNER_NAME_DATA_ID, "");
        dataWatcher.addObject(OWNER_UUID_DATA_ID, Platform.NIL_UUID.toString());
    }

    public boolean isWaiting() {
        return dataWatcher.getWatchableObjectByte(IS_WAITING_DATA_ID) != 0;
    }

    public void setWaiting(boolean bool) {
        dataWatcher.updateObject(IS_WAITING_DATA_ID, (byte) (bool ? 1 : 0));
    }

    @Override
    // getOwnerName() from IEntityOwnable
    public String func_152113_b() {
        return dataWatcher.getWatchableObjectString(OWNER_NAME_DATA_ID);
    }

    /**
     * Wrapper for func_152113_b()
     */
    public String getOwnerName() {
        return func_152113_b();
    }

    public void setOwnerName(String name) {
        dataWatcher.updateObject(OWNER_NAME_DATA_ID, name);
    }

    public UUID getOwnerUUID() {
        String uuidString = dataWatcher.getWatchableObjectString(OWNER_UUID_DATA_ID);
        if (uuidString.isEmpty()) return Platform.NIL_UUID;
        return UUID.fromString(uuidString);
    }

    public void setOwnerUUID(UUID uuid) {
        dataWatcher.updateObject(OWNER_UUID_DATA_ID, uuid.toString());
    }

    public void setOwner(EntityPlayer player) {
        setOwnerUUID(
            player.getGameProfile()
                .getId());
        setOwnerName(
            player.getGameProfile()
                .getName());
    }

    @Override
    public EntityLivingBase getOwner() {
        return worldObj.func_152378_a(getOwnerUUID());
    }

    public boolean isOwner(EntityLivingBase entity) {
        return entity.getUniqueID()
            .equals(getOwnerUUID());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);

        UUID ownerUUID = getOwnerUUID();
        data.setString(OWNER_UUID_KEY, ownerUUID.toString());
        data.setString(OWNER_NAME_KEY, getOwnerName());

        data.setBoolean(WAITING_KEY, isWaiting());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);

        UUID uuid = Platform.NIL_UUID;

        if (data.hasKey(OWNER_UUID_KEY, Constants.NBT.TAG_STRING)) {
            String uuidString = data.getString(OWNER_UUID_KEY);
            uuid = uuidString.isEmpty() ? Platform.NIL_UUID : UUID.fromString(uuidString);
            setOwnerUUID(uuid);
        }

        if (data.hasKey(OWNER_NAME_KEY, Constants.NBT.TAG_STRING)) {
            setOwnerName(data.getString(OWNER_NAME_KEY));
        }

        // when the player name is missing restore it if possible
        if (Platform.isServer() && getOwnerName().isEmpty()) {
            GameProfile profile = Platform.findGameProfileByUUID(uuid);
            if (profile != null) {
                setOwnerName(profile.getName());
            }
        }

        setWaiting(data.getBoolean(WAITING_KEY));
    }

    @Override
    public Team getTeam() {
        EntityLivingBase owner = getOwner();
        if (owner != null) return owner.getTeam();
        return super.getTeam();
    }

    @Override
    public boolean isOnSameTeam(EntityLivingBase entity) {
        EntityLivingBase owner = getOwner();
        if (entity == owner) return true;
        if (owner != null) return owner.isOnSameTeam(entity);
        return super.isOnSameTeam(entity);
    }

    public boolean canAttack(EntityLivingBase attacker, EntityLivingBase owner) {
        return true;
    }

}
