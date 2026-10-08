package com.github.elenterius.magianaturalis.item.baubles;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.api.ISpectacles;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;
import com.github.elenterius.magianaturalis.init.client.MNKeyBindings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;
import vazkii.botania.api.item.IBaubleRender;

/**
 * 【面具 · 饰品版】
 *
 * meta 0 = 狞笑恶魔面具
 * meta 1 = 暴怒幽魂面具
 * meta 2 = 嗜血邪妖面具
 * meta 3 = 态度面具（4 张脸，NBT 切换）
 * meta 4 = 原始面具（7 张脸，NBT 切换）
 */
public class ItemMask extends Item implements IBauble, IBaubleRender, ISpectacles, IRevealer, IGoggles {

    public static final int MASK_COUNT = 3;
    public static final int META_ATTITUDE = 3;
    public static final int META_PRIMAL = 4;

    private static final String TAG_ATTITUDE_FACE = "attitude_face";
    private static final String TAG_PRIMAL_FACE = "primal_face";

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    private IIcon[] attitudeIcons;

    @SideOnly(Side.CLIENT)
    private IIcon[] primalIcons;

    public ItemMask() {
        super();
        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    // ==================================================
    // 【图标注册】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        icons = new IIcon[MASK_COUNT];
        icons[0] = ir.registerIcon(MagiaNaturalis.rlString("masks/mask_smile"));
        icons[1] = ir.registerIcon(MagiaNaturalis.rlString("masks/mask_ghost"));
        icons[2] = ir.registerIcon(MagiaNaturalis.rlString("masks/mask_fiend"));

        attitudeIcons = new IIcon[4];
        attitudeIcons[0] = ir.registerIcon(MagiaNaturalis.rlString("masks/attitude/mask_attitude_0"));
        attitudeIcons[1] = ir.registerIcon(MagiaNaturalis.rlString("masks/attitude/mask_attitude_1"));
        attitudeIcons[2] = ir.registerIcon(MagiaNaturalis.rlString("masks/attitude/mask_attitude_2"));
        attitudeIcons[3] = ir.registerIcon(MagiaNaturalis.rlString("masks/attitude/mask_attitude_3"));

        primalIcons = new IIcon[7];
        primalIcons[0] = ir.registerIcon(MagiaNaturalis.rlString("masks/primal/mask_primal_0"));
        primalIcons[1] = ir.registerIcon(MagiaNaturalis.rlString("masks/primal/mask_primal_1"));
        primalIcons[2] = ir.registerIcon(MagiaNaturalis.rlString("masks/primal/mask_primal_2"));
        primalIcons[3] = ir.registerIcon(MagiaNaturalis.rlString("masks/primal/mask_primal_3"));
        primalIcons[4] = ir.registerIcon(MagiaNaturalis.rlString("masks/primal/mask_primal_4"));
        primalIcons[5] = ir.registerIcon(MagiaNaturalis.rlString("masks/primal/mask_primal_5"));
        primalIcons[6] = ir.registerIcon(MagiaNaturalis.rlString("masks/primal/mask_primal_6"));
    }

    // ==================================================
    // 【物品栏图标 · 静态】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (damage == META_ATTITUDE) return attitudeIcons[0];
        if (damage == META_PRIMAL) return primalIcons[0];
        if (damage < 0 || damage >= MASK_COUNT) return icons[0];
        return icons[damage];
    }

    // ==================================================
    // 【物品栏图标 · 动态】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        int meta = stack.getItemDamage();
        if (meta == META_ATTITUDE) {
            return attitudeIcons[getAttitudeFace(stack)];
        }
        if (meta == META_PRIMAL) {
            return primalIcons[getPrimalFace(stack)];
        }
        return getIconFromDamage(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        return getIcon(stack, 0);
    }

    // ==================================================
    // 【创造模式物品栏】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < MASK_COUNT; i++) {
            list.add(new ItemStack(item, 1, i));
        }
        list.add(new ItemStack(item, 1, META_ATTITUDE));
        list.add(new ItemStack(item, 1, META_PRIMAL));
    }

    // ==================================================
    // 【名字】
    // ==================================================
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        return super.getUnlocalizedName() + "." + meta;
    }

    // ==================================================
    // 【稀有度】
    // ==================================================
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    // ==================================================
    // 【Tooltip】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int meta = stack.getItemDamage();

        list.add(
            EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("item.magianaturalis.mask.tooltip.1"));
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.magianaturalis.mask.tooltip.2"));

        list.add("");
        list.add(
            EnumChatFormatting.DARK_GRAY
                + StatCollector.translateToLocal("item.magianaturalis.mask." + meta + ".desc"));

        // 态度面具
        if (meta == META_ATTITUDE) {
            int face = getAttitudeFace(stack);
            String faceName = StatCollector.translateToLocal("item.magianaturalis.mask.3.face." + face);
            list.add(
                EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.magianaturalis.mask.3.current")
                    + " "
                    + faceName);

            String keyName = GameSettings.getKeyDisplayString(MNKeyBindings.ATTITUDE_SWITCH_KEY.getKeyCode());

            list.add(
                EnumChatFormatting.DARK_GRAY
                    + StatCollector.translateToLocalFormatted("item.magianaturalis.mask.3.hint", keyName));
        }

        // 原始面具
        if (meta == META_PRIMAL) {
            int face = getPrimalFace(stack);
            String faceName = StatCollector.translateToLocal("item.magianaturalis.mask.4.face." + face);
            list.add(
                EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.magianaturalis.mask.4.current")
                    + " "
                    + faceName);

            list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("item.magianaturalis.mask.4.hint"));
        }

        super.addInformation(stack, player, list, advanced);
    }

    // ==================================================
    // 【IBauble】
    // ==================================================
    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.AMULET;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public void onPlayerLoad(ItemStack stack, EntityLivingBase player) {
        onEquipped(stack, player);
    }

    // ==================================================
    // 【装备时】
    // ==================================================
    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (stack == null) return;
        if (!(player instanceof EntityPlayer)) return;

        int meta = stack.getItemDamage();
        EntityPlayer p = (EntityPlayer) player;

        if (meta == META_ATTITUDE) {
            if (p.worldObj.isRemote) return;
            int face = getAttitudeFace(stack);
            p.addChatComponentMessage(
                new ChatComponentTranslation(
                    "msg.magianaturalis.mask.equipped",
                    StatCollector.translateToLocal("item.magianaturalis.mask.3.face." + face)));
        } else if (meta == META_PRIMAL) {
            if (p.worldObj.isRemote) return;
            int face = getPrimalFace(stack);
            p.addChatComponentMessage(
                new ChatComponentTranslation(
                    "msg.magianaturalis.mask.primal.equipped",
                    StatCollector.translateToLocal("item.magianaturalis.mask.4.face." + face)));
            // 立即给一次夜视
            p.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 6000, 0, true));
        }
    }

    // ==================================================
    // 【卸下时】
    // ==================================================
    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if (stack == null) return;
        if (!(player instanceof EntityPlayer)) return;

        int meta = stack.getItemDamage();
        EntityPlayer p = (EntityPlayer) player;

        if (meta == META_ATTITUDE) {
            setHiding(stack, false);
            setHidingEnd(stack, 0L);
            if (p.worldObj.isRemote) return;
            p.addChatComponentMessage(new ChatComponentTranslation("msg.magianaturalis.mask.unequipped"));
        } else if (meta == META_PRIMAL) {
            if (p.worldObj.isRemote) return;
            p.removePotionEffect(Potion.nightVision.getId());
        }
    }

    // ==================================================
    // 【WornTick】
    // ==================================================
    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (!(player instanceof EntityPlayer)) return;
        EntityPlayer p = (EntityPlayer) player;
        if (p.worldObj.isRemote) return;

        int meta = stack.getItemDamage();
        switch (meta) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case META_ATTITUDE:
                break;
            case META_PRIMAL: {
                if (p.ticksExisted % 40 == 0) {
                    p.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 6000, 0, true));
                }
                break;
            }
        }
    }

    // ==================================================
    // 【渲染到脸上】
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, RenderPlayerEvent event, RenderType type) {
        if (type != RenderType.HEAD) return;

        int meta = stack.getItemDamage();
        IIcon icon;

        if (meta == META_ATTITUDE) {
            icon = attitudeIcons[getAttitudeFace(stack)];
        } else if (meta == META_PRIMAL) {
            icon = primalIcons[getPrimalFace(stack)];
        } else {
            if (meta < 0 || meta >= MASK_COUNT) meta = 0;
            icon = icons[meta];
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        Helper.translateToHeadLevel(event.entityPlayer);

        GL11.glRotatef(90F, 0F, 1F, 0F);
        GL11.glRotatef(180F, 1F, 0F, 0F);
        GL11.glTranslatef(-0.30F, 0.10F, -0.25F);
        GL11.glScalef(0.55F, 0.55F, 0.55F);
        GL11.glTranslatef(0.04F, -0.5F, 0F);

        renderIcon(icon);
    }

    @SideOnly(Side.CLIENT)
    private void renderIcon(IIcon icon) {
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

    // ==================================================
    // 【态度面具 · 工具方法】
    // ==================================================
    public static int getAttitudeFace(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return 0;
        int face = stack.getTagCompound()
            .getInteger(TAG_ATTITUDE_FACE);
        if (face < 0 || face >= 4) return 0;
        return face;
    }

    public static void cycleAttitude(ItemStack stack) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        int face = getAttitudeFace(stack);
        face = (face + 1) % 4;
        stack.getTagCompound()
            .setInteger(TAG_ATTITUDE_FACE, face);
    }

    public static void setAttitudeFace(ItemStack stack, int face) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (face < 0 || face >= 4) face = 0;
        stack.getTagCompound()
            .setInteger(TAG_ATTITUDE_FACE, face);
    }

    // ==================================================
    // 【原始面具 · 工具方法】
    // ==================================================
    public static int getPrimalFace(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return 0;
        int face = stack.getTagCompound()
            .getInteger(TAG_PRIMAL_FACE);
        if (face < 0 || face >= 7) return 0;
        return face;
    }

    public static void setPrimalFace(ItemStack stack, int face) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (face < 0 || face >= 7) face = 0;
        stack.getTagCompound()
            .setInteger(TAG_PRIMAL_FACE, face);
    }

    public static void cyclePrimal(ItemStack stack) {
        if (stack == null) return;
        int face = (getPrimalFace(stack) + 1) % 7;
        setPrimalFace(stack, face);
    }

    // ==================================================
    // 【NBT 通用访问】
    // ==================================================
    public static long getLong(ItemStack stack, String key) {
        if (stack == null || !stack.hasTagCompound()) return 0L;
        return stack.getTagCompound()
            .getLong(key);
    }

    public static void setLong(ItemStack stack, String key, long value) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound()
            .setLong(key, value);
    }

    public static boolean getBool(ItemStack stack, String key) {
        if (stack == null || !stack.hasTagCompound()) return false;
        return stack.getTagCompound()
            .getBoolean(key);
    }

    public static void setBool(ItemStack stack, String key, boolean value) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound()
            .setBoolean(key, value);
    }

    // ==================================================
    // 【无态度】
    // ==================================================
    public static long getAttackTime(ItemStack stack) {
        return getLong(stack, "attitude_attack_time");
    }

    public static void setAttackTime(ItemStack stack, long v) {
        setLong(stack, "attitude_attack_time", v);
    }

    public static long getAttitudePvpCd(ItemStack stack) {
        return getLong(stack, "attitude_pvp_cd");
    }

    public static void setAttitudePvpCd(ItemStack stack, long v) {
        setLong(stack, "attitude_pvp_cd", v);
    }

    // ==================================================
    // 【威严】
    // ==================================================
    public static long getSolemnNext(ItemStack stack) {
        return getLong(stack, "solemn_next");
    }

    public static void setSolemnNext(ItemStack stack, long v) {
        setLong(stack, "solemn_next", v);
    }

    public static long getSolemnEnd(ItemStack stack) {
        return getLong(stack, "solemn_end");
    }

    public static void setSolemnEnd(ItemStack stack, long v) {
        setLong(stack, "solemn_end", v);
    }

    // ==================================================
    // 【隐匿】
    // ==================================================
    public static boolean isHiding(ItemStack stack) {
        return getBool(stack, "hiding_active");
    }

    public static void setHiding(ItemStack stack, boolean v) {
        setBool(stack, "hiding_active", v);
    }

    public static long getHidingEnd(ItemStack stack) {
        return getLong(stack, "hiding_end");
    }

    public static void setHidingEnd(ItemStack stack, long v) {
        setLong(stack, "hiding_end", v);
    }

    public static long getHidingCdEnd(ItemStack stack) {
        return getLong(stack, "hiding_cd_end");
    }

    public static void setHidingCdEnd(ItemStack stack, long v) {
        setLong(stack, "hiding_cd_end", v);
    }

    // ==================================================
    // 【ISpectacles】HUD 显示
    // ==================================================
    @Override
    public boolean drawSpectacleHUD(ItemStack stack, EntityLivingBase player) {
        return stack != null && stack.getItemDamage() == META_PRIMAL;
    }

    // ==================================================
    // 【IRevealer】节点高亮
    // ==================================================
    @Override
    public boolean showNodes(ItemStack stack, EntityLivingBase player) {
        return stack != null && stack.getItemDamage() == META_PRIMAL;
    }

    // ==================================================
    // 【IGoggles】准星信息
    // ==================================================
    @Override
    public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
        return stack != null && stack.getItemDamage() == META_PRIMAL;
    }

    // ==================================================
    // 【通用 · flash】
    // ==================================================
    public static void setFlash(ItemStack stack, long now) {
        setLong(stack, "attitude_flash", now);
    }

    public static long getFlash(ItemStack stack) {
        return getLong(stack, "attitude_flash");
    }
}
