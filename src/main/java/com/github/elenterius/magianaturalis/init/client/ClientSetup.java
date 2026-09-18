package com.github.elenterius.magianaturalis.init.client;

import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import com.github.elenterius.magianaturalis.block.banner.CustomBannerBlockEntity;
import com.github.elenterius.magianaturalis.block.chest.ArcaneChestBlockEntity;
import com.github.elenterius.magianaturalis.block.geopylon.GeoPylonBlockEntity;
import com.github.elenterius.magianaturalis.block.jar.PrisonJarBlockEntity;
import com.github.elenterius.magianaturalis.block.table.TranscribingTableBlockEntity;
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
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ScytheAuraHandler()); // 【新增】
        MNKeyBindings.register();
        KeyEventHandler.register();
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
        // RenderingRegistry.registerEntityRenderingHandler(EntityTaintman.class, new RenderTaintman());
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
    // 【新增】客户端的闪电特效：用神秘时代的 FXLightningBolt 真正画闪电
    // 这个方法覆盖了 CommonSetup 里的空方法
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
