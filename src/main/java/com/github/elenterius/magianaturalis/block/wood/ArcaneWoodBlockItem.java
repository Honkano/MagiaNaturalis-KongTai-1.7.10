package com.github.elenterius.magianaturalis.block.wood;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ArcaneWoodBlockItem extends ItemBlock {

    protected static final String[] translationKeys = { "gw_planks", "gw_orn", "sw_planks.0", "sw_planks.1",
        "gw_gold_orn.0", "gw_gold_orn.1", "gw_gold_trim" };

    public ArcaneWoodBlockItem(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + translationKeys[stack.getItemDamage()];
    }

}
