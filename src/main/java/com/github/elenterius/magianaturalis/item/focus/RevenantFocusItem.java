package com.github.elenterius.magianaturalis.item.focus;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.entity.EntityZombieExtended;
import com.github.elenterius.magianaturalis.util.Platform;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.EntityUtils;

public class RevenantFocusItem extends ItemFocusBasic {

    protected static final AspectList VIS_COST = new AspectList().add(Aspect.EARTH, 450)
        .add(Aspect.ENTROPY, 350)
        .add(Aspect.WATER, 200);
    protected static final FocusUpgradeType[] FOCUS_UPGRADES = { FocusUpgradeType.potency, FocusUpgradeType.frugal };

    public RevenantFocusItem() {
        super();
    }

    @Override
    public void registerIcons(IIconRegister registry) {
        icon = registry.registerIcon(getIconString());
    }

    @Override
    public int getFocusColor(ItemStack focusStack) {
        return 0x385226;
    }

    @Override
    public AspectList getVisCost(ItemStack focusStack) {
        return VIS_COST;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focusStack, int rank) {
        return FOCUS_UPGRADES;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player,
        MovingObjectPosition movingObjectPosition) {
        player.swingItem();

        if (Platform.isClient()) return wandStack;

        Entity pointedEntity = EntityUtils.getPointedEntity(player.worldObj, player, 32.0D, EntityZombieExtended.class);
        if (pointedEntity instanceof EntityLivingBase) {
            double px = player.posX;
            double py = player.boundingBox.minY;
            double pz = player.posZ;
            px -= MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
            pz -= MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
            Vec3 vec3d = player.getLook(1.0F);
            px += vec3d.xCoord * 0.5D;
            pz += vec3d.zCoord * 0.5D;

            if (!world.isRemote) {
                if (pointedEntity instanceof EntityPlayer && !MinecraftServer.getServer()
                    .isPVPEnabled()) return wandStack;
                EntityZombieExtended zombie = new EntityZombieExtended(world);
                zombie.setOwner(player.getCommandSenderName());
                zombie.setChild(true);

                if (world.rand.nextBoolean()) zombie.setVillager(true);

                zombie.setLocationAndAngles(px, py + 0.1, pz, player.rotationYaw, 0.0F);
                zombie.setExperienceValue(0);

                zombie.setTarget(pointedEntity);
                zombie.setAttackTarget((EntityLivingBase) pointedEntity);
                // player.setRevengeTarget((EntityLivingBase) pointedEntity); TODO: Use this for Bone Flute

                Item wandItem = wandStack.getItem();
                if (wandItem instanceof ItemWandCasting) {
                    ItemStack focusItem = ((ItemWandCasting) wandItem).getFocusItem(wandStack);
                    int potency = EnchantmentHelper.getEnchantmentLevel(ThaumcraftApi.enchantPotency, focusItem);
                    zombie.getEntityAttribute(SharedMonsterAttributes.attackDamage)
                        .setBaseValue(2.0D + 0.5 * potency);

                    if (ThaumcraftApiHelper.consumeVisFromWand(wandStack, player, getVisCost(focusItem), true, false)
                        && world.spawnEntityInWorld(zombie)) {
                        world.playAuxSFXAtEntity(null, 1016, (int) px, (int) py, (int) pz, 0);
                        world.playSoundAtEntity(zombie, "thaumcraft:ice", 0.2F, 0.95F + world.rand.nextFloat() * 0.1F);
                    } else {
                        world.playSoundAtEntity(
                            player,
                            "thaumcraft:wandfail",
                            0.2F,
                            0.8F + world.rand.nextFloat() * 0.1F);
                    }
                }
            }

            player.swingItem();
        }

        return wandStack;
    }

}
