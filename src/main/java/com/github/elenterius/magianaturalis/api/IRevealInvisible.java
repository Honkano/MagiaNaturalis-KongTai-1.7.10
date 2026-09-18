package com.github.elenterius.magianaturalis.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Equipped head slot items that implement this class will reveal any Invisible LivingEntity to the player.
 *
 * @author TrinaryBrain
 */
public interface IRevealInvisible {

    /**
     * If this method returns true invisible Entities will be visible.
     */
    boolean showInvisibleEntity(ItemStack itemStack, EntityLivingBase player, EntityLivingBase invisibleEntity);

}
