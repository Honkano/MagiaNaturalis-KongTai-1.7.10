package com.github.elenterius.magianaturalis.research;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ResearchItemProxy extends ResearchItem {

    public ResearchItem research;

    private ResearchItemProxy(String key, String category, ResearchItem research, int x, int y, ItemStack icon) {
        super(key, category, new AspectList(), x, y, 1, icon);
        this.research = research;
        addSiblingToOriginal();
        setStub();
        setHidden();
    }

    private ResearchItemProxy(String key, String category, ResearchItem research, int x, int y, ResourceLocation icon) {
        super(key, category, new AspectList(), x, y, 1, icon);
        this.research = research;
        addSiblingToOriginal();
        setStub();
        setHidden();
    }

    public static ResearchItemProxy createNamespaced(ResourceLocation name, String originalResearchKey, int x, int y) {
        return create(name.toString(), name.getResourceDomain(), originalResearchKey, x, y);
    }

    public static ResearchItemProxy create(String key, String category, String originalResearchKey, int x, int y) {
        ResearchItem research = ResearchCategories.getResearch(originalResearchKey);

        if (research.icon_item != null) {
            return new ResearchItemProxy(key, category, research, x, y, research.icon_item);
        }

        return new ResearchItemProxy(key, category, research, x, y, research.icon_resource);
    }

    protected void addSiblingToOriginal() {
        if (research.siblings == null) research.setSiblings(key);
        else {
            String[] newSiblings = Arrays.copyOf(research.siblings, research.siblings.length + 1);
            newSiblings[research.siblings.length] = key;
            research.setSiblings(newSiblings);
        }
        if (research.isSecondary()) setSecondary();
    }

    public ResearchPage[] getPages() {
        return research.getPages();
    }

    public String getName() {
        return research.getName();
    }

    public String getText() {
        return research.getText();
    }

    public boolean isHidden() {
        return true;
    }

    public int getComplexity() {
        return 1;
    }

}
