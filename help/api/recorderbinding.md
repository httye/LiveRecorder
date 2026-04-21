# RecorderBinding 类

录制者绑定数据模型，维护录制者与目标玩家之间的绑定关系。

## 类概述

```java
package com.liverecorder.model;

import org.bukkit.entity.Player;

public class RecorderBinding {
    private final Player recorder;
    private Player target;
    private Mode mode;
    private boolean active;
    private long lastSwitchTime;
    private boolean following;
}
```

**职责：**
- 存储录制者与目标的绑定关系
- 管理绑定模式（AUTO/MANUAL/SPECTATOR）
- 跟踪自动切换时间
- 维护跟随状态

## 绑定模式枚举

### Mode.AUTO（自动模式）

录制者会自动切换跟拍目标，切换策略由 `auto-switch.mode` 配置决定：
- `RANDOM` - 随机选择下一个目标
- `SEQUENTIAL` - 按顺序选择下一个目标

**适用场景：** 综艺节目、多玩家轮播展示

### Mode.MANUAL（手动模式）

只能通过 `/lr switch` 命令手动切换目标，不会自动切换。

**适用场景：** 专注跟拍特定玩家、导播控制

### Mode.SPECTATOR（观察者模式）

使用旁观者模式自动跟随目标，录制者处于旁观者状态。

**适用场景：** 隐蔽拍摄、不干扰游戏

## 构造函数

### RecorderBinding()

创建一个新的录制者绑定。

```java
public RecorderBinding(Player recorder, Player target, Mode mode)
```

**参数：**
- `recorder` - 录制者玩家（不能为 null）
- `target` - 初始目标玩家（可以为 null，但需要后续设置）
- `mode` - 绑定模式（不能为 null）

**示例：**
```java
Player recorder = Bukkit.getPlayer("CameraMan");
Player target = Bukkit.getPlayer("Steve");

// 创建自动模式的绑定
RecorderBinding binding = new RecorderBinding(recorder, target, Mode.AUTO);

// 创建手动模式的绑定
RecorderBinding manualBinding = new RecorderBinding(recorder, target, Mode.MANUAL);
```

**注意事项：**
- 构造函数会初始化 `lastSwitchTime` 为当前时间戳
- `active` 默认为 `true`
- `following` 默认为 `false`

## 核心方法

### switchTarget()

切换目标玩家。

```java
public void switchTarget(Player newTarget)
```

**参数：**
- `newTarget` - 新的目标玩家（可以为 null）

**副作用：**
- 更新 `target` 字段
- 重置 `lastSwitchTime` 为当前时间戳

**示例：**
```java
Player newTarget = Bukkit.getPlayer("Alex");
binding.switchTarget(newTarget);

// 验证切换成功
System.out.println("新目标: " + binding.getTarget().getName());
```

**注意事项：**
- 此方法不会触发任何事件
- 如果需要通知其他系统，需要在调用后手动处理
- 切换后应更新录制者的位置和视角

### setMode()

切换绑定模式。

```java
public void setMode(Mode mode)
```

**参数：**
- `mode` - 新的绑定模式（不能为 null）

**示例：**
```java
// 从自动模式切换到手动模式
binding.setMode(RecorderBinding.Mode.MANUAL);

// 从手动模式切换到自动模式
binding.setMode(RecorderBinding.Mode.AUTO);
```

**注意事项：**
- 切换模式不会影响当前的目标
- 从 MANUAL 切换到 AUTO 后，会自动开始计时
- 建议在切换模式时记录日志

### shouldAutoSwitch()

检查是否应该自动切换目标（仅自动模式下生效）。

```java
public boolean shouldAutoSwitch(long intervalMs)
```

**参数：**
- `intervalMs` - 自动切换间隔（毫秒）

**返回值：**
- `true` - 应该切换目标
- `false` - 不应该切换（非自动模式或时间未到）

**示例：**
```java
long interval = plugin.getConfig().getLong("auto-switch.interval", 30) * 1000;

if (binding.shouldAutoSwitch(interval)) {
    // 执行自动切换逻辑
    Player newTarget = selectNextTarget();
    binding.switchTarget(newTarget);
}
```

**实现细节：**
```java
public boolean shouldAutoSwitch(long intervalMs) {
    if (mode != Mode.AUTO) {
        return false;  // 非自动模式直接返回 false
    }
    return System.currentTimeMillis() - lastSwitchTime >= intervalMs;
}
```

**注意事项：**
- 仅在 `mode == Mode.AUTO` 时可能返回 true
- 时间比较基于系统时钟
- 如果服务器时间被调整，可能影响判断

## Getter/Setter 方法

### getRecorder()

获取录制者玩家。

```java
public Player getRecorder()
```

**返回值：** 录制者玩家对象（不为 null）

**示例：**
```java
Player recorder = binding.getRecorder();
if (recorder.isOnline()) {
    // 录制者在线，可以执行操作
}
```

### getTarget()

获取当前绑定的目标玩家。

```java
public Player getTarget()
```

**返回值：** 目标玩家对象（可能为 null）

**示例：**
```java
Player target = binding.getTarget();
if (target != null && target.isOnline()) {
    // 目标在线，可以计算镜头位置
    Location cameraLoc = calculateCameraLocation(target);
} else {
    // 目标离线，需要处理
    handleOfflineTarget();
}
```

**注意事项：**
- 使用前必须检查 null
- 建议同时检查 `isOnline()`

### getMode()

获取绑定模式。

```java
public Mode getMode()
```

**返回值：** 当前的绑定模式

**示例：**
```java
RecorderBinding.Mode mode = binding.getMode();
switch (mode) {
    case AUTO:
        sender.sendMessage("当前为自动模式");
        break;
    case MANUAL:
        sender.sendMessage("当前为手动模式");
        break;
    case SPECTATOR:
        sender.sendMessage("当前为观察者模式");
        break;
}
```

### isActive() / setActive()

检查/设置绑定是否激活。

```java
public boolean isActive()
public void setActive(boolean active)
```

**说明：**
- `active = true` - 绑定有效，录制者正常工作
- `active = false` - 绑定暂停，录制者停止跟随

**示例：**
```java
// 暂停录制
binding.setActive(false);

// 恢复录制
binding.setActive(true);

// 检查状态
if (binding.isActive()) {
    // 执行跟随逻辑
}
```

**应用场景：**
- 目标玩家暂时离开时暂停录制
- 服务器维护时暂停所有录制
- 玩家拒绝直播时临时禁用

### getLastSwitchTime()

获取上次切换目标的时间戳。

```java
public long getLastSwitchTime()
```

**返回值：** Unix 时间戳（毫秒）

**示例：**
```java
long lastSwitch = binding.getLastSwitchTime();
long now = System.currentTimeMillis();
long elapsed = now - lastSwitch;

sender.sendMessage("距离上次切换已过去 " + (elapsed / 1000) + " 秒");
```

### isFollowing() / setFollowing()

检查/设置是否正在跟随中。

```java
public boolean isFollowing()
public void setFollowing(boolean following)
```

**说明：**
- `following = true` - 录制者正在跟随目标
- `following = false` - 录制者未跟随（可能在传送或初始化）

**示例：**
```java
// 标记开始跟随
binding.setFollowing(true);

// 在跟随任务中检查
if (binding.isFollowing()) {
    updateFollowPosition();
}
```

**注意事项：**
- 这个标志主要用于内部状态管理
- 外部代码通常不需要手动设置

## toString()

返回绑定的字符串表示。

```java
@Override
public String toString()
```

**返回格式：**
```
RecorderBinding{recorder=CameraMan, target=Steve, mode=AUTO, active=true}
```

**用途：**
- 调试输出
- 日志记录
- 管理员查看绑定信息

**示例：**
```java
plugin.getLogger().info("绑定信息: " + binding.toString());
// 输出: 绑定信息: RecorderBinding{recorder=CameraMan, target=Steve, mode=AUTO, active=true}
```

## 完整使用示例

### 示例 1: 创建和管理绑定

```java
public class BindingManager {
    private final Map<UUID, RecorderBinding> bindings = new HashMap<>();

    /**
     * 绑定录制者到目标
     */
    public boolean bindRecorder(Player recorder, Player target, RecorderBinding.Mode mode) {
        // 检查是否已绑定
        if (bindings.containsKey(recorder.getUniqueId())) {
            recorder.sendMessage(ChatColor.RED + "你已经绑定了其他目标！");
            return false;
        }

        // 创建新绑定
        RecorderBinding binding = new RecorderBinding(recorder, target, mode);
        bindings.put(recorder.getUniqueId(), binding);

        // 应用隐身效果
        applyInvisibility(recorder);

        // 记录日志
        logBinding(recorder, target, mode);

        recorder.sendMessage(ChatColor.GREEN + "成功绑定到 " + target.getName());
        return true;
    }

    /**
     * 解除绑定
     */
    public boolean unbindRecorder(Player recorder) {
        RecorderBinding binding = bindings.remove(recorder.getUniqueId());
        if (binding == null) {
            return false;
        }

        // 取消隐身
        removeInvisibility(recorder);

        // 记录日志
        logUnbinding(recorder, binding.getTarget());

        return true;
    }

    /**
     * 获取绑定信息
     */
    public RecorderBinding getBinding(Player recorder) {
        return bindings.get(recorder.getUniqueId());
    }
}
```

### 示例 2: 自动切换逻辑

```java
public class AutoSwitchTask extends BukkitRunnable {
    private final LiveRecorder plugin;
    private final Map<UUID, RecorderBinding> bindings;

    @Override
    public void run() {
        long interval = plugin.getConfig().getLong("auto-switch.interval", 30) * 1000;

        for (RecorderBinding binding : bindings.values()) {
            // 检查是否需要切换
            if (!binding.isActive() || !binding.shouldAutoSwitch(interval)) {
                continue;
            }

            // 选择下一个目标
            Player currentTarget = binding.getTarget();
            Player nextTarget = selectNextTarget(currentTarget);

            if (nextTarget != null) {
                // 执行切换
                binding.switchTarget(nextTarget);

                // 通知录制者
                binding.getRecorder().sendMessage(
                    ChatColor.YELLOW + "自动切换到新目标: " + nextTarget.getName()
                );

                // 记录日志
                logSwitch(binding.getRecorder(), currentTarget, nextTarget);
            }
        }
    }

    private Player selectNextTarget(Player current) {
        // 根据配置的切换模式选择目标
        String mode = plugin.getConfig().getString("auto-switch.mode", "RANDOM");

        List<Player> onlinePlayers = getOnlinePlayers();
        if (onlinePlayers.isEmpty()) {
            return null;
        }

        if ("RANDOM".equalsIgnoreCase(mode)) {
            // 随机选择
            Random random = new Random();
            return onlinePlayers.get(random.nextInt(onlinePlayers.size()));
        } else {
            // 顺序选择
            int currentIndex = onlinePlayers.indexOf(current);
            int nextIndex = (currentIndex + 1) % onlinePlayers.size();
            return onlinePlayers.get(nextIndex);
        }
    }
}
```

### 示例 3: 绑定状态监控

```java
public void checkBindingStatus(CommandSender sender) {
    if (!(sender instanceof Player)) {
        return;
    }

    Player player = (Player) sender;
    RecorderBinding binding = liveCore.getBinding(player);

    if (binding == null) {
        player.sendMessage(ChatColor.RED + "你没有绑定任何目标");
        return;
    }

    // 显示详细信息
    player.sendMessage(ChatColor.GREEN + "========== 绑定信息 ==========");
    player.sendMessage(ChatColor.YELLOW + "录制者: " + binding.getRecorder().getName());

    Player target = binding.getTarget();
    if (target != null && target.isOnline()) {
        player.sendMessage(ChatColor.YELLOW + "目标: " + target.getName());
        player.sendMessage(ChatColor.YELLOW + "目标世界: " + target.getWorld().getName());
        player.sendMessage(ChatColor.YELLOW + "目标位置: " + formatLocation(target.getLocation()));
    } else {
        player.sendMessage(ChatColor.RED + "目标: 离线");
    }

    player.sendMessage(ChatColor.YELLOW + "模式: " + getModeName(binding.getMode()));
    player.sendMessage(ChatColor.YELLOW + "状态: " + (binding.isActive() ? "活跃" : "暂停"));
    player.sendMessage(ChatColor.YELLOW + "跟随中: " + (binding.isFollowing() ? "是" : "否"));

    long lastSwitch = binding.getLastSwitchTime();
    long elapsed = System.currentTimeMillis() - lastSwitch;
    player.sendMessage(ChatColor.YELLOW + "距上次切换: " + (elapsed / 1000) + " 秒");

    player.sendMessage(ChatColor.GREEN + "==============================");
}

private String getModeName(RecorderBinding.Mode mode) {
    switch (mode) {
        case AUTO:
            return "自动";
        case MANUAL:
            return "手动";
        case SPECTATOR:
            return "观察者";
        default:
            return "未知";
    }
}
```

## 最佳实践

### 1. 空值检查

始终检查 `getTarget()` 的返回值是否为 null：

```java
// ❌ 错误做法
Player target = binding.getTarget();
Location loc = target.getLocation();  // 可能抛出 NullPointerException

// ✅ 正确做法
Player target = binding.getTarget();
if (target != null && target.isOnline()) {
    Location loc = target.getLocation();
    // 安全使用
}
```

### 2. 线程安全

RecorderBinding 的方法不是线程安全的，应在主线程中调用：

```java
// ❌ 错误做法 - 在异步线程中修改绑定
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    binding.switchTarget(newTarget);  // 可能导致并发问题
});

// ✅ 正确做法 - 在主线程中修改
Bukkit.getScheduler().runTask(plugin, () -> {
    binding.switchTarget(newTarget);  // 安全
});
```

### 3. 状态一致性

修改绑定状态时，确保相关系统同步更新：

```java
// ✅ 完整的状态更新流程
public void updateBindingTarget(RecorderBinding binding, Player newTarget) {
    // 1. 更新绑定
    binding.switchTarget(newTarget);

    // 2. 更新视觉反馈
    updateGlowEffect(binding.getRecorder(), newTarget);

    // 3. 记录日志
    databaseManager.addLiveLog(new LiveLog(
        0,
        LiveLog.LogType.SWITCH,
        binding.getRecorder().getUniqueId().toString(),
        binding.getRecorder().getName(),
        newTarget.getUniqueId().toString(),
        newTarget.getName(),
        System.currentTimeMillis()
    ));

    // 4. 通知玩家
    binding.getRecorder().sendMessage(
        ChatColor.GREEN + "已切换到新目标: " + newTarget.getName()
    );
}
```

### 4. 资源清理

解除绑定时，清理所有相关资源：

```java
public void cleanupBinding(RecorderBinding binding) {
    Player recorder = binding.getRecorder();

    // 1. 取消隐身
    recorder.removePotionEffect(PotionEffectType.INVISIBILITY);

    // 2. 移除发光效果
    recorder.setGlowing(false);

    // 3. 清除 ActionBar
    sendActionBar(recorder, "");

    // 4. 重置游戏模式
    recorder.setGameMode(GameMode.SURVIVAL);

    // 5. 从管理器中移除
    bindings.remove(recorder.getUniqueId());
}
```

## 注意事项

### 性能考虑

- `RecorderBinding` 对象轻量级，可以安全地创建多个实例
- 避免频繁调用 `switchTarget()`，每次调用都会更新时间戳
- `shouldAutoSwitch()` 是 O(1) 操作，可以高频调用

### 内存管理

- 解除绑定时，确保从所有集合中移除引用
- 监听玩家退出事件，及时清理离线玩家的绑定
- 使用 `WeakReference` 存储玩家引用（如果需要）

### 兼容性

- `Mode.SPECTATOR` 需要 Minecraft 1.8+ 支持
- 隐身效果依赖于 Spigot 的 PotionEffect API
- 发光效果需要 1.9+ 版本

## 相关类

- [LiveCore](./livecore) - 核心管理器，管理所有绑定
- [PrivacySetting](../privacy/overview) - 隐私设置模型
- [LiveLog](../privacy/logs) - 直播日志模型
- [CameraGeometry](./camerageometry) - 镜头几何计算

## 常见问题

### Q: 如何判断绑定是否过期？

A: 使用 `isActive()` 方法检查绑定状态，结合 `getLastSwitchTime()` 判断是否超时。

### Q: 可以在不同世界间切换目标吗？

A: 可以，但需要注意跨世界传送的性能开销。建议在切换前检查目标是否在同一世界。

### Q: 如何处理目标玩家离线？

A: 监听 `PlayerQuitEvent`，当目标离线时暂停绑定（`setActive(false)`）或切换到其他在线玩家。
