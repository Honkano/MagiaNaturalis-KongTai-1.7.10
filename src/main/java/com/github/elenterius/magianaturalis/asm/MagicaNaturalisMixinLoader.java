package com.github.elenterius.magianaturalis.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class MagicaNaturalisMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.magianaturalis.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new ArrayList<>();
        mixins.add("MixinEntity");
        mixins.add("MixinEntityRenderer");
        mixins.add("MixinGuiContainer");
        mixins.add("MixinGuiResearchRecipe"); // ← 加这行
        return mixins;

    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(java.util.Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
