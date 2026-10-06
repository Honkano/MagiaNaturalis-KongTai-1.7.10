更新日志 / Changelog（v1.1.0）
所有值得记录的版本变更都会写在这里。

All notable changes to this project will be documented in this file.

[v1.1.0] - 2026-10-06
本次更新为面具学体系全面上线。三大分支——态度面具、原始面具、旧三张面具——全部就绪，搭配全新的 Mixin 渲染系统与元素代价机制。

This update brings the full Mask System online. Three branches — Attitude Mask, Primal Mask, and the three legacy masks — are all complete, paired with a new Mixin-based rendering system and elemental cost mechanics.

前置要求（重要）
从本版本起，模组必须搭配以下前置才能运行：

NotEnoughItems (NEI) — 1.7.10 GTNH 版本

GTNHLib（格雷科技维护版共用库）

UniMixins（含 MixinBooterLegacy、GTNHMixins、MixinExtras）

缺少以上任一前置将无法启动。老版（非 GTNH）前置组合不受支持。

Prerequisites (Important)

Starting with this version, the mod requires the following:

NotEnoughItems (NEI) — GTNH 1.7.10 version

GTNHLib (shared library with GTNH-maintained mods)

UniMixins (with MixinBooterLegacy, GTNHMixins, and MixinExtras)

The mod will not start without all three. The legacy (non-GTNH) prerequisite set is not supported.

新增：态度面具（meta 3）
以《蛊真人》态度蛊为原型的四态面具。四张脸，通过按键切换，每次切换消耗心力（生命值 + 饥饿值）。

四种态度：

face 0 · 无态度（旁观者）

敌对生物选目标概率减半（16 格内，僵尸除外）

PVP：30% 概率完全取消攻击 + 攻击者反胃 III 5 秒

你出手后 5 秒内态度失效

切换消耗：生命 0、饥饿 0

face 1 · 友善（和光同尘）

8 格内敌对生物每 2 tick 脱战

中立生物不主动远离

被攻击时，30% 概率让攻击者停手 3 秒

切换消耗：生命 2（1 心）、饥饿 1

face 2 · 威严（不动如山）

8 格内所有生物停止攻击 AI

每 10 tick 施加挖掘疲劳 III + 虚弱 III

自身攻击 +6

每 60 秒触发一次，持续 10 秒

切换消耗：生命 4（2 心）、饥饿 2

face 3 · 隐匿（雾里看花）

半透明

16 格内怪物绝对追不上（每 tick 清目标）

速度翻倍

每秒扣六大元始各 100 vis

60 秒冷却

切换消耗：生命 3（1.5 心）、饥饿 1

心力不足（切换后生命 < 1.5 心，或饥饿不够）→ 当场死亡
提示语言："一个没有心的人，怎么又能戴上态度呢？"

New: Attitude Mask (meta 3)

A four-face mask inspired by the Gu Zhen Ren attitude gu. Four faces, switched by keybind, each switch costs heart-force (health + hunger).

Four attitudes:

face 0 · No Attitude (Observer)

Hostile creatures have their targeting chance halved (16 blocks, zombies excluded)

PVP: 30% chance to fully cancel an attack + attacker receives Confusion III for 5s

The attitude lapses for 5s after you strike

Switch cost: 0 HP, 0 hunger

face 1 · Friendly (Blend with the Light)

Hostile creatures within 8 blocks disengage every 2 ticks

Neutral creatures do not flee

When struck, 30% chance to make the attacker stop for 3s

Switch cost: 2 HP, 1 hunger

face 2 · Solemn (Still as a Mountain)

All creatures within 8 blocks cease attack AI

Mining Fatigue III + Weakness III applied every 10 ticks

Self attack +6

Triggers every 60s, lasts 10s

Switch cost: 4 HP, 2 hunger

face 3 · Hiding (Flowers in the Mist)

Semi-transparent

Monsters within 16 blocks cannot catch you (targets cleared every tick)

Movement speed doubled

Drains 100 vis per second from each of the six primals

60s cooldown

Switch cost: 3 HP, 1 hunger

Heart-force insufficient (HP after switch < 1.5 hearts, or hunger too low) → instant death
Message: "One without a heart cannot bear an attitude."

新增：原始面具（meta 4）
以六元始之根为面的七大形态面具。通过潜行 + 手持法杖右键切换，每次切换消耗对应元始的 vis。

七张脸：

face 0 · 元始

窥秘之镜全套：节点高亮、准星信息、HUD、夜视

空手右键扫描：方块 / 生物 / 节点

每 6 秒回 1 血，每 10 秒回 1 饥饿

副作用：扫描时 -4 饥饿、-2 血

face 1 · 火

攻击 +4，攻击命中点燃目标 3 秒

完全免疫火焰 / 岩浆 / 着火（无伤害、无红闪、无第一人称遮罩、无第三人称模型火焰、无身上粒子）

副作用：溺水伤害 ×2；泡水 / 踩雪 / 踩冰 / 下雪 → 每秒 -3 血

face 2 · 风

移速 +60%，完全免疫摔伤

副作用：攻击力 -60%，每 2 秒 -1 饥饿

face 3 · 混沌

攻击命中 → 随机上一种 III 级、10 秒 debuff（中毒 / 虚弱 / 缓慢 / 失明 / 反胃 / 凋零）

副作用：10% 概率自己中同一种 debuff 5 秒；完全随机，无法控制

face 4 · 大地

固定 40% 减伤（所有伤害 × 0.6），击退免疫

副作用：移速 -60%

face 5 · 秩序

每 2.5 秒回 4 血，最大生命 +4

副作用：每 0.5 秒 -1 饥饿；进食效果减半

face 6 · 水

水下呼吸，完全免疫中毒

副作用：陆地移速 -10%；离水每秒 -3 血

通用

切换消耗：切到 6 元始之一 → 扣该元始 50 vis；切到元始脸 → 六大元始各扣 10 vis

vis 不足 → 提示"元始之力不足"，不切换

音效：thaumcraft:cameraticks

除元始脸的夜视外，全部无 buff 图标

New: Primal Mask (meta 4)

A seven-form mask built upon the six primals. Switched via sneak + right-click with a wand, each switch costs the corresponding primal's vis.

Seven faces:

face 0 · Primal

Full Goggles of Revealing package: node highlight, crosshair info, HUD, night vision

Empty-hand right-click scans blocks / mobs / nodes

Heals 1 HP every 6s; 1 hunger every 10s

Side effect: scanning costs -4 hunger, -2 HP

face 1 · Fire

Attack +4; hits set the target on fire for 3s

Complete immunity to fire / lava / burning (no damage, no red flash, no first-person overlay, no third-person model fire, no particles)

Side effect: drowning damage ×2; in water / on snow / on ice / under snowfall → -3 HP per second

face 2 · Wind

+60% movement speed; complete fall immunity

Side effect: -60% attack damage; -1 hunger every 2s

face 3 · Chaos

Attacks inflict a random tier-III, 10-second debuff (Poison / Weakness / Slowness / Blindness / Nausea / Wither)

Side effect: 10% chance to self-inflict the same debuff for 5s; fully random, uncontrollable

face 4 · Earth

Fixed 40% damage reduction (all damage × 0.6); knockback immunity

Side effect: -60% movement speed

face 5 · Order

Heals 4 HP every 2.5s; max HP +4

Side effect: -1 hunger every 0.5s; food effects halved

face 6 · Water

Water breathing; complete poison immunity

Side effect: -10% land movement speed; -3 HP per second when out of water

General

Switch cost: to a primal face → 50 vis of that primal; to Primal face → 10 vis from each of the six primals

Insufficient vis → "Primal force insufficient", switch cancelled

Sound: thaumcraft:cameraticks

All effects display no buff icons except the Primal face's night vision

新增：面具学 · 旧三张面具（meta 0 / 1 / 2）
meta 0 · 狞笑恶魔面具

每 100 秒减少 1~2 点粘性扭曲

meta 1 · 暴怒幽魂面具

被生物攻击时，30% 概率让攻击者染上凋零 I，持续 5 秒

meta 2 · 嗜血邪妖面具

攻击生物时，25% 概率回血（伤害的 50%，上限 2 心）

三张面具共用 Baubles 项链栏，独立于态度面具与原始面具。

New: Legacy Three Masks (meta 0 / 1 / 2)

meta 0 · Grinning Devil Mask

Reduces sticky warp by 1–2 every 100 seconds

meta 1 · Angry Ghost Mask

When struck by a creature, 30% chance to inflict Wither I on the attacker for 5s

meta 2 · Sipping Fiend Mask

When attacking a creature, 25% chance to heal (50% of damage dealt, capped at 2 hearts)

The three masks share the Baubles amulet slot, independent of the Attitude and Primal masks.

技术变更
新增 Mixin 支持：MagicaNaturalisMixinLoader 通过 IEarlyMixinLoader 主动注册

新增 MixinEntity：注入 Entity.setFire，从源头阻止火脸玩家被点燃

新增 MixinEntityRenderer：注入 EntityRenderer.renderWorld，每帧兜底清 fire

mixins.magianaturalis.json 加入 MixinEntity / MixinEntityRenderer

gradle.properties 启用 usesMixins = true 与 mixinsPackage = mixin

新增 PrimalEffectHandler：七大脸的属性 / 事件 / 副作用分发

新增 PrimalSwitchHandler：潜行 + 法杖右键切换原始面具

新增 MaskScanHandler / MaskScanHelper：空手右键扫描（复用 TC4 ScanManager）

新增 MaskHUDHandler：瞄向已扫目标显示名字 + 源质

新增 MaskFXHelper / PacketAttitudeParticles：触发式粒子包

ItemMask 新增 meta 4 支持，NBT 存 primal_face 0~6

MaskHelper 新增 getPrimalMask / getPrimalFace

Technical Changes

Mixin support added: MagicaNaturalisMixinLoader self-registers via IEarlyMixinLoader

Added MixinEntity: injects into Entity.setFire to stop the Fire-face player from ever being ignited at the source

Added MixinEntityRenderer: injects into EntityRenderer.renderWorld as a per-frame failsafe clearing fire

mixins.magianaturalis.json now includes MixinEntity / MixinEntityRenderer

gradle.properties enables usesMixins = true and mixinsPackage = mixin

Added PrimalEffectHandler: dispatches attributes / events / side effects for the seven faces

Added PrimalSwitchHandler: sneak + wand right-click to switch the Primal mask

Added MaskScanHandler / MaskScanHelper: empty-hand scan (reuses TC4 ScanManager)

Added MaskHUDHandler: shows target name + aspects when aiming at a scanned target

Added MaskFXHelper / PacketAttitudeParticles: trigger-based particle packets

ItemMask gains meta 4 support; NBT stores primal_face 0–6

MaskHelper gains getPrimalMask / getPrimalFace

未来计划 / Roadmap
下一版本将强化模组间的生态联动：

植物魔法（Botania）联动：为部分面具增加与魔力（Mana）体系的交互，例如原始面具的水脸与魔力池共鸣

血魔法（Blood Magic）联动：面具的心力代价可能改为与 LP（Life Points）系统挂钩；隐藏面具可能获得"以血换力"的变体

格雷生态深度加强：与 GT 的能量网络、材料体系进一步对接

UniMixins 深化：更多底层 Mixin，减少对事件层的依赖，提升与其他大型模组的共存稳定性

The next version will strengthen cross-mod ecosystem links:

Botania integration: some masks will interact with the Mana system; e.g., the Primal Water face may resonate with mana pools

Blood Magic integration: the Attitude Mask's heart-force cost may become tied to the LP (Life Points) system; a hidden mask variant may offer "blood for power"

Deeper GregTech ecosystem ties: further hooks into GT's energy network and material system

UniMixins deepening: more low-level Mixins, less reliance on the event layer, better coexistence with other large mods

本地化
新增语言键（中英双语）：

key.magianaturalis.attitude_switch — 切换态度

item.magianaturalis.mask.4.name — 原始面具 / Primal Mask

item.magianaturalis.mask.4.face.0 ~ .6 — 七张脸的名字

msg.magianaturalis.mask.primal.switch — 原始面具切换提示

msg.magianaturalis.mask.primal.no_vis — 元始之力不足

msg.magianaturalis.mask.no_heart — 一个没有心的人

msg.magianaturalis.mask.hiding_start / hiding_end / hiding_fail — 隐匿提示

Localization

New language keys (bilingual):

key.magianaturalis.attitude_switch — Attitude Switch

item.magianaturalis.mask.4.name — Primal Mask

item.magianaturalis.mask.4.face.0 ~ .6 — names of the seven faces

msg.magianaturalis.mask.primal.switch — Primal mask switch message

msg.magianaturalis.mask.primal.no_vis — not enough primal vis

msg.magianaturalis.mask.no_heart — one without a heart

msg.magianaturalis.mask.hiding_start / hiding_end / hiding_fail — hiding messages

持续更新中 / Still in progress — 2026-10-06

作者 / Credits
空太 & AI 共同完成

若在游玩或开发中遇到问题，欢迎前往本仓库的 Issues 页面提交反馈。

Made by KongTai & AI

If you run into any issues while playing or developing, feel free to open an Issue on this repository.







更新日志 / Changelog（v1.0.9）
所有值得记录的版本变更都会写在这里。

All notable changes to this project will be documented in this file.

[v1.0.9] - 2026-10-06
本次更新为全物品槽要素可视化系统，让要素容器（安瓿、灵气精华等）中封存的要素一目了然，无需再靠瓶中液体的淡色猜谜。

前置要求（重要）
从本版本起，模组必须搭配以下前置才能运行：

NotEnoughItems (NEI) — 1.7.10 GTNH 版本

GTNHLib（格雷科技维护版共用库）

UniMixins（含 MixinBooterLegacy、GTNHMixins、MixinExtras）

缺少以上任一前置将无法启动。老版（非 GTNH）前置组合不受支持。

新增：物品槽要素图标
容器 GUI 内显示：箱子、背包、熔炉、合成台等任意容器界面中，凡是存放在槽位里的要素容器，其左上角会直接显示对应要素的彩色图标

目前支持：TC4 要素安瓿（Phial of Essence）、灵气精华（Wispy Essence）

结晶要素（Crystal Essence） 不做显示（其本身即为纯要素形态，无需额外标注）

快捷栏显示：关闭背包后，屏幕底部快捷栏中的要素容器同样显示要素图标

拖拽跟随：鼠标拿起要素容器时，图标会跟随鼠标移动——无论拖到空槽、有物品的槽还是 GUI 之外

悬停不遮挡：图标绘制在 Tooltip 下方，悬停查看物品信息时依然能看到图标，且不影响 Tooltip 阅读

层级清晰：图标使用 GL 状态隔离（PushAttrib / PopAttrib），完全不影响原版 GUI、聊天框、文字渲染

新增：总开关按键
默认按键：O

效果：一键开关所有要素图标显示（容器槽 + 快捷栏 + 拖拽跟随）

聊天提示：切换时在聊天框显示当前状态，颜色随开/关变化（绿/红）

音效反馈：开启时播放清脆叮声，关闭时播放闷响

自动适配语言：中英文玩家看到各自语言的提示（"要素图标：已开启" / "Aspect Icons: ON"）

可在控制菜单中自定义按键

新增：配置项
在 config/magianaturalis.cfg 的 aspect_overlay 分类下：

scale — 图标缩放比例，0.0625 ~ 1.0，默认 0.5

alpha — 图标透明度，0.0 ~ 1.0，默认 1.0

position — 图标在槽内的位置，可选：

TOP_LEFT / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT / CENTER

默认 TOP_RIGHT

研究文本
内在规律（Intro） 节点研究文本重写

前段保持原神秘使世界观叙述

中段衔接"要素容器识别困难"的痛点

后段介绍本模组新增的要素图标系统与总开关

完整中英双语

本地化
新增语言键：

key.magianaturalis.toggle_display（按键名）

msg.magianaturalis.display.on / .off（开关提示）

全部走 StatCollector，玩家可自行翻译

中文："切换要素图标" / 英文："Toggle Aspect Icons"

技术变更
新增 client/aspect/AspectIconRenderer — 单个要素图标的 quad 渲染

新增 client/aspect/ContainerSlotAspectOverlay — 容器槽位与手持物品的图标调度

新增 client/aspect/HotbarSlotAspectOverlay — 快捷栏图标渲染

新增 client/display/DisplayType — 显示类型的枚举（当前含 REVEAL_SLOT_ASPECTS，为后续扩展预留）

新增 client/display/DisplayToggleManager — 显示开关状态管理器（含 save / load 接口）

新增 client/display/DisplayToggleHandler — 按键监听 + 自注册

新增 mixin/MixinGuiContainer — 两处注入点：

注入点 1：drawGuiContainerForegroundLayer 调用之后，渲染槽位图标

注入点 2：drawScreen 的 @At("TAIL")，渲染手持图标

MagicaNaturalisMixinLoader.getMixins() 注册 MixinGuiContainer

mixins.magianaturalis.json 的 mixins 数组加入 MixinGuiContainer

MNConfig 新增 aspect_overlay 分类与 OverlayPosition 枚举

ClientSetup 注册 HotbarSlotAspectOverlay，移除对已废弃的 ContainerSlotAspectOverlay 事件监听

English
This update introduces slot-wide aspect visualization, making the aspects stored inside essentia containers (phials, wispy essence, etc.) visible at a glance—no more guessing by the faint color of the liquid.

Prerequisites (Important)
Starting with this version, the mod requires the following:

NotEnoughItems (NEI) — GTNH 1.7.10 version

GTNHLib (shared library with GTNH-maintained mods)

UniMixins (with MixinBooterLegacy, GTNHMixins, and MixinExtras)

The mod will not start without all three. The legacy (non-GTNH) prerequisite set is not supported.

New: Aspect Icons in Item Slots
In container GUIs: any chest, inventory, furnace, crafting table, etc. — essentia containers stored in slots display the corresponding aspect's colored icon in the top-left corner

Supported: TC4 Essentia Phial and Wispy Essence

Crystal Essence is excluded (it is pure aspect already)

In the hotbar: after closing the inventory, essentia containers in the bottom hotbar also display icons

Drag follow: when you pick up an essentia container, the icon follows the cursor—whether you drag over an empty slot, an occupied slot, or outside the GUI

Hover doesn't obscure: icons are drawn under the tooltip, so hovering to read item info still shows the icon without blocking the tooltip

Clean layering: GL state is isolated via PushAttrib / PopAttrib, so vanilla GUI, chat, and text rendering are completely unaffected

New: Master Toggle Key
Default key: O

Effect: toggles all aspect icon rendering at once (container + hotbar + drag follow)

Chat feedback: toggling posts the current state in chat, colored by on/off (green/red)

Sound feedback: a bright chime when enabled, a muted click when disabled

Localized automatically: Chinese and English players see their own language ("要素图标：已开启" / "Aspect Icons: ON")

Rebindable in the Controls menu

New: Configuration
Under the aspect_overlay category in config/magianaturalis.cfg:

scale — icon scale, 0.0625 ~ 1.0, default 0.5

alpha — icon opacity, 0.0 ~ 1.0, default 1.0

position — icon anchor within the slot, one of:

TOP_LEFT / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT / CENTER

default TOP_RIGHT

Research Text
The Inner Laws (Intro) entry has been rewritten:

Opening keeps the in-universe thaumaturge narration

Middle connects to the pain point of identifying essentia containers

Closing introduces the mod's new aspect icon system and its master toggle

Fully bilingual

Localization
New language keys:

key.magianaturalis.toggle_display (key name)

msg.magianaturalis.display.on / .off (toggle messages)

All routed through StatCollector; players may translate freely

Chinese: "切换要素图标" / English: "Toggle Aspect Icons"

Technical Changes
Added client/aspect/AspectIconRenderer — quad rendering for a single aspect icon

Added client/aspect/ContainerSlotAspectOverlay — dispatches container-slot and held-item icons

Added client/aspect/HotbarSlotAspectOverlay — hotbar icon rendering

Added client/display/DisplayType — enum of display types (REVEAL_SLOT_ASPECTS for now, extensible)

Added client/display/DisplayToggleManager — toggle state manager (with save / load)

Added client/display/DisplayToggleHandler — key listener + self-registration

Added mixin/MixinGuiContainer — two injection points:

Point 1: after drawGuiContainerForegroundLayer — renders slot icons

Point 2: drawScreen's @At("TAIL") — renders held-item icon

MagicaNaturalisMixinLoader.getMixins() registers MixinGuiContainer

mixins.magianaturalis.json's mixins array includes MixinGuiContainer

MNConfig gains the aspect_overlay category and OverlayPosition enum

ClientSetup registers HotbarSlotAspectOverlay and removes the deprecated ContainerSlotAspectOverlay event listener

持续更新中 / Still in progress — 2026-10-06
















## [v1.0.8] - 2026-09-25

本次更新聚焦于**白瞳者之镰的数值重平衡**与**神秘多方块结构展示**。

### 白瞳者之镰（注魔配方重做）
- **要素大幅扩充**：在原有武器、能量、灵气、邪术、死亡五要素基础上，新增黑暗、虚空、治疗、饥饿、贪婪五要素，各 640 点
- **注魔材料重做**：配方材料全部替换为神秘时代后期素材，包括元始杖芯、邪术之眼、平衡碎片、血腥之刃、元始珍珠、元始法杖核心、元始杵、符文石板等
- **中心核心不变**：仍为富饶镰刀

### 白瞳者之镰（命名与品质）
- **物品命名重新定义**：五个阶段的名称、品质名、Tooltip 全面重写
  - 阶段名：初见 / 初醒 / 嗜血 / 狂乱 / 白瞳
  - 品质名：普通 / 精良 / 稀有 / 史诗 / 传奇 / 神话（MAX）
  - 名字颜色按阶段渐变：灰 → 白 → 金 → 红 → 深红 → 深红加粗
- **名称颜色系统**：不同阶段显示不同的物品名字颜色，一眼可辨阶段

### 白瞳者之镰（重平衡）
- **右键吸取 Vis 的耐久消耗重做**：按 Tier 缩放，低等级扣得多，高等级扣得少，MAX 永不扣
- **连锁攻击耐久消耗新增**：每次连锁命中会按 Tier 扣除耐久，命中越多扣得越多
- **被动修复重做**：
  - 修复间隔按 Tier 缩放，高等级修得更快
  - 每次修复量按 Tier 缩放，高等级一次修更多点
  - 修复消耗的 Vis 总量重新调整，低等级更"贵"
- **MAX 状态**：杀敌数到达 99999 后，耐久条隐藏、自动修复停止、全机制免疫耐久损耗

### 神秘多方块结构研究
- **地标塔（Geo Pylon）** 新增多方块结构展示页
  - 展示"地标塔 → 空隙 → 3 格神秘石"的完整结构
- **抄录台（Transcribing Table）** 新增多方块结构展示页
  - 展示"抄录台中心 + 解构工作台隔格环绕"的 5×5 布局
  - 教授玩家：抄录台与解构台之间必须留一格空隙，否则不工作
- 两个结构页均附完整中英文说明文本

### 研究文本
- 地标塔研究文本重写：完整叙述群系采样的三个步骤、要素代价机制、重置注意事项
- 抄录台研究文本重写：解释"隔格"规则，附带神秘使风格的小段子
- 所有研究文本走 `StatCollector`，完整中英双语

### Bug 修复
- 修复"研究页结构图渲染时方块列表长度不匹配导致的渲染异常"
- 修复"地标塔结构页 y 轴反向导致结构上下颠倒"

### 技术变更
- `MNResearch` 新增 `createGeoPylonStructurePage()` / `createTranscribingTableStructurePage()` 两个结构页构造方法
- 统一 TC4 结构页的 y 轴语义注释：**y=0 是视觉最顶层，y=dy-1 是视觉最底层**（与 MC 世界坐标相反）
- `ItemHerobrinesScythe` 新增 `TIER_CHAIN_DAMAGE` / `TIER_REPAIR_AMOUNT` 常量
- `ScytheBlockHandler` 的 `drainVis` 改为按目标数扣耐久

---

## English

This update focuses on **rebalancing Herobrine's Scythe** and **multi-block structure pages**.

### Herobrine's Scythe (Infusion Recipe Reworked)
- **Aspects greatly expanded**: alongside the original Weapon, Energy, Aura, Eldritch, and Death, now also includes Darkness, Void, Heal, Hunger, and Greed, each at 640
- **Infusion materials reworked**: materials replaced with late-game Thaumcraft components, including Primal Wand Rod, Eldritch Eye, Balanced Shard, Crimson Blade, Primordial Pearl, Primal Focus, Primal Crusher, Runic Tablet, and more
- **Center core unchanged**: still the Sickle of Abundance

### Herobrine's Scythe (Naming & Quality)
- **Naming redefined**: all five stages, quality tiers, and tooltips rewritten
  - Stage names: First Glimpse / First Awakening / Bloodthirst / Frenzy / The White Eye
  - Quality tiers: Common / Uncommon / Rare / Epic / Legendary / Mythic (MAX)
  - Name color gradient: Gray → White → Gold → Red → Dark Red → Dark Red Bold
- **Name color system**: each stage displays its own item name color at a glance

### Herobrine's Scythe (Rebalanced)
- **Right-click Vis drain durability cost reworked**: scales with Tier—early tiers cost more, higher tiers cost less, MAX never costs
- **Chain attack durability cost added**: each chain hit costs durability scaled by Tier; more hits, higher cost
- **Passive repair reworked**:
  - Repair interval scales with Tier (higher tiers repair faster)
  - Repair amount per tick scales with Tier (higher tiers repair more per tick)
  - Total Vis cost of repair rebalanced; early tiers pay more
- **MAX state**: upon reaching 99,999 kills, the durability bar is hidden, auto-repair stops, and all durability-cost mechanics are disabled

### Multi-Block Structure Research
- **Geo Pylon** gains a multi-block structure page
  - Shows "Geo Pylon → gap → 3 Arcane Stones" layout
- **Transcribing Table** gains a multi-block structure page
  - Shows "Table at center, Deconstruction Tables in a 5×5 grid with gaps"
  - Teaches players: the Table and Deconstruction Tables must be separated by one block of space, or the Table will not work
- Both structure pages come with full Chinese and English text

### Research Text
- Geo Pylon text rewritten: full walkthrough of biome sampling, essentia cost mechanics, and reset precautions
- Transcribing Table text rewritten: explains the "gap rule", with a small in-character joke
- All research text routed through `StatCollector`, fully localized

### Bug Fixes
- Fixed rendering anomaly when the structure page's block list length does not match `dx*dy*dz`
- Fixed the Geo Pylon structure rendering upside-down due to reversed y-axis

### Technical Changes
- `MNResearch` gains `createGeoPylonStructurePage()` / `createTranscribingTableStructurePage()`
- Unified y-axis semantics for TC4 structure pages: **y=0 is the visual top, y=dy-1 is the visual bottom** (opposite of MC world coordinates)
- `ItemHerobrinesScythe` gains `TIER_CHAIN_DAMAGE` / `TIER_REPAIR_AMOUNT`
- `ScytheBlockHandler.drainVis` now costs durability per target

---

*持续更新中 / Still in progress — 2026-09-25*



## [v1.0.7] - 2026-09-24

本次更新**重做了白瞳者之镰的整套机制**。该武器在旧版本中已存在，本次并非新增武器，而是彻底重构了它的成长逻辑、认主机制与战斗表现。

### 成长系统（重做）
- 五个阶段：初见 / 初醒 / 嗜血 / 狂乱 / 白瞳
- 杀敌数达到 30 / 120 / 450 / 1500 时依次升级
- 杀敌数上限 99999，到达后攻击力质变为 999
- 每个阶段独立物品贴图与光环贴图，从原色渐变至血红
- 扭曲值随阶段加深：0 → 15 → 50 → 140 → 300

### 认主机制（新增）
- 第一次手持时灵魂绑定，记录玩家 UUID 与玩家名
- 非主人拿在手上会被血光排斥，立刻强制丢出
- 原主人若在线会收到聊天框警示

### 战斗机制（重做）
- 连锁闪电：每次攻击会在周围 6 格内的敌人之间弹跳，直到无人生还
- 动态吸血：基础吸血随阶段提升，血量越低吸血越高（最多 +15%）
- 右键格挡：按阶段减伤，最终阶段 100% 完全免疫伤害
- 攻击力成长：5 / 7 / 10 / 14 / 20，满级质变 999
- 移动速度随阶段提升：+5% ~ +18%

### 吸取 Vis（新增）
- 右键按住时，吸取周围 8 格内的生物与玩家身上的 Vis
- 每阶段吸取上限：5 / 10 / 15 / 22 / 30 个目标
- 自动为背包与饰品栏中的神秘时代法杖和 Vis 护身符充能
- 被吸取目标只播放受伤动画，不造成伤害
- 血红色光束特效连接目标与玩家

### 背后的"他"（新增）
- 从第一阶段起，玩家背后会出现一个半透明的 Q 版 Herobrine 影子
- 随着阶段提升，影子逐渐变实、变大
- 到达最终阶段，影子完全实体化，与玩家同尺寸、不透明

### 研究
- 白瞳者之镰的研究文本重写为 8 页，讲述从"富饶镰刀"到"白瞳者之镰"的堕落史
- 1 页注魔配方

### 多语言
- 完整中英文支持
- 所有 UI 文本走 StatCollector，可自行翻译

### Bug 修复
- **#48**：宏伟之木 / 银木箱子开箱没有动画和音效
- **#47**：法杖核心径向界面重叠（`getSortingHelper` 键冲突）
- **#46**：新增 `jarWhitelist` / `jarBlacklist` 配置，可控制封存之罐能封哪些生物
- **#45**：召唤小僵尸不听话（不打友好生物、上限 5 只、加冷却）
- 修复"未手持镰刀时光环仍然渲染"的问题
- 修复"Herobrine 影子倒立"的问题

### 技术变更
- 新增 `ScytheBlockHandler` —— 战斗、认主、吸取 Vis
- 新增 `ScytheAuraHandler` —— 光环渲染 + Herobrine 影子
- 新增 `MNConfig` —— 配置文件支持
- 重做 `ItemHerobrinesScythe` —— NBT 驱动的阶段系统
- 所有硬编码 UI 文本改用 `StatCollector`

---

## English

This update **reworked the entire mechanics of Herobrine's Scythe**. The weapon already existed in previous versions—this is not a new weapon, but a full rebuild of its growth logic, soul-binding, and combat behavior.

### Growth System (Reworked)
- Five stages: First Glimpse / First Awakening / Bloodthirst / Frenzy / The White Eye
- Advances at 30 / 120 / 450 / 1500 kills
- Kill cap at 99999; upon reaching it, attack power surges to 999
- Each stage has its own item texture and aura texture, fading from natural tones to blood red
- Warp deepens with each stage: 0 → 15 → 50 → 140 → 300

### Soul Binding (New)
- Bound to the first player who holds it; UUID and player name are stored
- Non-owners are repelled by blood-light the instant they hold it, and the scythe is forcibly dropped
- The original owner receives a chat warning if online

### Combat (Reworked)
- Chain Lightning: each strike bounces between enemies within 6 blocks until none remain
- Dynamic Lifesteal: base lifesteal grows with stage; the lower your health, the more you drain (up to +15%)
- Right-click Block: damage reduction scales with stage; at the final stage, all damage is fully negated
- Attack scaling: 5 / 7 / 10 / 14 / 20, surging to 999 at MAX
- Movement speed grows with stage: +5% ~ +18%

### Vis Draining (New)
- Hold right-click to drain Vis from creatures and players within 8 blocks
- Per-stage target cap: 5 / 10 / 15 / 22 / 30
- Automatically refuels Thaumcraft wands and Vis amulets in your inventory and Baubles slots
- Draining targets only plays the hurt animation; no damage is dealt
- Crimson beam FX links each target to the wielder

### The One Behind You (New)
- From Stage 1 onward, a translucent chibi Herobrine appears behind the wielder
- As stages advance, the shadow grows more solid and larger
- At the final stage, it fully materializes—player-sized, fully opaque

### Research
- The Herobrine's Scythe research has been rewritten into 8 pages, tracing the fall from the "Sickle of Abundance" to "Herobrine's Scythe"
- 1 infusion recipe page

### Localization
- Full Chinese and English support
- All UI text routed through StatCollector for easy translation

### Bug Fixes
- **#48**: Greatwood / Silverwood chests missing open animation and sound
- **#47**: Wand focus radial overlap (`getSortingHelper` key collision)
- **#46**: Added `jarWhitelist` / `jarBlacklist` config for prison jar capture rules
- **#45**: Summoned baby zombies misbehaving (no friendly targets, cap of 5, cooldown)
- Fixed aura rendering while the scythe was not held
- Fixed Herobrine shadow rendering upside-down

### Technical Changes
- Added `ScytheBlockHandler` — combat, binding, Vis draining
- Added `ScytheAuraHandler` — aura rendering + Herobrine shadow
- Added `MNConfig` — configuration file support
- Reworked `ItemHerobrinesScythe` — NBT-driven tier system
- Localized all hardcoded UI strings via `StatCollector`


## [v1.0.6] - 2026-09-17

自本版本起，**空太与 AI 正式接手《自然魔法》的维护与开发**。

### 接手说明
- 接手日期：**2026 年 9 月 17 日**
- 原有内容保留，新内容由空太与 AI 共同推进
- 后续版本的开发进度、Bug 修复、语言本地化均由当前维护组负责

### 兼容性说明（重要）
本项目以 **格雷科技维护版的神秘基础学（Thaumic Bases: GregTech Edition）** 为模板开发，并与之共存。

- ✅ **与格雷版神秘基础学兼容**：本模组的所有研究、物品、配方均已适配
- ❌ **与老版神秘基础学不兼容**：请勿同时安装
  - 老版本神秘基础学（非格雷维护版）中同样存在一件名为 **"白瞳者之镰"** 的物品
  - 名称与本模组相同，但**实现机制与效果完全不同**
  - 同时安装会导致研究键冲突、物品混淆、配方覆盖等问题
  - 若你使用老版神秘基础学，请勿安装本模组

### 新增内容
- **白瞳者之镰**：以富饶镰刀为基底，注入死亡知识与虚空精华的成长型武器
  - 初始形态为收割农作物的镰刀，注魔后蜕变为战斗用魔器
  - 拥有独立的注魔配方与研究页
  - 后续版本中对该武器进行了完整重做（见 v1.0.7）

### 备注
- 旧版神秘基础学中同名物品仅供参考，**本模组不提供向下兼容**
- 如果你不确定自己用的是哪个版本，请检查该模组是否标注为"GregTech Edition"

---

## English

Starting from this version, **KongTai and AI have officially taken over the maintenance and development of Magia Naturalis**.

### Handover Notes
- Handover date: **September 17, 2026**
- Existing content is preserved; new content is being developed by KongTai and AI
- Future updates, bug fixes, and localization are handled by the current maintainer team

### Compatibility Notes (Important)
This project is built against **Thaumic Bases: GregTech Edition** and coexists with it.

- ✅ **Compatible with GregTech Edition** of Thaumic Bases
- ❌ **NOT compatible with the legacy (non-GregTech) version** of Thaumic Bases
  - The legacy version contains an item also named **"Herobrine's Scythe"**
  - The name is identical, but the mechanics and effects are **completely different**
  - Installing both will cause research key collisions, item confusion, and recipe overwrites
  - If you use the legacy Thaumic Bases, do not install this mod

### New Content
- **Herobrine's Scythe**: a growth-type weapon forged by infusing the Sickle of Abundance with death-lore and void essence
  - Starts as a farming tool; upon infusion, becomes a combat artifact
  - Has its own infusion recipe and research pages
  - Was fully reworked in a later release (see v1.0.7)

### Notes
- The same-named item in the legacy Thaumic Bases is provided for reference only; **this mod does not provide backward compatibility**
- If you are unsure which version you have, check whether it is marked as "GregTech Edition"


---

### 作者 / Credits

**空太 & AI 共同完成**

若在游玩或开发中遇到问题，欢迎前往本仓库的 Issues 页面提交反馈。

**Made by KongTai & AI**

If you run into any issues while playing or developing, feel free to open an Issue on this repository.