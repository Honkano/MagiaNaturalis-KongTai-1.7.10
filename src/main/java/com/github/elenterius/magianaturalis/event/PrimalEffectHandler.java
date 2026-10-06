package com.github.elenterius.magianaturalis.event;

import java.util.Random;
import java.util.UUID;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.util.MaskHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

public class PrimalEffectHandler {

    private static final boolean LOG = false;

    private static void log(String s) {
        if (LOG) System.out.println("[MN][Primal] " + s);
    }

    private static final int FACE_PRIMAL = 0;
    private static final int FACE_FIRE   = 1;
    private static final int FACE_AIR    = 2;
    private static final int FACE_CHAOS  = 3;
    private static final int FACE_EARTH  = 4;
    private static final int FACE_ORDER  = 5;
    private static final int FACE_WATER  = 6;

    private static final String TAG_LAST_FACE = "primal_last_face";

    // ==================================================
    // 效果参数
    // ==================================================
    // face 0：元始
    private static final int PRIMAL_HEAL_INTERVAL = 120;   // 每 6 秒回 1 血
    private static final int PRIMAL_FOOD_INTERVAL = 200;   // 每 10 秒回 1 饥饿

    // face 1：火
    private static final float FIRE_WATER_DAMAGE = 3.0F;   // 泡水/踩雪/踩冰/下雪 每秒扣 3 血

    // face 2：风
    private static final int AIR_HUNGER_INTERVAL = 40;     // 每 2 秒扣 1 饥饿

    // face 5：秩序
    private static final int ORDER_HUNGER_INTERVAL = 10;   // 每 0.5 秒扣 1 饥饿

    // face 6：水
    private static final float WATER_LAND_DAMAGE = 3.0F;   // 离水每秒扣 3 血

    // face 3：混沌
    private static final int[] CHAOS_DEBUFFS = {
        Potion.poison.getId(),
        Potion.weakness.getId(),
        Potion.moveSlowdown.getId(),
        Potion.blindness.getId(),
        Potion.confusion.getId(),
        Potion.wither.getId()
    };
    private static final int CHAOS_DEBUFF_DURATION = 200;                // 10 秒
    private static final int CHAOS_DEBUFF_AMPLIFIER = 2;                 // III 级
    private static final float CHAOS_SELF_BACKFIRE_CHANCE = 0.10F;       // 10% 自中
    private static final int CHAOS_SELF_BACKFIRE_DURATION = 100;         // 5 秒

    // ==================================================
    // 属性 UUID
    // ==================================================
    private static final UUID UUID_FIRE_ATK =
        UUID.nameUUIDFromBytes("magianaturalis:primal_fire_atk".getBytes());
    private static final UUID UUID_AIR_SPD =
        UUID.nameUUIDFromBytes("magianaturalis:primal_air_spd".getBytes());
    private static final UUID UUID_AIR_ATK_DOWN =
        UUID.nameUUIDFromBytes("magianaturalis:primal_air_atk_down".getBytes());
    private static final UUID UUID_EARTH_KB =
        UUID.nameUUIDFromBytes("magianaturalis:primal_earth_kb".getBytes());
    private static final UUID UUID_EARTH_SPD_DOWN =
        UUID.nameUUIDFromBytes("magianaturalis:primal_earth_spd_down".getBytes());
    private static final UUID UUID_ORDER_HP =
        UUID.nameUUIDFromBytes("magianaturalis:primal_order_hp".getBytes());
    private static final UUID UUID_WATER_SPD_DOWN =
        UUID.nameUUIDFromBytes("magianaturalis:primal_water_spd_down".getBytes());

    private static final Random RAND = new Random();

    // ==================================================
    // 【每 tick】切脸清理 + 应用当前 face 效果
    // ==================================================
    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj.isRemote) return;

        ItemStack mask = MaskHelper.getPrimalMask(player);
        if (mask == null) {
            clearAttrs(player);
            return;
        }

        int currentFace = ItemMask.getPrimalFace(mask);
        int lastFace = mask.hasTagCompound()
            ? mask.getTagCompound().getInteger(TAG_LAST_FACE) : -1;

        if (currentFace != lastFace) {
            clearAttrs(player);
            if (mask.hasTagCompound()) {
                mask.getTagCompound().setInteger(TAG_LAST_FACE, currentFace);
            }
            log("切换到 face=" + currentFace);
        }

        applyFace(player, currentFace);
    }

    private static void applyFace(EntityPlayer player, int face) {
        switch (face) {
            case FACE_FIRE:   applyFire(player);   break;
            case FACE_AIR:    applyAir(player);    break;
            case FACE_EARTH:  applyEarth(player);  break;
            case FACE_ORDER:  applyOrder(player);  break;
            case FACE_WATER:  applyWater(player);  break;
            case FACE_PRIMAL: applyPrimal(player); break;
            default: break;
        }
    }

    private static void clearAttrs(EntityPlayer p) {
        removeMod(p, SharedMonsterAttributes.attackDamage,       UUID_FIRE_ATK);
        removeMod(p, SharedMonsterAttributes.movementSpeed,      UUID_AIR_SPD);
        removeMod(p, SharedMonsterAttributes.attackDamage,       UUID_AIR_ATK_DOWN);
        removeMod(p, SharedMonsterAttributes.knockbackResistance, UUID_EARTH_KB);
        removeMod(p, SharedMonsterAttributes.movementSpeed,      UUID_EARTH_SPD_DOWN);
        removeMod(p, SharedMonsterAttributes.maxHealth,          UUID_ORDER_HP);
        removeMod(p, SharedMonsterAttributes.movementSpeed,      UUID_WATER_SPD_DOWN);
    }

    // ==================================================
    // face 0：元始 —— 回血 + 回饥饿
    // ==================================================
    private static void applyPrimal(EntityPlayer p) {
        if (p.ticksExisted % PRIMAL_HEAL_INTERVAL == 0
            && p.getHealth() < p.getMaxHealth()) {
            p.heal(1.0F);
        }
        if (p.ticksExisted % PRIMAL_FOOD_INTERVAL == 0) {
            int food = p.getFoodStats().getFoodLevel();
            if (food < 20) p.getFoodStats().setFoodLevel(food + 1);
        }
    }

    // ==================================================
    // face 1：火 —— 攻击 +4，灭火
    // 副作用：泡水 / 踩雪 / 踩冰 / 下雪 每秒扣 3 血
    // ==================================================
    private static void applyFire(EntityPlayer p) {
        applyMod(p, SharedMonsterAttributes.attackDamage, UUID_FIRE_ATK,
            "primal_fire_atk", 4.0D, 0);

        if (p.isBurning()) p.extinguish();

        boolean shouldDamage = p.isInWater() || p.isWet()
            || isOnIceOrSnow(p) || isInSnow(p);

        if (shouldDamage && p.ticksExisted % 20 == 0) {
            float newHealth = p.getHealth() - FIRE_WATER_DAMAGE;
            if (newHealth <= 0) {
                p.attackEntityFrom(DamageSource.drown, 1000.0F);
            } else {
                p.setHealth(newHealth);
            }
        }
    }

    // ==================================================
    // face 2：风 —— 移速 +60%
    // 副作用：攻击力 -60%，每 2 秒扣 1 饥饿
    // ==================================================
    private static void applyAir(EntityPlayer p) {
        // 移速 +60%
        applyMod(p, SharedMonsterAttributes.movementSpeed, UUID_AIR_SPD,
            "primal_air_spd", 0.60D, 2);

        // 攻击力 -60%（op 2 = 乘总值，-0.6 → 原值 × 0.4）
        applyMod(p, SharedMonsterAttributes.attackDamage, UUID_AIR_ATK_DOWN,
            "primal_air_atk_down", -0.6D, 2);

        if (p.ticksExisted % AIR_HUNGER_INTERVAL == 0) {
            int food = p.getFoodStats().getFoodLevel();
            if (food > 0) p.getFoodStats().setFoodLevel(food - 1);
        }
    }

    // ==================================================
    // face 4：大地 —— 击退免疫
    // 副作用：移速 -60%
    // ==================================================
    private static void applyEarth(EntityPlayer p) {
        applyMod(p, SharedMonsterAttributes.knockbackResistance, UUID_EARTH_KB,
            "primal_earth_kb", 2.0D, 0);
        applyMod(p, SharedMonsterAttributes.movementSpeed, UUID_EARTH_SPD_DOWN,
            "primal_earth_spd_down", -0.60D, 2);
    }

    // ==================================================
    // face 5：秩序 —— 回血 + 生命 +4
    // 副作用：每 0.5 秒扣 1 饥饿
    // ==================================================
    private static void applyOrder(EntityPlayer p) {
        if (p.ticksExisted % 50 == 0 && p.getHealth() < p.getMaxHealth()) {
            p.heal(4.0F);
        }
        applyMod(p, SharedMonsterAttributes.maxHealth, UUID_ORDER_HP,
            "primal_order_hp", 4.0D, 0);

        if (p.ticksExisted % ORDER_HUNGER_INTERVAL == 0) {
            int food = p.getFoodStats().getFoodLevel();
            if (food > 0) p.getFoodStats().setFoodLevel(food - 1);
        }
    }

    // ==================================================
    // face 6：水 —— 水下呼吸 + 免中毒
    // 副作用：陆地移速 -10%，离水每秒扣 3 血
    // ==================================================
    private static void applyWater(EntityPlayer p) {
        applyMod(p, SharedMonsterAttributes.movementSpeed, UUID_WATER_SPD_DOWN,
            "primal_water_spd_down", -0.10D, 2);

        if (p.isInWater()) {
            if (p.getAir() < 300) p.setAir(300);
        }
        if (p.isPotionActive(Potion.poison.getId())) {
            p.removePotionEffect(Potion.poison.getId());
        }

        // ★ 离水扣血
        if (!p.isInWater()) {
            if (p.ticksExisted % 20 == 0) {
                float newHealth = p.getHealth() - WATER_LAND_DAMAGE;
                if (newHealth <= 0) {
                    p.attackEntityFrom(DamageSource.drown, 1000.0F);
                } else {
                    p.setHealth(newHealth);
                }
            }
        }
    }

    // ==================================================
    // 【LivingAttackEvent】火免 / 风免摔伤
    // ==================================================
    @SubscribeEvent
    public void onAttacked(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer victim = (EntityPlayer) event.entityLiving;
        ItemStack mask = MaskHelper.getPrimalMask(victim);
        if (mask == null) return;

        int face = ItemMask.getPrimalFace(mask);

        // 火脸：免疫所有火焰 / 岩浆伤害
        if (face == FACE_FIRE && isFireLike(event.source)) {
            event.setCanceled(true);
            return;
        }

        // 风脸：免疫摔伤
        if (face == FACE_AIR && isFall(event.source)) {
            event.setCanceled(true);
        }
    }

    // ==================================================
    // 【LivingHurtEvent】混沌 / 火点燃 / 大地减伤 / 火冰水伤翻倍
    // ==================================================
    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        // ---------- 攻击者效果 ----------
        if (event.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer atk = (EntityPlayer) event.source.getEntity();
            ItemStack mask = MaskHelper.getPrimalMask(atk);
            if (mask != null) {
                int face = ItemMask.getPrimalFace(mask);
                Entity target = event.entity;

                if (target instanceof EntityLivingBase) {
                    EntityLivingBase livingTarget = (EntityLivingBase) target;

                    if (face == FACE_CHAOS) {
                        int debuff = CHAOS_DEBUFFS[RAND.nextInt(CHAOS_DEBUFFS.length)];
                        livingTarget.addPotionEffect(new PotionEffect(
                            debuff, CHAOS_DEBUFF_DURATION, CHAOS_DEBUFF_AMPLIFIER));

                        // 10% 自中
                        if (RAND.nextFloat() < CHAOS_SELF_BACKFIRE_CHANCE) {
                            atk.addPotionEffect(new PotionEffect(
                                debuff, CHAOS_SELF_BACKFIRE_DURATION, CHAOS_DEBUFF_AMPLIFIER));
                            log("混沌反噬：玩家自中 debuff " + debuff);
                        }
                    } else if (face == FACE_FIRE) {
                        target.setFire(3);
                    }
                }
            }
        }

        // ---------- 受害者效果 ----------
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer victim = (EntityPlayer) event.entityLiving;
            ItemStack mask = MaskHelper.getPrimalMask(victim);
            if (mask != null) {
                int face = ItemMask.getPrimalFace(mask);

                if (face == FACE_EARTH) {
                    event.ammount *= 0.6F;
                } else if (face == FACE_FIRE && isIceOrWater(event.source)) {
                    // 火脸：溺水伤害 ×2
                    event.ammount *= 2.0F;
                    log("火脸副作用：冰水伤害 ×2 → " + event.ammount);
                }
            }
        }
    }

    // ==================================================
    // 【PlayerUseItemEvent.Finish】秩序：进食效果减半
    // ==================================================
    @SubscribeEvent
    public void onEat(PlayerUseItemEvent.Finish event) {
        EntityPlayer player = event.entityPlayer;
        if (player.worldObj.isRemote) return;
        if (!(event.item.getItem() instanceof net.minecraft.item.ItemFood)) return;

        ItemStack mask = MaskHelper.getPrimalMask(player);
        if (mask == null) return;
        if (ItemMask.getPrimalFace(mask) != FACE_ORDER) return;

        // 玩家刚吃完，扣回去 1 饥饿
        int food = player.getFoodStats().getFoodLevel();
        if (food > 0) {
            player.getFoodStats().setFoodLevel(Math.max(0, food - 1));
            log("秩序脸副作用：进食效果减半 -1 饥饿");
        }
    }

    // ==================================================
    // 工具
    // ==================================================
    private static boolean isFireLike(DamageSource src) {
        String type = src.getDamageType();
        return "inFire".equals(type)
            || "onFire".equals(type)
            || "lava".equals(type)
            || "fire".equals(type)
            || src.isFireDamage();
    }

    private static boolean isFall(DamageSource src) {
        return src == DamageSource.fall || "fall".equals(src.getDamageType());
    }

    /** 冰 / 水类伤害（火脸副作用用） */
    private static boolean isIceOrWater(DamageSource src) {
        return src == DamageSource.drown
            || "drown".equals(src.getDamageType())
            || "freeze".equals(src.getDamageType());
    }

    /** 玩家脚下踩的是冰或雪 */
    private static boolean isOnIceOrSnow(EntityPlayer p) {
        int x = MathHelper.floor_double(p.posX);
        int y = MathHelper.floor_double(p.posY - 0.1D);
        int z = MathHelper.floor_double(p.posZ);
        Block below = p.worldObj.getBlock(x, y, z);
        return below == Blocks.ice
            || below == Blocks.packed_ice
            || below == Blocks.snow
            || below == Blocks.snow_layer;
    }

    /** 玩家头顶正在下雪（且能看到天空） */
    private static boolean isInSnow(EntityPlayer p) {
        if (!p.worldObj.isRaining()) return false;
        int x = MathHelper.floor_double(p.posX);
        int y = MathHelper.floor_double(p.posY);
        int z = MathHelper.floor_double(p.posZ);
        if (!p.worldObj.canBlockSeeTheSky(x, y, z)) return false;
        return p.worldObj.getBiomeGenForCoords(x, z).getEnableSnow();
    }

    private static void applyMod(EntityPlayer p,
            net.minecraft.entity.ai.attributes.IAttribute attr,
            UUID uuid, String name, double value, int op) {
        IAttributeInstance inst = p.getEntityAttribute(attr);
        if (inst == null) return;
        if (inst.getModifier(uuid) != null) return;
        inst.applyModifier(new AttributeModifier(uuid, name, value, op));
    }

    private static void removeMod(EntityPlayer p,
            net.minecraft.entity.ai.attributes.IAttribute attr, UUID uuid) {
        IAttributeInstance inst = p.getEntityAttribute(attr);
        if (inst == null) return;
        AttributeModifier m = inst.getModifier(uuid);
        if (m != null) inst.removeModifier(m);
    }
}