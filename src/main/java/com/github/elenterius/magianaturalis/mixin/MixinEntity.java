package com.github.elenterius.magianaturalis.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.elenterius.magianaturalis.item.baubles.ItemMask;
import com.github.elenterius.magianaturalis.util.MaskHelper;

@Mixin(Entity.class)
public abstract class MixinEntity {

    private static final int FACE_FIRE = 1;

    @Inject(method = "setFire", at = @At("HEAD"), cancellable = true)
    private void magiaNaturalis$blockSetFire(int seconds, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) (Object) this;

        ItemStack mask = MaskHelper.getPrimalMask(player);
        if (mask == null) return;
        if (ItemMask.getPrimalFace(mask) != FACE_FIRE) return;

        ci.cancel();
    }
}
