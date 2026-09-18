package com.github.elenterius.magianaturalis.item.artifact;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import thaumcraft.api.IRepairable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigItems;

public class ThaumiumSickleItem extends SickleItem implements IRepairable {

    public ThaumiumSickleItem() {
        super(ThaumcraftApi.toolMatThaumium);
        areaSize = 2;
    }

    @Override
    public int getItemEnchantability() {
        return 5;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack stackMaterial) {
        return stackMaterial.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 2))
            || super.getIsRepairable(stack, stackMaterial);
    }

}
