# CameraGeometry 类

镜头几何计算器，使用高精度数学计算镜头位置。

## 类概述

```java
package com.liverecorder.util;

public class CameraGeometry {
    private final LiveRecorder plugin;
    private BigDecimal cameraPitch;
    private BigDecimal cameraDistance;
    private BigDecimal heightOffset;
    private BigDecimal followSpeed;
    private BigDecimal arrivalThreshold;
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

计算镜头位置。

```java
public Location calculateCameraLocation(Player target)
```

**示例：**
```java
Player target = Bukkit.getPlayer("Steve");
Location cameraLoc = geometry.calculateCameraLocation(target);

// cameraLoc 是录制者应该到达的位置
recorder.teleport(cameraLoc);
```

**计算公式：**
```
镜头位置 = 目标玩家位置 + 偏移量

dx = -distance × sin(yaw)                    ← 水平X偏移（身后）
dz =  distance × cos(yaw)                    ← 水平Z偏移
dy =  distance × tan(pitch) + heightOffset   ← 垂直偏移
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

### calculateFollowVelocity()

计算跟随速度。

```java
public Location calculateFollowVelocity(Player recorder, Location cameraTarget)
```

**示例：**
```java
Player recorder = Bukkit.getPlayer("CameraMan");
Location cameraTarget = geometry.calculateCameraLocation(target);
Location followResult = geometry.calculateFollowVelocity(recorder, cameraTarget);

if (followResult != null) {
    // 计算速度向量
    double vx = followResult.getX() - recorder.getLocation().getX();
    double vy = followResult.getY() - recorder.getLocation().getY();
    double vz = followResult.getZ() - recorder.getLocation().getZ();
    
    recorder.setVelocity(new Vector(vx, vy, vz));
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

## 内部方法

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

### 检查跟随状态

```java
public void checkFollowStatus(Player recorder, Player target) {
    Location cameraTarget = geometry.calculateCameraLocation(target);
    double distance = recorder.getLocation().distance(cameraTarget);
    
    if (distance > 30.0) {
        // 距离过远，需要传送
        recorder.teleport(cameraTarget);
    } else if (distance > 5.0) {
        // 距离较远，使用速度跟随
        Location followResult = geometry.calculateFollowVelocity(recorder, cameraTarget);
        if (followResult != null) {
            double vx = followResult.getX() - recorder.getLocation().getX();
            double vy = followResult.getY() - recorder.getLocation().getY();
            double vz = followResult.getZ() - recorder.getLocation().getZ();
            recorder.setVelocity(new Vector(vx, vy, vz));
        }
    }
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

### 性能考虑

镜头计算相对复杂，避免在循环中频繁调用。缓存结果以提高性能。

### 线程安全

CameraGeometry 的方法是线程安全的，可以在任何线程中调用。

## 数学原理

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
```