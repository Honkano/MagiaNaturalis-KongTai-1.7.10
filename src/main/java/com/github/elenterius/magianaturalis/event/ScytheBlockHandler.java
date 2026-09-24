package com.github.elenterius.magianaturalis.event;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.init.MNItems;
import com.github.elenterius.magianaturalis.item.ItemHerobrinesScythe;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * 白瞳者之镰的全局事件处理器。
 *
 * 【本次改动】
 * - drainVis 里加"扣耐久"（低 Tier 扣得多）
 * - 新增 tryDrainVisForRepair（被动修复时抽 Vis，法杖/魔力石统一用 consumeAllVis）
 */
public class ScytheBlockHandler {

    // ==================================================
    // 多语言键
    // ==================================================
    private static final String LANG_CHAT_BLOCK = "chat.magianaturalis.block.damage";
    private static final String LANG_CHAT_LEVELUP = "chat.magianaturalis.scythe.levelup";
    private static final String LANG_CHAT_REJECT = "chat.magianaturalis.scythe.reject";
    private static final String LANG_CHAT_OWNER_NOTIFY = "chat.magianaturalis.scythe.owner.notify";

    // ==================================================
    // 右键吸取时扣的耐久（低 Tier 扣得多）
    // ==================================================
    /** 每个吸取目标扣的耐久。Tier 越高，每目标扣得越少 */
    private static final int[] TIER_DRAIN_DAMAGE = { 50, 30, 20, 10, 5 };

    public static void register() {
        ScytheBlockHandler handler = new ScytheBlockHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
    }

    // ==================================================
    // 1) 格挡 + 攻方 Tier 伤害补正
    // ==================================================
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {

        // ---------- A. 攻方补 Tier 伤害 ----------
        if (event.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) event.source.getEntity();
            ItemStack atkHeld = attacker.getCurrentEquippedItem();
            if (atkHeld != null && atkHeld.getItem() == MNItems.herobrinesScythe
                && ItemHerobrinesScythe.isOwner(atkHeld, attacker)) {
                int tier = ItemHerobrinesScythe.getTier(atkHeld);
                if (tier > 0) event.ammount += tier * 2.0F;
            }
        }

        // ---------- B. 守方格挡 ----------
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack heldItem = player.getCurrentEquippedItem();

        if (heldItem != null && heldItem.getItem() == MNItems.herobrinesScythe
            && player.isUsingItem()
            && player.getItemInUse() == heldItem) {

            int tier = ItemHerobrinesScythe.getTier(heldItem);
            float blockRate = ItemHerobrinesScythe.TIER_BLOCK[tier];

            float blockedAmount = event.ammount * blockRate;
            event.ammount -= blockedAmount;

            if (blockRate >= 1.0F) event.ammount = 0F;

            if (!player.worldObj.isRemote) {
                String msg = StatCollector.translateToLocalFormatted(
                    LANG_CHAT_BLOCK,
                    EnumChatFormatting.AQUA + String.format("%.1f", blockedAmount) + EnumChatFormatting.RESET);
                player.addChatMessage(new ChatComponentText(msg));
            }

            if (player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.block", 1.0F, 1.0F);
            }
        }
    }

    // ==================================================
    // 2) 击杀计数 + 升级提示
    // ==================================================
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.source.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.source.getEntity();
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != MNItems.herobrinesScythe) return;
        if (!ItemHerobrinesScythe.isOwner(held, player)) return;
        if (event.entityLiving == player) return;
        if (!(event.entityLiving instanceof EntityLivingBase)) return;

        boolean leveled = ItemHerobrinesScythe.addKill(held, player);

        if (!player.worldObj.isRemote && leveled) {
            int tier = ItemHerobrinesScythe.getTier(held);
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.DARK_RED + StatCollector.translateToLocalFormatted(
                        LANG_CHAT_LEVELUP,
                        tier + 1,
                        EnumChatFormatting.RED + ItemHerobrinesScythe.tierName(tier))));
            player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.evolve", 1.0F, 1.0F);
        }
    }

    // ==================================================
    // 3) 每 tick：认主校验 + 右键吸取 Vis
    // ==================================================
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != MNItems.herobrinesScythe) return;

        // ---------- A. 认主校验 ----------
        if (!ItemHerobrinesScythe.isOwner(held, player)) {
            if (!player.worldObj.isRemote) {
                int currentSlot = player.inventory.currentItem;
                player.inventory.setInventorySlotContents(currentSlot, null);
                player.inventory.markDirty();
                player.dropPlayerItemWithRandomChoice(held, false);

                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.DARK_RED + StatCollector.translateToLocal(LANG_CHAT_REJECT)));

                String ownerUuid = ItemHerobrinesScythe.getOwner(held);
                if (ownerUuid != null) {
                    for (Object o : player.worldObj.playerEntities) {
                        if (o instanceof EntityPlayer) {
                            EntityPlayer ownerPlayer = (EntityPlayer) o;
                            if (ownerPlayer.getUniqueID()
                                .toString()
                                .equals(ownerUuid)) {
                                ownerPlayer.addChatMessage(
                                    new ChatComponentText(
                                        EnumChatFormatting.DARK_RED
                                            + StatCollector.translateToLocal(LANG_CHAT_OWNER_NOTIFY)));
                                ownerPlayer.worldObj
                                    .playSoundAtEntity(ownerPlayer, "magianaturalis:item.herobrine.reject", 0.6F, 1.2F);
                                break;
                            }
                        }
                    }
                }

                player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.reject", 1.0F, 0.8F);
            }
            return;
        }

        // ---------- B. 右键按住 → 每 10 tick 吸一次 ----------
        if (player.isUsingItem() && player.getItemInUse() == held) {
            if (player.ticksExisted % 10 == 0) {
                drainVis(player, held);
            }
        }
    }

    // ==================================================
    // 4) 吸取 Vis 的核心逻辑
    // ==================================================
    private void drainVis(EntityPlayer player, ItemStack scythe) {
        int tier = ItemHerobrinesScythe.getTier(scythe);
        int maxTargets = ItemHerobrinesScythe.TIER_MAX_TARGETS[tier];

        double radius = 8.0D;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            player.posX - radius,
            player.posY - radius,
            player.posZ - radius,
            player.posX + radius,
            player.posY + radius,
            player.posZ + radius);

        @SuppressWarnings("unchecked")
        List<EntityLivingBase> targets = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        targets.remove(player);

        int drained = 0;
        for (EntityLivingBase target : targets) {
            if (drained >= maxTargets) break;
            if (target == null || !target.isEntityAlive()) continue;

            if (target.worldObj.isRemote) {
                target.hurtTime = 10;
                target.hurtResistantTime = 20;
            }

            if (player.worldObj.isRemote) {
                MagiaNaturalis.lightning(
                    player.worldObj,
                    target.posX,
                    target.posY + target.getEyeHeight() * 0.5D,
                    target.posZ,
                    player.posX,
                    player.posY + player.getEyeHeight() * 0.5D,
                    player.posZ,
                    12,
                    0.4F,
                    10,
                    0);
            }

            if (player.ticksExisted % 20 == 0) {
                player.worldObj.playSoundAtEntity(target, "thaumcraft:zap", 0.4F, 0.6F);
            }

            drained++;
        }

        // 服务端：充能 + 扣耐久
        if (!player.worldObj.isRemote && drained > 0) {
            chargeVisContainers(player, drained);

            // MAX：永不扣耐久
            if (!ItemHerobrinesScythe.isMaxed(scythe)) {
                // 每目标扣，吸的目标越多，总扣得越多
                int drainDamage = TIER_DRAIN_DAMAGE[tier] * drained;
                if (drainDamage > 0) {
                    scythe.damageItem(drainDamage, player);
                }
            }
        }
    }

    // ==================================================
    // 5) 被动修复：抽 Vis（被 ItemHerobrinesScythe.onUpdate 调用）
    // ==================================================
    /**
     * 从背包 + Baubles 里找法杖 / 魔力石，抽六大原始要素。
     * 抽到就扣，抽不到就跳过。
     * 无论成功与否，调用方都会 +1 耐久。
     *
     * 【单位】cost 单位是"点"，内部换算成"厘 Vis"（×100）
     */
    public static void tryDrainVisForRepair(EntityPlayer player, int cost) {
        if (player.worldObj.isRemote) return;

        List<Aspect> primals = Aspect.getPrimalAspects();
        int per = Math.max(1, cost / primals.size());
        int perCenti = per * 100;

        AspectList costList = new AspectList();
        for (Aspect aspect : primals) {
            costList.add(aspect, perCenti);
        }

        // 1. 主背包
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack == null) continue;

            Item item = stack.getItem();

            if (item instanceof ItemWandCasting) {
                if (((ItemWandCasting) item).consumeAllVis(stack, player, costList, true, false)) {
                    player.inventory.markDirty();
                    return;
                }
            } else if (item instanceof ItemAmuletVis) {
                if (((ItemAmuletVis) item).consumeAllVis(stack, player, costList, true, false)) {
                    player.inventory.markDirty();
                    return;
                }
            }
        }

        // 2. Baubles 饰品栏
        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles != null) {
                for (int i = 0; i < baubles.getSizeInventory(); i++) {
                    ItemStack stack = baubles.getStackInSlot(i);
                    if (stack == null) continue;
                    if (!(stack.getItem() instanceof ItemAmuletVis)) continue;

                    if (((ItemAmuletVis) stack.getItem()).consumeAllVis(stack, player, costList, true, false)) {
                        baubles.markDirty();
                        return;
                    }
                }
            }
        } catch (Throwable ignored) {
            // 没装 Baubles 也不崩
        }
    }

    // ==================================================
    // 6) 给所有 Vis 容器充能（右键吸取用）
    // ==================================================
    private void chargeVisContainers(EntityPlayer player, int amount) {

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack == null) continue;

            Item item = stack.getItem();

            if (item instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) item;
                for (Aspect aspect : Aspect.getPrimalAspects()) {
                    wand.addVis(stack, aspect, amount, true);
                }
            } else if (item instanceof ItemAmuletVis) {
                ItemAmuletVis amulet = (ItemAmuletVis) item;
                for (Aspect aspect : Aspect.getPrimalAspects()) {
                    amulet.addVis(stack, aspect, amount, true);
                }
            }
        }

        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles != null) {
                for (int i = 0; i < baubles.getSizeInventory(); i++) {
                    ItemStack stack = baubles.getStackInSlot(i);
                    if (stack == null) continue;
                    if (!(stack.getItem() instanceof ItemAmuletVis)) continue;

                    ItemAmuletVis amulet = (ItemAmuletVis) stack.getItem();
                    for (Aspect aspect : Aspect.getPrimalAspects()) {
                        amulet.addVis(stack, aspect, amount, true);
                    }
                }
            }
        } catch (Throwable ignored) {}

        player.inventory.markDirty();
    }
}
