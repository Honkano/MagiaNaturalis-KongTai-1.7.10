package com.github.elenterius.magianaturalis.util;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import com.github.elenterius.magianaturalis.util.access.UserAccess;

public final class NBTUtil {

    public static NBTTagCompound getOrCreate(ItemStack stack) {
        NBTTagCompound data = stack.getTagCompound();
        if (data == null) {
            data = new NBTTagCompound();
            stack.setTagCompound(data);
        }
        return data;
    }

    public static NBTTagList newDoubleNBTList(double[] values) {
        NBTTagList list = new NBTTagList();
        for (double v : values) {
            list.appendTag(new NBTTagDouble(v));
        }
        return list;
    }

    public static void saveInventoryToNBT(ItemStack stack, ItemStack[] inventory) {
        NBTUtil.saveInventoryToNBT(NBTUtil.getOrCreate(stack), inventory);
    }

    public static void saveInventoryToNBT(NBTTagCompound data, ItemStack[] inventory) {
        if (inventory == null) return;

        NBTTagList items = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            NBTTagCompound tempData = new NBTTagCompound();
            tempData.setByte("Slot", (byte) i);
            if (inventory[i] != null) inventory[i].writeToNBT(tempData);
            items.appendTag(tempData);
        }
        data.setTag("Items", items);
    }

    public static ArrayList<ItemStack> loadInventoryFromNBT(ItemStack stack) {
        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        if (!data.hasKey("Items")) return null;

        NBTTagList items = data.getTagList("Items", NBT.TAG_COMPOUND);
        ArrayList<ItemStack> inventory = new ArrayList<>();
        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound tempData = items.getCompoundTagAt(i);
            byte j = tempData.getByte("Slot");
            if (j >= 0) {
                inventory.add(ItemStack.loadItemStackFromNBT(tempData));
            }
        }
        return inventory;
    }

    public static ItemStack[] loadInventoryFromNBT(ItemStack stack, int invSize) {
        return NBTUtil.loadInventoryFromNBT(NBTUtil.getOrCreate(stack), invSize);
    }

    public static ItemStack[] loadInventoryFromNBT(NBTTagCompound data, int inventorySize) {
        if (!data.hasKey("Items")) return new ItemStack[inventorySize];

        NBTTagList items = data.getTagList("Items", NBT.TAG_COMPOUND);
        ItemStack[] inventory = new ItemStack[inventorySize];
        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound tempData = items.getCompoundTagAt(i);
            byte j = tempData.getByte("Slot");
            if (j >= 0) {
                inventory[i] = ItemStack.loadItemStackFromNBT(tempData);
            }
        }
        return inventory;
    }

    public static void saveUserAccessToNBT(ItemStack stack, ArrayList<UserAccess> users) {
        NBTUtil.saveUserAccessToNBT(NBTUtil.getOrCreate(stack), users);
    }

    public static ArrayList<UserAccess> loadUserAccessFromNBT(ItemStack stack) {
        return NBTUtil.loadUserAccessFromNBT(NBTUtil.getOrCreate(stack));
    }

    public static void saveUserAccessToNBT(NBTTagCompound data, ArrayList<UserAccess> users) {
        if (users == null) return;

        NBTTagList accessList = new NBTTagList();
        for (UserAccess user : users) {
            NBTTagCompound tempData = new NBTTagCompound();
            tempData.setString(
                "UUID",
                user.getUUID()
                    .toString());
            tempData.setByte("ArcaneChestType", user.getAccessLevel());
            accessList.appendTag(tempData);
        }
        data.setTag("AccessList", accessList);
    }

    public static ArrayList<UserAccess> loadUserAccessFromNBT(NBTTagCompound data) {
        if (!data.hasKey("AccessList")) return new ArrayList<>();

        NBTTagList tagList = data.getTagList("AccessList", NBT.TAG_COMPOUND);

        ArrayList<UserAccess> accessList = new ArrayList<>();
        UserAccess user;
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tempData = tagList.getCompoundTagAt(i);
            user = new UserAccess();
            user.setUUID(UUID.fromString(tempData.getString("UUID")));
            user.setAccesLevel(tempData.getByte("ArcaneChestType"));
            accessList.add(user);
        }
        return accessList;
    }

    public static boolean spawnEntityFromNBT(NBTTagCompound data, World world, double x, double y, double z) {
        if (world == null) return false;
        if (data == null || !data.hasKey("id")) return false;

        data.setTag("Pos", NBTUtil.newDoubleNBTList(new double[] { x, y, z }));
        data.setTag("Motion", NBTUtil.newDoubleNBTList(new double[] { 0.0D, 0.0D, 0.0D }));
        data.setFloat("FallDistance", 0.0F);
        data.setInteger("Dimension", world.provider.dimensionId);
        Entity entity = EntityList.createEntityFromNBT(data, world);
        if (entity == null) return false;
        return world.spawnEntityInWorld(entity);
    }

}
