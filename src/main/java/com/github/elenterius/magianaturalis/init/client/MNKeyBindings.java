package com.github.elenterius.magianaturalis.init.client;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;

public final class MNKeyBindings {

    public static final KeyBinding MISC_KEY = new KeyBinding(
        "key.magianaturalis.misc",
        Keyboard.KEY_V,
        "key.categories.magianaturalis");
    public static final KeyBinding INCREASE_SIZE_KEY = new KeyBinding(
        "key.magianaturalis.increase_size",
        Keyboard.KEY_ADD,
        "key.categories.magianaturalis");
    public static final KeyBinding DECREASE_SIZE_KEY = new KeyBinding(
        "key.magianaturalis.decrease_size",
        Keyboard.KEY_SUBTRACT,
        "key.categories.magianaturalis");
    public static final KeyBinding PICK_BLOCK_KEY = new KeyBinding(
        "key.magianaturalis.pick_block",
        -98,
        "key.categories.magianaturalis");

    private MNKeyBindings() {}

    static void register() {
        ClientRegistry.registerKeyBinding(INCREASE_SIZE_KEY);
        ClientRegistry.registerKeyBinding(DECREASE_SIZE_KEY);
        ClientRegistry.registerKeyBinding(MISC_KEY);
        ClientRegistry.registerKeyBinding(PICK_BLOCK_KEY);
    }

}
