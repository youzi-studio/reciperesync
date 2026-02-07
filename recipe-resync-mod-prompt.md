# 任务：编写一个 NeoForge 服务端模组 - RecipeResync

## 背景

我有一个 Minecraft 模组服网络，架构如下：

```
NeoForge客户端 → Velocity代理(25565) → Paper大厅(20000) → 转服 → NeoForge模组服(20001)
```

由于 NeoForge 客户端需要先连接 Paper（非NeoForge）大厅，客户端必须安装 `be_quiet_negotiator` 模组来绕过 NeoForge 的握手协商。但这个模组会静默吞掉数据包错误，导致从大厅转入模组服时，服务端发送的**配方同步数据包被丢弃**，JEI（Just Enough Items）无法显示任何模组物品的合成表。

已确认：在服务端执行 `/reload` 后，配方会重新同步给所有玩家，JEI 恢复正常。但 `/reload` 是全服操作，会导致所有在线玩家短暂卡顿。

## 需求

编写一个**纯服务端** NeoForge 模组，功能如下：

1. 监听玩家加入服务器事件（`PlayerLoggedInEvent`）
2. 延迟一段时间（建议 3-5 秒，可配置）
3. **仅对该玩家**重新发送配方同步数据包（`ClientboundUpdateRecipesPacket`）
4. 不影响其他在线玩家

## 技术要求

- **Minecraft 版本**：1.21.1
- **模组加载器**：NeoForge 21.1.219
- **Java 版本**：21
- **构建工具**：Gradle（使用 NeoForge MDK 模板）
- **模组ID建议**：`reciperesync`
- **仅服务端运行**，客户端不需要安装此模组

## 实现要点

1. 使用 NeoForge 事件系统监听 `PlayerLoggedInEvent`
2. 使用服务器的调度器（`server.execute()` 或 tick 计时）实现延迟执行
3. 从服务器的 `RecipeManager` 获取所有配方数据
4. 构造 `ClientboundUpdateRecipesPacket` 并通过 `ServerPlayer.connection.send()` 发送给目标玩家
5. 注意：在 1.21.1 中，配方同步可能涉及 `ClientboundRecipeBookAddPacket`、`ClientboundRecipeBookRemovePacket` 等多个包，需要参考 `/reload` 命令（`ReloadCommand`）或 `PlayerList.sendAllPlayerInfo()` 中配方同步的具体实现，确保发送的数据包与 `/reload` 触发的一致
6. 添加日志输出，方便调试（如 "Resending recipes to player xxx"）

## 可选功能

- 配置文件支持：延迟时间可配置（默认3秒）
- 添加一个管理员命令如 `/reciperesync <玩家名>` 用于手动触发单玩家配方重同步

## 当前服务端已安装的模组（供参考）

- Mekanism 10.7.17（通用机械）
- MekanismGenerators 10.7.17
- Applied Energistics 2 19.2.17
- EnderIO 8.1.1-beta
- JEI 19.27.0.340
- NeoForwarding 1.3.0（Velocity代理转发）
- 以及其他 AE2 附属模组

## 验证方法

1. 将编译好的模组 jar 放入服务端 `mods/` 文件夹
2. 启动服务器
3. 客户端安装 `be_quiet_negotiator`，从 Paper 大厅转入模组服
4. 等待几秒后检查 JEI 是否能正常显示所有模组物品的合成表
5. 查看服务端日志确认 "Resending recipes to player xxx" 输出
