package com.github.elenterius.magianaturalis.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.github.elenterius.magianaturalis.init.MNItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ScytheBlockHandler {

    public static void register() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ScytheBlockHandler());
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack heldItem = player.getCurrentEquippedItem();

        // 判断：手持白瞳者之镰，并且正在格挡（isUsingItem 说明右键按住中）
        if (heldItem != null && heldItem.getItem() == MNItems.herobrinesScythe && player.isUsingItem()) {

            // 【新增】记录原本的伤害数值，用于聊天提示
            float blockedAmount = event.ammount;

            // 100% 完全免疫
            event.ammount = 0;

            // 【新增】在服务端发送聊天提示（这样所有玩家都能看到，且同步可靠）
            if (!player.worldObj.isRemote) {
                String msg = StatCollector.translateToLocalFormatted(
                    "chat.magianaturalis.block.damage",
                    EnumChatFormatting.AQUA + String.format("%.1f", blockedAmount) + EnumChatFormatting.RESET);
                player.addChatMessage(new ChatComponentText(msg));
            }

            // 播放格挡音效（只在客户端，避免双声道重复）
            if (player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.block", 1.0F, 1.0F);
            }
        }
    }
}
