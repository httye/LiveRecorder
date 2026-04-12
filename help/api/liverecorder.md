# LiveRecorder 类

LiveRecorder 主插件类，提供插件的核心功能。

## 类概述

```java
package com.liverecorder;

public class LiveRecorder extends JavaPlugin {
    // 插件实例
    private static LiveRecorder instance;
    
    // 核心组件
    private DatabaseManager databaseManager;
    private LiveCore liveCore;
    private CameraGeometry cameraGeometry;
}
```

## 获取插件实例

### 静态方法

```java
// 获取插件实例
LiveRecorder plugin = LiveRecorder.getInstance();
```

### 从 Bukkit 获取

```java
// 从插件管理器获取
LiveRecorder plugin = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
```

## 核心方法

### onEnable()

插件启用时调用，初始化所有组件。

```java
@Override
public void onEnable() {
    instance = this;
    saveDefaultConfig();
    databaseManager = new DatabaseManager(this);
    cameraGeometry = new CameraGeometry(this);
    liveCore = new LiveCore(this);
    
    // 注册监听器
    Bukkit.getPluginManager().registerEvents(new CameraListener(this), this);
    Bukkit.getPluginManager().registerEvents(new RecorderRestrictionListener(this), this);
    Bukkit.getPluginManager().registerEvents(new VisualListener(this), this);
    
    // 注册命令
    getCommand("liverecorder").setExecutor(new LiveRecorderCommand(this));
    getCommand("liverecorder").setTabCompleter(new LiveRecorderTabCompleter());
    
    liveCore.startTasks();
}
```

### onDisable()

插件禁用时调用，清理所有资源。

```java
@Override
public void onDisable() {
    if (liveCore != null) {
        liveCore.shutdown();
    }
    if (databaseManager != null) {
        databaseManager.close();
    }
}
```

### reloadPluginConfig()

重载配置文件。

```java
public void reloadPluginConfig() {
    reloadConfig();
    cameraGeometry.reload();
    liveCore.reload();
}
```

## Getter 方法

### getDatabaseManager()

获取数据库管理器。

```java
public DatabaseManager getDatabaseManager() {
    return databaseManager;
}
```

### getLiveCore()

获取核心管理器。

```java
public LiveCore getLiveCore() {
    return liveCore;
}
```

### getCameraGeometry()

获取镜头几何计算器。

```java
public CameraGeometry getCameraGeometry() {
    return cameraGeometry;
}
```

## 使用示例

### 获取插件实例

```java
import com.liverecorder.LiveRecorder;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // 获取 LiveRecorder 实例
        LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
        
        if (lr != null) {
            // 使用 LiveRecorder API
            LiveCore liveCore = lr.getLiveCore();
            // ...
        }
    }
}
```

### 检查玩家是否为录制者

```java
import com.liverecorder.LiveRecorder;

public class MyListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
        if (lr != null) {
            boolean isRecorder = lr.getLiveCore().isRecorder(event.getPlayer());
            if (isRecorder) {
                // 录制者移动
            }
        }
    }
}
```

### 获取直播日志

```java
import com.liverecorder.LiveRecorder;
import com.liverecorder.model.LiveLog;

public class MyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
        if (lr != null) {
            List<LiveLog> logs = lr.getLiveCore().getLiveLogs(20);
            for (LiveLog log : logs) {
                sender.sendMessage(log.getDescription());
            }
        }
        return true;
    }
}
```

## 注意事项

### 插件依赖

确保 LiveRecorder 已安装并启用：

```java
LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
if (lr == null) {
    getLogger().warning("LiveRecorder not found!");
    return;
}
```

### 版本兼容性

检查插件版本兼容性：

```java
LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
if (lr != null) {
    String version = lr.getDescription().getVersion();
    if (!version.startsWith("1.0")) {
        getLogger().warning("LiveRecorder version mismatch!");
    }
}
```

### 异常处理

处理可能的异常：

```java
try {
    LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
    if (lr != null) {
        // 使用 API
    }
} catch (Exception e) {
    getLogger().severe("Error accessing LiveRecorder API: " + e.getMessage());
}
```