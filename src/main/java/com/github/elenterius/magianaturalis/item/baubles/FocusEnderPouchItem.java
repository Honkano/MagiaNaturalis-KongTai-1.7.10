package com.github.elenterius.magianaturalis.item.baubles;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.util.NBTUtil;
import com.github.elenterius.magianaturalis.util.Platform;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.items.wands.ItemFocusPouch;

public class FocusEnderPouchItem extends ItemFocusPouch implements IBauble {

    public FocusEnderPouchItem() {
        setMaxStackSize(1);
        setHasSubtypes(false);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registry) {
        icon = registry.registerIcon(getIconString());
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Platform.isServer()) {
            InventoryEnderChest invEnderchest = player.getInventoryEnderChest();
            if (invEnderchest != null) player.displayGUIChest(invEnderchest);
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int ticks, boolean bool) {
        // TODO: Remove this Hacky/Brutforce Way and asm the shit out of thaumcraft
        if (Platform.isServer() && entity instanceof EntityPlayer && entity.ticksExisted % 5 == 0) {
            InventoryEnderChest invEnderChest = ((EntityPlayer) entity).getInventoryEnderChest();
            if (invEnderChest != null) {
                NBTTagCompound data = NBTUtil.getOrCreate(stack);
                boolean isInvDirty = data.getBoolean("isInvDirty");

                if (isInvDirty) // Push InvPouch to InvEnderChest
                {
                    NBTTagList dataList = data.getTagList("Inventory", 10);
                    invEnderChest.loadInventoryFromNBT(dataList);
                    data.setBoolean("isInvDirty", false);
                } else // Push InvEnderChest to InvPouch
                {
                    NBTTagList dataListChest = invEnderChest.saveInventoryToNBT();
                    NBTTagList dataListPouch = data.getTagList("Inventory", 10);

                    if (!dataListChest.equals(dataListPouch)) {
                        data.setTag("Inventory", dataListChest);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack[] getInventory(ItemStack stack) {
        ItemStack[] stackList = new ItemStack[27];
        if (stack.hasTagCompound()) {
            NBTTagList dataList = stack.stackTagCompound.getTagList("Inventory", 10);
            for (int index = 0; index < dataList.tagCount(); index++) {
                NBTTagCompound data = dataList.getCompoundTagAt(index);
                byte slot = data.getByte("Slot");
                if (slot >= 0 && slot < stackList.length) stackList[slot] = ItemStack.loadItemStackFromNBT(data);
            }
        }
        return stackList;
    }

    @Override
    public void setInventory(ItemStack stack, ItemStack[] stackList) {
        NBTTagList dataList = new NBTTagList();
        for (int index = 0; index < stackList.length; index++) {
            if (stackList[index] != null) {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) index);
                stackList[index].writeToNBT(data);
                dataList.appendTag(data);
            }
        }
        stack.setTagInfo("Inventory", dataList);
        stack.stackTagCompound.setBoolean("isInvDirty", true);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        onUpdate(stack, player.worldObj, player, player.ticksExisted, false);
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

}
