package com.github.elenterius.magianaturalis.mixin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.elenterius.magianaturalis.api.Aspects;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.client.gui.GuiResearchRecipe;
import thaumcraft.common.Thaumcraft;

/**
 * 打开 INTRO 时，动态追加要素页：
 * 1. 六大原始要素（默认显示，和原版"魔力要素"页对齐）
 * 2. 玩家已发现的自定义要素（只显示自己的，不显示 TC4 原版复合要素）
 * 每 4 个一页，与原版行为一致。
 *
 * remap = false：GuiResearchRecipe 是 TC4 的 deobf 类，不需要 SRG 映射。
 */
@Mixin(value = GuiResearchRecipe.class, priority = 1000, remap = false)
public class MixinGuiResearchRecipe {

    @Shadow
    private ResearchPage[] pages;

    @Shadow
    private int maxPages;

    @Inject(method = "<init>", at = @At("TAIL"), require = 0, remap = false)
    private void magiaNaturalis$injectCustomAspects(ResearchItem research, int page, double x, double y,
        CallbackInfo ci) {

        if (research == null) return;
        if (!"intro".equals(research.key) && !"magianaturalis:intro".equals(research.key)) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        String username = mc.thePlayer.getCommandSenderName();

        AspectList discovered = Thaumcraft.proxy.getPlayerKnowledge()
            .getAspectsDiscovered(username);

        AspectList result = new AspectList();

        // ---- 1. 六大原始要素：默认显示 ----
        for (Aspect primal : Aspect.getPrimalAspects()) {
            if (primal == null) continue;
            int amount = discovered.getAmount(primal);
            if (amount > 0) {
                result.add(primal, amount);
            }
        }

        // ---- 2. 自定义要素：只有玩家已发现的才显示 ----
        for (Aspect a : Aspects.ALL_CUSTOM) {
            if (a == null) continue;
            int amount = discovered.getAmount(a);
            if (amount > 0) result.add(a, amount);
        }

        if (result.size() <= 0) return;

        // 复制原页面
        List<ResearchPage> newPages = new ArrayList<ResearchPage>();
        for (ResearchPage p : this.pages) newPages.add(p);

        // 每 4 个一页
        AspectList group = new AspectList();
        int count = 0;
        for (Aspect a : result.getAspectsSorted()) {
            group.add(a, result.getAmount(a));
            ++count;
            if (count >= 4) {
                newPages.add(new ResearchPage(group.copy()));
                group = new AspectList();
                count = 0;
            }
        }
        if (count > 0) newPages.add(new ResearchPage(group));

        this.pages = newPages.toArray(new ResearchPage[0]);
        this.maxPages = this.pages.length;
    }
}
