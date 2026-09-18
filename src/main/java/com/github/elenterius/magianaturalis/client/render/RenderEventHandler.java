package com.github.elenterius.magianaturalis.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.api.ISpectacles;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.item.artifact.DarkCrystalGogglesItem;
import com.github.elenterius.magianaturalis.item.focus.BuilderFocusItem;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil.Mode;
import com.github.elenterius.magianaturalis.util.Platform;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileNodeEnergized;
import thaumcraft.common.tiles.TileOwned;

@SideOnly(Side.CLIENT)
public final class RenderEventHandler {

    private static final ResourceLocation SILKTOUCH_TEXTURE = new ResourceLocation(
        "thaumcraft",
        "textures/foci/silktouch.png");
    private static final ResourceLocation GLOWING_EYES_TEXTURE = MagiaNaturalis.rl("textures/models/glowingEyes.png");
    private static final ModelBiped OVERLAY_MODEL = new ModelBiped();

    private final RenderItem itemRender;
    private ItemStack prevPickedBlock = null;
    private int prevCount = 0;

    private RenderEventHandler() {
        itemRender = new RenderItem();
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HELMET) return;

        Minecraft mc = Minecraft.getMinecraft();

        if (Minecraft.isGuiEnabled() && !mc.isGamePaused() /* && mc.currentScreen == null */
            && !mc.gameSettings.showDebugInfo) {
            if (mc.renderViewEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) mc.renderViewEntity;

                ItemStack stack = player.inventory.armorItemInSlot(3);
                if (stack != null && stack.getItem() instanceof ISpectacles
                    && ((ISpectacles) stack.getItem()).drawSpectacleHUD(stack, player)) {
                    renderSpectaclesHUD(mc, player);
                }

                stack = player.inventory.getCurrentItem();
                if (stack != null && stack.getItem() instanceof ItemWandCasting) {
                    ItemWandCasting wand = (ItemWandCasting) stack.getItem();
                    ItemFocusBasic focus = wand.getFocus(stack);
                    if (focus instanceof BuilderFocusItem) {
                        ItemStack focusStack = wand.getFocusItem(stack);
                        renderBuildFocusHUD(mc, focusStack, player);
                    }
                }
            }
        }
    }

    // @SubscribeEvent
    // public void renderBlockHighlight(DrawBlockHighlightEvent event)
    // {
    // int ticks = event.player.ticksExisted;
    // MovingObjectPosition target = event.target;
    //
    // if(Thaumcraft.instance.renderEventHandler.wandHandler == null) Thaumcraft.instance.renderEventHandler.wandHandler
    // = new REHWandHandler();
    //
    // if(target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
    // {
    // if(event.player.getHeldItem() != null && event.player.getHeldItem().getItem() instanceof ItemWandCasting)
    // {
    // ItemWandCasting wand = (ItemWandCasting) event.player.getHeldItem().getItem();
    // ItemStack focus = wand.getFocusItem( event.player.getHeldItem());
    // if(focus.getItem() instanceof ItemFocusBuild)
    // {
    // Log.logger.info("DO IT NOW");
    // if(Thaumcraft.instance.renderEventHandler.wandHandler.handleArchitectOverlay(event.player.getHeldItem(), event,
    // ticks, target))
    // event.setCanceled(true);
    // }
    // }
    // }
    // }

    @SubscribeEvent
    public void renderPlayerSpecial(RenderPlayerEvent.Specials.Pre event) {
        if (!event.renderHelmet) return;

        ItemStack itemStack = event.entityPlayer.inventory.armorItemInSlot(3);

        if (itemStack != null && itemStack.getItem() instanceof DarkCrystalGogglesItem) {
            GL11.glPushMatrix();
            float scale = 0.0625F;

            event.renderer.modelBipedMain.bipedHead.postRender(scale);
            Minecraft.getMinecraft().renderEngine.bindTexture(GLOWING_EYES_TEXTURE);

            GL11.glTranslatef(0.0F, scale, -0.01F);
            GL11.glScalef(1.25F, 1.25F, 1.25F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glDepthMask(!event.entityPlayer.isInvisible());

            char c0 = 0xf0f0;
            int j = c0 % 0x10000;
            int k = c0 / 0x10000;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            OVERLAY_MODEL.bipedHead.render(scale);
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glPopMatrix();
        }
    }

    private void renderBuildFocusHUD(Minecraft mc, ItemStack focusStack, EntityPlayer player) {
        GL11.glClear(GL11.GL_ACCUM);
        FontRenderer fontRenderer = mc.fontRenderer;

        Mode builderMode = BuilderFocusUtil.getMode(focusStack);
        Block pblock = null;
        int pbdata = 0;
        Item item = null;
        ItemStack pickedBlock = null;

        if (builderMode == BuilderFocusUtil.Mode.UNIFORM) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                pblock = player.worldObj
                    .getBlock(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
                pbdata = player.worldObj
                    .getBlockMetadata(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
                item = Item.getItemFromBlock(pblock);
            }

            if (item != null) {
                if (pblock == Blocks.double_plant) pbdata = pblock.getDamageValue(
                    player.worldObj,
                    mc.objectMouseOver.blockX,
                    mc.objectMouseOver.blockY,
                    mc.objectMouseOver.blockZ);

                pickedBlock = new ItemStack(item, 1, pbdata);
            }

            if (pickedBlock == null && pblock != null) {
                if (pblock == Blocks.lit_redstone_ore) pickedBlock = new ItemStack(Blocks.redstone_ore);
                else pickedBlock = pblock.getPickBlock(
                    mc.objectMouseOver,
                    player.worldObj,
                    mc.objectMouseOver.blockX,
                    mc.objectMouseOver.blockY,
                    mc.objectMouseOver.blockZ);
            }
        } else {
            int[] i = BuilderFocusUtil.getPickedBlock(focusStack);
            pblock = Block.getBlockById(i[0]);
            pbdata = i[1];

            if (pblock != Blocks.air && pblock != null) {
                item = Item.getItemFromBlock(pblock);
                pickedBlock = new ItemStack(item, 1, pbdata);
            }
        }

        if (pickedBlock != null) {
            int amount = prevCount;

            if (!player.capabilities.isCreativeMode) {
                if (player.inventory.inventoryChanged || pickedBlock != prevPickedBlock
                    || !pickedBlock.isItemEqual(prevPickedBlock)) {
                    amount = countItemsInInventory(player, pickedBlock);
                }
            }

            GL11.glPushMatrix();
            {
                GL11.glTranslatef(49F, 44F, 0F);
                GL11.glScalef(1.5F, 1.5F, 1.5F);
                RenderUtil.drawItemStack(itemRender, fontRenderer, pickedBlock, 0, 0);
                GL11.glEnable(GL11.GL_BLEND);

                GL11.glTranslatef(0.0F, -fontRenderer.FONT_HEIGHT, 500F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);

                if (player.capabilities.isCreativeMode) {
                    RenderUtil.drawStringWithBorder(fontRenderer, "Infinite", 0, 32 + 16, 0xffffff, 0);
                } else {
                    RenderUtil.drawStringWithBorder(fontRenderer, "" + amount, 0, 32 + 16, 0xffffff, 0);
                }

                RenderUtil.drawStringWithBorder(
                    fontRenderer,
                    "Shape: " + BuilderFocusUtil.getShape(focusStack),
                    0,
                    -2,
                    0xffffff,
                    0);
                RenderUtil.drawStringWithBorder(
                    fontRenderer,
                    "Size: " + BuilderFocusUtil.getSize(focusStack),
                    0,
                    -1 + fontRenderer.FONT_HEIGHT,
                    0xffffff,
                    0);

                if (builderMode == BuilderFocusUtil.Mode.UNIFORM) {
                    GL11.glPushMatrix();
                    {
                        GL11.glScalef(1.5F, 1.5F, 1.5F);
                        GL11.glTranslatef(15F, 15F, 0F);
                        RenderUtil.drawTextureQuad(SILKTOUCH_TEXTURE, 16, 16);
                    }
                    GL11.glPopMatrix();
                }
            }
            GL11.glPopMatrix();

            prevCount = amount;
        } else {
            GL11.glPushMatrix();
            {
                GL11.glTranslatef(49F, 44F, 0F);
                GL11.glScalef(1.5F, 1.5F, 1.5F);

                GL11.glTranslatef(0.0F, -fontRenderer.FONT_HEIGHT, 500F);
                RenderUtil.drawStringWithBorder(fontRenderer, "?", 6, 14, 0xffffff, 0);

                GL11.glScalef(0.5F, 0.5F, 0.5F);
                RenderUtil.drawStringWithBorder(
                    fontRenderer,
                    "Shape: " + BuilderFocusUtil.getShape(focusStack),
                    0,
                    -2,
                    0xffffff,
                    0);
                RenderUtil.drawStringWithBorder(
                    fontRenderer,
                    "Size: " + BuilderFocusUtil.getSize(focusStack),
                    0,
                    -1 + fontRenderer.FONT_HEIGHT,
                    0xffffff,
                    0);

                if (builderMode == BuilderFocusUtil.Mode.UNIFORM) {
                    GL11.glPushMatrix();
                    {
                        GL11.glScalef(1.5F, 1.5F, 1.5F);
                        GL11.glTranslatef(15F, 15F, 0F);
                        RenderUtil.drawTextureQuad(SILKTOUCH_TEXTURE, 16, 16);
                    }
                    GL11.glPopMatrix();
                }
            }
            GL11.glPopMatrix();

            prevCount = 0;
        }

        prevPickedBlock = pickedBlock;
    }

    private int countItemsInInventory(EntityPlayer player, ItemStack itemStack) {
        int amount = 0;

        for (ItemStack is : player.inventory.mainInventory) {
            if (is != null && is.isItemEqual(itemStack)) {
                amount += is.stackSize;
            }
        }
        player.inventory.inventoryChanged = false;

        return amount;
    }

    private void renderSpectaclesHUD(Minecraft mc, EntityPlayer player) {
        boolean meterEquiped = false;
        if (player.inventory.getCurrentItem() != null) {
            if (player.inventory.getCurrentItem()
                .getItem() == ConfigItems.itemThaumometer) {
                meterEquiped = true;
            }
        }

        if (mc.gameSettings.thirdPersonView == 0 && mc.currentScreen == null && !meterEquiped) {
            TileEntity tile = null;
            MovingObjectPosition mop = mc.objectMouseOver;
            if (mop != null) {
                if (mop.typeOfHit == MovingObjectType.BLOCK) {
                    tile = player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
                }
            }

            FontRenderer fontRenderer = mc.fontRenderer;

            if (tile != null) {
                ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                int w = scaledresolution.getScaledWidth();
                int h = scaledresolution.getScaledHeight();

                if (tile instanceof INode) {
                    INode node = (INode) tile;
                    String meta = Platform.translate("nodetype." + node.getNodeType() + ".name");
                    if (node.getNodeModifier() != null)
                        meta = meta + ", " + Platform.translate("nodemod." + node.getNodeModifier() + ".name");

                    String name = Platform.translate("tile.blockAiry.0.name");
                    GL11.glPushMatrix();
                    GL11.glTranslatef(w / 2, h / 2, 0F);
                    fontRenderer.drawStringWithShadow(name, -fontRenderer.getStringWidth(name) / 2, 25, 0xF057BC);
                    fontRenderer.drawStringWithShadow(meta, -fontRenderer.getStringWidth(meta) / 2, 35, 0xFFFFFF);
                    GL11.glPopMatrix();
                } else if (tile instanceof TileNodeEnergized) {
                    TileNodeEnergized nodeEnergized = (TileNodeEnergized) tile;

                    String meta = Platform.translate("nodetype." + nodeEnergized.getNodeType() + ".name");
                    if (nodeEnergized.getNodeModifier() != null)
                        meta = meta + ", " + Platform.translate("nodemod." + nodeEnergized.getNodeModifier() + ".name");

                    String name = Platform.translate("tile.blockAiry.5.name");
                    GL11.glPushMatrix();
                    GL11.glTranslatef(w / 2, h / 2, 0F);
                    fontRenderer.drawStringWithShadow(name, -fontRenderer.getStringWidth(name) / 2, 25, 0xF057BC);
                    fontRenderer.drawStringWithShadow(meta, -fontRenderer.getStringWidth(meta) / 2, 35, 0xFFFFFF);
                    GL11.glPopMatrix();
                } else if (tile instanceof TileOwned) {
                    TileOwned owned = (TileOwned) tile;
                    String owner = EnumChatFormatting.DARK_PURPLE + "Owner"
                        + EnumChatFormatting.RESET
                        + " "
                        + owned.owner;
                    GL11.glPushMatrix();
                    GL11.glTranslatef(w / 2, h / 2, 0F);
                    fontRenderer
                        .drawStringWithShadow(owner, -(fontRenderer.getStringWidth(owner) - 4) / 2, 25, 0xFFFFFF);
                    GL11.glPopMatrix();
                } else if (tile instanceof ArcaneChestBlockEntity) {
                    ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) tile;
                    String name = EnumChatFormatting.DARK_PURPLE + "Owner"
                        + EnumChatFormatting.RESET
                        + " "
                        + chest.getOwnerName();
                    GL11.glPushMatrix();
                    GL11.glTranslatef(w / 2, h / 2, 0F);
                    fontRenderer.drawStringWithShadow(name, -(fontRenderer.getStringWidth(name) - 4) / 2, 25, 0xFFFFFF);
                    GL11.glPopMatrix();
                }
            }
        }
    }

}
