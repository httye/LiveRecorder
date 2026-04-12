# LiveCore 类

LiveCore 核心管理器，管理录制者绑定和跟随系统。

## 类概述

```java
package com.liverecorder.manager;

public class LiveCore {
    private final LiveRecorder plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, RecorderBinding> bindings;
    private final Set<UUID> registeredTargets;
    private final Map<UUID, List<UUID>> pendingRequests;
}
```

## 获取实例

```java
import com.liverecorder.LiveRecorder;

LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
LiveCore liveCore = lr.getLiveCore();
```

## 核心方法

### 绑定管理

#### bindRecorder()

绑定录制者到目标玩家。

```java
public int bindRecorder(Player recorder, Player target, RecorderBinding.Mode mode)
```

**返回值：**
- `0` - 绑定成功
- `1` - 录制者已绑定
- `2` - 目标玩家拒绝直播
- `3` - 等待目标玩家确认

**示例：**
```java
Player recorder = Bukkit.getPlayer("CameraMan");
Player target = Bukkit.getPlayer("Steve");

int result = liveCore.bindRecorder(recorder, target, RecorderBinding.Mode.AUTO);

switch (result) {
    case 0:
        // 绑定成功
        break;
    case 1:
        // 录制者已绑定
        break;
    case 2:
        // 目标玩家拒绝
        break;
    case 3:
        // 等待确认
        break;
}
```

#### unbindRecorder()

解除录制者绑定。

```java
public boolean unbindRecorder(Player recorder)
```

**示例：**
```java
Player recorder = Bukkit.getPlayer("CameraMan");
boolean success = liveCore.unbindRecorder(recorder);
```

#### getBinding()

获取录制者的绑定信息。

```java
public RecorderBinding getBinding(Player recorder)
```

**示例：**
```java
Player recorder = Bukkit.getPlayer("CameraMan");
RecorderBinding binding = liveCore.getBinding(recorder);
if (binding != null) {
    Player target = binding.getTarget();
    RecorderBinding.Mode mode = binding.getMode();
}
```

### 状态检查

#### isRecorder()

检查玩家是否为录制者。

```java
public boolean isRecorder(Player player)
```

**示例：**
```java
Player player = Bukkit.getPlayer("CameraMan");
boolean isRecorder = liveCore.isRecorder(player);
```

#### isTarget()

检查玩家是否为被跟拍的目标。

```java
public boolean isTarget(Player player)
```

**示例：**
```java
Player player = Bukkit.getPlayer("Steve");
boolean isTarget = liveCore.isTarget(player);
```

### 手动切换

#### manualSwitch()

手动切换录制者的目标。

```java
public boolean manualSwitch(Player recorder, Player newTarget)
```

**示例：**
```java
Player recorder = Bukkit.getPlayer("CameraMan");
Player newTarget = Bukkit.getPlayer("Alex");
boolean success = liveCore.manualSwitch(recorder, newTarget);
```

### 隐私功能

#### getPlayerPrivacy()

获取玩家的隐私设置。

```java
public PrivacySetting getPlayerPrivacy(Player player)
```

**示例：**
```java
Player player = Bukkit.getPlayer("Steve");
PrivacySetting privacy = liveCore.getPlayerPrivacy(player);

PrivacySetting.ConsentStatus status = privacy.getConsentStatus();
boolean canBeStreamed = privacy.canBeStreamed();
boolean hasDeclined = privacy.hasDeclined();
```

#### setPlayerPrivacy()

设置玩家的隐私状态。

```java
public boolean setPlayerPrivacy(Player player, PrivacySetting.ConsentStatus consentStatus)
```

**示例：**
```java
Player player = Bukkit.getPlayer("Steve");
boolean success = liveCore.setPlayerPrivacy(player, PrivacySetting.ConsentStatus.ACCEPTED);
```

#### acceptStreaming()

玩家同意直播。

```java
public boolean acceptStreaming(Player player)
```

**示例：**
```java
Player player = Bukkit.getPlayer("Steve");
boolean success = liveCore.acceptStreaming(player);
```

#### declineStreaming()

玩家拒绝直播。

```java
public boolean declineStreaming(Player player)
```

**示例：**
```java
Player player = Bukkit.getPlayer("Steve");
boolean success = liveCore.declineStreaming(player);
```

### 日志功能

#### getLiveLogs()

获取直播日志。

```java
public List<LiveLog> getLiveLogs(int limit)
```

**示例：**
```java
List<LiveLog> logs = liveCore.getLiveLogs(20);
for (LiveLog log : logs) {
    String description = log.getDescription();
    long timestamp = log.getTimestamp();
}
```

### 其他方法

#### getAllBindings()

获取所有绑定。

```java
public Collection<RecorderBinding> getAllBindings()
```

**示例：**
```java
Collection<RecorderBinding> bindings = liveCore.getAllBindings();
for (RecorderBinding binding : bindings) {
    Player recorder = binding.getRecorder();
    Player target = binding.getTarget();
}
```

#### getOnlineTargets()

获取所有在线的目标玩家。

```java
public List<Player> getOnlineTargets()
```

**示例：**
```java
List<Player> targets = liveCore.getOnlineTargets();
for (Player target : targets) {
    // 处理目标
}
```

## 使用示例

### 检查绑定状态

```java
public void checkBindingStatus(Player player) {
    if (liveCore.isRecorder(player)) {
        RecorderBinding binding = liveCore.getBinding(player);
        Player target = binding.getTarget();
        if (target != null && target.isOnline()) {
            player.sendMessage("正在跟拍: " + target.getName());
        } else {
            player.sendMessage("目标离线");
        }
    } else if (liveCore.isTarget(player)) {
        player.sendMessage("你正在被直播");
    } else {
        player.sendMessage("你没有绑定");
    }
}
```

### 切换所有录制者到新目标

```java
public void switchAllRecorders(Player newTarget) {
    for (RecorderBinding binding : liveCore.getAllBindings()) {
        Player recorder = binding.getRecorder();
        if (recorder.isOnline()) {
            liveCore.manualSwitch(recorder, newTarget);
        }
    }
}
```

### 统计直播时长

```java
public void calculateStreamingDuration() {
    List<LiveLog> logs = liveCore.getLiveLogs(100);
    Map<UUID, Long> durations = new HashMap<>();
    
    for (LiveLog log : logs) {
        if (log.getLogType() == LiveLog.LogType.START) {
            durations.put(log.getTargetUuid(), log.getTimestamp());
        } else if (log.getLogType() == LiveLog.LogType.END) {
            Long startTime = durations.get(log.getTargetUuid());
            if (startTime != null) {
                long duration = log.getTimestamp() - startTime;
                // 处理时长
                durations.remove(log.getTargetUuid());
            }
        }
    }
}
```

## 注意事项

### 线程安全

LiveCore 的方法可以在主线程中调用，不要在异步线程中调用。

### 玩家在线状态

使用前检查玩家是否在线：

```java
if (recorder != null && recorder.isOnline()) {
    // 操作
}
```

### 权限检查

确保调用者有相应权限：

```java
if (sender.hasPermission("liverecorder.admin")) {
    // 操作
}
```