package com.github.elenterius.magianaturalis.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.container.ContainerEvilTrunk;
import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;
import com.github.elenterius.magianaturalis.util.Platform;

public class EvilTrunkGui extends GuiContainer {

    public static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/guitrunkbase.png");

    private final EntityPlayer entityPlayer;
    private final EntityEvilTrunk entityTrunk;

    public EvilTrunkGui(EntityPlayer player, EntityEvilTrunk entity) {
        super(new ContainerEvilTrunk(player.inventory, player.worldObj, entity));
        entityPlayer = player;
        entityTrunk = entity;
        ySize = 200;
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        GL11.glPushMatrix();
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        fontRendererObj
            .drawString(entityTrunk.getOwnerName() + Platform.translate("entity.trunk.guiname"), 8, 4, 0xC0A0F0);
        GL11.glPopMatrix();
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        if (entityTrunk.isDead) mc.thePlayer.closeScreen();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(TEXTURE);
        GL11.glEnable(GL11.GL_BLEND);

        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

        int healthbar = Math.round(entityTrunk.getHealth() / entityTrunk.getMaxHealth() * 39.0F);
        drawTexturedModalRect(j + 134, k + 2, 176, 16, healthbar, 6);
        drawTexturedModalRect(j, k + 80, 0, 206, xSize, 27);

        if (entityTrunk.isWaiting()) {
            drawTexturedModalRect(j + 112, k, 176, 0, 10, 10);
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    protected void mouseClicked(int x, int y, int z) {
        super.mouseClicked(x, y, z);
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;
        int i = x - (sx + 112);
        int j = y - (sy + 0);

        if (i >= 0 && j >= 0 && i < 10 && j <= 10) {
            entityTrunk.worldObj.playSound(
                entityTrunk.posX,
                entityTrunk.posY,
                entityTrunk.posZ,
                "random.click",
                0.3F,
                0.6F + (entityTrunk.isWaiting() ? 0.0F : 0.2F),
                false);

            if (entityTrunk.isWaiting())
                entityPlayer.addChatMessage(new ChatComponentTranslation("entity.trunk.move", new Object[0]));
            else entityPlayer.addChatMessage(new ChatComponentTranslation("entity.trunk.stay", new Object[0]));

            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 1);
        }
    }

    public void onGuiClosed() {
        entityTrunk.setOpen(false);
        super.onGuiClosed();
    }

}
