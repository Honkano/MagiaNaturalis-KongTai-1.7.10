package com.github.elenterius.magianaturalis.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(value = "MN-COREMOD")
@IFMLLoadingPlugin.MCVersion(value = "1.7.10")
@IFMLLoadingPlugin.TransformerExclusions(value = "com.github.elenterius.magianaturalis.asm")
public class Plugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.github.elenterius.magianaturalis.asm.ClassTransformer" };
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
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
