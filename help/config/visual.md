# 视觉反馈

视觉反馈设置控制直播过程中的各种视觉效果。

## 配置项

```yaml
visual:
  target-glow: true        # 目标玩家发光效果
  glow-color: YELLOW       # 发光颜色
  camera-particle: false   # 镜头位置粒子
  particle-type: END_ROD   # 粒子类型
  actionbar-enabled: true  # ActionBar 状态显示
  actionbar-interval: 20   # ActionBar 刷新间隔
  target-actionbar: true   # 目标玩家 ActionBar 提示
```

## 参数详解

### target-glow（目标发光）

是否为被直播的目标玩家添加发光效果。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**效果：**
- `true` - 目标玩家发光，容易被识别
- `false` - 目标玩家不发光

### glow-color（发光颜色）

目标玩家发光效果的颜色。

- **类型：** string
- **默认值：** YELLOW
- **选项：**
  - `WHITE` - 白色
  - `ORANGE` - 橙色
  - `MAGENTA` - 洋红色
  - `LIGHT_BLUE` - 浅蓝色
  - `YELLOW` - 黄色（默认）
  - `LIME` - 青绿色
  - `PINK` - 粉色
  - `GRAY` - 灰色
  - `CYAN` - 青色
  - `PURPLE` - 紫色
  - `BLUE` - 蓝色
  - `GREEN` - 绿色
  - `RED` - 红色

**颜色建议：**
- 高对比度：YELLOW、RED、LIME
- 柔和色调：LIGHT_BLUE、PINK、CYAN
- 深色调：PURPLE、BLUE、GREEN

### camera-particle（镜头粒子）

是否在镜头位置显示粒子效果。

- **类型：** boolean
- **默认值：** false
- **选项：** true / false

**效果：**
- `true` - 镜头位置显示粒子，便于识别
- `false` - 镜头位置不显示粒子

**注意：** 粒子效果可能影响性能，建议仅在需要时启用。

### particle-type（粒子类型）

镜头粒子的类型。

- **类型：** string
- **默认值：** END_ROD
- **选项：**
  - `END_ROD` - 末地烛（默认）
  - `FLAME` - 火焰
  - `SMOKE` - 烟雾
  - `HEART` - 爱心
  - `VILLAGER_HAPPY` - 快乐村民
  - `NOTE` - 音符
  - `PORTAL` - 传送门
  - `DRAGON_BREATH` - 龙息

### actionbar-enabled（ ActionBar 显示）

是否在录制者的 ActionBar 显示状态信息。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**效果：**
- `true` - 录制者 ActionBar 显示跟随状态
- `false` - 录制者 ActionBar 不显示状态

**显示内容：**
```
LiveRecorder | ● 跟随中 | 目标: Steve | 模式: 自动
```

### actionbar-interval（刷新间隔）

ActionBar 状态信息的刷新间隔。

- **类型：** int
- **单位：** tick（1 tick = 0.05 秒）
- **默认值：** 20
- **范围：** 5 - 100

**调整建议：**
- 更流畅：减小 interval（如 10）
- 更省性能：增大 interval（如 40）

### target-actionbar（目标提示）

是否在被直播的目标玩家 ActionBar 显示提示。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**效果：**
- `true` - 目标玩家 ActionBar 显示"正在被直播"提示
- `false` - 目标玩家 ActionBar 不显示提示

**显示内容：**
```
🔴 您正在被直播 | 1 位录制者跟拍中
```

## 预设配置

### 标准配置（推荐）

```yaml
visual:
  target-glow: true
  glow-color: YELLOW
  camera-particle: false
  particle-type: END_ROD
  actionbar-enabled: true
  actionbar-interval: 20
  target-actionbar: true
```

### 简约配置

```yaml
visual:
  target-glow: true
  glow-color: YELLOW
  camera-particle: false
  particle-type: END_ROD
  actionbar-enabled: false
  actionbar-interval: 20
  target-actionbar: false
```

关闭 ActionBar，只保留发光效果。

### 炫酷配置

```yaml
visual:
  target-glow: true
  glow-color: RED
  camera-particle: true
  particle-type: FLAME
  actionbar-enabled: true
  actionbar-interval: 10
  target-actionbar: true
```

启用粒子和更快的刷新，视觉效果更炫酷。

## 常见问题

### 目标玩家不发光？

检查以下配置：
1. `target-glow` 是否为 `true`
2. 目标玩家是否真的被跟拍
3. 渲染距离是否足够

### ActionBar 不显示？

检查以下配置：
1. `actionbar-enabled` 是否为 `true`
2. 录制者是否真的在跟随
3. 游戏版本是否支持 ActionBar（1.8+）

### 粒子效果影响性能？

如果觉得粒子影响性能，将 `camera-particle` 设置为 `false`。

### 如何更改发光颜色？

修改 `glow-color` 值，选择一个合适的颜色。