package com.github.elenterius.magianaturalis.block.table;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;

import com.github.elenterius.magianaturalis.init.MNBlocks;
import com.github.elenterius.magianaturalis.item.artifact.ResearchLogItem;
import com.github.elenterius.magianaturalis.util.Platform;

import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class TranscribingTableBlockEntity extends TileThaumcraft implements ISidedInventory {

    public int timer = 40;
    private ItemStack[] inventory = new ItemStack[2];
    private String customName;
    private static final int[] sides = { 0 };

    @Override
    public int getSizeInventory() {
        return 2;
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
        if (inventory[index] == null) return null;

        ItemStack stack = inventory[index];
        inventory[index] = null;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) stack.stackSize = getInventoryStackLimit();
    }

    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? customName : "container.transtable";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return customName != null && !customName.isEmpty();
    }

    public void setCustomInventoryNamee(String name) {
        customName = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("CustomName", 8)) {
            customName = data.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (hasCustomInventoryName()) {
            data.setString("CustomName", customName);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound data) {
        NBTTagList nbttaglist = data.getTagList("Items", NBT.TAG_COMPOUND);
        inventory = new ItemStack[getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound tempData = nbttaglist.getCompoundTagAt(i);
            byte b = tempData.getByte("Slot");
            if (b >= 0 && b < inventory.length) {
                inventory[b] = ItemStack.loadItemStackFromNBT(tempData);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound data) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound tempData = new NBTTagCompound();
                tempData.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(tempData);
                nbttaglist.appendTag(tempData);
            }
        }
        data.setTag("Items", nbttaglist);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index != 1 && (index != 0 || stack.getItem() instanceof ResearchLogItem);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return side != 1 ? sides : new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return side != 1 && isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return index == 1;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        boolean update = false;
        if (Platform.isServer()) {
            if (getStackInSlot(0) != null && getStackInSlot(0).getItem() instanceof ResearchLogItem) if (--timer <= 0) {
                update = handleKnowledgeHarvest(update);
                timer = 40;
            }
            if (update) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    private boolean handleKnowledgeHarvest(boolean update) {
        if (worldObj.getBlock(xCoord, yCoord, zCoord) != MNBlocks.transcribingTable) return update;

        ItemStack stack = getStackInSlot(0);

        int x = worldObj.rand.nextInt(2) - worldObj.rand.nextInt(2);
        int z = worldObj.rand.nextInt(2) - worldObj.rand.nextInt(2);
        x += x;
        z += z;

        if (x != 0 || z != 0) {
            TileEntity tile = worldObj.getTileEntity(xCoord + x, yCoord, zCoord + z);
            if (tile instanceof TileDeconstructionTable
                && worldObj.getBlock(xCoord + x, yCoord, zCoord + z) == ConfigBlocks.blockTable) {
                TileDeconstructionTable tileDT = (TileDeconstructionTable) tile;
                Aspect aspect = tileDT.aspect;
                if (aspect != null) {
                    // ADD ASPECT TO TOME/SCROLL OF KNOWLEDGE ADD THE METHOD
                    ResearchLogItem log = (ResearchLogItem) stack.getItem();
                    if (log.addResearchPoint(stack, aspect, (short) 1)) {
                        tileDT.aspect = null;
                        worldObj.markBlockForUpdate(xCoord + x, yCoord, zCoord + z);
                        update = true;
                    } else {
                        // Check if Book is full & put it into the appropriate slot if necessary
                        if (log.getResearchPoint(stack, Aspect.AIR) == 64
                            && log.getResearchPoint(stack, Aspect.EARTH) == 64
                            && log.getResearchPoint(stack, Aspect.WATER) == 64
                            && log.getResearchPoint(stack, Aspect.FIRE) == 64
                            && log.getResearchPoint(stack, Aspect.ORDER) == 64
                            && log.getResearchPoint(stack, Aspect.ENTROPY) == 64) {
                            if (getStackInSlot(1) == null) {
                                ItemStack stack2 = stack.copy();
                                setInventorySlotContents(0, null);
                                setInventorySlotContents(1, stack2);
                            }
                        }
                    }
                }
            }
        }
        return update;
    }

}
