package com.github.elenterius.magianaturalis.mixin;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.elenterius.magianaturalis.client.aspect.AspectIconRenderer;
import com.github.elenterius.magianaturalis.client.aspect.ContainerSlotAspectOverlay;
import com.github.elenterius.magianaturalis.client.display.DisplayToggleManager;
import com.github.elenterius.magianaturalis.client.display.DisplayType;

import thaumcraft.api.aspects.Aspect;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {

    @Shadow
    private Slot theSlot;

    @Inject(
        method = "drawScreen(IIF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGuiContainerForegroundLayer(II)V",
            shift = At.Shift.AFTER
        )
    )
    private void magiaNaturalis$renderSlotAspectOverlay(
            int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {

        GuiContainer self = (GuiContainer) (Object) this;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_DEPTH_TEST);   // ← 关深度测试
        try {
            ContainerSlotAspectOverlay.renderSlotIcons(self);
        } finally {
            GL11.glPopAttrib();
        }
    }

    @Inject(method = "drawScreen(IIF)V", at = @At("TAIL"))
    private void magiaNaturalis$renderHeldAspectOverlay(
            int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {

        if (!DisplayToggleManager.getInstance().isEnabled(DisplayType.REVEAL_SLOT_ASPECTS)) return;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        ItemStack held = player.inventory.getItemStack();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_DEPTH_TEST);   // ← 关深度测试
        try {
            if (held != null) {
                ContainerSlotAspectOverlay.renderHeldIcon(mouseX, mouseY);
            } else if (this.theSlot != null && this.theSlot.getStack() != null) {
                renderTooltipAspectPanel(mouseX, mouseY, this.theSlot.getStack());
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    /**
     * 在 Tooltip 正上方紧贴一个面板，宽度与 Tooltip 相同，居中显示图标。
     */
    private void renderTooltipAspectPanel(int mouseX, int mouseY, ItemStack stack) {
        Aspect aspect = ContainerSlotAspectOverlay.getDisplayedAspectStatic(stack);
        if (aspect == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRenderer;

        @SuppressWarnings("unchecked")
        List<String> tooltip = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

        int contentWidth = 0;
        for (String line : tooltip) {
            int w = fr.getStringWidth(line);
            if (w > contentWidth) contentWidth = w;
        }
        contentWidth += 30;

        int contentHeight = 8;
        if (tooltip.size() > 1) {
            contentHeight += 2 + (tooltip.size() - 1) * 10;
        }

        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        int tipContentX = mouseX + 12;
        if (tipContentX + contentWidth > screenW - 4) {
            tipContentX = mouseX - 16 - contentWidth;
        }

        int tipContentY = mouseY - 12;
        if (tipContentY + contentHeight + 4 > screenH) {
            tipContentY = screenH - contentHeight - 4;
        }

        int tipLeft   = tipContentX - 4;
        int tipRight  = tipContentX + contentWidth + 3;
        int tipTop    = tipContentY - 4;
        int tipBottom = tipContentY + contentHeight + 3;

        int panelWidth  = tipRight - tipLeft;
        int panelHeight = 22;
        int gap         = 2;

        int panelX = tipLeft;
        int panelY = tipTop - gap - panelHeight;

        boolean below = false;
        if (panelY < 2) {
            panelY = tipBottom + gap;
            below = true;
            if (panelY + panelHeight > screenH - 2) return;
        }

        int bgColor   = 0xF0100010;
        int borderTop = 0x505000FF;
        int borderBot = 0x5028007F;

        Gui.drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, bgColor);

        Gui.drawRect(panelX, panelY, panelX + panelWidth, panelY + 1, borderTop);
        Gui.drawRect(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, borderBot);
        Gui.drawRect(panelX, panelY, panelX + 1, panelY + panelHeight, borderTop);
        Gui.drawRect(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, borderBot);

        int iconSize = 16;
        int iconX = panelX + (panelWidth - iconSize) / 2;
        int iconY = panelY + (panelHeight - iconSize) / 2;
        AspectIconRenderer.renderAspect(aspect, iconX, iconY, iconSize, 1.0F);
    }
}