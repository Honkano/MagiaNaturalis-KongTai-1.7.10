package com.github.elenterius.magianaturalis.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.client.fx.particles.FXSparkle;
import thaumcraft.client.fx.particles.FXWisp;

@SideOnly(Side.CLIENT)
public class MaskFXHelper {

    public static void spawn(EntityPlayer player, int face) {
        if (player == null) return;
        World world = player.worldObj;
        if (world == null || !world.isRemote) return;

        switch (face) {
            case 0:  // 无态度 白
                for (int i = 0; i < 12; i++) {
                    FXWisp fx = new FXWisp(world,
                        player.posX + (world.rand.nextDouble() - 0.5) * 1.5,
                        player.posY + 0.2 + world.rand.nextDouble() * 1.8,
                        player.posZ + (world.rand.nextDouble() - 0.5) * 1.5,
                        0.8F, 6);
                    fx.noClip = true;
                    Minecraft.getMinecraft().effectRenderer.addEffect(fx);
                }
                break;
            case 1:  // 友善 绿
                for (int i = 0; i < 12; i++) {
                    FXWisp fx = new FXWisp(world,
                        player.posX + (world.rand.nextDouble() - 0.5) * 1.8,
                        player.posY + 0.2 + world.rand.nextDouble() * 1.8,
                        player.posZ + (world.rand.nextDouble() - 0.5) * 1.8,
                        0.9F, 3);
                    fx.noClip = true;
                    Minecraft.getMinecraft().effectRenderer.addEffect(fx);
                }
                break;
            case 2:  // 威严 金橙
                for (int i = 0; i < 16; i++) {
                    FXWisp fx = new FXWisp(world,
                        player.posX + (world.rand.nextDouble() - 0.5) * 2.0,
                        player.posY + 0.2 + world.rand.nextDouble() * 2.0,
                        player.posZ + (world.rand.nextDouble() - 0.5) * 2.0,
                        1.2F, 7);
                    fx.noClip = true;
                    Minecraft.getMinecraft().effectRenderer.addEffect(fx);
                }
                break;
            case 3:  // 隐匿 紫
                for (int i = 0; i < 20; i++) {
                    FXSparkle fx = new FXSparkle(world,
                        player.posX + (world.rand.nextDouble() - 0.5) * 1.5,
                        player.posY + world.rand.nextDouble() * 2.0,
                        player.posZ + (world.rand.nextDouble() - 0.5) * 1.5,
                        1.2F, 0, 4);
                    fx.noClip = true;
                    Minecraft.getMinecraft().effectRenderer.addEffect(fx);
                }
                break;
        }
    }
}