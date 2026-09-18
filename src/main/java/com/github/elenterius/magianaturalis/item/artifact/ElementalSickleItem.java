package com.github.elenterius.magianaturalis.item.artifact;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import thaumcraft.api.IRepairable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigItems;

public class ElementalSickleItem extends SickleItem implements IRepairable {

    public ElementalSickleItem() {
        super(ThaumcraftApi.toolMatElemental);
        areaSize = 9;
        collectLoot = true;
        colorLoot = 0;
        abundanceLevel = 2;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack resourceStack) {
        return resourceStack.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 2))
            || super.getIsRepairable(itemStack, resourceStack);
    }

}
