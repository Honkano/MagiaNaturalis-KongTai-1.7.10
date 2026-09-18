package com.github.elenterius.magianaturalis.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;
import com.github.elenterius.magianaturalis.inventory.InventoryEvilTrunk;

public class ContainerEvilTrunk extends Container {

    private final EntityEvilTrunk entityTrunk;

    private InventoryEvilTrunk inventory;
    private int numRows = 4;

    public ContainerEvilTrunk(IInventory iinventory, World world, EntityEvilTrunk entity) {
        entityTrunk = entity;
        inventory = entity.inventory;

        for (int row = 0; row < numRows; row++) for (int slot = 0; slot < 9; slot++)
            addSlotToContainer(new Slot(inventory, slot + row * 9, 8 + slot * 18, 15 + row * 23));

        for (int row = 0; row < 3; row++) for (int slot = 0; slot < 9; slot++)
            addSlotToContainer(new Slot(iinventory, slot + row * 9 + 9, 8 + slot * 18, 118 + row * 18));

        for (int slot = 0; slot < 9; slot++) addSlotToContainer(new Slot(iinventory, slot, 8 + slot * 18, 176));

        entityTrunk.setOpen(true);
        entityTrunk.worldObj.playSoundAtEntity(
            entityTrunk,
            "random.chestopen",
            0.5F,
            entityTrunk.worldObj.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int button) {
        if (button == 1) {
            entityTrunk.setWaiting(!entityTrunk.isWaiting());
            return true;
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack tempStack = slot.getStack();
            stack = tempStack.copy();

            if (index < 37) {
                if (!mergeItemStack(tempStack, 36, inventorySlots.size(), true)) return null;
            } else if (!mergeItemStack(tempStack, 0, 36, false)) {
                return null;
            }

            if (tempStack.stackSize == 0) slot.putStack((ItemStack) null);
            else slot.onSlotChanged();

            if (tempStack.stackSize == stack.stackSize) return null;
            slot.onPickupFromSlot(player, tempStack);
        }
        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        entityTrunk.setOpen(false);
        entityTrunk.worldObj.playSoundAtEntity(
            entityTrunk,
            "random.chestclosed",
            0.5F,
            entityTrunk.worldObj.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
