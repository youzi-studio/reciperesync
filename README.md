# RecipeResync

一个服务端 NeoForge 模组，用于修复代理服务器架构下 JEI 配方不同步的问题。

## 问题背景

当使用以下代理架构时：
```
NeoForge 客户端 → Velocity (25565) → Paper 大厅 (20000) → 转服 → NeoForge 模组服 (20001)
```

客户端需要安装 `be_quiet_negotiator` 模组来绕过 NeoForge 握手协商以连接 Paper 大厅。但该模组会静默丢弃数据包错误，导致从大厅转入模组服时**标签和配方同步数据包丢失**，JEI 无法显示任何模组配方。

## 解决方案

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
