package com.github.elenterius.magianaturalis.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSource;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.network.packet.PacketAttitudeSwitch.AttitudeSwitchMessage;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketAttitudeSwitch implements IMessageHandler<AttitudeSwitchMessage, IMessage> {

    private static final int[][] COSTS = { { 0, 0 }, // 无态度
        { 2, 1 }, // 友善
        { 4, 2 }, // 威严
        { 3, 1 }, // 隐匿
    };

    private static final float MIN_HP_AFTER = 3.0F;

    @Override
    public IMessage onMessage(AttitudeSwitchMessage message, MessageContext ctx) {
        if (!ctx.side.isServer()) return null;

        EntityPlayer player = ctx.getServerHandler().playerEntity;
        if (player == null) return null;

        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles == null) return null;

            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack == null) continue;
                if (!(stack.getItem() instanceof ItemMask)) continue;
                if (stack.getItemDamage() != ItemMask.META_ATTITUDE) continue;

                int current = ItemMask.getAttitudeFace(stack);
                int next = message.targetFace;

                // 已同步到目标 → 跳过（防止重复处理）
                if (current == next) {
                    return null;
                }

                int hpCost = COSTS[next][0];
                int hungerCost = COSTS[next][1];

                float hp = player.getHealth();
                int food = player.getFoodStats()
                    .getFoodLevel();

                // ---------- 消耗检查（创造模式免消耗） ----------
                if (!player.capabilities.isCreativeMode) {
                    boolean noHeart = false;
                    if (hp <= hpCost) noHeart = true;
                    else if (hp - hpCost < MIN_HP_AFTER) noHeart = true;
                    if (hungerCost > 0 && food < hungerCost) noHeart = true;

                    if (noHeart) {
                        player
                            .addChatComponentMessage(new ChatComponentTranslation("msg.magianaturalis.mask.no_heart"));
                        player.attackEntityFrom(new EntityDamageSource("mask_attitude", player), Float.MAX_VALUE);
                        if (player.getHealth() > 0) player.setHealth(0);
                        return null;
                    }

                    if (hpCost > 0) player.setHealth(hp - hpCost);
                    if (hungerCost > 0) player.getFoodStats()
                        .setFoodLevel(food - hungerCost);
                }

                // ★ 直接 set 到目标 face（不 cycleAttitude）
                ItemMask.setAttitudeFace(stack, next);
                baubles.markDirty();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class AttitudeSwitchMessage implements IMessage {

        public int targetFace;

        public AttitudeSwitchMessage() {
            super();
        }

        public AttitudeSwitchMessage(int targetFace) {
            super();
            this.targetFace = targetFace;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            targetFace = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeByte(targetFace);
        }
    }
}
