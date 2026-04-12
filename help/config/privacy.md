# 隐私设置

隐私设置控制直播隐私保护相关功能。

## 配置项

```yaml
privacy:
  recorder-invisible:
    enabled: true
    invisibility-message: "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
  live-logs:
    enabled: true
    keep-count: 100
  consent-prompt:
    enabled: true
    timeout: 60
```

## 参数详解

### recorder-invisible（录制者隐身）

录制者自动隐身功能的设置。

#### enabled（启用状态）

是否启用录制者自动隐身。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**效果：**
- `true` - 录制者在直播时自动对其他玩家隐身
- `false` - 录制者不对其他玩家隐身

#### invisibility-message（隐身提示消息）

录制者隐身时的提示消息。

- **类型：** string
- **默认值：** "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
- **支持颜色代码**

**效果：**
录制者开始直播时，会收到此提示消息。

### live-logs（直播日志）

直播日志记录功能的设置。

#### enabled（启用状态）

是否启用直播日志记录。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**效果：**
- `true` - 记录所有直播操作
- `false` - 不记录直播操作

**记录内容：**
- 开始直播
- 结束直播
- 切换目标
- 同意/拒绝直播

#### keep-count（保留数量）

日志保留的数量。

- **类型：** int
- **默认值：** 100
- **范围：** 10 - 1000

**效果：**
- 插件会自动保留最近 N 条日志
- 超出限制的日志会在下次启动时自动清理

**调整建议：**
- 短期活动：10-50 条
- 长期活动：100-200 条
- 重要记录：500-1000 条

### consent-prompt（同意确认提示）

同意确认提示功能的设置。

#### enabled（启用状态）

是否启用同意确认提示。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**效果：**
- `true` - 玩家首次被直播时会收到确认请求
- `false` - 玩家不会收到确认请求

#### timeout（超时时间）

确认请求的超时时间。

- **类型：** int
- **单位：** 秒
- **默认值：** 60
- **范围：** 30 - 300

**效果：**
- 玩家需要在指定时间内回复
- 超时后请求自动取消

## 预设配置

### 标准配置（推荐）

```yaml
privacy:
  recorder-invisible:
    enabled: true
    invisibility-message: "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
  live-logs:
    enabled: true
    keep-count: 100
  consent-prompt:
    enabled: true
    timeout: 60
```

### 无隐身配置

```yaml
privacy:
  recorder-invisible:
    enabled: false
    invisibility-message: "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
  live-logs:
    enabled: true
    keep-count: 100
  consent-prompt:
    enabled: true
    timeout: 60
```

关闭录制者隐身，录制者对其他玩家可见。

### 无记录配置

```yaml
privacy:
  recorder-invisible:
    enabled: true
    invisibility-message: "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
  live-logs:
    enabled: false
    keep-count: 100
  consent-prompt:
    enabled: true
    timeout: 60
```

关闭日志记录，不保存任何操作记录。

## 常见问题

### 录制者为什么对其他玩家隐身？

因为 `recorder-invisible.enabled` 设置为 `true`，这是为了不影响其他玩家的游戏体验。

### 如何禁用录制者隐身？

将 `recorder-invisible.enabled` 设置为 `false`：

```yaml
privacy:
  recorder-invisible:
    enabled: false
```

### 直播日志保存在哪里？

直播日志保存在 `plugins/LiveRecorder/privacy.db` SQLite 数据库文件中。

### 如何调整日志保留数量？

修改 `live-logs.keep-count` 值：

```yaml
live-logs:
  keep-count: 200
```

### 如何禁用同意确认提示？

将 `consent-prompt.enabled` 设置为 `false`：

```yaml
consent-prompt:
  enabled: false
```

## 注意事项

### 隐私保护的重要性

隐私保护功能是为了：
1. 尊重玩家的隐私权
2. 让玩家自主决定是否被直播
3. 记录所有直播操作，便于追溯

### 数据存储

隐私数据保存在 SQLite 数据库中：
- 数据库位置：`plugins/LiveRecorder/privacy.db`
- 数据表：`privacy_settings`、`live_logs`
- 自动清理：超出限制的日志会自动清理

### 权限管理

隐私相关命令需要 `liverecorder.use` 权限，默认授予所有玩家。