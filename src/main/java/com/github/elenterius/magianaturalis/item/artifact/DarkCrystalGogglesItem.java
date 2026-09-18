package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.api.IRevealInvisible;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.IRevealer;

public class DarkCrystalGogglesItem extends ItemArmor
    implements IRepairable, IVisDiscountGear, IRevealer, IGoggles, IRevealInvisible {

    public DarkCrystalGogglesItem() {
        super(ThaumcraftApi.armorMatSpecial, 4, 0);
        setMaxDamage(350);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);
        list.add(
            EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tc.visdiscount")
                + ": "
                + getVisDiscount(stack, player, null)
                + "%");
        list.add(
            EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tc.visdiscount")
                + " (Perditio): "
                + getVisDiscount(stack, player, Aspect.ENTROPY)
                + "%");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return MagiaNaturalis.rlString("textures/models/armor/dark_crystal_goggles.png");
    }

    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.rare;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (player.isPotionActive(Potion.blindness)) {
            player.removePotionEffect(Potion.blindness.id);
        }
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        int i = 5;
        if (aspect == Aspect.ENTROPY) {
            i = player.worldObj.isDaytime() ? 9 : 7;
        }
        return i;
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(Items.gold_ingot)) || super.getIsRepairable(stack, stack2);
    }

    @Override
    public boolean showInvisibleEntity(ItemStack itemStack, EntityLivingBase player, EntityLivingBase invisibleEntity) {
        return true;
    }

}
