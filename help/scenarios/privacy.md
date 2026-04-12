# 隐私保护模式

启用隐私保护，玩家可以拒绝被直播。

## 场景描述

- **适用模式：** AUTO 或 MANUAL
- **录制者数量：** 不限
- **目标数量：** 不限
- **推荐配置：** 隐私优先

## 操作步骤

### 1. 确认隐私功能已启用

检查 `config.yml`：

```yaml
privacy:
  recorder-invisible:
    enabled: true
  live-logs:
    enabled: true
  consent-prompt:
    enabled: true
    timeout: 60
```

### 2. 玩家设置隐私状态

**方式一：玩家自主设置**

```bash
# 拒绝被直播
/lr setprivacy decline

# 同意被直播
/lr setprivacy accept

# 重置为未设置（每次确认）
/lr setprivacy unset
```

**方式二：使用命令白名单**

```yaml
recorder-restrictions:
  block-command: true
  command-whitelist:
    - lr
    - liverecorder
    - setprivacy
```

### 3. 管理员绑定录制者

```bash
/lr bind CameraMan 玩家A auto
```

### 4. 处理隐私请求

**如果玩家未设置隐私：**

玩家会收到请求：
```
§6[LiveRecorder] §e录制者 CameraMan 请求直播您的视角
§6[LiveRecorder] §a输入 /lr accept §7同意直播
§6[LiveRecorder] §c输入 /lr decline §7拒绝直播
```

玩家可以输入 `/lr accept` 或 `/lr decline`。

**如果玩家拒绝直播：**

录制者会收到通知：
```
§6[LiveRecorder] §c玩家 玩家A 已拒绝被直播
```

## 效果预览

### 玩家视角（未设置隐私）

```
§6[LiveRecorder] §e录制者 CameraMan 请求直播您的视角
§6[LiveRecorder] §a输入 /lr accept §7同意直播
§6[LiveRecorder] §c输入 /lr decline §7拒绝直播
```

### 玩家视角（已拒绝）

```
§6[LiveRecorder] §c你已拒绝被直播，录制者无法绑定到你
```

### 录制者视角（绑定失败）

```
§6[LiveRecorder] §c玩家 玩家A 已拒绝被直播
```

## 隐私状态

### ACCEPTED（同意）

玩家同意被直播。

**效果：**
- 录制者可以立即开始跟随
- 不会收到确认请求
- 适合经常被直播的玩家

**设置方法：**
```bash
/lr setprivacy accept
```

### DECLINED（拒绝）

玩家拒绝被直播。

**效果：**
- 录制者无法绑定到该玩家
- 录制者尝试绑定时会收到拒绝通知
- 适合注重隐私的玩家

**设置方法：**
```bash
/lr setprivacy decline
```

### UNSET（未设置）

玩家未设置隐私状态，需要确认。

**效果：**
- 每次被直播时需要确认
- 会收到直播请求
- 选择会持久化保存

**设置方法：**
```bash
/lr setprivacy unset
```

## 推荐配置

### 标准隐私保护

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

recorder-restrictions:
  block-command: true
  command-whitelist:
    - lr
    - liverecorder
    - setprivacy
    - accept
    - decline
```

**特点：**
- 完整的隐私保护
- 玩家可以自主设置
- 记录所有操作

### 严格隐私保护

```yaml
privacy:
  recorder-invisible:
    enabled: true
  live-logs:
    enabled: true
    keep-count: 200
  consent-prompt:
    enabled: true
    timeout: 120

recorder-restrictions:
  block-command: true
  command-whitelist:
    - lr
    - liverecorder
    - setprivacy
    - accept
    - decline
```

**特点：**
- 更长的请求超时时间
- 保留更多日志
- 更严格的限制

### 简化隐私保护

```yaml
privacy:
  recorder-invisible:
    enabled: true
  live-logs:
    enabled: false
  consent-prompt:
    enabled: false

recorder-restrictions:
  block-command: false
```

**特点：**
- 只保留隐身功能
- 不记录日志
- 不需要确认请求

## 常见问题

### 玩家如何拒绝被直播？

```bash
/lr setprivacy decline
```

### 玩家如何允许被直播？

```bash
/lr setprivacy accept
```

### 如何查看玩家的隐私设置？

```bash
/lr privacy
```

### 录制者为什么看不到我？

这不是隐私设置的问题，而是录制者的隐身功能。录制者在直播时会对其他玩家隐身，这是正常行为。

### 隐私数据保存在哪里？

隐私数据保存在 `plugins/LiveRecorder/privacy.db` SQLite 数据库文件中。

### 如何清理旧的直播日志？

插件会自动保留最近 100 条日志，超出限制的会在下次启动时自动清理。

## 最佳实践

### 玩家隐私设置

1. **默认设置为未设置：** 让玩家自主选择
2. **提供清晰的说明：** 告诉玩家如何设置隐私
3. **尊重玩家选择：** 不要强制直播

### 管理员操作

1. **检查隐私设置：** 绑定前检查目标隐私状态
2. **处理拒绝请求：** 及时处理玩家拒绝的通知
3. **查看日志：** 定期查看直播日志

### 配置建议

1. **启用所有隐私功能：** 提供完整的隐私保护
2. **合理的超时时间：** 60 秒是合理的默认值
3. **保留足够的日志：** 100 条日志足够日常使用

## 示例脚本

### 批量设置隐私

```bash
#!/bin/bash
# 批量为玩家设置隐私状态

PLAYERS=("玩家A" "玩家B" "玩家C")

for player in "${PLAYERS[@]}"; do
  # 玩家需要自己执行以下命令
  echo "玩家 $player 请执行: /lr setprivacy accept"
done
```

### 查看所有玩家隐私设置

```bash
#!/bin/bash
# 查看所有在线玩家的隐私设置

# 需要插件支持批量查询
# 当前版本不支持此功能

# 手动查询方式：
echo "请各玩家执行: /lr privacy"
```

### 隐私状态检查

```bash
#!/bin/bash
# 检查目标玩家的隐私状态

TARGET="玩家A"

# 尝试绑定
/lr bind CameraMan $TARGET auto

# 根据返回值判断隐私状态
# 0 = 成功（同意）
# 1 = 已绑定
# 2 = 拒绝
# 3 = 待确认
```