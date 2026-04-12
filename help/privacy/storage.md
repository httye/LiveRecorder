# 数据存储

LiveRecorder 使用 SQLite 数据库存储隐私设置和直播日志。

## 数据库信息

### 数据库位置

```
plugins/LiveRecorder/privacy.db
```

### 数据库类型

SQLite 3.x

### 访问方式

#### 命令行

```bash
# 使用 sqlite3 命令
sqlite3 plugins/LiveRecorder/privacy.db
```

#### 图形化工具

推荐使用以下工具：
- DB Browser for SQLite
- SQLiteStudio
- DBeaver

#### 编程方式

```java
// 使用 JDBC 连接
Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/LiveRecorder/privacy.db");
```

## 数据表结构

### privacy_settings 表

存储玩家的隐私设置。

| 字段 | 类型 | 说明 | 示例 |
|:-----|:-----|:-----|:-----|
| player_uuid | TEXT | 玩家 UUID（主键） | "550e8400-e29b-41d4-a716-446655440000" |
| player_name | TEXT | 玩家名称 | "Steve" |
| consent_status | TEXT | 同意状态 | "ACCEPTED" |
| invisible | INTEGER | 是否隐身（0/1） | 0 |
| last_updated | INTEGER | 最后更新时间戳 | 1681308625000 |

**consent_status 枚举值：**
- `ACCEPTED` - 同意直播
- `DECLINED` - 拒绝直播
- `UNSET` - 未设置

### live_logs 表

存储直播操作日志。

| 字段 | 类型 | 说明 | 示例 |
|:-----|:-----|:-----|:-----|
| id | INTEGER | 日志 ID（主键，自增） | 1 |
| log_type | TEXT | 日志类型 | "START" |
| recorder_uuid | TEXT | 录制者 UUID | "660e8400-e29b-41d4-a716-446655440000" |
| recorder_name | TEXT | 录制者名称 | "Camera01" |
| target_uuid | TEXT | 目标 UUID | "550e8400-e29b-41d4-a716-446655440000" |
| target_name | TEXT | 目标名称 | "Steve" |
| timestamp | INTEGER | 时间戳（毫秒） | 1681308625000 |
| extra_info | TEXT | 额外信息 | "" |

**log_type 枚举值：**
- `START` - 开始直播
- `END` - 结束直播
- `SWITCH` - 切换目标
- `ACCEPTED` - 同意直播
- `DECLINED` - 拒绝直播

## 常用查询

### 隐私设置查询

#### 查看所有隐私设置

```sql
SELECT * FROM privacy_settings;
```

#### 查看特定玩家的隐私设置

```sql
SELECT * FROM privacy_settings WHERE player_name = 'Steve';
```

#### 查看所有同意直播的玩家

```sql
SELECT * FROM privacy_settings WHERE consent_status = 'ACCEPTED';
```

#### 查看所有拒绝直播的玩家

```sql
SELECT * FROM privacy_settings WHERE consent_status = 'DECLINED';
```

#### 查看所有未设置的玩家

```sql
SELECT * FROM privacy_settings WHERE consent_status = 'UNSET';
```

### 日志查询

#### 查看所有日志

```sql
SELECT * FROM live_logs ORDER BY timestamp DESC;
```

#### 查看最近的 10 条日志

```sql
SELECT * FROM live_logs ORDER BY timestamp DESC LIMIT 10;
```

#### 查看特定录制者的日志

```sql
SELECT * FROM live_logs WHERE recorder_name = 'Camera01';
```

#### 查看特定目标的日志

```sql
SELECT * FROM live_logs WHERE target_name = 'Steve';
```

#### 查看特定类型的日志

```sql
SELECT * FROM live_logs WHERE log_type = 'START';
```

#### 统计日志数量

```sql
SELECT log_type, COUNT(*) as count 
FROM live_logs 
GROUP BY log_type;
```

### 统计分析

#### 统计直播次数

```sql
SELECT recorder_name, COUNT(*) as live_count
FROM live_logs
WHERE log_type = 'START'
GROUP BY recorder_name
ORDER BY live_count DESC;
```

#### 统计被直播次数

```sql
SELECT target_name, COUNT(*) as streamed_count
FROM live_logs
WHERE log_type = 'START'
GROUP BY target_name
ORDER BY streamed_count DESC;
```

#### 统计同意/拒绝比例

```sql
SELECT 
    consent_status, 
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM privacy_settings), 2) as percentage
FROM privacy_settings
GROUP BY consent_status;
```

## 数据备份

### 自动备份

插件不提供自动备份功能，建议定期手动备份。

### 手动备份

```bash
# 备份数据库
cp plugins/LiveRecorder/privacy.db plugins/LiveRecorder/privacy.db.backup.$(date +%Y%m%d)
```

### 定时备份

使用 cron 定时备份：

```bash
# 每天凌晨 2 点备份
0 2 * * * cp /path/to/plugins/LiveRecorder/privacy.db /path/to/backup/privacy.db.$(date +\%Y\%m\%d)
```

## 数据恢复

### 从备份恢复

```bash
# 停止服务器
cp plugins/LiveRecorder/privacy.db.backup plugins/LiveRecorder/privacy.db
# 启动服务器
```

### 从 SQL 文件恢复

```bash
# 导出为 SQL 文件
sqlite3 plugins/LiveRecorder/privacy.db .dump > backup.sql

# 从 SQL 文件恢复
sqlite3 plugins/LiveRecorder/privacy.db < backup.sql
```

## 数据迁移

### 迁移到新数据库

```bash
# 1. 导出数据
sqlite3 old.db .dump > dump.sql

# 2. 导入到新数据库
sqlite3 new.db < dump.sql

# 3. 替换数据库
cp new.db plugins/LiveRecorder/privacy.db
```

## 常见问题

### 数据库损坏怎么办？

如果数据库损坏，尝试：
1. 使用 SQLite 工具修复：`sqlite3 privacy.db ".recover" | sqlite3 recovery.db`
2. 从备份恢复
3. 重新创建数据库（会丢失所有数据）

### 如何清空所有数据？

```bash
# 删除数据库文件（会丢失所有数据）
rm plugins/LiveRecorder/privacy.db

# 重启服务器，插件会自动创建新的数据库
```

### 数据库文件有多大？

数据库大小取决于：
- 隐私设置数量（通常很小，几 KB）
- 日志数量（取决于 keep-count 配置）

一般情况下，数据库文件不会超过几 MB。

### 如何优化数据库？

SQLite 会自动优化，通常不需要手动优化。如果需要：

```bash
sqlite3 plugins/LiveRecorder/privacy.db "VACUUM;"
```

## 最佳实践

### 定期备份

建议每天备份一次数据库：
```bash
cp plugins/LiveRecorder/privacy.db backup/privacy.db.$(date +%Y%m%d)
```

### 监控数据库大小

定期检查数据库大小，避免过大：
```bash
ls -lh plugins/LiveRecorder/privacy.db
```

### 合理设置日志保留数量

根据服务器情况设置合理的 keep-count：
- 小型服务器：50-100 条
- 中型服务器：100-200 条
- 大型服务器：200-500 条

### 使用查询工具

使用图形化工具查看和管理数据：
- DB Browser for SQLite
- SQLiteStudio
- DBeaver