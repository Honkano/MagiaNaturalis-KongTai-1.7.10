package com.github.elenterius.magianaturalis.client.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class DisplayToggleHandler {

    public static final KeyBinding TOGGLE_DISPLAY_KEY = new KeyBinding(
        "key.magianaturalis.toggle_display",
        Keyboard.KEY_O,
        "key.categories.magianaturalis");

    private DisplayToggleHandler() {}

    public static void register() {
        ClientRegistry.registerKeyBinding(TOGGLE_DISPLAY_KEY);
        FMLCommonHandler.instance()
            .bus()
            .register(new Handler());
    }

    @SideOnly(Side.CLIENT)
    public static class Handler {

        @SubscribeEvent
        public void onKeyInput(InputEvent.KeyInputEvent event) {
            if (!TOGGLE_DISPLAY_KEY.isPressed()) return;

            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer == null) return;
            if (mc.currentScreen != null) return;

            DisplayToggleManager manager = DisplayToggleManager.getInstance();
            manager.cycleAll();

            boolean anyOn = manager.isAnyEnabled();

            // 从语言文件读取消息，中英文自动适配
            String key = anyOn ? "msg.magianaturalis.display.on" : "msg.magianaturalis.display.off";
            String localized = StatCollector.translateToLocal(key);

            EnumChatFormatting color = anyOn ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
            mc.thePlayer.addChatMessage(new ChatComponentText(color + localized));

            // 音效：开→清脆的叮，关→闷响
            String sound = anyOn ? "random.orb" : "random.click";
            float pitch = anyOn ? 1.4F : 0.8F;
            mc.thePlayer.worldObj.playSoundAtEntity(mc.thePlayer, sound, 0.4F, pitch);
        }
    }
}
