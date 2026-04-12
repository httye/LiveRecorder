# 隐私保护

## 隐私概览

LiveRecorder 提供完善的隐私保护功能，确保玩家的直播体验：

### 核心功能

- ✅ **同意/拒绝机制** - 玩家可以自主决定是否被直播
- 👻 **录制者隐身** - 录制者对其他玩家完全隐身
- 📝 **直播日志** - 记录所有直播操作，可追溯
- 💾 **数据持久化** - SQLite 数据库存储隐私设置

### 工作流程

```
1. 管理员尝试绑定录制者
   ↓
2. 插件检查目标玩家隐私设置
   ↓
3. 如果未设置，发送确认请求
   ↓
4. 目标玩家选择同意或拒绝
   ↓
5. 记录选择并开始/拒绝直播
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

## 录制者隐身

录制者在直播时会自动对其他玩家隐身。

**效果：**
- 其他玩家无法看到录制者
- 不会干扰游戏进程
- 录制者仍然可以看到自己

**配置：**
```yaml
privacy:
  recorder-invisible:
    enabled: true
    invisibility-message: "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
```

**禁用隐身：**
```yaml
privacy:
  recorder-invisible:
    enabled: false
```

## 直播日志

所有直播操作都会记录到数据库。

**记录内容：**
- 开始直播
- 结束直播
- 切换目标
- 同意/拒绝直播

**查看日志：**
```bash
/lr logs 20
```

**日志存储：**
- 数据库位置：`plugins/LiveRecorder/privacy.db`
- 自动保留最近 100 条
- 超出限制的会自动清理

## 数据存储

### SQLite 数据库

插件使用 SQLite 存储隐私设置和直播日志。

**数据库位置：**
```
plugins/LiveRecorder/privacy.db
```

**数据表：**

1. **privacy_settings** - 隐私设置
   - player_uuid - 玩家 UUID
   - player_name - 玩家名称
   - consent_status - 同意状态
   - invisible - 是否隐身
   - last_updated - 最后更新时间

2. **live_logs** - 直播日志
   - id - 日志 ID
   - log_type - 日志类型
   - recorder_uuid - 录制者 UUID
   - recorder_name - 录制者名称
   - target_uuid - 目标 UUID
   - target_name - 目标名称
   - timestamp - 时间戳
   - extra_info - 额外信息

## 详细说明

- [同意/拒绝机制](./consent) - 详细的同意/拒绝机制说明
- [录制者隐身](./invisibility) - 录制者隐身功能详解
- [直播日志](./logs) - 直播日志功能详解
- [数据存储](./storage) - 数据存储详解

## 常见问题

### 如何拒绝被直播？

```bash
/lr setprivacy decline
```

### 如何允许被直播？

```bash
/lr setprivacy accept
```

### 如何每次都确认？

```bash
/lr setprivacy unset
```

### 录制者为什么看不到我？

这不是隐私设置的问题，而是录制者的隐身功能。录制者在直播时会对其他玩家隐身，这是正常行为。

### 如何查看我的隐私设置？

```bash
/lr privacy
```

### 隐私数据保存在哪里？

隐私数据保存在 `plugins/LiveRecorder/privacy.db` SQLite 数据库文件中。

### 如何清理旧的直播日志？

插件会自动保留最近 100 条日志，超出限制的会在下次启动时自动清理。