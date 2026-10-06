package com.github.elenterius.magianaturalis.client.aspect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.github.elenterius.magianaturalis.client.display.DisplayToggleManager;
import com.github.elenterius.magianaturalis.client.display.DisplayType;
import com.github.elenterius.magianaturalis.init.MNConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

@SideOnly(Side.CLIENT)
public final class ContainerSlotAspectOverlay {

    private ContainerSlotAspectOverlay() {
    }

    /** 槽位图标（矩阵处于 translate(guiLeft, guiTop, 0) 状态，用相对坐标） */
    public static void renderSlotIcons(GuiContainer gui) {
        if (!isEnabled()) return;

        int overlaySize = MNConfig.getSlotOverlaySize();
        float alpha = MNConfig.getSlotOverlayAlpha();
        if (overlaySize <= 0 || alpha <= 0.0F) return;

        MNConfig.OverlayPosition position = MNConfig.getSlotOverlayPosition();

        for (Slot slot : gui.inventorySlots.inventorySlots) {
            Aspect aspect = getDisplayedAspectStatic(slot.getStack());
            if (aspect == null) continue;

            int x = slot.xDisplayPosition + position.getOffsetX(overlaySize);
            int y = slot.yDisplayPosition + position.getOffsetY(overlaySize);

            AspectIconRenderer.renderAspect(aspect, x, y, overlaySize, alpha);
        }
    }

    /** 手持物品图标（绝对屏幕坐标，鼠标位置附近） */
    public static void renderHeldIcon(int mouseX, int mouseY) {
        if (!isEnabled()) return;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        ItemStack held = player.inventory.getItemStack();
        Aspect aspect = getDisplayedAspectStatic(held);
        if (aspect == null) return;

        int overlaySize = MNConfig.getSlotOverlaySize();
        float alpha = MNConfig.getSlotOverlayAlpha();
        if (overlaySize <= 0 || alpha <= 0.0F) return;

        MNConfig.OverlayPosition position = MNConfig.getSlotOverlayPosition();

        // 拖拽物品渲染在 mouseX - 8, mouseY - 8 处（左上角）
        int x = mouseX - 8 + position.getOffsetX(overlaySize);
        int y = mouseY - 8 + position.getOffsetY(overlaySize);

        AspectIconRenderer.renderAspect(aspect, x, y, overlaySize, alpha);
    }

    private static boolean isEnabled() {
        return DisplayToggleManager.getInstance().isEnabled(DisplayType.REVEAL_SLOT_ASPECTS);
    }

    public static Aspect getDisplayedAspectStatic(ItemStack stack) {
        if (stack == null) return null;
        if (!(stack.getItem() instanceof IEssentiaContainerItem)) return null;
        if (stack.getItem() instanceof thaumcraft.common.items.ItemCrystalEssence) return null;

        IEssentiaContainerItem containerItem = (IEssentiaContainerItem) stack.getItem();
        AspectList storedAspects = containerItem.getAspects(stack);
        if (storedAspects == null || storedAspects.size() <= 0) return null;

        Aspect[] aspects = storedAspects.getAspects();
        if (aspects.length <= 0) return null;

        int index = 0;
        while (index < aspects.length && aspects[index] == null) index++;
        return index < aspects.length ? aspects[index] : null;
    }


    /** 悬停槽时，在鼠标右侧显示图标（贴 Tooltip 附近） */
    public static void renderHoveredIcon(int mouseX, int mouseY, ItemStack stack) {
        if (!isEnabled()) return;

        Aspect aspect = getDisplayedAspectStatic(stack);
        if (aspect == null) return;

        int overlaySize = MNConfig.getSlotOverlaySize();
        float alpha = MNConfig.getSlotOverlayAlpha();
        if (overlaySize <= 0 || alpha <= 0.0F) return;

        // 放在鼠标右侧略下位置，避开 Tooltip 主区域
        int x = mouseX + 14;
        int y = mouseY - 8;

        // 使用固定 16×16 的大图标，比槽位图标更醒目
        AspectIconRenderer.renderAspect(aspect, x, y, 16, alpha);
    }


}