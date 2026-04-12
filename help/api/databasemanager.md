# DatabaseManager 类

数据库管理器，管理 SQLite 数据库操作。

## 类概述

```java
package com.liverecorder.database;

public class DatabaseManager {
    private final LiveRecorder plugin;
    private Connection connection;
}
```

## 获取实例

```java
import com.liverecorder.LiveRecorder;

LiveRecorder lr = (LiveRecorder) Bukkit.getPluginManager().getPlugin("LiveRecorder");
DatabaseManager dbManager = lr.getDatabaseManager();
```

## 隐私设置操作

### getPrivacySetting()

获取玩家的隐私设置。

```java
public PrivacySetting getPrivacySetting(UUID playerUuid)
```

**示例：**
```java
UUID playerUuid = player.getUniqueId();
PrivacySetting privacy = dbManager.getPrivacySetting(playerUuid);

if (privacy != null) {
    PrivacySetting.ConsentStatus status = privacy.getConsentStatus();
    boolean invisible = privacy.isInvisible();
}
```

### savePrivacySetting()

保存或更新隐私设置。

```java
public boolean savePrivacySetting(PrivacySetting setting)
```

**示例：**
```java
Player player = Bukkit.getPlayer("Steve");
PrivacySetting privacy = new PrivacySetting(player.getUniqueId(), player.getName());
privacy.setConsentStatus(PrivacySetting.ConsentStatus.ACCEPTED);

boolean success = dbManager.savePrivacySetting(privacy);
```

### deletePrivacySetting()

删除隐私设置。

```java
public boolean deletePrivacySetting(UUID playerUuid)
```

**示例：**
```java
UUID playerUuid = player.getUniqueId();
boolean success = dbManager.deletePrivacySetting(playerUuid);
```

### getAcceptedPlayers()

获取所有同意直播的玩家 UUID。

```java
public List<UUID> getAcceptedPlayers()
```

**示例：**
```java
List<UUID> acceptedPlayers = dbManager.getAcceptedPlayers();
for (UUID uuid : acceptedPlayers) {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null && player.isOnline()) {
        // 处理玩家
    }
}
```

### getDeclinedPlayers()

获取所有拒绝直播的玩家 UUID。

```java
public List<UUID> getDeclinedPlayers()
```

**示例：**
```java
List<UUID> declinedPlayers = dbManager.getDeclinedPlayers();
for (UUID uuid : declinedPlayers) {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null && player.isOnline()) {
        // 处理玩家
    }
}
```

## 直播日志操作

### addLiveLog()

添加直播日志。

```java
public boolean addLiveLog(LiveLog log)
```

**示例：**
```java
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

### getLiveLogs()

获取直播日志。

```java
public List<LiveLog> getLiveLogs(int limit)
```

**示例：**
```java
List<LiveLog> logs = dbManager.getLiveLogs(20);
for (LiveLog log : logs) {
    String description = log.getDescription();
    long timestamp = log.getTimestamp();
}
```

### cleanupOldLogs()

清理旧的日志（保留最近 N 条）。

```java
public int cleanupOldLogs(int keepCount)
```

**示例：**
```java
int deleted = dbManager.cleanupOldLogs(100);
plugin.getLogger().info("清理了 " + deleted + " 条旧日志");
```

## 连接管理

### getConnection()

获取数据库连接。

```java
public Connection getConnection()
```

**示例：**
```java
Connection conn = dbManager.getConnection();
try (Statement stmt = conn.createStatement()) {
    ResultSet rs = stmt.executeQuery("SELECT * FROM privacy_settings");
    // 处理结果
}
```

### close()

关闭数据库连接。

```java
public void close()
```

**示例：**
```java
// 插件禁用时自动调用
dbManager.close();
```

## 使用示例

### 自定义查询

```java
public void customQuery() {
    Connection conn = dbManager.getConnection();
    try {
        String sql = "SELECT * FROM live_logs WHERE log_type = ? ORDER BY timestamp DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "START");
            stmt.setInt(2, 10);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String recorderName = rs.getString("recorder_name");
                    String targetName = rs.getString("target_name");
                    long timestamp = rs.getLong("timestamp");
                    // 处理结果
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

### 批量操作

```java
public void batchUpdate(List<Player> players, PrivacySetting.ConsentStatus status) {
    Connection conn = dbManager.getConnection();
    try {
        String sql = "INSERT OR REPLACE INTO privacy_settings (player_uuid, player_name, consent_status, invisible, last_updated) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            
            for (Player player : players) {
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setString(2, player.getName());
                stmt.setString(3, status.name());
                stmt.setBoolean(4, false);
                stmt.setLong(5, System.currentTimeMillis());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            conn.commit();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

### 统计分析

```java
public void analyzeStats() {
    Connection conn = dbManager.getConnection();
    try {
        String sql = "SELECT consent_status, COUNT(*) as count FROM privacy_settings GROUP BY consent_status";
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String status = rs.getString("consent_status");
                    int count = rs.getInt("count");
                    // 处理统计结果
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

## 注意事项

### 线程安全

数据库操作应该在主线程中执行，避免并发问题。

### 异常处理

总是处理 SQLException：

```java
try {
    // 数据库操作
} catch (SQLException e) {
    plugin.getLogger().severe("数据库错误: " + e.getMessage());
}
```

### 资源释放

使用 try-with-resources 确保资源释放：

```java
try (Connection conn = dbManager.getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT * FROM privacy_settings")) {
    // 处理结果
}
```

### SQL 注入

使用 PreparedStatement 防止 SQL 注入：

```java
String sql = "SELECT * FROM privacy_settings WHERE player_name = ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setString(1, playerName);
    // ...
}
```