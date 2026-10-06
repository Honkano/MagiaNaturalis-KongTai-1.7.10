package com.github.elenterius.magianaturalis.client.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IBaubleRender;

/**
 * 渲染玩家身上所有 IBaubleRender 饰品。
 * 
 * ⚠️ 如果你装了 Botania，Botania 自己会做这件事，
 * 那就不需要注册这个 Handler，否则会双重渲染。
 * 
 * 如果你想独立于 Botania，就注册它。
 */
@SideOnly(Side.CLIENT)
public class BaubleRenderHandler {

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.entityPlayer;
        InventoryBaubles inv = PlayerHandler.getPlayerBaubles(player);

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof IBaubleRender) {
                GL11.glPushMatrix();
                GL11.glColor4f(1F, 1F, 1F, 1F);
                ((IBaubleRender) stack.getItem())
                    .onPlayerBaubleRender(stack, event, IBaubleRender.RenderType.HEAD);
                GL11.glPopMatrix();
            }
        }
    }
}