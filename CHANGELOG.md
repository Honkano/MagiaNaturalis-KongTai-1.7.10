# 更新日志 / Changelog

所有值得记录的版本变更都会写在这里。

All notable changes to this project will be documented in this file.


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