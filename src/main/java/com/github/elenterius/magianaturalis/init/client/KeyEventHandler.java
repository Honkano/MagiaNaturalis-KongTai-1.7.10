package com.github.elenterius.magianaturalis.init.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.item.focus.BuilderFocusItem;
import com.github.elenterius.magianaturalis.network.PacketHandler;
import com.github.elenterius.magianaturalis.network.packet.PacketAttitudeSwitch;
import com.github.elenterius.magianaturalis.network.packet.PacketKeyInput;
import com.github.elenterius.magianaturalis.network.packet.PacketPickedBlock;
import com.github.elenterius.magianaturalis.util.WorldUtil;

import baubles.api.BaublesApi;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
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
                id = 4;
            } else if (MNKeyBindings.MISC_KEY.isPressed()) {
                id = 5;
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

    // ==================================================
    // 【态度面具切换】按下 G 键触发
    //
    // 1.7.10 的 KeyInputEvent 在 cpw.mods.fml.common.gameevent.InputEvent 里。
    // 它不带 key / keyState 字段，要自己用 KeyBinding.isPressed() 判断。
    // ==================================================
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // 不是态度键 → 跳过
        if (!MNKeyBindings.ATTITUDE_SWITCH_KEY.isPressed()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (mc.currentScreen != null) return;

        EntityPlayer player = mc.thePlayer;

        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles == null) return;

            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemMask
                    && stack.getItemDamage() == ItemMask.META_ATTITUDE) {

                    // ★ 1. 客户端算好目标 face，本地立即切
                    int currentFace = ItemMask.getAttitudeFace(stack);
                    int nextFace = (currentFace + 1) % 4;
                    ItemMask.setAttitudeFace(stack, nextFace);

                    // ★ 2. 发包给服务端（带目标 face）
                    PacketHandler.network.sendToServer(new PacketAttitudeSwitch.AttitudeSwitchMessage(nextFace));

                    int face = ItemMask.getAttitudeFace(stack);
                    String faceName = StatCollector.translateToLocal("item.magianaturalis.mask.3.face." + face);

                    // 聊天提示
                    player.addChatComponentMessage(
                        new ChatComponentText(
                            "§5" + StatCollector.translateToLocal("item.magianaturalis.mask.3.current")
                                + "§d"
                                + faceName));

                    // 翻书音效
                    player.worldObj
                        .playSound(player.posX, player.posY, player.posZ, "thaumcraft:page", 0.6F, 1.2F, false);

                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
