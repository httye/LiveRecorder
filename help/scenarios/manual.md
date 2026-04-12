# 手动控制跟拍

导播手动决定录制者跟拍谁。

## 场景描述

- **适用模式：** MANUAL
- **录制者数量：** 1-3
- **目标数量：** 不限
- **推荐配置：** 精细控制

## 操作步骤

### 1. 准备录制者账号

创建专用录制者账号（如 `CameraMan`）：

```bash
# 设置为旁观者模式
/gamemode spectator CameraMan
```

### 2. 绑定录制者为手动模式

```bash
/lr bind CameraMan 玩家A manual
```

### 3. 手动切换目标

使用命令手动切换目标：

```bash
# 切换到玩家B
/lr switch CameraMan 玩家B

# 切换到玩家C
/lr switch CameraMan 玩家C

# 切换回来
/lr switch CameraMan 玩家A
```

## 效果预览

### 录制者视角
```
LiveRecorder | ● 跟随中 | 目标: 玩家A | 模式: 手动
[导播手动切换]
LiveRecorder | ● 跟随中 | 目标: 玩家B | 模式: 手动
```

### 目标视角
```
玩家A: 🔴 您正在被直播 | 1 位录制者跟拍中
玩家B: 🔴 您正在被直播 | 1 位录制者跟拍中
```

## 操作技巧

### 快速切换

使用 Tab 补全快速输入玩家名称：

```bash
/lr switch CameraMan Pla[TAB]  → 自动补全为玩家名称
```

### 创建快捷命令

为常用目标创建快捷命令：

```bash
# 切换到主播
/switch主播  → /lr switch CameraMan 主播小明

# 切换到嘉宾
/switch嘉宾  → /lr switch CameraMan 嘉宾A
```

需要插件支持快捷命令或别名功能。

### 使用多录制者

绑定多个录制者，每个录制者跟拍不同目标：

```bash
/lr bind Camera01 玩家A manual
/lr bind Camera02 玩家B manual
/lr bind Camera03 玩家C manual
```

导播可以在不同录制者间切换画面。

## 推荐配置

### 标准手动控制

```yaml
auto-switch:
  enabled: false          # 禁用自动切换

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
- 完全手动控制
- 不会自动切换
- 适合精细控制

### 快速手动控制

```yaml
auto-switch:
  enabled: false

camera:
  pitch: 25.0
  distance: 4.0
  follow-speed: 0.50    # 更快的跟随速度
```

**特点：**
- 快速跟随
- 适合快速切换
- 适合活动直播

### 慢速手动控制

```yaml
auto-switch:
  enabled: false

camera:
  pitch: 35.0
  distance: 6.0
  follow-speed: 0.20    # 更慢的跟随速度
```

**特点：**
- 平滑跟随
- 适合慢节奏活动
- 适合建筑展示

## 常见问题

### 如何切换为手动模式？

绑定时指定模式：

```bash
/lr bind CameraMan 玩家A manual
```

或修改现有绑定：

```bash
/lr mode CameraMan manual
```

### 手动模式下会自动切换吗？

不会，手动模式下不会自动切换，完全由导播控制。

### 如何查看当前目标？

使用 `/lr list` 命令查看所有绑定的详细信息。

### 如何快速切换目标？

使用 Tab 补全快速输入玩家名称，或创建快捷命令。

## 最佳实践

### 导播操作

1. **熟悉玩家列表：** 提前了解所有在线玩家
2. **使用 Tab 补全：** 快速输入玩家名称
3. **创建快捷命令：** 为常用目标创建快捷方式
4. **查看绑定状态：** 定期使用 `/lr list` 确认状态

### 镜头配置

1. **标准配置：** 适合大多数场景
2. **快速配置：** 适合快速切换
3. **慢速配置：** 适合慢节奏活动

### 应急处理

1. **备用录制者：** 准备备用录制者账号
2. **管理员权限：** 确保有管理员权限可以随时操作
3. **备用命令：** 准备备用命令或脚本

## 示例脚本

### 快速切换脚本

```bash
#!/bin/bash
# 快速切换录制者目标

RECORDER="CameraMan"

# 切换到目标A
/lr switch $RECORDER 玩家A

# 等待 5 秒
sleep 5

# 切换到目标B
/lr switch $RECORDER 玩家B

# 等待 5 秒
sleep 5

# 切换到目标C
/lr switch $RECORDER 玩家C
```

### 循环切换脚本

```bash
#!/bin/bash
# 循环切换录制者目标

RECORDER="CameraMan"
TARGETS=("玩家A" "玩家B" "玩家C")

while true; do
  for target in "${TARGETS[@]}"; do
    /lr switch $RECORDER $target
    sleep 30
  done
done
```

## 注意事项

### 隐私设置

手动切换也会遵守玩家的隐私设置。如果玩家拒绝被直播，录制者无法切换到该玩家。

### 在线状态

只能切换到在线玩家。如果目标离线，切换会失败。

### 网络延迟

切换时会有短暂的网络延迟，录制者会传送到新目标的位置。