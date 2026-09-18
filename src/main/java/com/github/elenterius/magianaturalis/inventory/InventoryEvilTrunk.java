package com.github.elenterius.magianaturalis.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;

public class InventoryEvilTrunk implements IInventory {

    protected static final int STACK_LIMIT = 64;

    public ItemStack[] inventory;
    public EntityEvilTrunk entityTrunk;
    public boolean inventoryChanged;

    public InventoryEvilTrunk(EntityEvilTrunk entity, int slots) {
        inventory = new ItemStack[slots];
        inventoryChanged = false;
        entityTrunk = entity;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        if (inventory[index] == null) return null;

        if (inventory[index].stackSize <= amount) {
            ItemStack stack = inventory[index];
            inventory[index] = null;
            return stack;
        }

        ItemStack stack = inventory[index].splitStack(amount);
        if (inventory[index].stackSize == 0) inventory[index] = null;
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory[index] = stack;
    }

    @Override
    public String getInventoryName() {
        return "Inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return STACK_LIMIT;
    }

    @Override
    public void markDirty() {
        inventoryChanged = true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {
        entityTrunk.setOpen(true);
    }

    @Override
    public void closeInventory() {
        entityTrunk.setOpen(false);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public void dropAllItems() {
        for (int index = 0; index < inventory.length; index++) {
            if (inventory[index] != null) {
                entityTrunk.entityDropItem(inventory[index], 0.0F);
                inventory[index] = null;
            }
        }
    }

    public boolean hasItems() {
        for (ItemStack stack : inventory) {
            if (stack != null) return true;
        }
        return false;
    }

    public NBTBase writeToNBT(NBTTagList dataList) {
        for (int index = 0; index < inventory.length; index++) {
            if (inventory[index] != null) {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) index);
                inventory[index].writeToNBT(data);
                dataList.appendTag(data);
            }
        }
        return dataList;
    }

    public void readFromNBT(NBTTagList dataList) {
        inventory = new ItemStack[inventory.length];
        for (int i = 0; i < dataList.tagCount(); i++) {
            NBTTagCompound data = dataList.getCompoundTagAt(i);
            byte index = data.getByte("Slot");
            ItemStack stack = ItemStack.loadItemStackFromNBT(data);
            if (stack != null) {
                if (index >= 0 && index < inventory.length) {
                    inventory[index] = stack;
                }
            }
        }
    }
}
