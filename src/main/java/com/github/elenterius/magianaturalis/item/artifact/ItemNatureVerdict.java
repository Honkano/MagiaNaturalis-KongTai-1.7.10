package com.github.elenterius.magianaturalis.item.artifact;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IWarpingGear;

/**
 * 自然裁决 —— 银木与宏伟之木锻造的终极神剑。
 * 机制参考 TC4 血腥之刃，但效果更偏向自然主题。
 */
public class ItemNatureVerdict extends ItemSword implements IRepairable, IWarpingGear {

    // 材质：等级 4、耐久 300（比血腥之刃高）、效率 8.0、伤害 4.0、附魔性 25
    public static final ToolMaterial MATERIAL_VERDICT = EnumHelper
        .addToolMaterial("MN_VERDICT", 4, 300, 8.0F, 4.0F, 25);

    public IIcon icon;

    public ItemNatureVerdict() {
        super(MATERIAL_VERDICT);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        icon = ir.registerIcon(MagiaNaturalis.rlString("nature_verdict"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return icon;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack stack2) {
        return super.getIsRepairable(stack, stack2);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        super.onUpdate(stack, world, entity, slot, held);
        // 每 20 tick 自动修复 1 点耐久
        if (stack.isItemDamaged() && entity != null
            && entity.ticksExisted % 20 == 0
            && entity instanceof EntityLivingBase) {
            stack.damageItem(-1, (EntityLivingBase) entity);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        // 攻击时给敌人施加"虚弱"和"饥饿"（保留血腥之刃的诅咒感）
        if (!target.worldObj.isRemote && (!(target instanceof EntityPlayer) || !(attacker instanceof EntityPlayer)
            || MinecraftServer.getServer()
                .isPVPEnabled())) {
            try {
                target.addPotionEffect(new PotionEffect(Potion.weakness.getId(), 80));
                target.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 160));
            } catch (Exception ignored) {}
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public int getWarp(ItemStack stack, EntityPlayer player) {
        return 1; // 比血腥之刃低一点，因为它是"自然裁决"不是邪术
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("enchantment.special.sapgreat"));
        super.addInformation(stack, player, list, advanced);
    }
}
