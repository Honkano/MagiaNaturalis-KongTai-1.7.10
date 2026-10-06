package com.github.elenterius.magianaturalis.client.display;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 全局显示开关管理器（单例）。
 * - 每个 DisplayType 独立开关
 * - 有 masterEnabled 总开关，关掉后所有显示被强制屏蔽
 * - 用 save()/load() 做持久化
 */
@SideOnly(Side.CLIENT)
public final class DisplayToggleManager {

    private static final DisplayToggleManager INSTANCE = new DisplayToggleManager();

    private final Map<DisplayType, Boolean> typeStates = new EnumMap<DisplayType, Boolean>(DisplayType.class);

    private boolean masterEnabled = true;
    private boolean initialized = false;

    private DisplayToggleManager() {
        for (DisplayType type : DisplayType.values()) {
            typeStates.put(type, Boolean.TRUE);
        }
    }

    public static DisplayToggleManager getInstance() {
        return INSTANCE;
    }

    // ------------------------------------------------------------------
    // 查询
    // ------------------------------------------------------------------

    public boolean isEnabled(DisplayType type) {
        if (!masterEnabled) return false;
        Boolean state = typeStates.get(type);
        return state == null || state.booleanValue();
    }

    public boolean isMasterEnabled() {
        return masterEnabled;
    }

    /** 有任何一项开着就返回 true（给提示文字用） */
    public boolean isAnyEnabled() {
        if (!masterEnabled) return false;
        for (DisplayType type : DisplayType.values()) {
            if (isEnabled(type)) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------
    // 切换
    // ------------------------------------------------------------------

    public void toggle(DisplayType type) {
        Boolean cur = typeStates.get(type);
        typeStates.put(type, Boolean.valueOf(cur == null || !cur.booleanValue()));
    }

    public void toggleMaster() {
        masterEnabled = !masterEnabled;
    }

    /**
     * 一个按键控制所有显示：
     * - 只要有任何一项关着 → 全部开
     * - 全部开着         → 全部关
     */
    public void cycleAll() {
        boolean anyOff = false;
        for (DisplayType type : DisplayType.values()) {
            if (!isEnabled(type)) {
                anyOff = true;
                break;
            }
        }
        boolean target = anyOff;
        for (DisplayType type : DisplayType.values()) {
            typeStates.put(type, Boolean.valueOf(target));
        }
    }

    // ------------------------------------------------------------------
    // 持久化
    // ------------------------------------------------------------------

    public NBTTagCompound save() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("master", masterEnabled);

        NBTTagCompound states = new NBTTagCompound();
        for (Map.Entry<DisplayType, Boolean> e : typeStates.entrySet()) {
            states.setBoolean(e.getKey().getKey(), e.getValue().booleanValue());
        }
        tag.setTag("states", states);
        return tag;
    }

    public void load(NBTTagCompound tag) {
        if (tag == null) return;

        if (tag.hasKey("master")) {
            masterEnabled = tag.getBoolean("master");
        }

        if (tag.hasKey("states")) {
            NBTTagCompound states = tag.getCompoundTag("states");
            for (DisplayType type : DisplayType.values()) {
                if (states.hasKey(type.getKey())) {
                    typeStates.put(type, Boolean.valueOf(states.getBoolean(type.getKey())));
                }
            }
        }

        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
}