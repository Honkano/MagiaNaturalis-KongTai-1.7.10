package com.github.elenterius.magianaturalis.client.display;

/**
 * 所有可以被"显示开关"控制的显示类型。
 * 想加新的显示项，就在这里加一行。
 */
public enum DisplayType {

    /** 暗水晶护目镜：透视不可见方块 */
    REVEAL_INVISIBLE("reveal_invisible"),

    /** 眼镜/护目镜：显示要素 */
    REVEAL_ASPECTS("reveal_aspects"),

    /** 眼镜：显示容器内要素 */
    REVEAL_CONTAINER_ASPECTS("reveal_container_aspects"),

    /** 生物群系采样器：显示群系边界 */
    REVEAL_BIOME_BOUNDS("reveal_biome_bounds"),

    /** 建筑师焦点：显示建筑投影 */
    REVEAL_BUILDER_GHOST("reveal_builder_ghost"),

    /** 通灵之眼：显示节点/灵气 */
    REVEAL_NODES("reveal_nodes"),

    /** 物品槽内显示要素图标 */
    REVEAL_SLOT_ASPECTS("reveal_slot_aspects"); // ← 只有最后一项用分号

    private final String key;

    DisplayType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
