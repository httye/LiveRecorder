# CameraGeometry 类

镜头几何计算器，使用高精度数学计算镜头位置，并提供平滑插值功能。

## 类概述

```java
package com.liverecorder.util;

public class CameraGeometry {
    private final LiveRecorder plugin;
    private BigDecimal cameraPitch;
    private BigDecimal cameraDistance;
    private BigDecimal heightOffset;
    private double positionSmooth;
    private double rotationSmooth;
}
```

## 获取实例

```java
import com.liverecorder.LiveRecorder;

LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
CameraGeometry geometry = lr.getCameraGeometry();
```

## 核心方法

### calculateCameraLocation()

计算镜头目标位置。

```java
public Location calculateCameraLocation(Player target)
```

**示例：**
```java
Player target = Bukkit.getPlayer("Steve");
Location cameraLoc = geometry.calculateCameraLocation(target);

// cameraLoc 是录制者应该到达的目标位置
recorder.teleport(cameraLoc);
```

**计算公式：**
```
镜头位置 = 目标玩家位置 + 偏移量

dx = -distance × sin(yaw)                    ← 水平X偏移（身后）
dz =  distance × cos(yaw)                    ← 水平Z偏移
dy =  distance × tan(pitch) + heightOffset   ← 垂直偏移
```

### calculateSmoothedState()

计算平滑后的镜头状态（位置 + 视角），使用指数衰减平滑算法实现流畅的镜头移动和视角旋转。

```java
public Location calculateSmoothedState(Location current, Location cameraTarget, Player target)
```

**参数：**
- `current` - 录制者当前位置
- `cameraTarget` - 镜头目标位置（由 `calculateCameraLocation` 计算）
- `target` - 目标玩家（用于计算精确的视角方向）

**示例：**
```java
Player recorder = Bukkit.getPlayer("CameraMan");
Player target = Bukkit.getPlayer("Steve");
Location cameraTarget = geometry.calculateCameraLocation(target);

// 计算平滑后的位置和视角
Location smoothed = geometry.calculateSmoothedState(
        recorder.getLocation(), cameraTarget, target);

// 传送到平滑后的位置
recorder.teleport(smoothed);
```

**平滑算法：**
```
位置平滑：newPos = currentPos + (targetPos - currentPos) × positionSmooth
视角平滑：newAngle = currentAngle + shortestPathDiff × rotationSmooth
```

### needsTeleport()

检查是否需要传送。

```java
public boolean needsTeleport(Player recorder, Location cameraTarget, double maxDistance)
```

**示例：**
```java
Player recorder = Bukkit.getPlayer("CameraMan");
Location cameraTarget = geometry.calculateCameraLocation(target);

if (geometry.needsTeleport(recorder, cameraTarget, 30.0)) {
    recorder.teleport(cameraTarget);
}
```

## 配置方法

### reload()

重载配置。

```java
public void reload()
```

**示例：**
```java
geometry.reload();
```

## Getter 方法

### getCameraPitch()

获取俯角（度数）。

```java
public double getCameraPitch()
```

### getCameraDistance()

获取镜头距离（格）。

```java
public double getCameraDistance()
```

### getPositionSmooth()

获取位置平滑系数。

```java
public double getPositionSmooth()
```

### getRotationSmooth()

获取视角平滑系数。

```java
public double getRotationSmooth()
```

## 内部方法

### interpolateAngle()

角度平滑插值，处理 360° 环绕，始终选择最短旋转路径。

```java
private float interpolateAngle(float current, float target, float smooth)
```

### toRadians()

将角度转换为弧度。

```java
private BigDecimal toRadians(BigDecimal degrees)
```

### sin()

计算正弦值（泰勒级数展开）。

```java
private BigDecimal sin(BigDecimal x)
```

### cos()

计算余弦值（泰勒级数展开）。

```java
private BigDecimal cos(BigDecimal x)
```

### tan()

计算正切值。

```java
private BigDecimal tan(BigDecimal x)
```

### sqrt()

计算平方根（牛顿迭代法）。

```java
private BigDecimal sqrt(BigDecimal x)
```

## 使用示例

### 完整的平滑跟随流程

```java
public void updateFollower(Player recorder, Player target) {
    CameraGeometry geometry = plugin.getCameraGeometry();
    
    // 1. 计算镜头目标位置
    Location cameraTarget = geometry.calculateCameraLocation(target);
    
    // 2. 检查是否需要传送（距离过远）
    if (geometry.needsTeleport(recorder, cameraTarget, 30.0)) {
        recorder.teleport(cameraTarget);
        return;
    }
    
    // 3. 计算平滑后的位置和视角
    Location smoothed = geometry.calculateSmoothedState(
            recorder.getLocation(), cameraTarget, target);
    
    // 4. 传送到平滑后的位置
    recorder.teleport(smoothed);
}
```

### 自定义镜头位置

```java
public Location customCameraLocation(Player target, double pitch, double distance, double heightOffset) {
    Location targetLoc = target.getLocation();
    BigDecimal yawRad = geometry.toRadians(new BigDecimal(String.valueOf(targetLoc.getYaw())));
    
    // 计算水平偏移
    BigDecimal dx = new BigDecimal(String.valueOf(-distance)).multiply(geometry.sin(yawRad), MC);
    BigDecimal dz = new BigDecimal(String.valueOf(distance)).multiply(geometry.cos(yawRad), MC);
    
    // 计算垂直偏移
    BigDecimal pitchRad = geometry.toRadians(new BigDecimal(String.valueOf(pitch)));
    BigDecimal dy = new BigDecimal(String.valueOf(distance))
        .multiply(geometry.tan(pitchRad), MC)
        .add(new BigDecimal(String.valueOf(heightOffset)), MC);
    
    Location cameraLoc = targetLoc.clone();
    cameraLoc.add(dx.doubleValue(), dy.doubleValue(), dz.doubleValue());
    cameraLoc.setDirection(targetLoc.toVector().subtract(cameraLoc.toVector()));
    
    return cameraLoc;
}
```

### 计算镜头角度

```java
public double calculateCameraAngle(Player target, Location cameraLoc) {
    Vector direction = target.getLocation().toVector().subtract(cameraLoc.toVector());
    float yaw = (float) Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90.0f;
    float pitch = (float) -Math.toDegrees(Math.atan2(direction.getY(), Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ())));
    
    return Math.sqrt(yaw * yaw + pitch * pitch);
}
```

## 注意事项

### 高精度计算

CameraGeometry 使用 BigDecimal 进行高精度计算，确保镜头位置准确。

### MathContext

内部使用 MathContext(12, RoundingMode.HALF_UP) 确保精度。

### 平滑系数调优

- `position-smooth` 控制位置移动的平滑度，值越小越平滑（推荐 0.06-0.35）
- `rotation-smooth` 控制视角旋转的平滑度，值越小越平滑（推荐 0.04-0.25）
- 两个系数独立控制，可以根据场景分别调优

### 性能考虑

镜头计算相对复杂，避免在循环中频繁调用。缓存结果以提高性能。

### 线程安全

CameraGeometry 的方法是线程安全的，可以在任何线程中调用。

## 数学原理

### 指数衰减平滑

位置和视角使用指数衰减平滑（Exponential Smoothing）：

```
smoothedValue = currentValue + (targetValue - currentValue) × smoothFactor
```

- smoothFactor 越小，平滑效果越强，响应越慢
- smoothFactor 越大，平滑效果越弱，响应越快
- 当 smoothFactor = 1.0 时，无平滑效果，直接跳到目标值

### 角度插值

视角旋转使用最短路径角度插值，自动处理 360°/0° 边界：

```
diff = targetAngle - currentAngle
// 归一化到 [-180, 180]
while (diff > 180) diff -= 360
while (diff < -180) diff += 360
result = currentAngle + diff × smoothFactor
```

### 泰勒级数展开

三角函数使用泰勒级数展开计算：

```
sin(x) = x - x³/3! + x⁵/5! - x⁷/7! + ...
cos(x) = 1 - x²/2! + x⁴/4! - x⁶/6! + ...
```

### 牛顿迭代法

平方根使用牛顿迭代法计算：

```
xₙ₊₁ = (xₙ + a/xₙ) / 2
```

### 坐标变换

使用标准的 3D 坐标变换公式：

```
x' = x × cos(θ) - y × sin(θ)
y' = x × sin(θ) + y × cos(θ)