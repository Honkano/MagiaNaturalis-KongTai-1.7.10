package com.github.elenterius.magianaturalis.init.client;

import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.github.elenterius.magianaturalis.block.banner.CustomBannerBlockEntity;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.block.geopylon.GeoPylonBlockEntity;
import com.github.elenterius.magianaturalis.block.jar.PrisonJarBlockEntity;
import com.github.elenterius.magianaturalis.block.table.TranscribingTableBlockEntity;
import com.github.elenterius.magianaturalis.client.aspect.HotbarSlotAspectOverlay;
import com.github.elenterius.magianaturalis.client.gui.ArcaneChestGui;
import com.github.elenterius.magianaturalis.client.gui.EvilTrunkGui;
import com.github.elenterius.magianaturalis.client.gui.GuiTranscribingTable;
import com.github.elenterius.magianaturalis.client.handler.ScytheAuraHandler;
import com.github.elenterius.magianaturalis.client.render.RenderEventHandler;
import com.github.elenterius.magianaturalis.client.render.block.BlockEntityRenderer;
import com.github.elenterius.magianaturalis.client.render.block.BlockJarRenderer;
import com.github.elenterius.magianaturalis.client.render.entity.breeder.TaintBreederRenderer;
import com.github.elenterius.magianaturalis.client.render.entity.trunk.EvilTrunkRenderer;
import com.github.elenterius.magianaturalis.client.render.item.HerobrinesScytheRenderer;
import com.github.elenterius.magianaturalis.client.render.item.RenderItemEvilTrunkSpawner;
import com.github.elenterius.magianaturalis.client.render.tile.*;
import com.github.elenterius.magianaturalis.easteregg.EntityHerobrineWatcher;
import com.github.elenterius.magianaturalis.easteregg.RenderHerobrineWatcher;
import com.github.elenterius.magianaturalis.entity.EntityEvilTrunk;
import com.github.elenterius.magianaturalis.entity.EntityZombieExtended;
import com.github.elenterius.magianaturalis.entity.taint.EntityTaintBreeder;
import com.github.elenterius.magianaturalis.init.CommonSetup;
import com.github.elenterius.magianaturalis.init.MNItems;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import thaumcraft.client.fx.bolt.FXLightningBolt;

public class ClientSetup extends CommonSetup {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        registerRenderer();
        RenderEventHandler.register();
        MinecraftForge.EVENT_BUS.register(new ScytheAuraHandler());
        MinecraftForge.EVENT_BUS.register(new com.github.elenterius.magianaturalis.event.PrimalSwitchHandler());
        MinecraftForge.EVENT_BUS.register(new com.github.elenterius.magianaturalis.event.MaskScanHandler());
        MinecraftForge.EVENT_BUS.register(new com.github.elenterius.magianaturalis.client.render.MaskHUDHandler());

        MNKeyBindings.register();
        KeyEventHandler.register();
        com.github.elenterius.magianaturalis.client.display.DisplayToggleHandler.register();

        // 快捷栏要素图标
        MinecraftForge.EVENT_BUS.register(new HotbarSlotAspectOverlay());



        // 注意：容器 GUI 里的图标不再由事件总线驱动，
        // 而是由 MixinGuiContainer 注入到 GuiContainer.drawScreen 里，
        // 这样图标会压在 Tooltip 下方，且鼠标悬停时也显示。
    }

    public void registerRenderer() {
        registerBlockRenderer(new BlockEntityRenderer());
        registerTileEntitySpecialRenderer(TranscribingTableBlockEntity.class, new TileTranscribingTableRenderer());
        registerTileEntitySpecialRenderer(ArcaneChestBlockEntity.class, new TileArcaneChestRenderer());
        registerTileEntitySpecialRenderer(CustomBannerBlockEntity.class, new TileBannerCustomRenderer());
        registerBlockRenderer(new BlockJarRenderer());
        registerTileEntitySpecialRenderer(PrisonJarBlockEntity.class, new TileJarPrisonRenderer());
        registerTileEntitySpecialRenderer(GeoPylonBlockEntity.class, new TileGeoMorpherRenderer());

        MinecraftForgeClient.registerItemRenderer(MNItems.evilTrunkSpawner, new RenderItemEvilTrunkSpawner());
        MinecraftForgeClient.registerItemRenderer(MNItems.herobrinesScythe, new HerobrinesScytheRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntityTaintBreeder.class, new TaintBreederRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityEvilTrunk.class, new EvilTrunkRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityZombieExtended.class, new RenderZombie());
        MNItems.NATURAL_ROBE_RENDER_INDEX = RenderingRegistry.addNewArmourRendererPrefix("natural_robe");
        MNItems.NATURAL_WOOD_RENDER_INDEX = RenderingRegistry.addNewArmourRendererPrefix("natural_wood");
        MNItems.GREATWOOD_RENDER_INDEX = RenderingRegistry.addNewArmourRendererPrefix("greatwood");
        MNItems.GREATWOOD_ADVANCED_RENDER_INDEX = RenderingRegistry.addNewArmourRendererPrefix("greatwood_advanced");
        MNItems.WATER_GOD_RENDER_INDEX = RenderingRegistry.addNewArmourRendererPrefix("water_god");

        // ==================================================
        // 【彩蛋】白瞳凝视者
        // ==================================================
        RenderingRegistry.registerEntityRenderingHandler(
            EntityHerobrineWatcher.class,
            new RenderHerobrineWatcher());
    }

    public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz,
        TileEntitySpecialRenderer renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(clazz, renderer);
    }

    public void registerBlockRenderer(ISimpleBlockRenderingHandler renderer) {
        RenderingRegistry.registerBlockHandler(renderer);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 1:
                return new GuiTranscribingTable(
                    player.inventory,
                    (TranscribingTableBlockEntity) world.getTileEntity(x, y, z));
            case 2:
                return new ArcaneChestGui(player.inventory, (ArcaneChestBlockEntity) world.getTileEntity(x, y, z));
            case 3:
                return new EvilTrunkGui(player, (EntityEvilTrunk) world.getEntityByID(x));
            default:
                return null;
        }
    }

    // ======================================================================
    // 【客户端】闪电特效：用神秘时代的 FXLightningBolt 真正画闪电
    // ======================================================================
    @Override
    public void lightning(World world, double sx, double sy, double sz, double ex, double ey, double ez, int dur,
        float curve, int speed, int type) {
        FXLightningBolt bolt = new FXLightningBolt(
            world,
            sx,
            sy,
            sz,
            ex,
            ey,
            ez,
            world.rand.nextLong(),
            dur,
            curve,
            speed);
        bolt.defaultFractal();
        bolt.setType(type);
        bolt.setWidth(0.125F);
        bolt.finalizeBolt();
    }
}