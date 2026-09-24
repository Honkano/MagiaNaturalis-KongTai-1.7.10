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
import com.github.elenterius.magianaturalis.event.ScytheBlockHandler;
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
 * ScytheOwner String 认主玩家 UUID
 * ScytheOwnerName String 认主玩家名（仅显示）
 *
 * 【耐久机制】
 * - Tier 0~4：统一 50000 耐久（原版耐久条正常显示）
 * - MAX（99999 杀）：damageItem 被拦截 → 永不损坏
 * - 自动修复：按 Tier 间隔触发，消耗法杖/魔力石的六大原始要素
 * - 右键吸取 Vis 时：按 Tier 扣耐久（低等级扣得多）
 */
public class ItemHerobrinesScythe extends ItemSword implements IRepairable, IWarpingGear {

    // ==================================================
    // NBT 键
    // ==================================================
    public static final String NBT_TIER = "ScytheTier";
    public static final String NBT_KILLS = "ScytheKills";
    public static final String NBT_OWNER = "ScytheOwner";
    public static final String NBT_OWNER_NAME = "ScytheOwnerName";

    // ==================================================
    // 多语言键
    // ==================================================
    private static final String LANG_TIER_PREFIX = "item.magianaturalis.herobrines_scythe.tier.";
    private static final String LANG_QUALITY_PREFIX = "item.magianaturalis.herobrines_scythe.quality.";
    private static final String LANG_TOOLTIP_STAGE = "item.magianaturalis.herobrines_scythe.tooltip.stage";
    private static final String LANG_TOOLTIP_QUALITY = "item.magianaturalis.herobrines_scythe.tooltip.quality";
    private static final String LANG_TOOLTIP_KILLS = "item.magianaturalis.herobrines_scythe.tooltip.kills";
    private static final String LANG_TOOLTIP_KILLS_MAX = "item.magianaturalis.herobrines_scythe.tooltip.kills.max";
    private static final String LANG_TOOLTIP_LIFESTEAL = "item.magianaturalis.herobrines_scythe.tooltip.lifesteal";
    private static final String LANG_TOOLTIP_BLOCK = "item.magianaturalis.herobrines_scythe.tooltip.block";
    private static final String LANG_TOOLTIP_ATTACK = "item.magianaturalis.herobrines_scythe.tooltip.attack";
    private static final String LANG_TOOLTIP_SPEED = "item.magianaturalis.herobrines_scythe.tooltip.speed";
    private static final String LANG_TOOLTIP_WARP = "item.magianaturalis.herobrines_scythe.tooltip.warp";
    private static final String LANG_TOOLTIP_MAX_TARGETS = "item.magianaturalis.herobrines_scythe.tooltip.max_targets";
    private static final String LANG_TOOLTIP_DURABILITY = "item.magianaturalis.herobrines_scythe.tooltip.durability";
    private static final String LANG_TOOLTIP_DURABILITY_MAX = "item.magianaturalis.herobrines_scythe.tooltip.durability.max";
    private static final String LANG_TOOLTIP_MAXED = "item.magianaturalis.herobrines_scythe.tooltip.maxed";
    private static final String LANG_TOOLTIP_OWNER = "item.magianaturalis.herobrines_scythe.tooltip.owner";
    private static final String LANG_TOOLTIP_OWNER_NONE = "item.magianaturalis.herobrines_scythe.tooltip.owner.none";
    private static final String LANG_TOOLTIP_AUTHOR_1 = "item.magianaturalis.herobrines_scythe.tooltip.author.1";
    private static final String LANG_TOOLTIP_AUTHOR_2 = "item.magianaturalis.herobrines_scythe.tooltip.author.2";
    private static final String LANG_TOOLTIP_AUTHOR_3 = "item.magianaturalis.herobrines_scythe.tooltip.author.3";
    private static final String LANG_CHAT_BIND = "chat.magianaturalis.scythe.bind";

    // ==================================================
    // 平衡数值表
    // ==================================================
    public static final int[] TIER_THRESHOLDS = { 0, 30, 120, 450, 1500 };
    public static final int MAX_KILLS = 99999;

    public static final float[] TIER_LIFESTEAL = { 0.03F, 0.05F, 0.07F, 0.09F, 0.12F };
    public static final float[] TIER_BLOCK = { 0.20F, 0.40F, 0.60F, 0.80F, 1.00F };
    public static final float[] TIER_ATTACK = { 5.0F, 7.0F, 10.0F, 14.0F, 20.0F };
    public static final float MAX_ATTACK = 999.0F;
    public static final float[] TIER_SPEED = { 0.05F, 0.08F, 0.11F, 0.14F, 0.18F };
    public static final int[] TIER_WARP = { 0, 15, 50, 140, 300 };
    public static final int[] TIER_MAX_TARGETS = { 5, 10, 15, 22, 30 };

    /** 每级基础修复间隔（tick / 1 点耐久） */
    public static final int[] TIER_REPAIR_INTERVAL = { 10, 6, 4, 2, 1 };

    /** 每级每点耐久的 Vis 消耗总量（六大原始要素均分） */
    public static final int[] TIER_REPAIR_COST = { 16, 8, 6, 4, 2 };

    /** 连锁攻击每次命中扣的耐久。Tier 越高，每命中扣得越少 */
    public static final int[] TIER_CHAIN_DAMAGE = { 30, 20, 12, 6, 3 };

    /** 内部耐久上限。所有 Tier 共享 */
    public static final int INTERNAL_MAX_DAMAGE = 50000;

    private static final UUID ATK_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID SPD_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CE");

    @SideOnly(Side.CLIENT)
    private IIcon[] tierIcons;

    public ItemHerobrinesScythe() {
        super(ToolMaterial.EMERALD);
        this.setMaxDamage(INTERNAL_MAX_DAMAGE);
    }

    // ==================================================
    // 贴图
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        tierIcons = new IIcon[5];
        tierIcons[0] = register.registerIcon("magianaturalis:herobrines_scythe");
        tierIcons[1] = register.registerIcon("magianaturalis:herobrines_scythe1");
        tierIcons[2] = register.registerIcon("magianaturalis:herobrines_scythe2");
        tierIcons[3] = register.registerIcon("magianaturalis:herobrines_scythe3");
        tierIcons[4] = register.registerIcon("magianaturalis:herobrines_scythe4");
        this.itemIcon = tierIcons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        return tierIcons[getTier(stack)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return tierIcons[getTier(stack)];
    }

    // ==================================================
    // 右键
    // ==================================================
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    // ==================================================
    // NBT 工具方法
    // ==================================================
    private static NBTTagCompound ensureTag(ItemStack stack) {
        if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
        return stack.stackTagCompound;
    }

    public static int getTier(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return 0;
        return MathHelper.clamp_int(stack.stackTagCompound.getByte(NBT_TIER), 0, 4);
    }

    public static int getKills(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return 0;
        return stack.stackTagCompound.getInteger(NBT_KILLS);
    }

    public static String getOwner(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return null;
        if (!stack.stackTagCompound.hasKey(NBT_OWNER)) return null;
        return stack.stackTagCompound.getString(NBT_OWNER);
    }

    public static String getOwnerName(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return null;
        if (!stack.stackTagCompound.hasKey(NBT_OWNER_NAME)) return null;
        String name = stack.stackTagCompound.getString(NBT_OWNER_NAME);
        return name.isEmpty() ? null : name;
    }

    public static boolean bindOwnerIfEmpty(ItemStack stack, EntityPlayer player) {
        if (stack == null || player == null) return false;
        NBTTagCompound tag = ensureTag(stack);
        if (tag.hasKey(NBT_OWNER)) return false;

        tag.setString(
            NBT_OWNER,
            player.getUniqueID()
                .toString());
        tag.setString(NBT_OWNER_NAME, player.getCommandSenderName());

        if (!player.worldObj.isRemote) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal(LANG_CHAT_BIND)));
            player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.evolve", 1.0F, 0.6F);
        }

        return true;
    }

    public static boolean isOwner(ItemStack stack, EntityPlayer player) {
        String owner = getOwner(stack);
        if (owner == null) return true;
        return owner.equals(
            player.getUniqueID()
                .toString());
    }

    public static boolean addKill(ItemStack stack, EntityPlayer owner) {
        NBTTagCompound tag = ensureTag(stack);
        int kills = tag.getInteger(NBT_KILLS);

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

    public static boolean isMaxed(ItemStack stack) {
        return getKills(stack) >= MAX_KILLS;
    }

    // ==================================================
    // 阶段名 / 品质名
    // ==================================================
    public static String tierName(int tier) {
        return StatCollector.translateToLocal(LANG_TIER_PREFIX + tier);
    }

    public static String qualityName(ItemStack stack) {
        int idx = isMaxed(stack) ? 5 : getTier(stack);
        return StatCollector.translateToLocal(LANG_QUALITY_PREFIX + idx);
    }

    // ==================================================
    // 名字按 Tier 上色
    // ==================================================
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = super.getItemStackDisplayName(stack);
        int tier = getTier(stack);

        EnumChatFormatting color;
        boolean bold = false;

        if (isMaxed(stack)) {
            color = EnumChatFormatting.DARK_RED;
            bold = true;
        } else {
            switch (tier) {
                case 0:
                    color = EnumChatFormatting.GRAY;
                    break;
                case 1:
                    color = EnumChatFormatting.WHITE;
                    break;
                case 2:
                    color = EnumChatFormatting.GOLD;
                    break;
                case 3:
                    color = EnumChatFormatting.RED;
                    break;
                case 4:
                    color = EnumChatFormatting.DARK_RED;
                    break;
                default:
                    color = EnumChatFormatting.GRAY;
            }
        }

        return color.toString() + (bold ? EnumChatFormatting.BOLD.toString() : "") + name;
    }

    // ==================================================
    // 挥动音效 / 稀有度
    // ==================================================
    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.swing", 0.8F, 1.0F);
        }
        return super.onEntitySwing(entityLiving, stack);
    }

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

        // ---- 品质 ----
        lst.add(
            EnumChatFormatting.GRAY
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_QUALITY, qualityName(stack)));

        // ---- 阶段 ----
        lst.add(
            EnumChatFormatting.DARK_RED + StatCollector.translateToLocalFormatted(
                LANG_TOOLTIP_STAGE,
                EnumChatFormatting.RED + tierName(tier) + EnumChatFormatting.GRAY,
                tier + 1));

        // ---- 杀敌数 ----
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

        // ---- 耐久 ----
        if (isMaxed(stack)) {
            lst.add(
                EnumChatFormatting.DARK_RED + ""
                    + EnumChatFormatting.BOLD
                    + StatCollector.translateToLocal(LANG_TOOLTIP_DURABILITY_MAX));
        } else {
            int max = stack.getMaxDamage();
            int current = max - stack.getItemDamage();
            if (current < 0) current = 0;
            lst.add(
                EnumChatFormatting.GREEN
                    + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_DURABILITY, current, max));
        }

        // ---- 数值面板 ----
        lst.add(
            EnumChatFormatting.RED
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_LIFESTEAL, (int) (TIER_LIFESTEAL[tier] * 100)));

        lst.add(
            EnumChatFormatting.BLUE
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_BLOCK, (int) (TIER_BLOCK[tier] * 100)));

        String atkText;
        if (isMaxed(stack)) {
            atkText = EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + (int) MAX_ATTACK;
        } else {
            int realAtk = (int) (TIER_ATTACK[tier] + tier * 2.0F) + 1;
            atkText = EnumChatFormatting.GOLD + String.valueOf(realAtk);
        }
        lst.add(EnumChatFormatting.GOLD + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_ATTACK, atkText));

        lst.add(
            EnumChatFormatting.AQUA
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_SPEED, (int) (TIER_SPEED[tier] * 100)));

        lst.add(
            EnumChatFormatting.DARK_PURPLE
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_WARP, TIER_WARP[tier]));

        lst.add(
            EnumChatFormatting.LIGHT_PURPLE
                + StatCollector.translateToLocalFormatted(LANG_TOOLTIP_MAX_TARGETS, TIER_MAX_TARGETS[tier]));

        // ---- 到顶提示 ----
        if (isMaxed(stack)) {
            lst.add("");
            lst.add(
                EnumChatFormatting.DARK_RED + ""
                    + EnumChatFormatting.ITALIC
                    + StatCollector.translateToLocal(LANG_TOOLTIP_MAXED));
        }

        // ---- 认主信息 ----
        String ownerUuid = getOwner(stack);
        lst.add("");
        if (ownerUuid == null) {
            lst.add(
                EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocalFormatted(
                    LANG_TOOLTIP_OWNER,
                    EnumChatFormatting.GRAY + StatCollector.translateToLocal(LANG_TOOLTIP_OWNER_NONE)));
        } else {
            String ownerName = getOwnerName(stack);
            if (ownerName == null) {
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
    // onUpdate：认主 + 自动修复
    // ==================================================
    @Override
    public void onUpdate(ItemStack stk, World w, Entity entity, int slot, boolean held) {
        super.onUpdate(stk, w, entity, slot, held);

        // 首次被玩家手持时绑定主人
        if (held && entity instanceof EntityPlayer) {
            bindOwnerIfEmpty(stk, (EntityPlayer) entity);
        }
        // MAX：永不损坏，耐久强制归零
        if (isMaxed(stk)) {
            if (stk.getItemDamage() > 0) {
                stk.setItemDamage(0);
            }
            return;
        }

        if (!(entity instanceof EntityPlayer)) return;
        if (!stk.isItemDamaged()) return;

        EntityPlayer player = (EntityPlayer) entity;
        int tier = getTier(stk);
        int interval = TIER_REPAIR_INTERVAL[tier];

        if (entity.ticksExisted % interval != 0) return;

        // 尝试抽 Vis（法杖 + 魔力石，六大原始要素）
        int cost = TIER_REPAIR_COST[tier];
        ScytheBlockHandler.tryDrainVisForRepair(player, cost);

        // 无论是否抽到 Vis，都修复 1 点耐久
        stk.damageItem(-1, player);
    }

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
                if (mobs.get(index) != null && mobs.get(index)
                    .isEntityAlive() && mobs.get(index) instanceof IMob && !(mobs.get(index) instanceof EntityPlayer)) {

                    performPlayerAttackAt(attacker, mobs.get(index));

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

                    // ==================================================
                    // 【新增】连锁每命中一个目标，就扣一次耐久
                    // 连锁目标越多 → 总扣得越多
                    // Tier 越高 → 每命中扣得越少
                    // MAX → 不扣
                    // ==================================================
                    ItemStack held = attacker.getCurrentEquippedItem();
                    if (held != null && held.getItem() instanceof ItemHerobrinesScythe && !isMaxed(held)) {
                        int tier = getTier(held);
                        int chainDamage = TIER_CHAIN_DAMAGE[tier];
                        if (chainDamage > 0) {
                            held.damageItem(chainDamage, attacker);
                        }
                    }

                    attack(attacker, doNotAttack, mobs.get(index));
                    break;
                } else {
                    mobs.remove(index);
                }
            }
        }
    }

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

    public static void performPlayerAttackAt(EntityPlayer p, Entity p_71059_1_) {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(p, p_71059_1_))) return;
        if (!p_71059_1_.canAttackWithItem()) return;
        if (p_71059_1_.hitByEntity(p)) return;

        float f = (float) p.getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .getAttributeValue();
        int i = 0;
        float f1 = 0.0F;

        if (p_71059_1_ instanceof EntityLivingBase) {
            f1 = EnchantmentHelper.getEnchantmentModifierLiving(p, (EntityLivingBase) p_71059_1_);
            i += EnchantmentHelper.getKnockbackModifier(p, (EntityLivingBase) p_71059_1_);
        }
        if (p.isSprinting()) ++i;

        if (f > 0.0F || f1 > 0.0F) {
            boolean flag = p.fallDistance > 0.0F && !p.onGround
                && !p.isOnLadder()
                && !p.isInWater()
                && !p.isPotionActive(Potion.blindness)
                && p.ridingEntity == null
                && p_71059_1_ instanceof EntityLivingBase;
            if (flag && f > 0.0F) f *= 1.5F;
            f += f1;

            boolean flag1 = false;
            int j = EnchantmentHelper.getFireAspectModifier(p);
            if (p_71059_1_ instanceof EntityLivingBase && j > 0 && !p_71059_1_.isBurning()) {
                flag1 = true;
                p_71059_1_.setFire(1);
            }

            boolean flag2 = p_71059_1_.attackEntityFrom(DamageSource.causePlayerDamage(p), f);
            if (flag2) {
                ItemStack heldStack = p.getCurrentEquippedItem();
                if (heldStack != null && heldStack.getItem() instanceof ItemHerobrinesScythe) {
                    int tier = getTier(heldStack);
                    float healthRatio = p.getHealth() / p.getMaxHealth();
                    float lifesteal = TIER_LIFESTEAL[tier] + (1.0F - healthRatio) * 0.15F;
                    p.heal(f * lifesteal);
                }

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

                if (p_71059_1_ instanceof EntityLivingBase)
                    EnchantmentHelper.func_151384_a((EntityLivingBase) p_71059_1_, p);
                EnchantmentHelper.func_151385_b(p, p_71059_1_);

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

                if (p_71059_1_ instanceof EntityLivingBase) {
                    p.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));
                    if (j > 0) p_71059_1_.setFire(j * 4);
                }

                p.addExhaustion(0.3F);
            } else if (flag1) {
                p_71059_1_.extinguish();
            }
        }
    }

    // ==================================================
    // 扭曲值
    // ==================================================
    @Override
    public int getWarp(ItemStack stack, EntityPlayer player) {
        if (stack == null) return 0;
        return TIER_WARP[getTier(stack)];
    }

    // ==================================================
    // 属性修饰符
    // ==================================================
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap attribs = HashMultimap.create();
        int tier = getTier(stack);

        float atk = isMaxed(stack) ? MAX_ATTACK : TIER_ATTACK[tier];

        attribs.put(
            SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
            new AttributeModifier(ATK_UUID, "Weapon modifier", atk, 0));

        attribs.put(
            SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(),
            new AttributeModifier(SPD_UUID, "Speed modifier", TIER_SPEED[tier], 2));

        return attribs;
    }

    @Override
    public boolean isItemTool(ItemStack stk) {
        return true;
    }
}
