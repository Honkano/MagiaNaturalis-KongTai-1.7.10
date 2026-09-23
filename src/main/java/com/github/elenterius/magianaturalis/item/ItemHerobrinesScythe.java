package com.github.elenterius.magianaturalis.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IWarpingGear;

/**
 * 白瞳者之镰 —— 成长型认主魔器
 *
 * 【NBT 结构】
 * ScytheTier byte 0~4 当前等级
 * ScytheKills int 击杀计数，上限 99999
 * ScytheOwner String 认主玩家 UUID（判断归属用）
 * ScytheOwnerName String 认主玩家名（仅显示用，不参与判断）
 *
 * 【设计思路】
 * - 认主判定永远用 UUID，玩家改 ID 也不影响归属。
 * - 显示名字缓存到 NBT，Tooltip 直接读，避免实时反查。
 * - 所有等级数值放进 TIER_* 数组，想改平衡只动这里一处。
 * - 攻击力：Tier 0~4 温和成长，杀敌数到 MAX 时质变 999。
 *
 * 【多语言】
 * 所有 UI 文本走 StatCollector，键名见 zh_CN.lang：
 * item.magianaturalis.herobrines_scythe.tier.*
 * item.magianaturalis.herobrines_scythe.tooltip.*
 * chat.magianaturalis.scythe.bind
 */
public class ItemHerobrinesScythe extends ItemSword implements IRepairable, IWarpingGear {

    // ==================================================
    // NBT 键（改键名会导致旧存档读不到）
    // ==================================================
    public static final String NBT_TIER = "ScytheTier";
    public static final String NBT_KILLS = "ScytheKills";
    public static final String NBT_OWNER = "ScytheOwner"; // UUID，判断归属
    public static final String NBT_OWNER_NAME = "ScytheOwnerName"; // 玩家名，仅显示

    // ==================================================
    // 多语言键（集中声明，方便统一改）
    // ==================================================
    /** 阶段名：tier.0 ~ tier.4 */
    private static final String LANG_TIER_PREFIX = "item.magianaturalis.herobrines_scythe.tier.";
    private static final String LANG_TOOLTIP_STAGE = "item.magianaturalis.herobrines_scythe.tooltip.stage";
    private static final String LANG_TOOLTIP_KILLS = "item.magianaturalis.herobrines_scythe.tooltip.kills";
    private static final String LANG_TOOLTIP_KILLS_MAX = "item.magianaturalis.herobrines_scythe.tooltip.kills.max";
    private static final String LANG_TOOLTIP_LIFESTEAL = "item.magianaturalis.herobrines_scythe.tooltip.lifesteal";
    private static final String LANG_TOOLTIP_BLOCK = "item.magianaturalis.herobrines_scythe.tooltip.block";
    private static final String LANG_TOOLTIP_ATTACK = "item.magianaturalis.herobrines_scythe.tooltip.attack";
    private static final String LANG_TOOLTIP_SPEED = "item.magianaturalis.herobrines_scythe.tooltip.speed";
    private static final String LANG_TOOLTIP_WARP = "item.magianaturalis.herobrines_scythe.tooltip.warp";
    private static final String LANG_TOOLTIP_MAX_TARGETS = "item.magianaturalis.herobrines_scythe.tooltip.max_targets";
    private static final String LANG_TOOLTIP_MAXED = "item.magianaturalis.herobrines_scythe.tooltip.maxed";
    private static final String LANG_TOOLTIP_OWNER = "item.magianaturalis.herobrines_scythe.tooltip.owner";
    private static final String LANG_TOOLTIP_OWNER_NONE = "item.magianaturalis.herobrines_scythe.tooltip.owner.none";
    private static final String LANG_TOOLTIP_AUTHOR_1 = "item.magianaturalis.herobrines_scythe.tooltip.author.1";
    private static final String LANG_TOOLTIP_AUTHOR_2 = "item.magianaturalis.herobrines_scythe.tooltip.author.2";
    private static final String LANG_TOOLTIP_AUTHOR_3 = "item.magianaturalis.herobrines_scythe.tooltip.author.3";
    private static final String LANG_CHAT_BIND = "chat.magianaturalis.scythe.bind";

    // ==================================================
    // 平衡数值表（想改平衡只动这里）
    // ==================================================
    /**
     * 升到 Tier 1/2/3/4 所需累计击杀数。
     * 索引 0 永远是 0，因为 Tier 0 出生就是。
     * 也就是说：
     * 杀敌数 >= 30 → Tier 1
     * 杀敌数 >= 120 → Tier 2
     * 杀敌数 >= 450 → Tier 3
     * 杀敌数 >= 1500 → Tier 4
     */
    public static final int[] TIER_THRESHOLDS = { 0, 30, 120, 450, 1500 };

    /** 杀敌数上限。到顶后不再累积，Tooltip 显示 MAX */
    public static final int MAX_KILLS = 99999;

    /** 每级基础吸血（满血时）。实际吸血会随血量越低越高，见 performPlayerAttackAt */
    public static final float[] TIER_LIFESTEAL = { 0.03F, 0.05F, 0.07F, 0.09F, 0.12F };

    /** 每级格挡减伤。1.00F = 100% 完全免伤 */
    public static final float[] TIER_BLOCK = { 0.20F, 0.40F, 0.60F, 0.80F, 1.00F };

    /**
     * 每级基础攻击力，写进属性修饰符，参与原版暴击/击退计算。
     * 【新曲线】前期温和，满杀敌数时质变。
     * Tier 0~4 属性值：5 / 7 / 10 / 14 / 20
     * 实际普攻 = 属性 + Tier*2（事件补正） + 1（玩家空手基础）
     */
    public static final float[] TIER_ATTACK = { 5.0F, 7.0F, 10.0F, 14.0F, 20.0F };

    /**
     * 杀敌数达到 MAX_KILLS 时的终极攻击力。
     * 这个是属性值，实际普攻会额外 +1（玩家空手基础）。
     * 想精确 999 伤害就把这里改成 998.0F。
     */
    public static final float MAX_ATTACK = 999.0F;

    /** 每级移动速度加成。第二参数 2 表示"加法百分比"，0.05F = +5% */
    public static final float[] TIER_SPEED = { 0.05F, 0.08F, 0.11F, 0.14F, 0.18F };

    /** 每级扭曲值（IWarpingGear 手持生效，收起来不占玩家永久扭曲） */
    public static final int[] TIER_WARP = { 0, 15, 50, 140, 300 };

    /** 每级右键吸取 Vis 的最大目标数 */
    public static final int[] TIER_MAX_TARGETS = { 5, 10, 15, 22, 30 };

    /**
     * 属性修饰符的 UUID。
     * 【重要】同一个属性如果 UUID 相同，新的会覆盖旧的，不会叠加。
     * 所以每次 getAttributeModifiers 用同一个 UUID 就安全。
     * 千万别每次随机一个 UUID，否则属性会无限叠加。
     */
    private static final UUID ATK_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID SPD_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CE");

    /** 5 张图标，索引 = Tier。客户端渲染用 */
    @SideOnly(Side.CLIENT)
    private IIcon[] tierIcons;

    public ItemHerobrinesScythe() {
        super(ToolMaterial.EMERALD);
        this.setMaxDamage(50000);
    }

    // ==================================================
    // 贴图：按 Tier 切换
    // ==================================================

    /**
     * 注册 5 张物品贴图。
     * 1.7.10 会把所有 register.registerIcon 注册的贴图拼进 items.png 图集，
     * 之后 getIconIndex 才能从图集里取到具体的 IIcon。
     * 这一步是必须的，不注册就找不到贴图，会变紫黑方格。
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        tierIcons = new IIcon[5];
        tierIcons[0] = register.registerIcon("magianaturalis:herobrines_scythe");
        tierIcons[1] = register.registerIcon("magianaturalis:herobrines_scythe1");
        tierIcons[2] = register.registerIcon("magianaturalis:herobrines_scythe2");
        tierIcons[3] = register.registerIcon("magianaturalis:herobrines_scythe3");
        tierIcons[4] = register.registerIcon("magianaturalis:herobrines_scythe4");
        this.itemIcon = tierIcons[0]; // 默认图标 = 0 级
    }

    /**
     * 返回当前 Stack 对应的图标。
     * 1.7.10 的 ItemStack.getIconIndex() 会调用到这里，
     * 所以自定义渲染器 HerobrinesScytheRenderer 不用改，自动跟着 NBT 变。
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        return tierIcons[getTier(stack)];
    }

    /** 老版本渲染管线的备用入口，保持返回一致 */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return tierIcons[getTier(stack)];
    }

    // ==================================================
    // 右键（格挡 + 吸取）
    // ==================================================

    /** 借用剑的格挡动作，1.7.10 里 EnumAction.block 触发使用中的减速和手臂姿态 */
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    /** 最长右键持续时间，72000 tick = 1 小时，够用了 */
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    /** 右键开始"使用"状态。实际吸取和格挡逻辑在 ScytheBlockHandler 里处理 */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    // ==================================================
    // NBT 工具方法
    // ==================================================

    /**
     * 保证 stack 有 tagCompound，没有就 new 一个。
     * 所有写 NBT 的地方都先调这个，防止 NPE。
     */
    private static NBTTagCompound ensureTag(ItemStack stack) {
        if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
        return stack.stackTagCompound;
    }

    /**
     * 读当前 Tier。
     * clamp_int 把值夹在 0~4，防止 NBT 被改坏导致数组越界。
     */
    public static int getTier(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return 0;
        return MathHelper.clamp_int(stack.stackTagCompound.getByte(NBT_TIER), 0, 4);
    }

    /** 读击杀数。没标签就当 0 */
    public static int getKills(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return 0;
        return stack.stackTagCompound.getInteger(NBT_KILLS);
    }

    /** 读 UUID 字符串。没绑定时返回 null */
    public static String getOwner(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return null;
        if (!stack.stackTagCompound.hasKey(NBT_OWNER)) return null;
        return stack.stackTagCompound.getString(NBT_OWNER);
    }

    /**
     * 读缓存的玩家名，仅用于 Tooltip 显示。
     * 空字符串也返回 null，方便上层统一判空。
     */
    public static String getOwnerName(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return null;
        if (!stack.stackTagCompound.hasKey(NBT_OWNER_NAME)) return null;
        String name = stack.stackTagCompound.getString(NBT_OWNER_NAME);
        return name.isEmpty() ? null : name;
    }

    /**
     * 首次持有时绑定主人。已经有 owner 就什么都不做。
     * 返回 true 表示本次真的绑定了。
     */
    public static boolean bindOwnerIfEmpty(ItemStack stack, EntityPlayer player) {
        if (stack == null || player == null) return false;
        NBTTagCompound tag = ensureTag(stack);
        if (tag.hasKey(NBT_OWNER)) return false; // 已认主，不再改

        // 认主判定永远用 UUID
        tag.setString(
            NBT_OWNER,
            player.getUniqueID()
                .toString());
        // 玩家名只缓存一份，仅用于显示
        tag.setString(NBT_OWNER_NAME, player.getCommandSenderName());

        // 认主成功提示。只在服务端发，客户端由 addChatMessage 自动同步
        if (!player.worldObj.isRemote) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal(LANG_CHAT_BIND)));
            player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.evolve", 1.0F, 0.6F);
        }

        return true;
    }

    /**
     * 判断玩家是否是当前 Stack 的主人。
     * 未绑定 → 返回 true（人人都能拿，因为还没认主）。
     */
    public static boolean isOwner(ItemStack stack, EntityPlayer player) {
        String owner = getOwner(stack);
        if (owner == null) return true;
        return owner.equals(
            player.getUniqueID()
                .toString());
    }

    /**
     * 击杀 +1，并检查是否跨过升级阈值。
     * 返回 true 表示本次触发升级。
     *
     * 实现思路：从高到低遍历 TIER_THRESHOLDS，
     * 找到第一个 kills >= 阈值的下标，就是当前应处的 Tier。
     * 比当前 Tier 大就升级。
     */
    public static boolean addKill(ItemStack stack, EntityPlayer owner) {
        NBTTagCompound tag = ensureTag(stack);
        int kills = tag.getInteger(NBT_KILLS);

        // 到顶就不再累积
        if (kills >= MAX_KILLS) return false;

        kills++;
        tag.setInteger(NBT_KILLS, kills);

        int newTier = 0;
        for (int i = TIER_THRESHOLDS.length - 1; i >= 0; i--) {
            if (kills >= TIER_THRESHOLDS[i]) {
                newTier = i;
                break;
            }
        }

        int oldTier = getTier(stack);
        if (newTier > oldTier) {
            tag.setByte(NBT_TIER, (byte) newTier);
            return true;
        }
        return false;
    }

    /** 是否已到杀敌数上限 */
    public static boolean isMaxed(ItemStack stack) {
        return getKills(stack) >= MAX_KILLS;
    }

    // ==================================================
    // 阶段名（走多语言）
    // ==================================================
    /**
     * 阶段显示名。
     * 键名：item.magianaturalis.herobrines_scythe.tier.0 ~ .4
     * 找不到键会直接显示键名本身，不会崩。
     */
    public static String tierName(int tier) {
        return StatCollector.translateToLocal(LANG_TIER_PREFIX + tier);
    }

    // ==================================================
    // 挥动音效 / 稀有度
    // ==================================================

    /** 空挥也能听到声音 */
    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.swing", 0.8F, 1.0F);
        }
        return super.onEntitySwing(entityLiving, stack);
    }

    /** 物品稀有度（名字黄色） */
    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.uncommon;
    }

    // ==================================================
    // Tooltip
    // ==================================================
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List lst, boolean held) {
        int tier = getTier(stack);
        int kills = getKills(stack);

        // ---- 阶段 ----
        // 键：tooltip.stage = "阶段: %s (%s/5)"
        lst.add(
            EnumChatFormatting.DARK_RED + StatCollector.translateToLocalFormatted(
                LANG_TOOLTIP_STAGE,
                EnumChatFormatting.RED + tierName(tier) + EnumChatFormatting.GRAY,
                tier + 1));

        // ---- 杀敌数 ----
        // 键：tooltip.kills = "杀敌数: %s"
        // 键：tooltip.kills.max = "MAX"
        String killText;
        if (isMaxed(stack)) {
            killText = EnumChatFormatting.DARK_RED + ""
                + EnumChatFormatting.BOLD
                + StatCollector.translateToLocal(LANG_TOOLTIP_KILLS_MAX);
        } else if (tier < 4) {
            killText = EnumChatFormatting.RED + String.valueOf(kills)
                + EnumChatFormatting.GRAY
                + " / "
                + TIER_THRESHOLDS[tier + 1];
        } else {
            killText = EnumChatFormatting.RED + String.valueOf(kills) + EnumChatFormatting.GRAY + " / " + MAX_KILLS;
        }
        lst.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_KILLS, killText));

        // ---- 数值面板 ----
        // 键：tooltip.lifesteal = "吸血: %s%% (血量越低，吸血越高)"
        lst.add(
            EnumChatFormatting.RED
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_LIFESTEAL, (int) (TIER_LIFESTEAL[tier] * 100)));

        // 键：tooltip.block = "格挡: %s%%"
        lst.add(
            EnumChatFormatting.BLUE
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_BLOCK, (int) (TIER_BLOCK[tier] * 100)));

        // 键：tooltip.attack = "攻击: %s"
        String atkText;
        if (isMaxed(stack)) {
            atkText = EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + (int) MAX_ATTACK;
        } else {
            int realAtk = (int) (TIER_ATTACK[tier] + tier * 2.0F) + 1;
            atkText = EnumChatFormatting.GOLD + String.valueOf(realAtk);
        }
        lst.add(EnumChatFormatting.GOLD + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_ATTACK, atkText));

        // 键：tooltip.speed = "速度: +%s%%"
        lst.add(
            EnumChatFormatting.AQUA
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_SPEED, (int) (TIER_SPEED[tier] * 100)));

        // 键：tooltip.warp = "扭曲: %s"
        lst.add(
            EnumChatFormatting.DARK_PURPLE
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_WARP, TIER_WARP[tier]));

        // 键：tooltip.max_targets = "吸取VIS上限: %s 个目标"
        lst.add(
            EnumChatFormatting.LIGHT_PURPLE
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_MAX_TARGETS, TIER_MAX_TARGETS[tier]));

        // ---- 到顶提示 ----
        if (isMaxed(stack)) {
            lst.add("");
            // 键：tooltip.maxed
            lst.add(
                EnumChatFormatting.DARK_RED + ""
                    + EnumChatFormatting.ITALIC
                    + StatCollector.translateToLocal(LANG_TOOLTIP_MAXED));
        }

        // ---- 认主信息 ----
        // 键：tooltip.owner = "认主: %s"
        // 键：tooltip.owner.none = "未认主"
        String ownerUuid = getOwner(stack);
        lst.add("");
        if (ownerUuid == null) {
            // 配方书 / JEI / NEI 里显示的裸物品没有 NBT，走到这里
            lst.add(
                EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocalFormatted(
                    LANG_TOOLTIP_OWNER,
                    EnumChatFormatting.GRAY + StatCollector.translateToLocal(LANG_TOOLTIP_OWNER_NONE)));
        } else {
            String ownerName = getOwnerName(stack);
            if (ownerName == null) {
                // 兼容旧版本只存 UUID 的镰刀
                ownerName = ownerUuid.substring(0, Math.min(8, ownerUuid.length())) + "...";
            }
            lst.add(
                EnumChatFormatting.DARK_GRAY
                    + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_OWNER, EnumChatFormatting.RED + ownerName));
        }

        // ---- 作者签名 ----
        lst.add("");
        lst.add(
            EnumChatFormatting.DARK_GRAY + ""
                + EnumChatFormatting.ITALIC
                + StatCollector.translateToLocal(LANG_TOOLTIP_AUTHOR_1));
        lst.add(
            EnumChatFormatting.DARK_GRAY + ""
                + EnumChatFormatting.ITALIC
                + StatCollector.translateToLocal(LANG_TOOLTIP_AUTHOR_2));
        lst.add(
            EnumChatFormatting.DARK_GRAY + ""
                + EnumChatFormatting.ITALIC
                + StatCollector.translateToLocal(LANG_TOOLTIP_AUTHOR_3));
    }

    // ==================================================
    // onUpdate：认主绑定 + 自动修复
    // ==================================================
    @Override
    public void onUpdate(ItemStack stk, World w, Entity entity, int slot, boolean held) {
        super.onUpdate(stk, w, entity, slot, held);

        // 首次被玩家手持时绑定主人
        if (held && entity instanceof EntityPlayer) {
            bindOwnerIfEmpty(stk, (EntityPlayer) entity);
        }

        // 自动修复：每 20 tick 恢复 1 点耐久（虚空锄同款）
        if (stk.isItemDamaged() && entity != null
            && entity.ticksExisted % 20 == 0
            && entity instanceof EntityLivingBase) {
            stk.damageItem(-1, (EntityLivingBase) entity);
        }
    }

    // ==================================================
    // 攻击连锁（保留你原版）
    // ==================================================

    /**
     * 连锁闪电攻击。
     * 从 attacked 周围 6 格内随机挑一个敌对生物，
     * 对他执行一次和玩家普攻一样的伤害，再画一条闪电，再递归。
     *
     * doNotAttack 记录已经被打过的不重复打。
     */
    @SuppressWarnings("unchecked")
    public static void attack(EntityPlayer attacker, List<EntityLivingBase> doNotAttack, EntityLivingBase attacked) {
        AxisAlignedBB aabb = AxisAlignedBB
            .getBoundingBox(
                attacked.posX - 1,
                attacked.posY - 1,
                attacked.posZ - 1,
                attacked.posX + 1,
                attacked.posY + 1,
                attacked.posZ + 1)
            .expand(6, 6, 6);

        List<EntityLivingBase> mobs = attacked.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        Random rnd = attacker.worldObj.rand;
        mobs.removeAll(doNotAttack);

        if (!mobs.isEmpty()) {
            while (!mobs.isEmpty()) {
                int index = rnd.nextInt(mobs.size());
                // 只打敌对生物，不误伤玩家和其他生物
                if (mobs.get(index) != null && mobs.get(index)
                    .isEntityAlive() && mobs.get(index) instanceof IMob && !(mobs.get(index) instanceof EntityPlayer)) {

                    performPlayerAttackAt(attacker, mobs.get(index));

                    // 画血红色闪电
                    MagiaNaturalis.lightning(
                        attacker.worldObj,
                        attacked.posX,
                        attacked.posY + rnd.nextDouble() * attacked.getEyeHeight(),
                        attacked.posZ,
                        mobs.get(index).posX,
                        mobs.get(index).posY + rnd.nextDouble() * mobs.get(index)
                            .getEyeHeight(),
                        mobs.get(index).posZ,
                        20,
                        2F,
                        10,
                        0);

                    attacker.worldObj.playSoundAtEntity(mobs.get(index), "thaumcraft:zap", 1F, 0.8F);
                    attacker.worldObj
                        .playSoundAtEntity(mobs.get(index), "magianaturalis:item.herobrine.attack", 1.0F, 1.0F);

                    doNotAttack.add(mobs.get(index));
                    attack(attacker, doNotAttack, mobs.get(index)); // 递归跳到下一个
                    break;
                } else {
                    mobs.remove(index);
                }
            }
        }
    }

    /** 左键点生物时触发连锁。非主人不触发 */
    @Override
    public boolean onLeftClickEntity(ItemStack stk, EntityPlayer attacker, Entity attacked) {
        if (!isOwner(stk, attacker)) {
            return super.onLeftClickEntity(stk, attacker, attacked);
        }
        if (attacked.isEntityAlive() && attacked instanceof IMob) {
            attack(attacker, new ArrayList<EntityLivingBase>(), (EntityLivingBase) attacked);
        }
        return super.onLeftClickEntity(stk, attacker, attacked);
    }

    /**
     * 对目标执行一次和玩家普攻几乎一样的伤害计算。
     * 这是照抄 EntityPlayer.attackTargetEntityWithCurrentItem 的流程，
     * 因为直接调 attackTargetEntityWithCurrentItem 会受玩家自己目标锁定的影响。
     *
     * 动态吸血也写在这里：每次伤害结算后按 Tier 治疗玩家。
     * 注意：这里的攻击力已经包含了 getAttributeModifiers 提供的属性值。
     */
    public static void performPlayerAttackAt(EntityPlayer p, Entity p_71059_1_) {
        // 其他模组监听攻击事件，取消就不打
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(p, p_71059_1_))) return;
        if (!p_71059_1_.canAttackWithItem()) return;
        if (p_71059_1_.hitByEntity(p)) return;

        // 基础伤害 = 玩家当前攻击属性（已经包含了镰刀的属性修饰符）
        float f = (float) p.getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .getAttributeValue();
        int i = 0;
        float f1 = 0.0F;

        // 附魔额外伤害
        if (p_71059_1_ instanceof EntityLivingBase) {
            f1 = EnchantmentHelper.getEnchantmentModifierLiving(p, (EntityLivingBase) p_71059_1_);
            i += EnchantmentHelper.getKnockbackModifier(p, (EntityLivingBase) p_71059_1_);
        }
        if (p.isSprinting()) ++i;

        if (f > 0.0F || f1 > 0.0F) {
            // 跳劈判定
            boolean flag = p.fallDistance > 0.0F && !p.onGround
                && !p.isOnLadder()
                && !p.isInWater()
                && !p.isPotionActive(Potion.blindness)
                && p.ridingEntity == null
                && p_71059_1_ instanceof EntityLivingBase;
            if (flag && f > 0.0F) f *= 1.5F;
            f += f1;

            // 火焰附加（原版 Fire Aspect）：附魔了就会点燃目标
            boolean flag1 = false;
            int j = EnchantmentHelper.getFireAspectModifier(p);
            if (p_71059_1_ instanceof EntityLivingBase && j > 0 && !p_71059_1_.isBurning()) {
                flag1 = true;
                p_71059_1_.setFire(1);
            }

            // 真正造成伤害
            boolean flag2 = p_71059_1_.attackEntityFrom(DamageSource.causePlayerDamage(p), f);
            if (flag2) {
                // ==========================================
                // 按 Tier 动态吸血
                // 基础吸血 + (1 - 当前血量百分比) * 15%
                // 也就是血越少吸得越多
                // ==========================================
                ItemStack heldStack = p.getCurrentEquippedItem();
                if (heldStack != null && heldStack.getItem() instanceof ItemHerobrinesScythe) {
                    int tier = getTier(heldStack);
                    float healthRatio = p.getHealth() / p.getMaxHealth();
                    float lifesteal = TIER_LIFESTEAL[tier] + (1.0F - healthRatio) * 0.15F;
                    p.heal(f * lifesteal);
                }

                // 击退
                if (i > 0) {
                    p_71059_1_.addVelocity(
                        -MathHelper.sin(p.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F,
                        0.1D,
                        MathHelper.cos(p.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F);
                    p.motionX *= 0.6D;
                    p.motionZ *= 0.6D;
                    p.setSprinting(false);
                }
                if (flag) p.onCriticalHit(p_71059_1_);
                if (f1 > 0.0F) p.onEnchantmentCritical(p_71059_1_);
                if (f >= 18.0F) p.triggerAchievement(AchievementList.overkill);
                p.setLastAttacker(p_71059_1_);

                // 触发护甲附魔
                if (p_71059_1_ instanceof EntityLivingBase)
                    EnchantmentHelper.func_151384_a((EntityLivingBase) p_71059_1_, p);
                EnchantmentHelper.func_151385_b(p, p_71059_1_);

                // 触发武器 hitEntity（比如耐久消耗、吸血附魔）
                ItemStack itemstack = p.getCurrentEquippedItem();
                Object object = p_71059_1_;
                if (p_71059_1_ instanceof EntityDragonPart) {
                    IEntityMultiPart ientitymultipart = ((EntityDragonPart) p_71059_1_).entityDragonObj;
                    if (ientitymultipart != null && ientitymultipart instanceof EntityLivingBase) {
                        object = (EntityLivingBase) ientitymultipart;
                    }
                }
                if (itemstack != null && object instanceof EntityLivingBase) {
                    itemstack.hitEntity((EntityLivingBase) object, p);
                    if (itemstack.stackSize <= 0) p.destroyCurrentEquippedItem();
                }

                // 统计 + 火焰附加的持续燃烧
                if (p_71059_1_ instanceof EntityLivingBase) {
                    p.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));
                    if (j > 0) p_71059_1_.setFire(j * 4);
                }

                // 消耗饥饿
                p.addExhaustion(0.3F);
            } else if (flag1) {
                p_71059_1_.extinguish();
            }
        }
    }

    // ==================================================
    // 扭曲值：随 Tier 增长
    // ==================================================

    /**
     * IWarpingGear 接口。
     * 神秘时代每 tick 查一次玩家手持物品，是 IWarpingGear 就把这里的值加到玩家。
     * 所以收起来就不占玩家扭曲，只是手持时生效。
     */
    @Override
    public int getWarp(ItemStack stack, EntityPlayer player) {
        if (stack == null) return 0;
        return TIER_WARP[getTier(stack)];
    }

    // ==================================================
    // 属性修饰符：按 Tier 动态
    // ==================================================

    /**
     * 每次攻击计算伤害时都会调用这个方法读属性。
     * 因为读的是 NBT，所以 Tier 变了伤害也立即跟着变。
     *
     * 攻击力分两段：
     * - 未满杀敌数：读 TIER_ATTACK[tier]（5 / 7 / 10 / 14 / 20）
     * - 满杀敌数：直接读 MAX_ATTACK（999），跳过 Tier 曲线
     *
     * UUID 必须固定，否则会叠加。第二参数 2 表示"百分比加法"。
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap attribs = HashMultimap.create();
        int tier = getTier(stack);

        // 满杀敌数时攻击力直接质变到 999，否则按 Tier 曲线
        float atk = isMaxed(stack) ? MAX_ATTACK : TIER_ATTACK[tier];

        attribs.put(
            SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
            new AttributeModifier(ATK_UUID, "Weapon modifier", atk, 0));

        attribs.put(
            SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(),
            new AttributeModifier(SPD_UUID, "Speed modifier", TIER_SPEED[tier], 2));

        return attribs;
    }

    /** 声明为工具，用于某些附魔判定（比如耐久附魔） */
    @Override
    public boolean isItemTool(ItemStack stk) {
        return true;
    }
}
