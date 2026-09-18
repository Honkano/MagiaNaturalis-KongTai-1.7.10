package com.github.elenterius.magianaturalis.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestType;
import com.github.elenterius.magianaturalis.client.render.RenderUtil;
import com.github.elenterius.magianaturalis.container.ContainerArcaneChest;

public class ArcaneChestGui extends GuiContainer {

    private static final ResourceLocation GREATWOOD_TEXTURE = MagiaNaturalis
        .rl("textures/gui/container/chest_greatwood.png");
    private static final ResourceLocation SILVERWOOD_TEXTURE = MagiaNaturalis
        .rl("textures/gui/container/chest_silverwood.png");

    private final ArcaneChestBlockEntity chest;

    public ArcaneChestGui(InventoryPlayer playerInventory, ArcaneChestBlockEntity tile) {
        super(new ContainerArcaneChest(playerInventory, tile));
        chest = tile;

        if (tile.getChestType() == ArcaneChestType.SILVER_WOOD) {
            xSize = 212;
            ySize = 240;
        } else {
            ySize = 222;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(chest.getInventoryName(), 8, 6, 0x404040);

        int offset = chest.getChestType() == ArcaneChestType.SILVER_WOOD ? 18 : 0;
        fontRendererObj.drawString(I18n.format("container.inventory"), 8 + offset, ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderUtil
            .bindTexture(chest.getChestType() == ArcaneChestType.GREAT_WOOD ? GREATWOOD_TEXTURE : SILVERWOOD_TEXTURE);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
    }

}
