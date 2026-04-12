# API 参考

## API 概览

LiveRecorder 提供了丰富的 API 供开发者使用。

**主要类：**

| 类 | 说明 | 包路径 |
|:---|:-----|:--------|
| LiveRecorder | 主插件类 | `com.liverecorder.LiveRecorder` |
| LiveCore | 核心管理器 | `com.liverecorder.manager.LiveCore` |
| DatabaseManager | 数据库管理器 | `com.liverecorder.database.DatabaseManager` |
| CameraGeometry | 镜头几何计算 | `com.liverecorder.util.CameraGeometry` |

## 获取插件实例

```java
import com.liverecorder.LiveRecorder;

// 获取插件实例
LiveRecorder plugin = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");

// 或者使用静态方法
LiveRecorder plugin = LiveRecorder.getInstance();
```

## 获取核心组件

```java
// 获取核心管理器
LiveCore liveCore = plugin.getLiveCore();

// 获取数据库管理器
DatabaseManager dbManager = plugin.getDatabaseManager();

// 获取镜头几何计算器
CameraGeometry geometry = plugin.getCameraGeometry();
```

## 常用操作

### 检查玩家是否为录制者

```java
Player player = ...;
boolean isRecorder = liveCore.isRecorder(player);
```

### 检查玩家是否为目标

```java
Player player = ...;
boolean isTarget = liveCore.isTarget(player);
```

### 获取录制者绑定

```java
Player recorder = ...;
RecorderBinding binding = liveCore.getBinding(recorder);
if (binding != null) {
    Player target = binding.getTarget();
    RecorderBinding.Mode mode = binding.getMode();
}
```

### 获取所有绑定

```java
Collection<RecorderBinding> bindings = liveCore.getAllBindings();
for (RecorderBinding binding : bindings) {
    Player recorder = binding.getRecorder();
    Player target = binding.getTarget();
}
```

### 绑定录制者

```java
Player recorder = ...;
Player target = ...;
RecorderBinding.Mode mode = RecorderBinding.Mode.AUTO;

int result = liveCore.bindRecorder(recorder, target, mode);
// 0 = 成功
// 1 = 已绑定
// 2 = 拒绝
// 3 = 待确认
```

### 解除录制者

```java
Player recorder = ...;
boolean success = liveCore.unbindRecorder(recorder);
```

### 手动切换目标

```java
Player recorder = ...;
Player newTarget = ...;
boolean success = liveCore.manualSwitch(recorder, newTarget);
```

## 隐私相关操作

### 获取玩家隐私设置

```java
Player player = ...;
PrivacySetting privacy = liveCore.getPlayerPrivacy(player);

PrivacySetting.ConsentStatus status = privacy.getConsentStatus();
boolean canBeStreamed = privacy.canBeStreamed();
boolean hasDeclined = privacy.hasDeclined();
boolean needsPrompt = privacy.needsPrompt();
```

### 设置玩家隐私状态

```java
Player player = ...;
PrivacySetting.ConsentStatus status = PrivacySetting.ConsentStatus.ACCEPTED;

boolean success = liveCore.setPlayerPrivacy(player, status);
```

### 玩家同意直播

```java
Player player = ...;
boolean success = liveCore.acceptStreaming(player);
```

### 玩家拒绝直播

```java
Player player = ...;
boolean success = liveCore.declineStreaming(player);
```

## 数据库操作

### 获取直播日志

```java
int limit = 20;
List<LiveLog> logs = liveCore.getLiveLogs(limit);

for (LiveLog log : logs) {
    LiveLog.LogType type = log.getLogType();
    String description = log.getDescription();
    long timestamp = log.getTimestamp();
}
```

### 添加直播日志

```java
DatabaseManager dbManager = plugin.getDatabaseManager();

LiveLog log = new LiveLog(
    0,
    LiveLog.LogType.START,
    recorderUuid,
    recorderName,
    targetUuid,
    targetName,
    System.currentTimeMillis()
);

boolean success = dbManager.addLiveLog(log);
```

## 镜头计算

### 计算镜头位置

```java
Player target = ...;
CameraGeometry geometry = plugin.getCameraGeometry();

Location cameraLoc = geometry.calculateCameraLocation(target);
```

### 检查是否需要传送

```java
Player recorder = ...;
Location cameraTarget = ...;
double maxDistance = 30.0;

boolean needsTeleport = geometry.needsTeleport(recorder, cameraTarget, maxDistance);
```

### 计算跟随速度

```java
Player recorder = ...;
Location cameraTarget = ...;

Location followResult = geometry.calculateFollowVelocity(recorder, cameraTarget);
```

## 事件监听

LiveRecorder 提供了多个事件监听器，你可以监听这些事件来实现自定义功能。

### CameraListener

处理镜头跟随相关事件：

- 玩家移动
- 玩家传送
- 玩家重生
- 玩家退出

### RecorderRestrictionListener

处理录制者操作限制：

- 打开背包
- 打开容器
- 丢弃物品
- 交互方块
- 攻击实体
- 放置/破坏方块
- 使用命令

### VisualListener

处理视觉反馈：

- 目标玩家发光
- 镜头位置粒子
- ActionBar 状态显示

## 依赖管理

### Maven

```xml
<dependency>
    <groupId>com.liverecorder</groupId>
    <artifactId>LiveRecorder</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
    compileOnly 'com.liverecorder:LiveRecorder:1.0.0'
}
```

## 详细说明

- [LiveRecorder 类](./liverecorder) - 主插件类详细说明
- [LiveCore 类](./livecore) - 核心管理器详细说明
- [DatabaseManager 类](./databasemanager) - 数据库管理器详细说明
- [CameraGeometry 类](./camerageometry) - 镜头几何计算详细说明

## 示例项目

查看 [示例项目](https://github.com/httye/LiveRecorder-Examples) 了解如何使用 LiveRecorder API。