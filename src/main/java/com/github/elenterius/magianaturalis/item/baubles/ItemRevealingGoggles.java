package com.github.elenterius.magianaturalis.item.baubles;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.api.ISpectacles;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;
import vazkii.botania.api.item.IBaubleRender;

/**
 * 【揭示之护目镜 · 饰品版】
 * 
 * 一个纯装饰品，戴在 Baubles 项链栏：
 * - 会渲染眼镜到玩家头上
 * - 会显示节点高亮
 * - 会显示节点/箱子信息
 * - 会赋予近乎无限的夜视效果
 */
public class ItemRevealingGoggles extends Item implements IBauble, IBaubleRender, ISpectacles, IRevealer, IGoggles {

    /** 夜视效果的刷新间隔（tick） */
    private static final int NIGHTVISION_REFRESH_INTERVAL = 40;

    /** 每次赋予的夜视持续时间（tick），6000 = 5分钟 */
    private static final int NIGHTVISION_DURATION = 6000;

    private IIcon icon;

    public ItemRevealingGoggles() {
        super();
        setMaxStackSize(1);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    // ==================================================
    // 【图标】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        icon = ir.registerIcon(MagiaNaturalis.rlString("revealing_goggles"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return icon;
    }

    // ==================================================
    // 【稀有度】让物品名变成紫色
    //
    // EnumRarity.epic = 紫色（史诗级）
    // ==================================================
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    // ==================================================
    // 【Tooltip】鼠标悬停时显示的信息
    //
    // 全部走语言键，中英文自动切换
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        // 第 1 段：类型和槽位
        list.add(
            EnumChatFormatting.LIGHT_PURPLE
                + StatCollector.translateToLocal("item.magianaturalis.revealing_goggles.tooltip.1"));
        list.add(
            EnumChatFormatting.GRAY
                + StatCollector.translateToLocal("item.magianaturalis.revealing_goggles.tooltip.2"));

        // 第 2 段：感谢和署名
        list.add("");
        list.add(
            EnumChatFormatting.DARK_GRAY
                + StatCollector.translateToLocal("item.magianaturalis.revealing_goggles.tooltip.3"));
        list.add(
            EnumChatFormatting.DARK_GRAY
                + StatCollector.translateToLocal("item.magianaturalis.revealing_goggles.tooltip.4"));

        // 第 3 段：吐槽
        list.add("");
        list.add(
            EnumChatFormatting.DARK_GRAY + ""
                + EnumChatFormatting.ITALIC
                + StatCollector.translateToLocal("item.magianaturalis.revealing_goggles.tooltip.5"));

        super.addInformation(stack, player, list, advanced);
    }

    // ==================================================
    // 【IBauble · 饰品基础】
    // ==================================================
    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.AMULET; // 项链槽
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    // ==================================================
    // 【夜视效果】戴上时赋予
    //
    // 思路：每 NIGHTVISION_REFRESH_INTERVAL tick 刷新一次，
    // 每次赋予 NIGHTVISION_DURATION tick。
    // 只要戴着就不会断，卸下时 onUnequipped 会清除。
    // ==================================================
    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (!(player instanceof EntityPlayer)) return;
        EntityPlayer p = (EntityPlayer) player;
        if (p.worldObj.isRemote) return;

        // 立即给一次效果，避免等待
        p.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), NIGHTVISION_DURATION, 0, true));
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (!(player instanceof EntityPlayer)) return;
        EntityPlayer p = (EntityPlayer) player;
        if (p.worldObj.isRemote) return;

        // 定期刷新，防止断档
        if (p.ticksExisted % NIGHTVISION_REFRESH_INTERVAL == 0) {
            p.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), NIGHTVISION_DURATION, 0, true));
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if (!(player instanceof EntityPlayer)) return;
        EntityPlayer p = (EntityPlayer) player;
        if (p.worldObj.isRemote) return;

        // 卸下时清除夜视
        p.removePotionEffect(Potion.nightVision.getId());
    }

    @Override
    public void onPlayerLoad(ItemStack stack, EntityLivingBase player) {
        onEquipped(stack, player);
    }

    // ==================================================
    // 【ISpectacles · HUD 文字显示】
    // ==================================================
    @Override
    public boolean drawSpectacleHUD(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    // ==================================================
    // 【IRevealer · 节点高亮】
    // ==================================================
    @Override
    public boolean showNodes(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    // ==================================================
    // 【IGoggles · 准星对准方块时显示信息】
    // ==================================================
    @Override
    public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    // ==================================================
    // 【IBaubleRender · 模型渲染】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, RenderPlayerEvent event, RenderType type) {
        if (type != RenderType.HEAD) return;

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        Helper.translateToHeadLevel(event.entityPlayer);

        GL11.glRotatef(90F, 0F, 1F, 0F);
        GL11.glRotatef(180F, 1F, 0F, 0F);
        GL11.glTranslatef(-0.4F, 0.1F, -0.25F);
        GL11.glScalef(0.75F, 0.75F, 0.75F);
        GL11.glTranslatef(0.04F, -0.5F, 0F);

        renderIcon();
    }

    private void renderIcon() {
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        ItemRenderer.renderItemIn2D(
            Tessellator.instance,
            maxU,
            minV,
            minU,
            maxV,
            icon.getIconWidth(),
            icon.getIconHeight(),
            1F / 16F);
    }
}
