# 更新日志 / Changelog

所有值得记录的版本变更都会写在这里。

All notable changes to this project will be documented in this file.


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