package com.github.elenterius.magianaturalis.client.render.entity.trunk;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;

public class EvilTrunkRenderer extends RenderLiving {

    public static final ResourceLocation CORRUPTED_TEXTURE = MagiaNaturalis.rl("textures/models/trunk_corrupted.png");
    public static final ResourceLocation DEMONIC_TEXTURE = MagiaNaturalis.rl("textures/models/trunk_demonic_wings.png");
    public static final ResourceLocation SINISTER_TEXTURE = MagiaNaturalis.rl("textures/models/trunk_sinister.png");
    public static final ResourceLocation TAINTED_TEXTURE = MagiaNaturalis.rl("textures/models/trunk_tainted.png");

    public CorruptedTrunkModel modelTC = new CorruptedTrunkModel();
    public SinisterTrunkModel modelTS = new SinisterTrunkModel();
    public DemonicTrunkModel modelTD = new DemonicTrunkModel();
    public TaintedTrunkModel modelTT = new TaintedTrunkModel();

    public EvilTrunkRenderer() {
        super(new CorruptedTrunkModel(), 0.5F);
    }

    @Override
    protected void renderModel(EntityLivingBase entity, float x, float y, float z, float f1, float f2, float f3) {
        if (entity != null) {
            // "corrupted", "sinister", "demonic", "tainted"
            switch (((EntityEvilTrunk) entity).getType()) {
                case 0:
                    this.mainModel = modelTC;
                    break;
                case 1:
                    this.mainModel = modelTS;
                    break;
                case 2:
                    this.mainModel = modelTD;
                    break;
                case 3:
                    this.mainModel = modelTT;
            }
        }

        super.renderModel(entity, x, y, z, f1, f2, f3);
    }

    protected ResourceLocation getEntityTexture(EntityEvilTrunk entity) {
        switch (entity.getType()) {
            case 1:
                return SINISTER_TEXTURE;
            case 2:
                return DEMONIC_TEXTURE;
            case 3:
                return TAINTED_TEXTURE;
            case 0:
            default:
                return CORRUPTED_TEXTURE;
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return getEntityTexture((EntityEvilTrunk) entity);
    }

}
