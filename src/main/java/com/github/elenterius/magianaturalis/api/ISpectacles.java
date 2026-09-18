package com.github.elenterius.magianaturalis.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Equipped head slot items that extend this class will draw info on HUD.
 *
 * @author TrinaryBrain
 */
public interface ISpectacles {

    /**
     * If this method returns true the info will be drawn on the HUD
     */
    boolean drawSpectacleHUD(ItemStack itemStack, EntityLivingBase player);

}
