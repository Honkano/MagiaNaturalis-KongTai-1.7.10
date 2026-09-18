package com.github.elenterius.magianaturalis.init.client;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;

import com.github.elenterius.magianaturalis.item.focus.BuilderFocusItem;
import com.github.elenterius.magianaturalis.network.PacketHandler;
import com.github.elenterius.magianaturalis.network.packet.PacketKeyInput;
import com.github.elenterius.magianaturalis.network.packet.PacketPickedBlock;
import com.github.elenterius.magianaturalis.util.WorldUtil;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

public final class KeyEventHandler {

    public static void register() {
        FMLCommonHandler.instance()
            .bus()
            .register(new KeyEventHandler());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER) return;

        if (event.phase == TickEvent.Phase.START && FMLClientHandler.instance()
            .getClient().inGameHasFocus) {
            byte id = 0;

            if (MNKeyBindings.DECREASE_SIZE_KEY.isPressed()) {
                id = 2;
            } else if (MNKeyBindings.INCREASE_SIZE_KEY.isPressed()) {
                id = 3;
            } else if (GuiScreen.isCtrlKeyDown() && MNKeyBindings.MISC_KEY.isPressed()) {
                id = 4; // Mode
            } else if (MNKeyBindings.MISC_KEY.isPressed()) {
                id = 5; // Shape
            } else if (MNKeyBindings.PICK_BLOCK_KEY.isPressed()) {
                EntityPlayer player = event.player;
                if (player != null && player.getCurrentEquippedItem() != null
                    && player.getCurrentEquippedItem()
                        .getItem() instanceof ItemWandCasting) {
                    ItemWandCasting wand = (ItemWandCasting) player.getCurrentEquippedItem()
                        .getItem();
                    ItemFocusBasic focus = wand.getFocus(player.getCurrentEquippedItem());
                    if (focus instanceof BuilderFocusItem) {
                        MovingObjectPosition target = WorldUtil.getMovingObjectPositionFromPlayer(
                            event.player.worldObj,
                            event.player,
                            BuilderFocusItem.reachDistance,
                            true);
                        if (target != null) {
                            if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                                int x = target.blockX;
                                int y = target.blockY;
                                int z = target.blockZ;

                                Block block = event.player.worldObj.getBlock(x, y, z);
                                byte meta = (byte) event.player.worldObj.getBlockMetadata(x, y, z);
                                if (block == Blocks.double_plant)
                                    meta = (byte) block.getDamageValue(event.player.worldObj, x, y, z);
                                if (meta < 0 || meta > 15) meta = 0;

                                PacketHandler.network.sendToServer(
                                    new PacketPickedBlock.PickedBlockMessage(Block.getIdFromBlock(block), meta));
                            }
                        }
                    }
                }
            }

            if (id > 0) {
                EntityPlayer player = event.player;
                if (player != null && player.getCurrentEquippedItem() != null
                    && player.getCurrentEquippedItem()
                        .getItem() instanceof ItemWandCasting) {
                    ItemWandCasting wand = (ItemWandCasting) player.getCurrentEquippedItem()
                        .getItem();
                    ItemFocusBasic focus = wand.getFocus(player.getCurrentEquippedItem());
                    if (focus instanceof BuilderFocusItem) {
                        PacketHandler.network.sendToServer(new PacketKeyInput.KeyInputMessage(id));
                    }
                }
            }
        }
    }

}
