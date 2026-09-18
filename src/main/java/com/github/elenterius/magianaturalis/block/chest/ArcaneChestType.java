package com.github.elenterius.magianaturalis.block.chest;

import com.github.elenterius.magianaturalis.MagiaNaturalis;

public enum ArcaneChestType {

    GREAT_WOOD(54, "gw"),
    SILVER_WOOD(77, "sw");

    public final int inventorySize;
    public final String translationKey;

    ArcaneChestType(int inventorySize, String key) {
        this.inventorySize = inventorySize;
        translationKey = "tile." + MagiaNaturalis.MOD_ID + ".arcane_chest." + key + ".name";
    }

    public static ArcaneChestType parseId(byte id) {
        if (id < 0 || id >= values().length) return GREAT_WOOD;
        return values()[id];
    }

    public byte id() {
        return (byte) (ordinal());
    }

}
