package com.github.elenterius.magianaturalis.event;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.util.MaskHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.wands.ItemWandCasting;

public class PrimalSwitchHandler {

    private static final Aspect[] FACE_ASPECT = { null, // 0 元始
        Aspect.FIRE, // 1 火
        Aspect.AIR, // 2 风
        Aspect.ENTROPY, // 3 混沌
        Aspect.EARTH, // 4 大地
        Aspect.ORDER, // 5 秩序
        Aspect.WATER // 6 水
    };

    private static final int COST_SINGLE = 50;
    private static final int COST_PRIMAL_PER = 1000;

    /** 防抖：200ms 内同一端只触发一次 */
    private static final long DEBOUNCE_MS = 200;
    private static long lastClient = 0;
    private static long lastServer = 0;

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR
            && event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        EntityPlayer player = event.entityPlayer;

        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || !(held.getItem() instanceof ItemWandCasting)) return;
        if (!player.isSneaking()) return;

        ItemStack mask = MaskHelper.getPrimalMask(player);
        if (mask == null) return;

        // ★ 防抖：200ms 内同端只跑一次
        long now = System.currentTimeMillis();
        if (event.world.isRemote) {
            if (now - lastClient < DEBOUNCE_MS) return;
            lastClient = now;
        } else {
            if (now - lastServer < DEBOUNCE_MS) return;
            lastServer = now;
        }

        int currentFace = ItemMask.getPrimalFace(mask);
        int nextFace = (currentFace + 1) % 7;

        ItemWandCasting casting = (ItemWandCasting) held.getItem();

        if (!consumeVis(casting, held, nextFace)) {
            if (event.world.isRemote) {
                player.addChatComponentMessage(new ChatComponentTranslation("msg.magianaturalis.mask.primal.no_vis"));
            }
            return;
        }

        ItemMask.setPrimalFace(mask, nextFace);

        if (event.world.isRemote) {
            player.worldObj.playSoundAtEntity(player, "thaumcraft:cameraticks", 0.3F, 1.0F);
            String faceName = StatCollector.translateToLocal("item.magianaturalis.mask.4.face." + nextFace);
            player.addChatComponentMessage(
                new ChatComponentTranslation("msg.magianaturalis.mask.primal.switch", faceName));
        }
    }

    private static boolean consumeVis(ItemWandCasting casting, ItemStack wand, int targetFace) {
        if (targetFace == 0) {
            List<Aspect> primals = Aspect.getPrimalAspects();
            for (Aspect a : primals) {
                if (casting.getVis(wand, a) < COST_PRIMAL_PER) return false;
            }
            for (Aspect a : primals) {
                casting.storeVis(wand, a, casting.getVis(wand, a) - COST_PRIMAL_PER);
            }
            return true;
        } else {
            Aspect a = FACE_ASPECT[targetFace];
            if (a == null) return false;
            if (casting.getVis(wand, a) < COST_SINGLE) return false;
            casting.storeVis(wand, a, casting.getVis(wand, a) - COST_SINGLE);
            return true;
        }
    }
}
