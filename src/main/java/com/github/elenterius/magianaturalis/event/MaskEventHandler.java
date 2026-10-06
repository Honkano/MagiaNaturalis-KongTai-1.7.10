package com.github.elenterius.magianaturalis.event;

import java.util.List;
import java.util.UUID;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.network.PacketHandler;
import com.github.elenterius.magianaturalis.network.packet.PacketAttitudeParticles;
import com.github.elenterius.magianaturalis.util.MaskHelper;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.wands.ItemWandCasting;

public class MaskEventHandler {

    private static final boolean SHOW_MESSAGES = true;
    private static final boolean LOG_ATTITUDE = false;

    private static void log(String msg) {
        if (LOG_ATTITUDE) System.out.println("[MN][Attitude] " + msg);
    }

    private static String pname(EntityPlayer p) {
        return p == null ? "null" : p.getCommandSenderName();
    }

    // ==================================================
    // 【属性修饰符 UUID】隐匿速度 / 威严力量
    // ==================================================
    private static final UUID HIDING_SPEED_UUID =
        UUID.nameUUIDFromBytes("magianaturalis:hiding_speed".getBytes());
    private static final UUID SOLEMN_STRENGTH_UUID =
        UUID.nameUUIDFromBytes("magianaturalis:solemn_strength".getBytes());

    // ==================================================
    // 【旧三张面具 · 保留原有数值，勿动】
    // ==================================================
    private static final float ANGRY_GHOST_CHANCE = 0.3f;
    private static final int WITHER_DURATION = 100;
    private static final float SIPPING_FIEND_CHANCE = 0.25f;
    private static final float SIPPING_FIEND_RATIO = 0.5f;
    private static final float SIPPING_FIEND_MAX_HEAL = 4.0f;
    private static final int WARP_CHECK_INTERVAL = 2000;
    private static final int WARP_REDUCTION_MIN = 1;
    private static final int WARP_REDUCTION_MAX = 2;

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (!MaskHelper.hasMask(player, MaskHelper.MASK_ANGRY_GHOST)) return;
        Entity source = event.source.getEntity();
        if (!(source instanceof EntityLivingBase)) return;
        if (player.getRNG().nextFloat() < ANGRY_GHOST_CHANCE) {
            ((EntityLivingBase) source).addPotionEffect(new PotionEffect(
                Potion.wither.getId(), WITHER_DURATION, 0));
        }
    }

    @SubscribeEvent
    public void onPlayerAttack(LivingHurtEvent event) {
        if (!(event.source.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.source.getEntity();
        if (player.worldObj.isRemote) return;
        if (!MaskHelper.hasMask(player, MaskHelper.MASK_SIPPING_FIEND)) return;
        if (player.getRNG().nextFloat() < SIPPING_FIEND_CHANCE) {
            float healAmount = Math.min(event.ammount * SIPPING_FIEND_RATIO, SIPPING_FIEND_MAX_HEAL);
            player.heal(healAmount);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (player.ticksExisted % WARP_CHECK_INTERVAL != 0) return;
        if (!MaskHelper.hasMask(player, MaskHelper.MASK_GRINNING_DEVIL)) return;
        try {
            int stickyWarp = thaumcraft.common.Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpSticky(player.getCommandSenderName());
            if (stickyWarp > 0) {
                int reduction = WARP_REDUCTION_MIN + player.getRNG().nextInt(
                    WARP_REDUCTION_MAX - WARP_REDUCTION_MIN + 1);
                thaumcraft.common.Thaumcraft.proxy.getPlayerKnowledge()
                    .setWarpSticky(player.getCommandSenderName(), Math.max(0, stickyWarp - reduction));
            }
        } catch (Exception e) {}
    }

    // ==================================================
    // 【态度面具 · face 索引】
    // ==================================================
    private static final int FACE_NO_ATTITUDE = 0;
    private static final int FACE_FRIENDLY    = 1;
    private static final int FACE_SOLEMN      = 2;
    private static final int FACE_HIDING      = 3;

    private static final float NO_ATTITUDE_PVP_CHANCE = 0.30F;
    private static final int NO_ATTITUDE_PVP_COOLDOWN = 600;
    private static final int NO_ATTITUDE_CONFUSION_DURATION = 100;
    private static final int NO_ATTITUDE_MOB_RADIUS = 16;
    private static final int NO_ATTITUDE_ATTACK_EXPIRE = 100;

    private static final float FRIENDLY_STOP_CHANCE = 0.30F;
    private static final int FRIENDLY_STOP_DURATION = 60;
    private static final String TAG_FRIENDLY_STOP_UNTIL = "magianaturalis:friendly_stop_until";

    private static final int SOLEMN_RADIUS = 8;
    private static final int SOLEMN_INTERVAL = 1200;
    private static final int SOLEMN_DURATION = 200;
    private static final int SOLEMN_POTION_DURATION = 40;

    private static final int HIDING_INTERVAL = 1200;
    private static final int HIDING_DURATION = 200;
    private static final int HIDING_VIS_PER_SECOND = 100;
    private static final int HIDING_NO_VIS_WARN_INTERVAL = 600;

    /** AI 压制检查频率：每 2 tick */
    private static final int AI_CLEAR_TICK = 2;

    private static void msg(EntityPlayer player, String key, Object... args) {
        if (!SHOW_MESSAGES) return;
        if (player == null) return;
        if (player.worldObj == null || player.worldObj.isRemote) return;
        player.addChatComponentMessage(new ChatComponentTranslation(
            "msg.magianaturalis.mask." + key, args));
    }

    private static void sendParticles(EntityPlayer player, int face) {
        if (!(player instanceof EntityPlayerMP)) return;
        try {
            PacketHandler.network.sendTo(
                new PacketAttitudeParticles.Message(face),
                (EntityPlayerMP) player);
        } catch (Exception e) {}
    }

    // ==================================================
    // 【属性工具】加 / 移除修饰符
    // ==================================================
    private static void applyAttribute(EntityPlayer player, net.minecraft.entity.ai.attributes.IAttribute attr,
                                       UUID uuid, String name, double value, int op) {
        IAttributeInstance inst = player.getEntityAttribute(attr);
        if (inst == null) return;
        if (inst.getModifier(uuid) != null) return;  // 已存在
        inst.applyModifier(new AttributeModifier(uuid, name, value, op));
    }

    private static void removeAttribute(EntityPlayer player, net.minecraft.entity.ai.attributes.IAttribute attr,
                                        UUID uuid) {
        IAttributeInstance inst = player.getEntityAttribute(attr);
        if (inst == null) return;
        AttributeModifier mod = inst.getModifier(uuid);
        if (mod == null) return;
        inst.removeModifier(mod);
    }
    // ==================================================
    // 【无态度 · PVP 攻击取消】
    // ==================================================
    @SubscribeEvent
    public void onAttitudeAttacked(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (MaskHelper.getAttitudeFace(player) != FACE_NO_ATTITUDE) return;

        Entity source = event.source.getEntity();
        if (!(source instanceof EntityPlayer)) return;
        EntityPlayer attacker = (EntityPlayer) source;

        ItemStack mask = MaskHelper.getAttitudeMask(player);
        if (mask == null) return;

        long now = player.worldObj.getTotalWorldTime();
        long lastCd = ItemMask.getAttitudePvpCd(mask);
        if (now - lastCd < NO_ATTITUDE_PVP_COOLDOWN) return;

        float r = player.getRNG().nextFloat();
        if (r >= NO_ATTITUDE_PVP_CHANCE) return;

        event.setCanceled(true);
        ItemMask.setAttitudePvpCd(mask, now);
        attacker.addPotionEffect(new PotionEffect(
            Potion.confusion.getId(), NO_ATTITUDE_CONFUSION_DURATION, 2));

        msg(player, "no_attitude_blocked");
        sendParticles(player, FACE_NO_ATTITUDE);
    }

    // ==================================================
    // 【无态度 · 记录玩家出手时间】
    // ==================================================
    @SubscribeEvent
    public void onPlayerAttackRecord(LivingAttackEvent event) {
        Entity source = event.source.getEntity();
        if (!(source instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) source;
        if (player.worldObj.isRemote) return;
        if (MaskHelper.getAttitudeFace(player) != FACE_NO_ATTITUDE) return;

        ItemStack mask = MaskHelper.getAttitudeMask(player);
        if (mask == null) return;

        ItemMask.setAttackTime(mask, player.worldObj.getTotalWorldTime());
    }

    // ==================================================
    // 【无态度 · 敌对生物选目标减半（僵尸除外）】
    // 每 2 tick 检查
    // ==================================================
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAttitudeMobTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (MaskHelper.getAttitudeFace(player) != FACE_NO_ATTITUDE) return;

        ItemStack mask = MaskHelper.getAttitudeMask(player);
        if (mask == null) return;

        long now = player.worldObj.getTotalWorldTime();
        long lastAttack = ItemMask.getAttackTime(mask);
        if (now - lastAttack < NO_ATTITUDE_ATTACK_EXPIRE) return;

        if (player.ticksExisted % AI_CLEAR_TICK != 0) return;

        List<EntityMob> mobs = player.worldObj.getEntitiesWithinAABB(
            EntityMob.class,
            player.boundingBox.expand(NO_ATTITUDE_MOB_RADIUS, 8, NO_ATTITUDE_MOB_RADIUS));

        int cleared = 0;
        for (EntityMob mob : mobs) {
            if (mob instanceof EntityZombie) continue;
            if (mob.getAttackTarget() != player && mob.getAITarget() != player) continue;
            if (player.getRNG().nextFloat() < 0.5F) {
                mob.setAttackTarget(null);
                mob.setRevengeTarget(null);
                cleared++;
            }
        }
        if (cleared > 0) {
            sendParticles(player, FACE_NO_ATTITUDE);
        }
    }

    // ==================================================
    // 【友善 · 8 格内脱战】每 2 tick 检查
    // ==================================================
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onFriendlyTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (MaskHelper.getAttitudeFace(player) != FACE_FRIENDLY) return;

        if (player.ticksExisted % AI_CLEAR_TICK != 0) return;

        List<EntityMob> mobs = player.worldObj.getEntitiesWithinAABB(
            EntityMob.class,
            player.boundingBox.expand(8, 4, 8));
        int cleared = 0;
        for (EntityMob mob : mobs) {
            boolean acted = false;
            if (mob.getAttackTarget() == player) {
                mob.setAttackTarget(null);
                acted = true;
            }
            if (mob.getAITarget() == player) {
                mob.setRevengeTarget(null);
                acted = true;
            }
            if (acted) cleared++;
        }

        List<EntityCreature> creatures = player.worldObj.getEntitiesWithinAABB(
            EntityCreature.class,
            player.boundingBox.expand(8, 4, 8));
        for (EntityCreature creature : creatures) {
            if (creature instanceof IMob) continue;
            if (creature.getAttackTarget() == player) creature.setAttackTarget(null);
            if (creature.getAITarget() == player) creature.setRevengeTarget(null);
        }

        if (cleared > 0 && player.ticksExisted % 40 == 0) {
            sendParticles(player, FACE_FRIENDLY);
        }
    }

    // ==================================================
    // 【友善 · 被攻击时，攻击者 30% 停手 3 秒】
    // ==================================================
    @SubscribeEvent
    public void onFriendlyHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (MaskHelper.getAttitudeFace(player) != FACE_FRIENDLY) return;

        Entity source = event.source.getEntity();
        if (!(source instanceof EntityLivingBase)) return;
        EntityLivingBase attacker = (EntityLivingBase) source;

        float r = player.getRNG().nextFloat();
        if (r >= FRIENDLY_STOP_CHANCE) return;

        long until = player.worldObj.getTotalWorldTime() + FRIENDLY_STOP_DURATION;
        attacker.getEntityData().setLong(TAG_FRIENDLY_STOP_UNTIL, until);
        if (attacker instanceof EntityCreature) {
            ((EntityCreature) attacker).setAttackTarget(null);
        }
        attacker.setRevengeTarget(null);
        attacker.addPotionEffect(new PotionEffect(
            Potion.weakness.getId(), FRIENDLY_STOP_DURATION, 0));

        sendParticles(player, FACE_FRIENDLY);
    }

    // ==================================================
    // 【友善 · 维持攻击者停手 3 秒】
    // ==================================================
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onFriendlyStopTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityLivingBase)) return;
        EntityLivingBase living = event.entityLiving;
        if (living.worldObj.isRemote) return;

        NBTTagCompound data = living.getEntityData();
        long until = data.getLong(TAG_FRIENDLY_STOP_UNTIL);
        if (until <= 0) return;

        long now = living.worldObj.getTotalWorldTime();
        if (now >= until) {
            data.removeTag(TAG_FRIENDLY_STOP_UNTIL);
            return;
        }

        if (living instanceof EntityCreature) {
            EntityCreature c = (EntityCreature) living;
            if (c.getAttackTarget() instanceof EntityPlayer) {
                c.setAttackTarget(null);
            }
        }
        if (living.getAITarget() instanceof EntityPlayer) {
            living.setRevengeTarget(null);
        }
    }

    // ==================================================
    // 【威严 · 周期触发 + 力量】
    // ==================================================
    @SubscribeEvent
    public void onSolemnTrigger(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (MaskHelper.getAttitudeFace(player) != FACE_SOLEMN) return;

        ItemStack mask = MaskHelper.getAttitudeMask(player);
        if (mask == null) return;

        long now = player.worldObj.getTotalWorldTime();
        if (now < ItemMask.getSolemnEnd(mask)) return;
        if (now < ItemMask.getSolemnNext(mask)) return;

        ItemMask.setSolemnEnd(mask, now + SOLEMN_DURATION);
        ItemMask.setSolemnNext(mask, now + SOLEMN_INTERVAL);
        msg(player, "solemn_start");
        sendParticles(player, FACE_SOLEMN);
    }

    // ==================================================
    // 【威严 · 效果维持 + 属性力量】
    // 每 2 tick 检查
    // ==================================================
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onSolemnTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (MaskHelper.getAttitudeFace(player) != FACE_SOLEMN) return;

        ItemStack mask = MaskHelper.getAttitudeMask(player);
        if (mask == null) return;

        long now = player.worldObj.getTotalWorldTime();
        boolean active = now < ItemMask.getSolemnEnd(mask);

        // ---------- 玩家力量加成 ----------
        if (active) {
            // 威严展开期间：攻击力 +6（约力量 II），不显示 buff
            applyAttribute(player,
                SharedMonsterAttributes.attackDamage,
                SOLEMN_STRENGTH_UUID,
                "magianaturalis:solemn_strength",
                6.0D, 0);
        } else {
            removeAttribute(player,
                SharedMonsterAttributes.attackDamage,
                SOLEMN_STRENGTH_UUID);
            return;
        }

        if (player.ticksExisted % AI_CLEAR_TICK != 0) return;

        List<EntityLivingBase> list = player.worldObj.getEntitiesWithinAABB(
            EntityLivingBase.class,
            player.boundingBox.expand(SOLEMN_RADIUS, 4, SOLEMN_RADIUS));

        for (EntityLivingBase living : list) {
            if (living == player) continue;

            if (living instanceof EntityCreature) {
                ((EntityCreature) living).setAttackTarget(null);
            }
            living.setRevengeTarget(null);
            living.addPotionEffect(new PotionEffect(
                Potion.digSlowdown.getId(), SOLEMN_POTION_DURATION, 2, true));
            living.addPotionEffect(new PotionEffect(
                Potion.weakness.getId(), SOLEMN_POTION_DURATION, 2, true));
        }
    }

    // ==================================================
    // 【隐匿 · 触发与维持 + 速度五】
    // ==================================================
    @SubscribeEvent
    public void onHidingTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;
        if (MaskHelper.getAttitudeFace(player) != FACE_HIDING) return;

        ItemStack mask = MaskHelper.getAttitudeMask(player);
        if (mask == null) return;

        long now = player.worldObj.getTotalWorldTime();

        if (ItemMask.isHiding(mask)) {
            // ---------- 结束 ----------
            if (now >= ItemMask.getHidingEnd(mask)) {
                ItemMask.setHiding(mask, false);
                ItemMask.setHidingCdEnd(mask, now + HIDING_INTERVAL);
                player.setInvisible(false);
                removeAttribute(player,
                    SharedMonsterAttributes.movementSpeed,
                    HIDING_SPEED_UUID);
                msg(player, "hiding_end");
                return;
            }

            // ---------- 每 2 tick 清 16 格 mob 目标 ----------
            if (player.ticksExisted % AI_CLEAR_TICK == 0) {
                List<EntityMob> mobs = player.worldObj.getEntitiesWithinAABB(
                    EntityMob.class,
                    player.boundingBox.expand(16, 8, 16));
                for (EntityMob mob : mobs) {
                    if (mob.getAttackTarget() == player) mob.setAttackTarget(null);
                    if (mob.getAITarget() == player) mob.setRevengeTarget(null);
                }
            }

            // ---------- 每秒扣 vis ----------
            if (now % 20 == 0) {
                if (!consumeWandVis(player, HIDING_VIS_PER_SECOND)) {
                    ItemMask.setHiding(mask, false);
                    ItemMask.setHidingCdEnd(mask, now + HIDING_INTERVAL);
                    player.setInvisible(false);
                    removeAttribute(player,
                        SharedMonsterAttributes.movementSpeed,
                        HIDING_SPEED_UUID);
                    msg(player, "hiding_fail");
                }
            }
            return;
        }

        // ---------- 冷却中：确保无速度加成 ----------
        if (now < ItemMask.getHidingCdEnd(mask)) {
            removeAttribute(player,
                SharedMonsterAttributes.movementSpeed,
                HIDING_SPEED_UUID);
            return;
        }

        // ---------- vis 检测 ----------
        if (!hasEnoughVis(player, HIDING_VIS_PER_SECOND)) {
            if (now % HIDING_NO_VIS_WARN_INTERVAL == 0) {
                msg(player, "hiding_no_vis");
            }
            return;
        }

        // ---------- 触发 ----------
        ItemMask.setHiding(mask, true);
        ItemMask.setHidingEnd(mask, now + HIDING_DURATION);
        player.setInvisible(true);
        // 速度翻倍 = 速度 V，不显示 buff
        applyAttribute(player,
            SharedMonsterAttributes.movementSpeed,
            HIDING_SPEED_UUID,
            "magianaturalis:hiding_speed",
            1.0D, 2);
        msg(player, "hiding_start");
        sendParticles(player, FACE_HIDING);
    }

    // ==================================================
    // 【工具】找法杖
    // ==================================================
    private static ItemStack findWand(EntityPlayer player) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemWandCasting) return held;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() instanceof ItemWandCasting) return stack;
        }
        return null;
    }

    private static boolean hasEnoughVis(EntityPlayer player, int amount) {
        ItemStack wand = findWand(player);
        if (wand == null) return false;
        ItemWandCasting casting = (ItemWandCasting) wand.getItem();
        int total = 0;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            total += casting.getVis(wand, aspect);
        }
        return total >= amount;
    }

    // ==================================================
    // 【工具】从法杖扣 vis · 六大元始平均扣
    // ==================================================
    private static boolean consumeWandVis(EntityPlayer player, int amount) {
        ItemStack wand = findWand(player);
        if (wand == null) return false;
        ItemWandCasting casting = (ItemWandCasting) wand.getItem();

        List<Aspect> primals = Aspect.getPrimalAspects();
        int n = primals.size();

        int total = 0;
        for (Aspect a : primals) total += casting.getVis(wand, a);
        if (total < amount) return false;

        int perAspect = amount / n;
        int extra = amount - perAspect * n;

        for (int i = 0; i < n; i++) {
            Aspect a = primals.get(i);
            int take = perAspect + (i < extra ? 1 : 0);
            if (take <= 0) continue;
            int have = casting.getVis(wand, a);
            casting.storeVis(wand, a, Math.max(0, have - take));
        }
        return true;
    }
}