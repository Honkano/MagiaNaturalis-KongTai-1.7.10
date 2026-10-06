package com.github.elenterius.magianaturalis.client.render;

import java.awt.Color;

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
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.api.ISpectacles;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.item.artifact.DarkCrystalGogglesItem;
import com.github.elenterius.magianaturalis.item.focus.BuilderFocusItem;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil.Mode;
import com.github.elenterius.magianaturalis.util.Platform;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.tile.TileNodeRenderer;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileNodeEnergized;
import thaumcraft.common.tiles.TileOwned;

@SideOnly(Side.CLIENT)
public final class RenderEventHandler {

    private static final ResourceLocation SILKTOUCH_TEXTURE =
        new ResourceLocation("thaumcraft", "textures/foci/silktouch.png");
    private static final ResourceLocation GLOWING_EYES_TEXTURE =
        MagiaNaturalis.rl("textures/models/glowingEyes.png");
    private static final ModelBiped OVERLAY_MODEL = new ModelBiped();

    /** 要素标签的淡入淡出系数 */
    private static float tagscale = 0.0F;

    private final RenderItem itemRender;
    private ItemStack prevPickedBlock = null;
    private int prevCount = 0;

    private RenderEventHandler() {
        itemRender = new RenderItem();
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
    }

    // ==================================================
    // 【HUD 渲染】护目镜文字 + 建筑核心提示
    // ==================================================
    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HELMET) return;

        Minecraft mc = Minecraft.getMinecraft();

        if (Minecraft.isGuiEnabled() && !mc.isGamePaused()
            && !mc.gameSettings.showDebugInfo) {
            if (mc.renderViewEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) mc.renderViewEntity;

                ItemStack specs = findSpectaclesOnPlayer(player);
                if (specs != null
                    && ((ISpectacles) specs.getItem()).drawSpectacleHUD(specs, player)) {
                    renderSpectaclesHUD(mc, player);
                }

                ItemStack stack = player.inventory.getCurrentItem();
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

    // ==================================================
    // 【节点高亮渲染】监听 RenderWorldLastEvent
    // ==================================================
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        renderBaublesNodes(player, event.partialTicks);

        // tagscale 每帧递减，实现淡出
        if (tagscale > 0.0F) {
            tagscale -= 0.005F;
            if (tagscale < 0.0F) tagscale = 0.0F;
        }
    }

    // ==================================================
    // 【准星对准节点显示要素】监听 DrawBlockHighlightEvent
    // ==================================================
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.player;
        if (player == null) return;

        if (event.target == null || event.target.typeOfHit != MovingObjectType.BLOCK) return;

        // 头盔槽有 TC4 护目镜 → TC4 会自己显示，跳过
        ItemStack helm = player.inventory.armorItemInSlot(3);
        if (helm != null && helm.getItem() instanceof IGoggles
            && ((IGoggles) helm.getItem()).showIngamePopups(helm, player)) {
            return;
        }

        // 在 Baubles 里找 IGoggles 护目镜
        ItemStack goggles = findGogglesOnPlayer(player);
        if (goggles == null) return;
        if (!(goggles.getItem() instanceof IGoggles)) return;
        if (!((IGoggles) goggles.getItem()).showIngamePopups(goggles, player)) return;

        int x = event.target.blockX;
        int y = event.target.blockY;
        int z = event.target.blockZ;
        TileEntity te = player.worldObj.getTileEntity(x, y, z);

        if (te == null || !(te instanceof IAspectContainer)) return;

        AspectList aspects = ((IAspectContainer) te).getAspects();
        if (aspects == null || aspects.size() <= 0) return;

        boolean spaceAbove = player.worldObj.isAirBlock(x, y + 1, z);
        ForgeDirection dir = spaceAbove
            ? ForgeDirection.UP
            : ForgeDirection.getOrientation(event.target.sideHit);

        if (tagscale < 0.3F) {
            tagscale += 0.031F - tagscale / 10.0F;
        }

        drawTagsOnContainer(
            (double) x,
            (double) y + (spaceAbove ? 0.4F : 0.0F),
            (double) z,
            aspects,
            220,
            dir,
            event.partialTicks);
    }

    // ==================================================
    // 【核心方法】渲染 Baubles 护目镜对应的节点高亮
    // ==================================================
    private void renderBaublesNodes(EntityPlayer player, float partialTicks) {
        ItemStack specs = findSpectaclesOnPlayer(player);
        if (specs == null) return;

        ItemStack helm = player.inventory.armorItemInSlot(3);
        if (helm != null && helm.getItem() instanceof IRevealer
            && ((IRevealer) helm.getItem()).showNodes(helm, player)) {
            return;
        }

        if (!(specs.getItem() instanceof IRevealer)) return;
        IRevealer revealer = (IRevealer) specs.getItem();
        if (!revealer.showNodes(specs, player)) return;

        int cx = (int) player.posX >> 4;
        int cz = (int) player.posZ >> 4;
        int range = 2;
        double maxDistSq = 64 * 64;

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);

        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                Chunk chunk = player.worldObj.getChunkFromChunkCoords(cx + dx, cz + dz);
                if (chunk == null) continue;

                for (Object obj : chunk.chunkTileEntityMap.values()) {
                    if (!(obj instanceof TileEntity)) continue;
                    TileEntity te = (TileEntity) obj;
                    if (te.isInvalid()) continue;

                    double distSq = player.getDistanceSq(
                        te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5);
                    if (distSq > maxDistSq) continue;

                    if (te instanceof INode && !(te instanceof TileNodeEnergized)) {
                        INode node = (INode) te;
                        TileNodeRenderer.renderNode(
                            player, 64.0D, true, true, 1.0F,
                            te.xCoord, te.yCoord, te.zCoord,
                            partialTicks,
                            node.getAspects(),
                            node.getNodeType(),
                            node.getNodeModifier());
                    } else if (te instanceof TileNodeEnergized) {
                        TileNodeEnergized node = (TileNodeEnergized) te;
                        TileNodeRenderer.renderNode(
                            player, 64.0D, true, true, 1.0F,
                            te.xCoord, te.yCoord, te.zCoord,
                            partialTicks,
                            node.getAuraBase(),
                            node.getNodeType(),
                            node.getNodeModifier());
                    }
                }
            }
        }

        GL11.glPopAttrib();
    }

    // ==================================================
    // 【工具方法】在玩家的头盔槽 + Baubles 里找 ISpectacles
    // ==================================================
    private ItemStack findSpectaclesOnPlayer(EntityPlayer player) {
        ItemStack helm = player.inventory.armorItemInSlot(3);
        if (helm != null && helm.getItem() instanceof ISpectacles) {
            return helm;
        }

        try {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
            if (baubles != null) {
                for (int i = 0; i < baubles.getSizeInventory(); i++) {
                    ItemStack s = baubles.getStackInSlot(i);
                    if (s != null && s.getItem() instanceof ISpectacles) {
                        return s;
                    }
                }
            }
        } catch (Throwable ignored) {}

        return null;
    }

    // ==================================================
    // 【工具方法】在玩家的头盔槽 + Baubles 里找 IGoggles
    // ==================================================
    private ItemStack findGogglesOnPlayer(EntityPlayer player) {
        ItemStack helm = player.inventory.armorItemInSlot(3);
        if (helm != null && helm.getItem() instanceof IGoggles) {
            return helm;
        }

        try {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
            if (baubles != null) {
                for (int i = 0; i < baubles.getSizeInventory(); i++) {
                    ItemStack s = baubles.getStackInSlot(i);
                    if (s != null && s.getItem() instanceof IGoggles) {
                        return s;
                    }
                }
            }
        } catch (Throwable ignored) {}

        return null;
    }

    // ==================================================
    // 【绘制要素标签】从 TC4 的 RenderEventHandler 复制改造而来
    // ==================================================
    private void drawTagsOnContainer(double x, double y, double z,
                                      AspectList tags, int bright,
                                      ForgeDirection dir, float partialTicks) {
        if (!(Minecraft.getMinecraft().renderViewEntity instanceof EntityPlayer)) return;
        if (tags == null || tags.size() <= 0) return;

        EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().renderViewEntity;

        double iPX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
        double iPY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks;
        double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;

        byte rowsize = 5;
        int current = 0;
        float shifty = 0.0F;
        int left = tags.size();

        Aspect[] arr = tags.getAspects();

        for (Aspect tag : arr) {
            int div = Math.min(left, rowsize);

            if (current >= rowsize) {
                current = 0;
                shifty -= tagscale * 1.05F;
                left -= rowsize;
                if (left < rowsize) {
                    div = left % rowsize;
                }
            }

            float shift = ((float) current - (float) div / 2.0F + 0.5F) * tagscale * 4.0F;
            shift *= tagscale;

            Color color = new Color(tag.getColor());

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glTranslated(
                -iPX + x + 0.5D + (double) (tagscale * 2.0F * (float) dir.offsetX),
                -iPY + y - (double) shifty + 0.5D + (double) (tagscale * 2.0F * (float) dir.offsetY),
                -iPZ + z + 0.5D + (double) (tagscale * 2.0F * (float) dir.offsetZ));

            float xd = (float) (iPX - (x + 0.5D));
            float zd = (float) (iPZ - (z + 0.5D));
            float rotYaw = (float) (Math.atan2((double) xd, (double) zd) * 180.0D / Math.PI);
            GL11.glRotatef(rotYaw + 180.0F, 0.0F, 1.0F, 0.0F);

            GL11.glTranslated((double) shift, 0.0D, 0.0D);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(tagscale, tagscale, tagscale);

            boolean discovered = Thaumcraft.proxy.getPlayerKnowledge()
                .hasDiscoveredAspect(player.getCommandSenderName(), tag);

            if (!discovered) {
                UtilsFX.renderQuadCenteredFromTexture(
                    "textures/aspects/_unknown.png", 1.0F,
                    (float) color.getRed() / 255.0F,
                    (float) color.getGreen() / 255.0F,
                    (float) color.getBlue() / 255.0F,
                    bright, 771, 0.75F);
            } else {
                UtilsFX.renderQuadCenteredFromTexture(
                    tag.getImage(), 1.0F,
                    (float) color.getRed() / 255.0F,
                    (float) color.getGreen() / 255.0F,
                    (float) color.getBlue() / 255.0F,
                    bright, 771, 0.75F);
            }

            if (tags.getAmount(tag) >= 0) {
                String am = "" + tags.getAmount(tag);
                GL11.glScalef(0.04F, 0.04F, 0.04F);
                GL11.glTranslated(0.0D, 6.0D, -0.1D);
                int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(am);
                GL11.glEnable(GL11.GL_BLEND);
                Minecraft.getMinecraft().fontRenderer.drawString(am, 14 - sw, 1, 1118481);
                GL11.glTranslated(0.0D, 0.0D, -0.1D);
                Minecraft.getMinecraft().fontRenderer.drawString(am, 13 - sw, 0, 16777215);
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
            ++current;
        }
    }

    // ==================================================
    // 【玩家特殊渲染】混沌护目镜的发光眼睛
    // ==================================================
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

    // ==================================================
    // 【HUD】建筑核心的显示
    // ==================================================
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
                    RenderUtil.drawStringWithBorder(
                        fontRenderer,
                        Platform.translate("hud.magianaturalis.builder.infinite"),
                        0,
                        32 + 16,
                        0xffffff,
                        0);
                } else {
                    RenderUtil.drawStringWithBorder(fontRenderer, "" + amount, 0, 32 + 16, 0xffffff, 0);
                }

                RenderUtil.drawStringWithBorder(
                    fontRenderer,
                    Platform.translate(
                        "item.magianaturalis.builder_focus.tooltip.shape",
                        Platform.translateEnum("enum.magianaturalis.shape.", BuilderFocusUtil.getShape(focusStack))),
                    0,
                    -2,
                    0xffffff,
                    0);
                RenderUtil.drawStringWithBorder(
                    fontRenderer,
                    Platform.translate(
                        "item.magianaturalis.builder_focus.tooltip.size",
                        BuilderFocusUtil.getSize(focusStack)),
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
                    Platform.translate(
                        "item.magianaturalis.builder_focus.tooltip.shape",
                        Platform.translateEnum("enum.magianaturalis.shape.", BuilderFocusUtil.getShape(focusStack))),
                    0,
                    -2,
                    0xffffff,
                    0);
                RenderUtil.drawStringWithBorder(
                    fontRenderer,
                    Platform.translate(
                        "item.magianaturalis.builder_focus.tooltip.size",
                        BuilderFocusUtil.getSize(focusStack)),
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

    // ==================================================
    // 【HUD】护目镜 HUD 信息
    // ==================================================
    private void renderSpectaclesHUD(Minecraft mc, EntityPlayer player) {
        boolean meterEquiped = false;
        if (player.inventory.getCurrentItem() != null) {
            if (player.inventory.getCurrentItem().getItem() == ConfigItems.itemThaumometer) {
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
                    String owner = EnumChatFormatting.DARK_PURPLE
                        + Platform.translate("hud.magianaturalis.spectacles.owner")
                        + ": "
                        + EnumChatFormatting.WHITE
                        + owned.owner;
                    GL11.glPushMatrix();
                    GL11.glTranslatef(w / 2, h / 2, 0F);
                    fontRenderer
                        .drawStringWithShadow(owner, -(fontRenderer.getStringWidth(owner) - 4) / 2, 25, 0xFFFFFF);
                    GL11.glPopMatrix();
                } else if (tile instanceof ArcaneChestBlockEntity) {
                    ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) tile;
                    String name = EnumChatFormatting.DARK_PURPLE
                        + Platform.translate("hud.magianaturalis.spectacles.owner")
                        + ": "
                        + EnumChatFormatting.WHITE
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