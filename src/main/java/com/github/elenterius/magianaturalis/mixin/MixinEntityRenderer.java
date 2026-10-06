package com.github.elenterius.magianaturalis.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.util.MaskHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    private static final int FACE_FIRE = 1;

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void magiaNaturalis$clearFireBeforeRender(
            float partialTicks, long finishTimeNano, CallbackInfo ci) {

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        ItemStack mask = MaskHelper.getPrimalMask(player);
        if (mask == null) return;
        if (ItemMask.getPrimalFace(mask) != FACE_FIRE) return;

        if (player.isBurning()) player.extinguish();
    }
}