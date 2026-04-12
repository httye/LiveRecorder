# 自动轮换跟拍

录制者自动在不同玩家之间切换（综艺节目风格）。

## 场景描述

- **适用模式：** AUTO
- **录制者数量：** 1
- **目标数量：** 2-10
- **推荐配置：** 快速切换 + 随机模式

## 操作步骤

### 1. 准备录制者账号

创建一个专用录制者账号（如 `CameraMan`）：

```bash
# 设置为旁观者模式
/gamemode spectator CameraMan
```

### 2. 配置自动切换

编辑 `config.yml`：

```yaml
auto-switch:
  enabled: true           # 启用自动切换
  interval: 15            # 切换间隔（秒）
  mode: RANDOM            # 随机模式
```

### 3. 绑定录制者

```bash
/lr bind CameraMan 玩家A auto
```

### 4. 开始录制

录制者会自动在不同玩家间切换，无需人工干预。

## 效果预览

### 录制者视角
```
LiveRecorder | ● 跟随中 | 目标: 玩家A | 模式: 自动
[15秒后切换到玩家B]
LiveRecorder | ● 跟随中 | 目标: 玩家B | 模式: 自动
[15秒后切换到玩家C]
LiveRecorder | ● 跟随中 | 目标: 玩家C | 模式: 自动
```

### 目标视角
```
玩家A: 🔴 您正在被直播 | 1 位录制者跟拍中
玩家B: （无提示，等待切换）
玩家C: （无提示，等待切换）
```

## 切换模式

### RANDOM（随机模式）

录制者随机选择下一个目标。

**特点：**
- 不可预测性
- 更有趣味性
- 每个目标被选中的概率相同

**配置：**
```yaml
auto-switch:
  enabled: true
  interval: 15
  mode: RANDOM
```

**适合场景：**
- 综艺节目
- 快节奏活动
- 需要惊喜的场景

### SEQUENTIAL（顺序模式）

录制者按顺序轮流选择目标。

**特点：**
- 可预测性
- 每个目标轮流出现
- 公平公正

**配置：**
```yaml
auto-switch:
  enabled: true
  interval: 20
  mode: SEQUENTIAL
```

**适合场景：**
- 多人活动展示
- 公平竞赛
- 需要轮流展示的场景

## 推荐配置

### 快速随机切换（综艺风格）

```yaml
auto-switch:
  enabled: true
  interval: 15
  mode: RANDOM

camera:
  pitch: 30.0
  distance: 5.0
  follow-speed: 0.35

visual:
  target-glow: true
  glow-color: YELLOW
  actionbar-enabled: true
  target-actionbar: true
```

**特点：**
- 15 秒快速切换
- 随机选择目标
- 营造紧张刺激氛围

### 标准随机切换（日常直播）

```yaml
auto-switch:
  enabled: true
  interval: 30
  mode: RANDOM

camera:
  pitch: 30.0
  distance: 5.0
  follow-speed: 0.35
```

**特点：**
- 30 秒标准切换
- 随机选择目标
- 适合日常直播

### 顺序轮播（多人活动）

```yaml
auto-switch:
  enabled: true
  interval: 45
  mode: SEQUENTIAL

camera:
  pitch: 45.0
  distance: 8.0
  follow-speed: 0.35
```

**特点：**
- 45 秒切换
- 顺序轮流展示
- 公平公正

## 注意事项

### 在线玩家数量

自动切换需要至少 2 个在线玩家才能正常工作。如果只有 1 个在线玩家，自动切换会暂停。

### 目标玩家离线

如果当前目标玩家离线，录制者会立即切换到下一个在线目标。

### 手动切换

即使启用了自动切换，管理员也可以手动切换目标：

```bash
/lr switch CameraMan 玩家A
```

手动切换后，自动切换的计时器会重置。

### 隐私设置

自动切换会遵守玩家的隐私设置。如果玩家拒绝被直播，录制者不会切换到该玩家。

## 常见问题

### 自动切换不工作？

检查以下配置：
1. `auto-switch.enabled` 是否为 `true`
2. 录制者是否为 AUTO 模式
3. 是否有至少 2 个在线玩家

### 如何调整切换速度？

修改 `auto-switch.interval` 值：
- 更快：减小 interval（如 10）
- 更慢：增大 interval（如 60）

### 如何让录制者不自动切换？

将 `auto-switch.enabled` 设置为 `false`，或将录制者模式改为 MANUAL：

```bash
/lr mode CameraMan manual
```

### RANDOM 和 SEQUENTIAL 有什么区别？

- **RANDOM** - 随机选择，不可预测
- **SEQUENTIAL** - 按顺序轮流，可预测

## 最佳实践

### 切换间隔

根据活动类型选择合适的切换间隔：

- **综艺节目：** 10-20 秒
- **日常直播：** 30-60 秒
- **慢节奏活动：** 90-180 秒

### 切换模式

根据需求选择合适的切换模式：

- **需要惊喜：** 使用 RANDOM
- **需要公平：** 使用 SEQUENTIAL

### 镜头配置

使用标准镜头配置，适合大多数场景：

```yaml
camera:
  pitch: 30.0
  distance: 5.0
  follow-speed: 0.35
```

## 示例脚本

### 快速开始自动轮换

```bash
#!/bin/bash
# 快速开始自动轮换跟拍

RECORDER="CameraMan"
FIRST_TARGET="玩家A"

# 配置自动切换（手动编辑 config.yml）
# auto-switch:
#   enabled: true
#   interval: 15
#   mode: RANDOM

# 重载配置
/lr reload

# 绑定录制者
/lr bind $RECORDER $FIRST_TARGET auto

# 查看绑定状态
/lr list
```

### 停止自动轮换

```bash
#!/bin/bash
# 停止自动轮换跟拍

# 方法一：禁用自动切换
# （手动编辑 config.yml）
# auto-switch:
#   enabled: false

# 重载配置
/lr reload

# 方法二：切换为手动模式
/lr mode CameraMan manual

# 查看绑定状态
/lr list
```