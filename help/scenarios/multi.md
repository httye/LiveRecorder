# 多机位直播

多个录制者从不同角度跟拍同一目标。

## 场景描述

- **适用模式：** AUTO 或 MANUAL
- **录制者数量：** 2-5
- **目标数量：** 1
- **推荐配置：** 多种镜头参数组合

## 操作步骤

### 1. 准备录制者账号

创建多个专用录制者账号（如 `Camera01`、`Camera02`、`Camera03`）：

```bash
# 设置为旁观者模式
/gamemode spectator Camera01
/gamemode spectator Camera02
/gamemode spectator Camera03
```

### 2. 为每个录制者设置不同的镜头参数

**方法一：修改配置文件后逐个绑定**

```yaml
# 先绑定第一个录制者（使用默认配置）
/lr bind Camera01 Steve auto

# 修改 config.yml，调整镜头参数
camera:
  pitch: 45.0
  distance: 8.0

# 重载配置
/lr reload

# 绑定第二个录制者（使用新配置）
/lr bind Camera02 Steve auto
```

**方法二：使用不同配置文件**

需要插件支持多个配置文件，目前 LiveRecorder 不支持此功能。

### 3. 绑定所有录制者

```bash
/lr bind Camera01 Steve auto
/lr bind Camera02 Steve auto
/lr bind Camera03 Steve auto
```

### 4. 开始录制

所有录制者会自动跟随目标，导播可在不同录制者间切换画面。

## 效果预览

### 录制者视角（多个）
```
Camera01: LiveRecorder | ● 跟随中 | 目标: Steve | 模式: 自动
Camera02: LiveRecorder | ● 跟随中 | 目标: Steve | 模式: 自动
Camera03: LiveRecorder | ● 跟随中 | 目标: Steve | 模式: 自动
```

### 目标视角
```
ActionBar: 🔴 您正在被直播 | 3 位录制者跟拍中
发光效果: 红色发光
```

## 镜头参数组合

### 标准跟拍机位

```yaml
camera:
  pitch: 30.0
  distance: 5.0
  follow-speed: 0.35
```

**用途：** 主要跟拍视角，展示玩家正面。

### 鸟瞰机位

```yaml
camera:
  pitch: 60.0
  distance: 8.0
  follow-speed: 0.35
```

**用途：** 俯视视角，展示周围环境。

### 侧面机位

```yaml
camera:
  pitch: 20.0
  distance: 4.0
  follow-speed: 0.40
```

**用途：** 侧面视角，展示动作细节。

### 远景机位

```yaml
camera:
  pitch: 45.0
  distance: 12.0
  follow-speed: 0.30
```

**用途：** 远景视角，展示大范围场景。

### 特写机位

```yaml
camera:
  pitch: 10.0
  distance: 3.0
  follow-speed: 0.45
```

**用途：** 近景特写，展示面部细节。

## 推荐配置

### 标准三机位配置

```yaml
# Camera01 - 标准跟拍
camera:
  pitch: 30.0
  distance: 5.0
  follow-speed: 0.35

# Camera02 - 鸟瞰视角
camera:
  pitch: 60.0
  distance: 8.0
  follow-speed: 0.35

# Camera03 - 侧面视角
camera:
  pitch: 20.0
  distance: 4.0
  follow-speed: 0.40
```

### 五机位配置

```yaml
# Camera01 - 标准跟拍
camera:
  pitch: 30.0
  distance: 5.0
  follow-speed: 0.35

# Camera02 - 鸟瞰视角
camera:
  pitch: 60.0
  distance: 8.0
  follow-speed: 0.35

# Camera03 - 侧面视角
camera:
  pitch: 20.0
  distance: 4.0
  follow-speed: 0.40

# Camera04 - 远景视角
camera:
  pitch: 45.0
  distance: 12.0
  follow-speed: 0.30

# Camera05 - 特写视角
camera:
  pitch: 10.0
  distance: 3.0
  follow-speed: 0.45
```

## 导播操作

### 切换机位

导播需要在不同录制者账号间切换：

1. **使用 OBS 等直播软件：**
   - 添加多个视频源
   - 每个视频源对应一个录制者账号
   - 使用快捷键快速切换

2. **使用游戏内切换：**
   - 管理员可以使用 `/tp` 命令在不同录制者间切换
   - 需要录制者支持旁观模式

### 机位选择

根据场景选择合适的机位：

- **对话场景：** 标准跟拍 + 特写
- **战斗场景：** 标准跟拍 + 侧面视角
- **探索场景：** 鸟瞰视角 + 远景视角
- **建筑场景：** 鸟瞰视角 + 标准跟拍

## 常见问题

### 多个录制者会互相干扰吗？

不会，录制者之间是独立的，互不影响。

### 如何为每个录制者设置不同的镜头参数？

目前 LiveRecorder 所有录制者使用相同的配置。如需不同参数，需要：
1. 手动修改配置文件
2. 使用 `/lr reload` 重载配置
3. 逐个绑定录制者

### 录制者太多会影响性能吗？

录制者数量过多会影响性能，建议：
- 最多 5 个录制者
- 使用性能较好的服务器
- 关闭不必要的视觉效果

### 如何同步多个录制者的跟随？

所有录制者会自动跟随目标，无需手动同步。如果发现不同步，检查：
1. 所有录制者是否都绑定到同一目标
2. 网络延迟是否正常
3. 服务器性能是否足够

## 最佳实践

### 录制者设置

1. 使用专用账号，数量不超过 5 个
2. 设置为旁观者模式
3. 给每个录制者账号单独的权限节点

### 镜头配置

1. 测试不同配置，找到最佳组合
2. 为每个机位准备预设配置
3. 使用热重载快速切换配置

### 导播技巧

1. 熟悉每个机位的特点
2. 根据场景选择合适的机位
3. 避免频繁切换，保持画面稳定

## 示例脚本

### 快速多机位绑定脚本

```bash
#!/bin/bash
# 快速绑定多个录制者到同一目标

TARGET="Steve"

# 绑定第一个录制者
/lr bind Camera01 $TARGET auto

# 修改配置
# （手动编辑 config.yml）

# 重载配置
/lr reload

# 绑定第二个录制者
/lr bind Camera02 $TARGET auto

# 查看所有绑定
/lr list
```

### 自动解绑所有录制者

```bash
#!/bin/bash
# 活动结束后自动解绑所有录制者

# 逐个解绑
/lr unbind Camera01
/lr unbind Camera02
/lr unbind Camera03
/lr unbind Camera04
/lr unbind Camera05

# 确认解绑
/lr list
```