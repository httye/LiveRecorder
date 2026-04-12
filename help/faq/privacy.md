# 隐私问题

关于隐私功能的常见问题。

## 玩家如何拒绝被直播？

### 方法一：使用命令拒绝当前请求

```bash
/lr decline
```

### 方法二：永久拒绝被直播

```bash
/lr setprivacy decline
```

### 方法三：每次确认

```bash
/lr setprivacy unset
```

## 玩家如何允许被直播？

### 方法一：使用命令同意当前请求

```bash
/lr accept
```

### 方法二：永久允许被直播

```bash
/lr setprivacy accept
```

## 如何查看隐私设置？

### 查看个人隐私设置

```bash
/lr privacy
```

**输出示例：**
```
========== 隐私设置 ==========
玩家: Steve
隐私状态: 同意直播
隐身: 否
最后更新: 2026-04-12 14:30:25
=================================
```

## 录制者为什么看不到我？

这不是隐私设置的问题，而是录制者的隐身功能。

**说明：**
- 录制者在直播时会对其他玩家隐身
- 这是正常行为，为了不影响其他玩家的游戏体验
- 录制者仍然可以看到自己

## 隐私数据保存在哪里？

### 数据库位置

```
plugins/LiveRecorder/privacy.db
```

### 数据类型

SQLite 数据库文件

### 数据内容

- 隐私设置（privacy_settings 表）
- 直播日志（live_logs 表）

## 如何清理旧的直播日志？

### 自动清理

插件会自动清理超出限制的日志：

```yaml
privacy:
  live-logs:
    keep-count: 100  # 保留最近 100 条
```

### 手动清理

#### 方法一：减小保留数量

修改配置文件，减小 `keep-count` 值，然后重启服务器。

#### 方法二：删除数据库文件（不推荐）

```bash
rm plugins/LiveRecorder/privacy.db
```

**注意：** 这会删除所有隐私设置和日志数据。

## 录制者为什么能隐身？

### 配置启用

录制者隐身功能默认启用：

```yaml
privacy:
  recorder-invisible:
    enabled: true
```

### 工作原理

使用 Minecraft 的隐藏玩家机制：

```java
// 对所有其他玩家隐藏录制者
for (Player other : Bukkit.getOnlinePlayers()) {
    if (!other.equals(recorder)) {
        other.hidePlayer(plugin, recorder);
    }
}
```

### 如何禁用

修改配置文件：

```yaml
privacy:
  recorder-invisible:
    enabled: false
```

然后执行 `/lr reload`。

## 隐私设置会影响游戏平衡吗？

### 不会

隐私功能不会影响游戏平衡：
- 只控制录制者能否绑定到目标
- 不影响其他游戏机制
- 录制者仍然受到录制者限制

### 公平性

隐私功能保护玩家的隐私权：
- 玩家可以自主决定是否被直播
- 不会给任何玩家带来不公平的优势
- 适用于所有玩家

## 如何管理大量玩家的隐私设置？

### 批量查询

目前插件不支持批量查询，需要玩家自行查看：

```bash
/lr privacy
```

### 数据库查询

管理员可以直接查询数据库：

```sql
-- 查看所有隐私设置
SELECT * FROM privacy_settings;

-- 查看所有同意直播的玩家
SELECT * FROM privacy_settings WHERE consent_status = 'ACCEPTED';

-- 查看所有拒绝直播的玩家
SELECT * FROM privacy_settings WHERE consent_status = 'DECLINED';
```

### 统计分析

```sql
-- 统计隐私设置分布
SELECT 
    consent_status, 
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM privacy_settings), 2) as percentage
FROM privacy_settings
GROUP BY consent_status;
```

## 隐私设置会持久化吗？

### 会持久化

隐私设置会保存到 SQLite 数据库：
- 即使服务器重启，设置仍然有效
- 玩家不需要每次都设置
- 数据存储在 `plugins/LiveRecorder/privacy.db`

### 何时清除

隐私设置只在以下情况下清除：
1. 玩家手动修改设置
2. 管理员删除数据库文件（不推荐）
3. 玩家使用 `/lr setprivacy unset` 重置

## 如何保护隐私数据？

### 数据库权限

确保 `plugins/LiveRecorder/` 目录的访问权限正确：

```bash
# 设置目录权限
chmod 755 plugins/LiveRecorder/

# 设置数据库权限
chmod 644 plugins/LiveRecorder/privacy.db
```

### 定期备份

定期备份数据库：

```bash
cp plugins/LiveRecorder/privacy.db backup/privacy.db.$(date +%Y%m%d)
```

### 限制访问

只给需要的管理员数据库访问权限。

## 常见问题

### 玩家忘记隐私设置怎么办？

让玩家查看自己的隐私设置：
```bash
/lr privacy
```

### 如何重置所有隐私设置？

删除数据库文件（会丢失所有数据）：
```bash
rm plugins/LiveRecorder/privacy.db
```

重启服务器，插件会自动创建新的数据库。

### 隐私功能可以禁用吗？

可以，修改配置文件：
```yaml
privacy:
  consent-prompt:
    enabled: false
```

但建议保持启用，以保护玩家隐私。