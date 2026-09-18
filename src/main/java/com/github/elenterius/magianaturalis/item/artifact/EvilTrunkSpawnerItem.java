package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;
import com.github.elenterius.magianaturalis.util.Platform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EvilTrunkSpawnerItem extends Item {

    protected static String[] NAME = new String[] { "corrupted", "sinister", "demonic", "tainted" };

    public EvilTrunkSpawnerItem() {
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public Item setTextureName(String icon) {
        iconString = MagiaNaturalis.rlString("empty_texture");
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("inventory")) {
            list.add(Platform.translate("item.TrunkSpawner.text.1"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + NAME[stack.getItemDamage()];
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (Platform.isClient()) return false;

        Block block = world.getBlock(x, y, z);
        x += net.minecraft.util.Facing.offsetsXForSide[side];
        y += net.minecraft.util.Facing.offsetsYForSide[side];
        z += net.minecraft.util.Facing.offsetsZForSide[side];

        double yOffset = 0D;
        if (side == 1 && !block.isAir(world, x, y, z) && block.getRenderType() == 11) {
            yOffset = 0.5D;
        }

        EntityEvilTrunk entity = new EntityEvilTrunk(world, stack.getItemDamage());

        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("inventory")) {
            NBTTagList list = stack.stackTagCompound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
            entity.inventory.readFromNBT(list);
        }

        entity.setOwner(player);

        if (stack.hasDisplayName()) {
            entity.setCustomNameTag(stack.getDisplayName());
        }

        entity.setLocationAndAngles(
            x,
            y + yOffset,
            z,
            MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360F),
            0F);
        entity.rotationYawHead = entity.rotationYaw;
        entity.renderYawOffset = entity.rotationYaw;

        if (world.spawnEntityInWorld(entity)) {
            entity.playLivingSound();
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize -= 1;
            }
        }

        return true;
    }

}
