# 录制者隐身

录制者在直播时会自动对其他玩家隐身。

## 功能说明

录制者隐身功能确保录制者不会干扰其他玩家的游戏体验。

**效果：**
- 其他玩家无法看到录制者
- 不会影响其他玩家的视线
- 录制者仍然可以看到自己
- 录制者不会与世界交互

## 配置项

### enabled（启用状态）

是否启用录制者自动隐身。

**配置：**
```yaml
privacy:
  recorder-invisible:
    enabled: true  # true 为启用，false 为禁用
```

**选项：**
- `true` - 启用隐身（推荐）
- `false` - 禁用隐身

### invisibility-message（隐身提示消息）

录制者隐身时的提示消息。

**配置：**
```yaml
privacy:
  recorder-invisible:
    invisibility-message: "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
```

**支持颜色代码：**
- `§0` - 黑色
- `§1` - 深蓝色
- `§2` - 深绿色
- `§3` - 深青色
- `§4` - 深红色
- `§5` - 紫色
- `§6` - 金色
- `§7` - 灰色
- `§8` - 深灰色
- `§9` - 蓝色
- `§a` - 绿色
- `§b` - 青色
- `§c` - 红色
- `§d` - 粉红色
- `§e` - 黄色
- `§f` - 白色

## 工作原理

### 隐身机制

录制者隐身使用 Minecraft 的隐藏玩家机制：

```java
// 对所有其他玩家隐藏录制者
for (Player other : Bukkit.getOnlinePlayers()) {
    if (!other.equals(recorder)) {
        other.hidePlayer(plugin, recorder);
    }
}
```

### 显示机制

当录制者解绑时，会自动对所有玩家显示：

```java
// 对所有其他玩家显示录制者
for (Player other : Bukkit.getOnlinePlayers()) {
    if (!other.equals(recorder)) {
        other.showPlayer(plugin, recorder);
    }
}
```

### 新玩家加入

当新玩家加入时，会自动更新隐身状态：

```java
// 隐藏所有正在直播的录制者
for (RecorderBinding binding : bindings.values()) {
    Player recorder = binding.getRecorder();
    if (recorder.isOnline()) {
        newPlayer.hidePlayer(plugin, recorder);
    }
}
```

## 使用场景

### 标准直播

**配置：**
```yaml
privacy:
  recorder-invisible:
    enabled: true
```

**效果：**
- 录制者对其他玩家完全隐身
- 不会干扰游戏进程
- 适合大多数直播场景

### 测试模式

**配置：**
```yaml
privacy:
  recorder-invisible:
    enabled: false
```

**效果：**
- 录制者对其他玩家可见
- 便于测试和调试
- 适合测试环境

## 常见问题

### 录制者为什么对其他玩家隐身？

这是正常行为，为了不影响其他玩家的游戏体验。

### 如何禁用录制者隐身？

修改配置文件：
```yaml
privacy:
  recorder-invisible:
    enabled: false
```

然后执行 `/lr reload`。

### 录制者能看到自己吗？

可以，录制者仍然可以看到自己，只是对其他玩家隐身。

### 录制者隐身后还能被攻击吗？

不能，隐身后其他玩家无法攻击录制者。

### 录制者隐身后还能被看到吗？

其他玩家无法看到录制者，但可以通过以下方式发现：
- 录制者的动作（如放置/破坏方块）
- 录制者的声音
- 录制者的粒子效果

### 录制者解绑后会显示吗？

会，录制者解绑后会自动对所有玩家显示。

## 注意事项

### 隐身效果

录制者隐身只会隐藏玩家模型，不会隐藏：
- 录制者的声音
- 录制者的动作
- 录制者的粒子效果
- 录制者的物品掉落

### 游戏平衡

隐身功能不会影响游戏平衡：
- 录制者仍然无法攻击其他玩家
- 录制者仍然无法与世界交互
- 录制者仍然受到录制者限制

### 性能影响

隐身功能对性能影响很小：
- 只需要在玩家加入时更新一次
- 不会持续消耗资源
- 适合大多数服务器

## 最佳实践

### 启用隐身

建议在大多数场景下启用隐身：
- 不会干扰其他玩家
- 提供更好的游戏体验
- 适合大多数直播场景

### 禁用隐身

仅在以下场景禁用隐身：
- 测试和调试
- 需要录制者可见的特殊活动
- 演示和教学

### 提示消息

设置清晰的提示消息：
```yaml
invisibility-message: "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
```

让录制者了解当前状态。