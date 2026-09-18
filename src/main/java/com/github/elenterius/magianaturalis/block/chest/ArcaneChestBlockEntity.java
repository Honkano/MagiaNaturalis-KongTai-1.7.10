package com.github.elenterius.magianaturalis.block.chest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.init.MNBlocks;
import com.github.elenterius.magianaturalis.util.NBTUtil;
import com.github.elenterius.magianaturalis.util.Platform;
import com.github.elenterius.magianaturalis.util.access.UserAccess;
import com.mojang.authlib.GameProfile;

import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.wands.IWandable;

public class ArcaneChestBlockEntity extends TileThaumcraft implements ISidedInventory, IWandable {

    private static final int[] sides = { 0, 1, 2, 3, 4, 5 };

    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;

    // UserAccess - accessLevel: 0 - nothing, 1 - access, 2 - administrator;
    public ArrayList<UserAccess> accessList = new ArrayList<>();

    private ArcaneChestType chestType = ArcaneChestType.GREAT_WOOD;
    private ItemStack[] inventory = new ItemStack[chestType.inventorySize];

    private UUID owner = Platform.NIL_UUID;
    private String ownerName;
    private String customName;

    @Override
    public boolean canUpdate() {
        return false;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID uuid) {
        owner = uuid;

        if (!worldObj.isRemote) {
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public String getOwnerName() {
        if (ownerName != null && !ownerName.isEmpty()) return ownerName;

        if (Platform.isServer() && owner != null) {
            GameProfile profile = Platform.findGameProfileByUUID(owner);
            if (profile != null) {
                return ownerName = profile.getName();
            } else {
                return ownerName = owner.toString();
            }
        }

        return "Unknown";
    }

    @Override
    public int getSizeInventory() {
        return chestType.inventorySize;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        if (inventory[index] == null) return null;

        if (!worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        if (inventory[index].stackSize <= amount) {
            ItemStack stack = inventory[index];
            inventory[index] = null;
            markDirty();
            return stack;
        }

        ItemStack stack = inventory[index].splitStack(amount);
        if (inventory[index].stackSize == 0) {
            inventory[index] = null;
        }

        markDirty();
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
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }

        if (!worldObj.isRemote) {
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;

        if (!worldObj.isRemote) {
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? customName : Platform.translate(chestType.translationKey);
    }

    @Override
    public boolean hasCustomInventoryName() {
        return customName != null && !customName.isEmpty();
    }

    public void setGuiName(String name) {
        customName = name;
    }

    public ArcaneChestType getChestType() {
        return chestType;
    }

    public void setChestType(ArcaneChestType type) {
        chestType = type;

        if (worldObj != null && !worldObj.isRemote) {
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound data) {
        owner = UUID.fromString(data.getString("owner"));
        ownerName = data.getString("owner_name");
        chestType = ArcaneChestType.parseId(data.getByte("Type"));
        accessList = NBTUtil.loadUserAccessFromNBT(data);

        if (inventory.length != getSizeInventory()) {
            inventory = Arrays.copyOf(inventory, getSizeInventory()); // update inventory size to match chest type
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound data) {
        data.setString("owner", owner != null ? owner.toString() : Platform.NIL_UUID.toString());
        data.setString("owner_name", getOwnerName());
        data.setByte("Type", chestType.id());
        NBTUtil.saveUserAccessToNBT(data, accessList);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inventory = NBTUtil.loadInventoryFromNBT(data, getSizeInventory());
        if (data.hasKey("CustomName")) {
            customName = data.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTUtil.saveInventoryToNBT(data, inventory);
        if (hasCustomInventoryName()) {
            data.setString("CustomName", customName);
        }
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
    public void openInventory() {
        if (numUsingPlayers < 0) {
            numUsingPlayers = 0;
        }

        numUsingPlayers += 1;
        worldObj.addBlockEvent(xCoord, yCoord, zCoord, MNBlocks.arcaneChest, 1, numUsingPlayers);
    }

    @Override
    public void closeInventory() {
        numUsingPlayers -= 1;
        worldObj.addBlockEvent(xCoord, yCoord, zCoord, MNBlocks.arcaneChest, 1, numUsingPlayers);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return sides;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return false;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        prevLidAngle = lidAngle;
        float angle = 0.1F;

        if (numUsingPlayers > 0 && lidAngle == 0.0F) worldObj.playSoundEffect(
            xCoord + 0.5D,
            yCoord + 0.5D,
            zCoord + 0.5D,
            "random.chestopen",
            0.5F,
            worldObj.rand.nextFloat() * 0.1F + 0.9F);

        if ((numUsingPlayers == 0 && lidAngle > 0.0F) || (numUsingPlayers > 0 && lidAngle < 1.0F)) {
            float currAngle = lidAngle;

            if (numUsingPlayers > 0) lidAngle += angle;
            else lidAngle -= angle;

            if (lidAngle > 1.0F) lidAngle = 1.0F;

            if (lidAngle < 0.5F && currAngle >= 0.5F) worldObj.playSoundEffect(
                xCoord + 0.5D,
                yCoord + 0.5D,
                zCoord + 0.5D,
                "random.chestclosed",
                0.5F,
                worldObj.rand.nextFloat() * 0.1F + 0.9F);

            if (lidAngle < 0.0F) lidAngle = 0.0F;
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int data) {
        if (id == 1) {
            numUsingPlayers = data;
            return true;
        }

        if (id == 2) {
            if (lidAngle < data / 10.0F) lidAngle = (data / 10.0F);
            return true;
        }
        return tileEntityInvalid;
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandStack, EntityPlayer player, int x, int y, int z, int side,
        int md) {
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandStack, EntityPlayer player) {
        if (Platform.isServer() && !world.restoringBlockSnapshots) {
            boolean hasAccess = false;
            if (player.capabilities.isCreativeMode || owner.equals(
                player.getGameProfile()
                    .getId())) {
                hasAccess = true;
            } else {
                hasAccess = accessList.contains(
                    new UserAccess(
                        player.getGameProfile()
                            .getId(),
                        (byte) 2));
            }
            if (!hasAccess) {
                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.DARK_PURPLE + Platform.translate("chat.magianaturalis.chest.resist")));
                return wandStack;
            }

            Block block = world.getBlock(xCoord, yCoord, zCoord);
            if (block == null) return wandStack;

            ItemStack chestStack = new ItemStack(MNBlocks.arcaneChest, 1, chestType.id());
            NBTUtil.saveInventoryToNBT(chestStack, inventory);
            if (!accessList.isEmpty()) {
                NBTUtil.saveUserAccessToNBT(chestStack, accessList);
            }

            // chest.breakBlock(world, xCoord, yCoord, zCoord, chest, blockMetadata);
            world.removeTileEntity(xCoord, yCoord, zCoord);
            world.setBlockToAir(xCoord, yCoord, zCoord);

            float f = 0.7F;
            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(
                world,
                (double) xCoord + d0,
                (double) yCoord + d1,
                (double) zCoord + d2,
                chestStack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
        return wandStack;
    }

    @Override
    public void onUsingWandTick(ItemStack wandStack, EntityPlayer player, int count) {}

    @Override
    public void onWandStoppedUsing(ItemStack wandStack, World world, EntityPlayer player, int count) {}

}
