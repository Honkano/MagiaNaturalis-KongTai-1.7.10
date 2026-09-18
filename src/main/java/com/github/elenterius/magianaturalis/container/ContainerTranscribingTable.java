package com.github.elenterius.magianaturalis.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.github.elenterius.magianaturalis.block.table.TranscribingTableBlockEntity;
import com.github.elenterius.magianaturalis.item.artifact.ResearchLogItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerTranscribingTable extends Container {

    private final TranscribingTableBlockEntity table;

    private int lastPauseTime;

    public ContainerTranscribingTable(InventoryPlayer inventoryPlayer, TranscribingTableBlockEntity tile) {
        table = tile;
        addSlotToContainer(new Slot(tile, 0, 64, 16));
        addSlotToContainer(new SlotInvalid(tile, 1, 64, 48));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);
        crafting.sendProgressBarUpdate(this, 0, table.timer);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : crafters) {
            ICrafting icrafting = (ICrafting) crafter;

            if (lastPauseTime != table.timer) {
                icrafting.sendProgressBarUpdate(this, 0, table.timer);
            }
        }
        lastPauseTime = table.timer;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int i, int j) {
        if (i == 0) {
            table.timer = j;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return table.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack selectedStack = slot.getStack();
            stack = selectedStack.copy();

            if (index == 1) {
                if (!mergeItemStack(selectedStack, 2, 38, true)) {
                    return null;
                }

                slot.onSlotChange(selectedStack, stack);
            }
            if (index > 1) {
                if (selectedStack.getItem() instanceof ResearchLogItem) {
                    if (!mergeItemStack(selectedStack, 0, 1, false)) return null;
                } else if (index > 1 && index < 29) {
                    if (!mergeItemStack(selectedStack, 29, 38, false)) return null;
                } else if (index >= 29 && index < 38 && !mergeItemStack(selectedStack, 2, 29, false)) {
                    return null;
                }
            } else if (!mergeItemStack(selectedStack, 2, 38, false)) {
                return null;
            }

            if (selectedStack.stackSize == 0) slot.putStack(null);
            else slot.onSlotChanged();

            if (selectedStack.stackSize == stack.stackSize) return null;

            slot.onPickupFromSlot(player, selectedStack);
        }

        return stack;
    }

}
