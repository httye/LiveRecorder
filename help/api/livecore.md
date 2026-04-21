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

LiveCore 的方法**必须在主线程中调用**,不要在异步线程中调用。

```java
// ❌ 错误做法 - 在异步线程中调用
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    liveCore.bindRecorder(recorder, target, mode);  // 可能导致并发问题
});

// ✅ 正确做法 - 在主线程中调用
Bukkit.getScheduler().runTask(plugin, () -> {
    liveCore.bindRecorder(recorder, target, mode);  // 安全
});
```

**原因:**
- LiveCore 内部使用 `ConcurrentHashMap` 存储绑定关系
- Bukkit API 的大部分方法不是线程安全的
- 玩家对象的状态可能在其他线程中被修改

### 玩家在线状态

使用前检查玩家是否在线:

```java
// ✅ 推荐做法
if (recorder != null && recorder.isOnline()) {
    int result = liveCore.bindRecorder(recorder, target, mode);
    // 处理结果
} else {
    sender.sendMessage(ChatColor.RED + "录制者不在线!");
}
```

**注意事项:**
- `bindRecorder()` 不会自动检查玩家在线状态
- `manualSwitch()` 需要确保新目标在线
- 监听 `PlayerQuitEvent` 及时清理离线玩家的绑定

### 权限检查

确保调用者有相应权限:

```java
public boolean executeBind(CommandSender sender, String recorderName, String targetName) {
    if (!sender.hasPermission("liverecorder.admin")) {
        sender.sendMessage(ChatColor.RED + "你没有权限执行此操作!");
        return false;
    }
    
    // 继续执行绑定逻辑
    // ...
}
```

### 异常处理

#### bindRecorder() 可能的异常情况

```java
try {
    int result = liveCore.bindRecorder(recorder, target, mode);
    
    switch (result) {
        case 0:
            // 成功
            break;
        case 1:
            sender.sendMessage(ChatColor.YELLOW + "录制者已经绑定到其他目标");
            break;
        case 2:
            sender.sendMessage(ChatColor.RED + "目标玩家拒绝被直播");
            break;
        case 3:
            sender.sendMessage(ChatColor.YELLOW + "等待目标玩家确认...");
            break;
        default:
            sender.sendMessage(ChatColor.RED + "未知错误");
    }
} catch (NullPointerException e) {
    // 玩家对象为 null
    plugin.getLogger().severe("玩家对象为空: " + e.getMessage());
    sender.sendMessage(ChatColor.RED + "玩家不存在或已离线");
} catch (IllegalStateException e) {
    // 插件未正确初始化
    plugin.getLogger().severe("LiveCore 未正确初始化: " + e.getMessage());
    sender.sendMessage(ChatColor.RED + "插件内部错误,请联系管理员");
}
```

#### manualSwitch() 的边界条件

```java
public boolean safeManualSwitch(Player recorder, Player newTarget) {
    // 1. 检查录制者是否存在
    RecorderBinding binding = liveCore.getBinding(recorder);
    if (binding == null) {
        recorder.sendMessage(ChatColor.RED + "你没有绑定任何目标");
        return false;
    }
    
    // 2. 检查新目标是否有效
    if (newTarget == null || !newTarget.isOnline()) {
        recorder.sendMessage(ChatColor.RED + "目标玩家不在线");
        return false;
    }
    
    // 3. 检查是否是同一个目标
    if (binding.getTarget() != null && binding.getTarget().equals(newTarget)) {
        recorder.sendMessage(ChatColor.YELLOW + "你已经在跟拍该玩家");
        return false;
    }
    
    // 4. 检查隐私设置
    PrivacySetting privacy = liveCore.getPlayerPrivacy(newTarget);
    if (privacy.hasDeclined()) {
        recorder.sendMessage(ChatColor.RED + "该玩家拒绝被直播");
        return false;
    }
    
    // 5. 执行切换
    return liveCore.manualSwitch(recorder, newTarget);
}
```

### 性能考虑

#### 避免频繁调用

```java
// ❌ 错误做法 - 每 tick 都查询所有绑定
new BukkitRunnable() {
    @Override
    public void run() {
        for (RecorderBinding binding : liveCore.getAllBindings()) {
            // 大量计算
        }
    }
}.runTaskTimer(plugin, 0L, 1L);  // 每 tick 执行

// ✅ 正确做法 - 降低频率
new BukkitRunnable() {
    @Override
    public void run() {
        for (RecorderBinding binding : liveCore.getAllBindings()) {
            // 适度计算
        }
    }
}.runTaskTimer(plugin, 0L, 20L);  // 每秒执行
```

#### 缓存常用数据

```java
public class BindingCache {
    private final Map<UUID, Long> lastCheckTime = new HashMap<>();
    private static final long CACHE_DURATION = 5000; // 5秒缓存
    
    public boolean isPlayerTargetCached(Player player) {
        Long lastCheck = lastCheckTime.get(player.getUniqueId());
        if (lastCheck == null) {
            return false;
        }
        return System.currentTimeMillis() - lastCheck < CACHE_DURATION;
    }
    
    public void updateCache(Player player) {
        lastCheckTime.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
```

### 内存管理

#### 及时清理无用绑定

```java
@EventHandler
public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    
    // 如果离线的玩家是录制者,解除绑定
    if (liveCore.isRecorder(player)) {
        liveCore.unbindRecorder(player);
        plugin.getLogger().info("录制者 " + player.getName() + " 离线,已自动解绑");
    }
    
    // 如果离线的玩家是目标,通知相关录制者
    if (liveCore.isTarget(player)) {
        notifyRecordersAboutOfflineTarget(player);
    }
}

private void notifyRecordersAboutOfflineTarget(Player offlineTarget) {
    for (RecorderBinding binding : liveCore.getAllBindings()) {
        if (offlineTarget.equals(binding.getTarget())) {
            Player recorder = binding.getRecorder();
            if (recorder.isOnline()) {
                recorder.sendMessage(ChatColor.YELLOW + "你的目标 " + 
                    offlineTarget.getName() + " 已离线");
                // 可以选择暂停或切换到其他目标
                binding.setActive(false);
            }
        }
    }
}
```

### 最佳实践总结

1. **始终检查玩家在线状态** - 在调用任何方法前验证
2. **处理所有返回值** - 不要忽略方法的返回码
3. **使用 try-catch** - 捕获可能的异常
4. **监听玩家事件** - 及时处理玩家上线/下线
5. **记录关键操作** - 便于调试和审计
6. **避免高频调用** - 合理使用缓存和定时器
7. **权限验证** - 在执行敏感操作前检查权限
8. **资源清理** - 确保在插件禁用时清理所有资源

### 常见错误及解决方案

| 错误 | 原因 | 解决方案 |
|------|------|---------|
| NullPointerException | 玩家对象为 null | 使用前检查 `player != null && player.isOnline()` |
| IllegalStateException | 插件未初始化 | 确保在 `onEnable()` 后调用 |
| ConcurrentModificationException | 遍历时修改集合 | 使用 `Iterator` 或 `CopyOnWriteArrayList` |
| PermissionDenied | 缺少权限 | 检查 `hasPermission()` |
| TargetDeclined | 目标拒绝直播 | 检查隐私设置并提示用户 |

### 调试技巧

启用调试模式查看详细日志:

```yaml
# config.yml
debug: true
```

在代码中添加调试输出:

```java
if (plugin.getConfig().getBoolean("debug", false)) {
    plugin.getLogger().info("[DEBUG] 绑定录制者: " + recorder.getName() + 
        " -> " + target.getName());
}
```

查看实时绑定状态:

```java
public void debugBindings() {
    plugin.getLogger().info("========== 调试信息 ==========");
    plugin.getLogger().info("活跃绑定数: " + liveCore.getAllBindings().size());
    
    for (RecorderBinding binding : liveCore.getAllBindings()) {
        plugin.getLogger().info(String.format(
            "录制者: %s, 目标: %s, 模式: %s, 激活: %b",
            binding.getRecorder().getName(),
            binding.getTarget() != null ? binding.getTarget().getName() : "null",
            binding.getMode(),
            binding.isActive()
        ));
    }
}
```