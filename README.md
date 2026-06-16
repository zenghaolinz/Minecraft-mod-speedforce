# 神速力模组 (Speed Force Mod)

<p align="center">
  <img src="src/main/resources/logo.png" alt="Speed Force Logo" width="200"/>
</p>

<p align="center">
  <strong>Minecraft NeoForge 1.21.1 模组 - 体验闪电侠般的超能力</strong>
</p>

## 功能特性

### 神速力能力
- **速度等级系统**：从 Level 1 到 Level 10，越高速度越快
- **闪电拖尾**：移动时产生闪电效果，支持 5 道分支
- **子弹时间**：激活时周围世界变慢，你可以正常移动
- **穿墙模式**：穿过任何方块（创造模式风格）
- **时间回溯**：按住 R 键回溯到之前的位置，带动画效果

### 时间残影系统
- **召唤分身**：速度等级 ≥ 4 时，按 H 键召唤时间残影
- **智能跟随**：残影会自动跟随玩家，距离过远自动传送
- **协同作战**：攻击玩家攻击的目标
- **多道闪电**：残影移动时显示 5 道闪电分支拖尾
- **完整装备**：残影继承玩家的盔甲和手持物品

### 其他功能
- **箭袋系统**：G 键切换不同箭矢类型
- **HUD 显示**：实时显示速度等级、残影倒计时等
- **帮助界面**：U 键显示/隐藏按键提示

## 按键指南

| 按键 | 功能 | 备注 |
|------|------|------|
| **C** | 激活/关闭神速力 | 需要穿戴神速力套装 |
| **X** | 增加速度等级 | 最大 Level 10 |
| **Z** | 降低速度等级 | 最小 Level 0 |
| **B** | 子弹时间 | 激活后周围时间变慢 |
| **V** | 穿墙模式 | 可穿过任何方块 |
| **N** | 切换拖尾颜色 | 更改闪电颜色 |
| **R** | 时间回溯（按住） | 回溯到之前的位置 |
| **G** | 切换箭矢类型 | 箭袋箭矢切换 |
| **H** | 时间残影 | 速度等级 ≥ 4 时可用 |
| **U** | 显示/隐藏帮助 | 切换按键提示 |

## 套装类型

| 套装 | 颜色 | 速度加成 |
|------|------|----------|
| **Flash (闪电侠)** | 黄色 | +4 |
| **Reverse Flash (逆闪电)** | 红色 | +5 |
| **Zoom (极速)** | 蓝色 | +6 |
| **Flash S4** | 黄色 | +4 |
| **Flash S5** | 黄色 | +4 |
| **Kid Flash (闪电小子)** | 黄色 | +4 |
| **Green Arrow (绿箭侠)** | 绿色 | +0 |

## 安装方法

### 前置要求
- Minecraft 1.21.1
- NeoForge 21.1.77 或更高版本

### 安装步骤
1. 下载并安装 NeoForge 1.21.1
2. 将 `speedforce-1.0.8v2.jar` 放入 `.minecraft/mods` 文件夹
3. 启动游戏

## 获取神速力

### 方法一：闪电击中
中毒状态下被闪电击中，30% 概率获得神速力

### 方法二：粒子加速器
右键点击粒子加速器方块，100% 获得神速力

### 方法三：命令
```
/speedforce grant [玩家] [等级]  # 授予神速力
/speedforce revoke [玩家]        # 移除神速力
/speedforce info                 # 查看当前状态
```

## 合成配方

### 粒子加速器
```
  I  
 IRI
  I  
I = 铁锭, R = 红石块
```

### 神速力套装
使用皮革和铁锭按照盔甲形状合成。

## 开发信息

| 项目 | 信息 |
|------|------|
| **版本** | 1.0.8v2 |
| **作者** | NLin |
| **许可** | MIT License |
| **Minecraft 版本** | 1.21.1 |
| **NeoForge 版本** | 21.1.77 |
| **Java 版本** | 21 |

### 构建项目
```bash
./gradlew build
```

输出文件位于 `build/libs/speedforce-1.0.8v2.jar`

### 运行测试客户端
```bash
./gradlew runClient
```

## 更新日志

### v1.0.8v2

**修复回溯后快捷栏选中槽与服务端不同步导致的「打火石变 TNT」问题**。

- **现象**：回溯后右键打火石，结果在地上放了一个 TNT，而不是引燃。客户端画面是打火石，服务端却以 TNT 槽位响应交互
- **根因**：`PlayerSnapshot` 恢复时只调用了 `inventoryMenu.broadcastChanges()`，它只发送检测到的槽位变化，**不会同步快捷栏选中槽**（`Inventory.selected`）。客户端选中槽和服务端选中槽不一致
- **修复**：新增 `forceSyncPlayerInventory()` 在每次恢复后强制同步：
  - `inventoryMenu.sendAllDataToRemote()`：发送完整槽位状态（不是 delta）
  - `containerMenu.sendAllDataToRemote()`：同步打开的其他菜单（箱子等）
  - `ClientboundSetCarriedItemPacket(selected)`：专门同步快捷栏选中槽位
- 调整恢复顺序：clear → load → 设置 selected（带 clamp） → setChanged → 完整同步 → 单独同步选中槽
- `confirmRewind()` 末尾再调用一次 `forceSyncPlayerInventory()`，确保最终帧覆盖倒流过程中可能乱序的数据包

### v1.0.8v1

**修复死亡生物复活后保持倾倒/红闪状态**。

- **不再使用尸体 NBT 复活**：新增 `LAST_ALIVE_SNAPSHOTS`，每次记录快照时只采集存活实体（过滤 `isDeadOrDying`/`Health<=0`），复活时使用最后一个存活快照作为来源
- **复活时清理死亡 NBT**：`sanitizeRevivalNbt()` 将 `DeathTime`/`HurtTime` 归零、`Health` 至少为 1
- **清理运行时死亡字段**：新增 `LivingEntityAccessor` Mixin，复活时通过 Accessor 重置 `dead`、`deathTime`、`hurtTime`、`hurtDuration`，确保客户端不会看到「血量恢复但仍然倒地」的状态
- **正确顺序**：clone NBT → 清理死亡 NBT → 创建实体 → 重置运行时字段 → 加入世界（避免客户端先收到死亡状态）
- **仅复活路径触发清理**：普通位置倒流不会清空 `hurtTime`，原本应该回放的红闪动画得以保留

### v1.0.8

**时间回溯系统重构** — 从「位置传送」升级为真正的时间倒流。

- **统一时间游标**：新增 `RewindSession`，让玩家、方块、实体三个回溯模块共享同一个 `cursorGameTime`，三者按相同时刻协同回退
- **完整实体快照**：`WorldRewindHandler` 改为按 UUID 对账（current-target 删除 / target-current 创建 / 交集更新），死亡生物可正确复活
- **完整 NBT 恢复**：实体快照存储完整 NBT，通过 `EntityType.loadEntityRecursive` 重建，不再需要 TNT/箭等特殊处理
- **玩家库存倒流**：`PlayerSnapshot` 扩展为完整状态（背包、经验、饥饿值、速度），彻底修复挖矿后倒流的物品复制漏洞
- **方块放置修复**：使用 `BlockSnapshot.getState()` 记录放置前的旧状态，倒流后正确恢复为空气
- **TNT 爆炸修复**：在 `ExplosionEvent.Start` 预快照爆炸范围方块状态，倒流时不再产生二次爆炸，爆炸坑可正确恢复
- **删除内存泄漏**：玩家断线时清理所有静态 Map 条目
- **删除反射代码**：移除 `AbstractArrow.inGround` 反射，统一通过 NBT 重建处理

### v1.0.7v7
- 新增时间残影系统（H 键召唤）
- 多道闪电拖尾效果（5 条分支）
- 修复寻路转圈问题
- 修复倒计时同步问题
- 添加盔甲/手持物品渲染层

### v1.0.7v2
- 时间回溯系统
- 箭袋系统
- 回溯视觉效果

### v1.0.6
- 神速力合成台
- 绿箭侠弓配方

### v1.0.1v4
- 修复绿箭侠弓生存模式可用
- 优化闪电拖尾渲染

## 项目链接

- **GitHub**: [https://github.com/zenghaolinz/Minecraft-mod-speedforce](https://github.com/zenghaolinz/Minecraft-mod-speedforce)
- **问题反馈**: [Issues](https://github.com/zenghaolinz/Minecraft-mod-speedforce/issues)

## 致谢

感谢 DC 漫画《闪电侠》系列给予的灵感。
