package com.github.elenterius.magianaturalis.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestType;

public class ContainerArcaneChest extends Container {

    private final ArcaneChestBlockEntity chest;

    public ContainerArcaneChest(InventoryPlayer inventoryPlayer, ArcaneChestBlockEntity tile) {
        chest = tile;
        chest.openInventory();

        ArcaneChestType type = chest.getChestType();
        int offset = type == ArcaneChestType.GREAT_WOOD ? 0 : 18;
        int rows = type == ArcaneChestType.GREAT_WOOD ? 6 : 7;
        int columns = type == ArcaneChestType.GREAT_WOOD ? 9 : 11;
        int tempIndex = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                addSlotToContainer(new Slot(chest, tempIndex++, 8 + col * 18, 18 + row * 18));
            }
        }

        // player main inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(
                    new Slot(inventoryPlayer, col + row * 9 + 9, 8 + col * 18 + offset, 140 + row * 18 + offset));
            }
        }

        // player hotbar
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(inventoryPlayer, col, 8 + col * 18 + offset, 198 + offset));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return chest.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(index);

        if ((slot != null) && (slot.getHasStack())) {
            ItemStack tempStack = slot.getStack();
            stack = tempStack.copy();

            int inventorySize = chest.getSizeInventory();

            if (index < inventorySize + 1) {
                if (!mergeItemStack(tempStack, inventorySize, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(tempStack, 0, inventorySize, false)) {
                return null;
            }

            if (tempStack.stackSize == 0) slot.putStack(null);
            else slot.onSlotChanged();

            if (tempStack.stackSize == stack.stackSize) return null;

            slot.onPickupFromSlot(player, tempStack);
        }
        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        chest.closeInventory();
    }

}
