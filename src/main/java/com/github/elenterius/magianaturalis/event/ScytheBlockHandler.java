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
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * 白瞳者之镰的全局事件处理器。
 *
 * 挂两条事件总线：
 * MinecraftForge.EVENT_BUS → LivingHurtEvent / LivingDeathEvent
 * FMLCommonHandler.bus() → TickEvent.PlayerTickEvent
 *
 * 这个类只是事件监听器，跟 ItemHerobrinesScythe 没有继承关系。
 *
 * 【多语言】
 * 所有聊天提示走 StatCollector，键名见 zh_CN.lang：
 * chat.magianaturalis.block.damage （格挡，原有）
 * chat.magianaturalis.scythe.levelup （升级）
 * chat.magianaturalis.scythe.reject （非主人被排斥）
 * chat.magianaturalis.scythe.owner.notify（原主人被通知）
 */
public class ScytheBlockHandler {

    // ==================================================
    // 多语言键（集中声明，方便统一改）
    // ==================================================
    private static final String LANG_CHAT_BLOCK = "chat.magianaturalis.block.damage";
    private static final String LANG_CHAT_LEVELUP = "chat.magianaturalis.scythe.levelup";
    private static final String LANG_CHAT_REJECT = "chat.magianaturalis.scythe.reject";
    private static final String LANG_CHAT_OWNER_NOTIFY = "chat.magianaturalis.scythe.owner.notify";

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

    /**
     * LivingHurtEvent 每次实体受伤时触发。
     * 服务端改 event.ammount 才真正生效。
     *
     * 两件事：
     * A. 攻击者手拿镰刀 → 按 Tier 补点额外伤害
     * B. 受伤者手持镰刀且右键按住 → 按 Tier 减伤
     */
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {

        // ---------- A. 攻方补 Tier 伤害 ----------
        if (event.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) event.source.getEntity();
            ItemStack atkHeld = attacker.getCurrentEquippedItem();
            if (atkHeld != null && atkHeld.getItem() == MNItems.herobrinesScythe
                && ItemHerobrinesScythe.isOwner(atkHeld, attacker)) {
                int tier = ItemHerobrinesScythe.getTier(atkHeld);
                // 基础攻击力由 getAttributeModifiers 提供，这里补 Tier*2 的成长
                if (tier > 0) event.ammount += tier * 2.0F;
            }
        }

        // ---------- B. 守方格挡 ----------
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack heldItem = player.getCurrentEquippedItem();

        // 三重判定：手持镰刀 + 正在右键使用 + 用的就是这把镰刀
        if (heldItem != null && heldItem.getItem() == MNItems.herobrinesScythe
            && player.isUsingItem()
            && player.getItemInUse() == heldItem) {

            int tier = ItemHerobrinesScythe.getTier(heldItem);
            float blockRate = ItemHerobrinesScythe.TIER_BLOCK[tier];

            // 记录本次被减免的伤害，用于聊天提示
            float blockedAmount = event.ammount * blockRate;
            event.ammount -= blockedAmount;

            // 4 级时 blockRate = 1.0，浮点误差硬清零
            if (blockRate >= 1.0F) event.ammount = 0F;

            // 聊天提示只在服务端发一次
            if (!player.worldObj.isRemote) {
                String msg = StatCollector.translateToLocalFormatted(
                    LANG_CHAT_BLOCK,
                    EnumChatFormatting.AQUA + String.format("%.1f", blockedAmount) + EnumChatFormatting.RESET);
                player.addChatMessage(new ChatComponentText(msg));
            }

            // 音效只在客户端播，防止双声道
            if (player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(player, "magianaturalis:item.herobrine.block", 1.0F, 1.0F);
            }
        }
    }

    // ==================================================
    // 2) 击杀计数 + 升级提示
    // ==================================================

    /**
     * LivingDeathEvent 在实体即将死亡时触发。
     * event.entityLiving → 被击杀的目标
     * event.source.getEntity() → 击杀者
     *
     * 玩家和怪物都算，自杀不算。
     */
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.source.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.source.getEntity();
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != MNItems.herobrinesScythe) return;
        if (!ItemHerobrinesScythe.isOwner(held, player)) return;
        if (event.entityLiving == player) return; // 自杀不算
        if (!(event.entityLiving instanceof EntityLivingBase)) return;

        boolean leveled = ItemHerobrinesScythe.addKill(held, player);

        if (!player.worldObj.isRemote && leveled) {
            int tier = ItemHerobrinesScythe.getTier(held);
            // 键：chat.magianaturalis.scythe.levelup = "白瞳者之镰已觉醒至第 %s 阶段：%s"
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

    /**
     * PlayerTickEvent 每 tick 触发两次（START / END），只处理 END。
     *
     * 两件事：
     * A. 认主校验：非主人强制丢出 + 通知原主人
     * B. 右键按住：每 10 tick 吸一次 Vis
     */
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != MNItems.herobrinesScythe) return;

        // ---------- A. 认主校验 ----------
        if (!ItemHerobrinesScythe.isOwner(held, player)) {
            if (!player.worldObj.isRemote) {
                // 从当前手持栏移除，丢到脚下
                int currentSlot = player.inventory.currentItem;
                player.inventory.setInventorySlotContents(currentSlot, null);
                player.inventory.markDirty();
                player.dropPlayerItemWithRandomChoice(held, false);

                // 提示抢刀者本人
                // 键：chat.magianaturalis.scythe.reject
                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.DARK_RED + StatCollector.translateToLocal(LANG_CHAT_REJECT)));

                // 通知原主人（如果在线）
                String ownerUuid = ItemHerobrinesScythe.getOwner(held);
                if (ownerUuid != null) {
                    for (Object o : player.worldObj.playerEntities) {
                        if (o instanceof EntityPlayer) {
                            EntityPlayer ownerPlayer = (EntityPlayer) o;
                            if (ownerPlayer.getUniqueID()
                                .toString()
                                .equals(ownerUuid)) {
                                // 键：chat.magianaturalis.scythe.owner.notify
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

    /**
     * 对玩家周围 8 格内的生物/玩家吸 Vis。
     * 目标数上限由 Tier 决定（5/10/15/22/30）。
     *
     * 吸取时：
     * - 目标只播受伤动画（红闪），不掉血
     * - 画一条从目标到玩家的血红色闪电
     * - 服务端把量汇总后给 Vis 容器充能
     */
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
        targets.remove(player); // 别吸自己

        int drained = 0;
        for (EntityLivingBase target : targets) {
            if (drained >= maxTargets) break;
            if (target == null || !target.isEntityAlive()) continue;

            // 伤害动画：hurtTime 是客户端渲染字段，只在客户端设才有效
            if (target.worldObj.isRemote) {
                target.hurtTime = 10;
                target.hurtResistantTime = 20;
            }

            // 血红色光束粒子，客户端画
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

            // 音效每 20 tick 一次，避免刷屏
            if (player.ticksExisted % 20 == 0) {
                player.worldObj.playSoundAtEntity(target, "thaumcraft:zap", 0.4F, 0.6F);
            }

            drained++;
        }

        // 服务端汇总后给 Vis 容器充能
        // amount 决定充多少，想改速度就改这里
        // drained → 每个目标给 1 vis（默认）
        // drained / 2 → 速度减半
        // drained * (tier+1) / 5 → 低等级慢，高等级快
        if (!player.worldObj.isRemote && drained > 0) {
            chargeVisContainers(player, drained);
        }
    }

    // ==================================================
    // 5) 给所有 Vis 容器充能
    // ==================================================

    /**
     * 遍历两个地方，给找到的所有 Vis 容器充能：
     *
     * 1. player.inventory（36 格：快捷栏 + 主背包）
     * → 法杖 ItemWandCasting
     * → 放在背包里的 Vis 护身符 ItemAmuletVis
     *
     * 2. Baubles 饰品栏
     * → 装在项链/腰带/戒指槽的 Vis 护身符
     *
     * 充能 API 说明：
     * 法杖 addVis(stack, aspect, n, true)
     * n 单位是 vis 点，内部自己 clamp 到 max。
     * 护身符 addVis(stack, aspect, n, true)
     * 【注意】千万不要用 storeVis，那是覆盖不是累加！
     * addVis 内部会把 n ×100 再存，同样自带 clamp。
     */
    private void chargeVisContainers(EntityPlayer player, int amount) {

        // ---------- 1. 主背包：法杖 + 放在背包里的护身符 ----------
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack == null) continue;

            Item item = stack.getItem();

            // 法杖
            if (item instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) item;
                for (Aspect aspect : Aspect.getPrimalAspects()) {
                    wand.addVis(stack, aspect, amount, true);
                }
            }
            // 放在背包里的 Vis 护身符
            else if (item instanceof ItemAmuletVis) {
                ItemAmuletVis amulet = (ItemAmuletVis) item;
                for (Aspect aspect : Aspect.getPrimalAspects()) {
                    amulet.addVis(stack, aspect, amount, true);
                }
            }
        }

        // ---------- 2. Baubles 饰品栏：Vis 护身符 ----------
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
        } catch (Throwable ignored) {
            // 没装 Baubles 也不崩
        }

        // 背包有 NBT 改动，标脏让 MC 存盘
        player.inventory.markDirty();
    }
}
