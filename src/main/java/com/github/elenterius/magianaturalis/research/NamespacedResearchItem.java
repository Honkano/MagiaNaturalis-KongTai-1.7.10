package com.github.elenterius.magianaturalis.research;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.github.elenterius.magianaturalis.util.Platform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class NamespacedResearchItem extends ResearchItem {

    public NamespacedResearchItem(ResourceLocation key, String category) {
        super(key.toString(), category);
    }

    public NamespacedResearchItem(ResourceLocation key, int col, int row, int complex, ResourceLocation icon) {
        super(key.toString(), key.getResourceDomain(), new AspectList(), col, row, complex, icon);
    }

    public NamespacedResearchItem(ResourceLocation key, int col, int row, int complex, ItemStack icon) {
        super(key.toString(), key.getResourceDomain(), new AspectList(), col, row, complex, icon);
    }

    public NamespacedResearchItem(ResourceLocation key, AspectList tags, int col, int row, int complex,
        ResourceLocation icon) {
        super(key.toString(), key.getResourceDomain(), tags, col, row, complex, icon);
    }

    public NamespacedResearchItem(ResourceLocation key, AspectList tags, int col, int row, int complex,
        ItemStack icon) {
        super(key.toString(), key.getResourceDomain(), tags, col, row, complex, icon);
    }

    @SideOnly(Side.CLIENT)
    public String getName() {
        return Platform.translate("research." + key.replace(":", ".") + ".name");
    }

    @SideOnly(Side.CLIENT)
    public String getText() {
        return Platform.translate("research." + key.replace(":", ".") + ".text");
    }

    @Override
    public NamespacedResearchItem setPages(ResearchPage... pages) {
        super.setPages(pages);
        return this;
    }

    public NamespacedResearchItem setParents(ResourceLocation... pages) {
        parents = new String[pages.length];
        for (int i = 0; i < pages.length; i++) {
            parents[i] = pages[i].toString();
        }
        return this;
    }

    @Override
    public NamespacedResearchItem setSecondary() {
        super.setSecondary();
        return this;
    }

}
