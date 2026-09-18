package com.github.elenterius.magianaturalis.client.render.entity.breeder;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.github.elenterius.magianaturalis.MagiaNaturalis;

public class TaintBreederRenderer extends RenderLiving {

    private static final ResourceLocation TEXTURE = MagiaNaturalis.rl("textures/models/taint_breeder.png");

    public TaintBreederRenderer() {
        super(new TaintBreederModel(), 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }

}
