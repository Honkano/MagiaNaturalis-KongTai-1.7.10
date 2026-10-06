package com.github.elenterius.magianaturalis.client.aspect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.client.display.DisplayToggleManager;
import com.github.elenterius.magianaturalis.client.display.DisplayType;
import com.github.elenterius.magianaturalis.init.MNConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

@SideOnly(Side.CLIENT)
public final class HotbarSlotAspectOverlay {

    private static final int HOTBAR_WIDTH  = 182;
    private static final int HOTBAR_HEIGHT = 22;
    private static final int SLOT_INSET    = 3;
    private static final int SLOT_STRIDE   = 20;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        if (!DisplayToggleManager.getInstance().isEnabled(DisplayType.REVEAL_SLOT_ASPECTS)) return;

        int overlaySize = MNConfig.getSlotOverlaySize();
        float alpha = MNConfig.getSlotOverlayAlpha();
        if (overlaySize <= 0 || alpha <= 0.0F) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        MNConfig.OverlayPosition position = MNConfig.getSlotOverlayPosition();
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        int hotbarLeft = (screenW - HOTBAR_WIDTH) / 2;
        int hotbarTop  = screenH - HOTBAR_HEIGHT;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = player.inventory.mainInventory[i];
                Aspect aspect = ContainerSlotAspectOverlay.getDisplayedAspectStatic(stack);
                if (aspect == null) continue;

                int slotX = hotbarLeft + SLOT_INSET + i * SLOT_STRIDE;
                int slotY = hotbarTop + SLOT_INSET;

                int x = slotX + position.getOffsetX(overlaySize);
                int y = slotY + position.getOffsetY(overlaySize);

                AspectIconRenderer.renderAspect(aspect, x, y, overlaySize, alpha);
            }
        } finally {
            GL11.glPopAttrib();
        }
    }
}