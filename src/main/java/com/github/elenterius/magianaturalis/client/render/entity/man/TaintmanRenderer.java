package com.github.elenterius.magianaturalis.client.render.entity.man;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.github.elenterius.magianaturalis.MagiaNaturalis;

public class TaintmanRenderer extends RenderLiving {

    private static final ResourceLocation TEXTURE = MagiaNaturalis.rl("textures/models/taintman.png");

    public TaintmanRenderer() {
        super(new TaintmanModel(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }

}
