# RecipeResync

一个服务端 NeoForge 模组，用于修复代理服务器架构下 JEI/原版/其他合成表配方不同步的问题。

## 问题背景

当使用以下代理架构时：
```
NeoForge 客户端 → Velocity → Paper 大厅 → 转服 → NeoForge 模组服
```

客户端需要安装 [`be_quiet_negotiator`](https://github.com/samuelh2005/BeQuietNegotiator) 模组，用于绕过 NeoForge 握手协商，以实现连接 Paper 大厅服务器。  
然而，该模组会静默丢弃握手阶段产生的数据包错误，这会导致玩家从大厅切换至模组服务器时，标签（Tags）与配方（Recipes）同步数据包丢失，从而使原版合成表与 JEI 均无法显示任何模组配方。

尽管该模组作者在原文中明确指出：

> Do not use this mod! You will likely run into issues with mod compatibility, as Be Quiet Negotiator just suppresses errors and does not magically make incompatible mods work.

并建议避免使用传统代理转发方式，而是为每个 NeoForge 服务器分配独立的公网IP。  
服务器间跳转则应使用 Minecraft 1.20.5 引入的原版 `/transfer` 指令，或Server Redirect模组。  
但上述方案对于**仅使用大厅作为统一登录入口的群组服架构而言并不友好**。  
还有一种办法是使用 `/reload` 指令，但该指令属于**全局操作**，会强制刷新服务器上的所有玩家数据与资源状态。在玩家数量较多或服务器负载较高的情况下会造成不可预期的问题。

## 解决方案
使用本模组  
本模组监听玩家加入事件，自动重新发送：
1. **标签数据** (Tags) - 物品/方块/流体分组，配方原料匹配依赖此数据
2. **配方数据** (Recipes) - 所有配方定义
3. **配方书状态** (Recipe Book) - 玩家已解锁的配方

效果等同于 `/reload`，但仅针对加入的玩家，不影响其他在线玩家。

## 功能

- 玩家加入时自动重新同步标签和配方（延迟可配置）
- 管理员命令 `/reciperesync [玩家名]` 手动触发重同步
- 纯服务端模组，客户端无需安装

## 环境要求

- Minecraft 1.21.1
- NeoForge 21.1.219+
- Java 21

## 配置文件

路径：`world/serverconfig/reciperesync-server.toml`

```toml
[general]
# 延迟时间（单位：tick，20 tick = 1 秒）
# 默认：60（3 秒）
delayTicks = 60
```

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/reciperesync` | OP 等级 2 | 为自己重新同步配方 |
| `/reciperesync <玩家名>` | OP 等级 2 | 为指定玩家重新同步配方 |

## 构建

```bash
./gradlew build
```

输出：`build/libs/reciperesync-1.0.0.jar`

## 许可证

GPL-3.0-only
