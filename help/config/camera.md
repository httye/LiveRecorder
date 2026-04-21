# 镜头设置

镜头设置控制录制者相对于目标玩家的位置和跟随行为。

## 配置项

```yaml
camera:
  pitch: 30.0              # 俯角（度数）
  distance: 5.0            # 镜头与目标的距离（格）
  height-offset: 0.0       # 高度偏移（格）
  position-smooth: 0.12    # 位置平滑系数
  rotation-smooth: 0.1     # 视角平滑系数
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

### position-smooth（位置平滑系数）

控制镜头位置移动的平滑程度，使用指数衰减平滑算法。

- **范围：** 0.01 - 1.0
- **默认值：** 0.12
- **值越小越平滑，值越大跟随越紧密**

**调整建议：**
- 想要丝滑的电影感镜头：减小 position-smooth（如 0.06-0.10）
- 想要紧密跟随快速目标：增大 position-smooth（如 0.20-0.35）
- 目标移动很快导致镜头跟不上：增大 position-smooth（如 0.25-0.40）

### rotation-smooth（视角平滑系数）

控制镜头视角旋转的平滑程度，使用角度插值算法，自动选择最短旋转路径。

- **范围：** 0.01 - 1.0
- **默认值：** 0.1
- **值越小旋转越平滑，值越大旋转越灵敏**

**调整建议：**
- 想要柔和的视角过渡：减小 rotation-smooth（如 0.04-0.08）
- 想要灵敏的视角追踪：增大 rotation-smooth（如 0.15-0.25）
- 目标频繁转向导致视角晃动：减小 rotation-smooth（如 0.05-0.08）

## 镜头计算公式

```
镜头目标位置 = 目标玩家位置 + 偏移量

dx = -distance × sin(yaw)                    ← 水平X偏移（身后）
dz =  distance × cos(yaw)                    ← 水平Z偏移
dy =  distance × tan(pitch) + heightOffset   ← 垂直偏移

平滑位置 = 当前位置 + (目标位置 - 当前位置) × positionSmooth
平滑视角 = 当前视角 + 最短路径角度差 × rotationSmooth
```

## 预设配置

### 鸟瞰视角

```yaml
camera:
  pitch: 60.0
  distance: 8.0
  height-offset: 0.0
  position-smooth: 0.12
  rotation-smooth: 0.1
```

适合：综艺节目、活动跟拍

### 标准跟拍

```yaml
camera:
  pitch: 30.0
  distance: 5.0
  height-offset: 0.0
  position-smooth: 0.12
  rotation-smooth: 0.1
```

适合：日常直播、游戏跟拍

### 平视特写

```yaml
camera:
  pitch: 10.0
  distance: 3.0
  height-offset: 0.0
  position-smooth: 0.15
  rotation-smooth: 0.12
```

适合：近景特写、对话场景

### 快速跟随

```yaml
camera:
  pitch: 25.0
  distance: 4.0
  height-offset: 0.0
  position-smooth: 0.30
  rotation-smooth: 0.20
```

适合：PVP、快速移动的场景

### 电影平滑

```yaml
camera:
  pitch: 35.0
  distance: 6.0
  height-offset: 0.0
  position-smooth: 0.06
  rotation-smooth: 0.05
```

适合：悠闲活动、建筑展示、电影感录制

## 常见问题

### 镜头位置不对怎么办？

检查以下配置：
1. `pitch` - 俯角是否合适
2. `distance` - 距离是否合适
3. `height-offset` - 高度偏移是否合适

### 录制者跟不上目标怎么办？

增大 `position-smooth` 值，或减小 `distance` 让镜头更近。

### 镜头移动太僵硬怎么办？

减小 `position-smooth` 值让位置移动更平滑，减小 `rotation-smooth` 值让视角旋转更柔和。

### 视角旋转时出现突然翻转怎么办？

这通常是因为视角跨越了 360°/0° 边界。系统已自动处理最短路径插值，如果仍有问题，尝试减小 `rotation-smooth` 值。

### 如何让镜头更高？

增大 `pitch` 或 `height-offset` 值。