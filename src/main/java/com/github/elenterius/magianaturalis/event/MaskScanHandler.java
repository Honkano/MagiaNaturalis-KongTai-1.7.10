package com.github.elenterius.magianaturalis.event;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.util.MaskHelper;
import com.github.elenterius.magianaturalis.util.MaskScanHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class MaskScanHandler {

    private static final boolean LOG = false;

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        if (LOG) System.out.println("[MN][Scan] handler 触发, action=" + event.action
            + ", isRemote=" + event.world.isRemote);

        if (!event.world.isRemote) return;

        // ★ 支持两种：右键空气 + 右键方块
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR
            && event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        EntityPlayer player = event.entityPlayer;

        if (player.getCurrentEquippedItem() != null) {
            if (LOG) System.out.println("[MN][Scan] 不是空手，跳过");
            return;
        }

        ItemStack mask = MaskHelper.getPrimalMask(player);
        if (mask == null) {
            if (LOG) System.out.println("[MN][Scan] 没戴原始面具，跳过");
            return;
        }

        int face = ItemMask.getPrimalFace(mask);
        if (LOG) System.out.println("[MN][Scan] 戴着原始面具, face=" + face);

        if (face != 0) {
            if (LOG) System.out.println("[MN][Scan] 不是元始脸（当前 " + face + "），跳过");
            return;
        }

        boolean ok = MaskScanHelper.tryScan(player);
        if (LOG) System.out.println("[MN][Scan] tryScan 返回 " + ok);

        if (ok) {
            player.worldObj.playSoundAtEntity(player, "thaumcraft:cameraticks",
                0.3F, 1.2F);
            event.setCanceled(true);
        }
    }
}