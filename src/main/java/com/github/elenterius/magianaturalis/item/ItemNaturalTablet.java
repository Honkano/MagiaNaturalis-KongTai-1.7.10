package com.github.elenterius.magianaturalis.item;


import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.init.MNCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 【自然石板模板 · 纯外观版】
 * 
 * 特点：
 *   - 双层渲染：底座 + 覆盖层
 *   - 底座贴图由构造参数决定（每个石板不一样）
 *   - 覆盖层共用一张（静态常量）
 *   - 暂时不存能量、不染色、无耐久
 * 
 * 以后加功能时，改 getColorFromItemStack 让覆盖层按能量比例染色。
 */
public class ItemNaturalTablet extends Item {

    /** ★ 共用覆盖层贴图名（不带 .png） */
    private static final String OVERLAY_TEXTURE = "tablet_overlay";

    /** 底座贴图名（每个实例不同） */
    private final String baseTextureName;

    /** 运行时拿到的 IIcon */
    private IIcon baseIcon;
    private IIcon overlayIcon;

    /**
     * 构造方法
     * @param baseTextureName 底座贴图名（不带 .png），比如 "greatwood_tablet"
     */
    public ItemNaturalTablet(String baseTextureName) {
        super();
        this.baseTextureName = baseTextureName;
        setMaxStackSize(1);                // 石板不可堆叠
        setCreativeTab(MNCreativeTabs.MAIN);
    }

    // ==================================================
    // 【注册图标】底座 + 覆盖层两张
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.baseIcon = ir.registerIcon(MagiaNaturalis.rlString(baseTextureName));
        this.overlayIcon = ir.registerIcon(MagiaNaturalis.rlString(OVERLAY_TEXTURE));
    }

    // ==================================================
    // 【获取图标】
    // 
    // MC 会问："pass 0 用什么图标？pass 1 用什么图标？"
    // pass = 0 → 底座
    // pass = 1 → 覆盖层
    // ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 0 ? baseIcon : overlayIcon;
    }

    // ==================================================
    // 【开启双层渲染】
    // 
    // 不返回 true 的话，MC 只会画一层（底座），
    // 覆盖层永远不会出现。
    // ==================================================
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    // ==================================================
    // 【每层染色】暂时不染色，都返回白色（0xFFFFFF）
    // 
    // 以后加能量时改这里：
    //   if (renderPass == 1) {
    //       float ratio = 能量 / 最大值;
    //       return Color.HSBtoRGB(0.528F, ratio, 1F);  // 蓝色渐变
    //   }
    // ==================================================
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return 0xFFFFFF;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.common;
    }
}