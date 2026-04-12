# 镜头设置

镜头设置控制录制者相对于目标玩家的位置和跟随行为。

## 配置项

```yaml
camera:
  pitch: 30.0              # 俯角（度数）
  distance: 5.0            # 镜头与目标的距离（格）
  height-offset: 0.0       # 高度偏移（格）
  follow-speed: 0.35       # 跟随速度系数
  arrival-threshold: 0.3   # 到达阈值（格）
```

## 参数详解

### pitch（俯角）

镜头的俯仰角度，控制镜头的高低位置。

- **范围：** 0° - 90°
- **0°** - 水平视角
- **30°** - 标准俯角（推荐）
- **60°** - 鸟瞰视角
- **90°** - 正上方视角

**调整建议：**
- 想要更广阔的视野：增大 pitch（如 45°-60°）
- 想要更贴近的视角：减小 pitch（如 15°-25°）

### distance（距离）

镜头与目标玩家的水平距离。

- **范围：** 2.0 - 15.0（推荐）
- **单位：** 格（blocks）
- **默认值：** 5.0

**调整建议：**
- 想要更远的视角：增大 distance（如 8.0-10.0）
- 想要更近的特写：减小 distance（如 3.0-4.0）

### height-offset（高度偏移）

镜头的额外高度偏移，在俯角计算之外额外增加的高度。

- **范围：** -2.0 - 2.0
- **单位：** 格（blocks）
- **默认值：** 0.0

**调整建议：**
- 想要更高的视角：增大 height-offset（如 1.0-2.0）
- 想要更低的视角：减小 height-offset（如 -1.0--2.0）

### follow-speed（跟随速度）

录制者跟随目标的速度系数。

- **范围：** 0.1 - 1.0
- **默认值：** 0.35
- **越大越快，越小越平滑**

**调整建议：**
- 目标移动很快：增大 follow-speed（如 0.5-0.7）
- 想要更平滑的跟随：减小 follow-speed（如 0.2-0.3）

### arrival-threshold（到达阈值）

录制者到达镜头位置的判断阈值。

- **范围：** 0.1 - 1.0
- **单位：** 格（blocks）
- **默认值：** 0.3

**调整建议：**
- 一般不需要调整
- 如果录制者抖动：增大 arrival-threshold（如 0.5）

## 镜头计算公式

```
镜头位置 = 目标玩家位置 + 偏移量

dx = -distance × sin(yaw)                    ← 水平X偏移（身后）
dz =  distance × cos(yaw)                    ← 水平Z偏移
dy =  distance × tan(pitch) + heightOffset   ← 垂直偏移
```

## 预设配置

### 鸟瞰视角

```yaml
camera:
  pitch: 60.0
  distance: 8.0
  height-offset: 0.0
  follow-speed: 0.35
```

适合：综艺节目、活动跟拍

### 标准跟拍

```yaml
camera:
  pitch: 30.0
  distance: 5.0
  height-offset: 0.0
  follow-speed: 0.35
```

适合：日常直播、游戏跟拍

### 平视特写

```yaml
camera:
  pitch: 10.0
  distance: 3.0
  height-offset: 0.0
  follow-speed: 0.40
```

适合：近景特写、对话场景

### 快速跟随

```yaml
camera:
  pitch: 25.0
  distance: 4.0
  height-offset: 0.0
  follow-speed: 0.50
```

适合：PVP、快速移动的场景

### 慢速平滑

```yaml
camera:
  pitch: 35.0
  distance: 6.0
  height-offset: 0.0
  follow-speed: 0.20
```

适合：悠闲活动、建筑展示

## 常见问题

### 镜头位置不对怎么办？

检查以下配置：
1. `pitch` - 俯角是否合适
2. `distance` - 距离是否合适
3. `height-offset` - 高度偏移是否合适

### 录制者跟不上目标怎么办？

增大 `follow-speed` 值，或减小 `distance` 让镜头更近。

### 录制者画面出现瞬移怎么办？

减小 `follow-speed` 值，或增大 `distance` 让镜头更远。

### 如何让镜头更高？

增大 `pitch` 或 `height-offset` 值。