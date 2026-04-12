# 直播日志

所有直播操作都会记录到数据库，便于追溯和审计。

## 日志类型

### START（开始直播）

记录录制者开始直播的事件。

**记录内容：**
- 录制者 UUID 和名称
- 目标玩家 UUID 和名称
- 时间戳

**示例：**
```
[14:30:25] 录制者 Camera01 开始直播 Steve
```

### END（结束直播）

记录录制者结束直播的事件。

**记录内容：**
- 录制者 UUID 和名称
- 目标玩家 UUID 和名称
- 时间戳

**示例：**
```
[14:45:30] 录制者 Camera01 结束直播 Steve
```

### SWITCH（切换目标）

记录录制者切换跟拍目标的事件。

**记录内容：**
- 录制者 UUID 和名称
- 目标玩家 UUID 和名称
- 时间戳

**示例：**
```
[14:29:45] 录制者 Camera01 从 Steve 切换到 Alex
```

### ACCEPTED（同意直播）

记录玩家同意直播的事件。

**记录内容：**
- 目标玩家 UUID 和名称
- 时间戳

**示例：**
```
[14:30:15] 玩家 Alex 同意被直播
```

### DECLINED（拒绝直播）

记录玩家拒绝直播的事件。

**记录内容：**
- 目标玩家 UUID 和名称
- 时间戳

**示例：**
```
[14:28:00] 玩家 Bob 拒绝被直播
```

## 配置项

### enabled（启用状态）

是否启用直播日志记录。

**配置：**
```yaml
privacy:
  live-logs:
    enabled: true  # true 为启用，false 为禁用
```

**选项：**
- `true` - 启用日志记录（推荐）
- `false` - 禁用日志记录

### keep-count（保留数量）

日志保留的数量。

**配置：**
```yaml
privacy:
  live-logs:
    keep-count: 100  # 保留最近 100 条日志
```

**范围：** 10 - 1000

**效果：**
- 插件会自动保留最近 N 条日志
- 超出限制的日志会在下次启动时自动清理

## 查看日志

### 命令查看

使用命令查看最近的直播日志：

```bash
# 查看最近 10 条日志（默认）
/lr logs

# 查看最近 20 条日志
/lr logs 20

# 查看最近 50 条日志
/lr logs 50
```

**输出示例：**
```
========== 直播日志 (最近 5 条) ==========
[14:30:25] 录制者 Camera01 开始直播 Steve
[14:30:15] 玩家 Alex 同意被直播
[14:29:45] 录制者 Camera01 从 Steve 切换到 Alex
[14:28:30] 录制者 Camera01 开始直播 Alex
[14:28:00] 玩家 Bob 拒绝被直播
==========================================
```

### 数据库查询

直接查询 SQLite 数据库：

```sql
-- 查看所有日志
SELECT * FROM live_logs ORDER BY timestamp DESC LIMIT 10;

-- 查看特定录制者的日志
SELECT * FROM live_logs WHERE recorder_name = 'Camera01';

-- 查看特定目标的日志
SELECT * FROM live_logs WHERE target_name = 'Steve';

-- 查看特定类型的日志
SELECT * FROM live_logs WHERE log_type = 'START';
```

## 数据存储

### 数据库位置

```
plugins/LiveRecorder/privacy.db
```

### 数据表结构

**live_logs 表：**

| 字段 | 类型 | 说明 |
|:-----|:-----|:-----|
| id | INTEGER | 日志 ID（主键，自增） |
| log_type | TEXT | 日志类型（START, END, SWITCH, ACCEPTED, DECLINED） |
| recorder_uuid | TEXT | 录制者 UUID |
| recorder_name | TEXT | 录制者名称 |
| target_uuid | TEXT | 目标 UUID |
| target_name | TEXT | 目标名称 |
| timestamp | INTEGER | 时间戳（毫秒） |
| extra_info | TEXT | 额外信息 |

## 日志管理

### 自动清理

插件会在下次启动时自动清理超出限制的日志：

```yaml
privacy:
  live-logs:
    keep-count: 100  # 保留最近 100 条
```

### 手动清理

管理员可以手动清理旧日志：

```bash
# 方法一：减小 keep-count 配置
# （修改配置文件后重启服务器）

# 方法二：直接删除数据库文件（不推荐）
rm plugins/LiveRecorder/privacy.db
```

### 导出日志

使用 SQLite 工具导出日志：

```bash
# 使用 sqlite3 命令
sqlite3 plugins/LiveRecorder/privacy.db "SELECT * FROM live_logs;" > logs.csv

# 或使用图形化工具（如 DB Browser for SQLite）
```

## 常见问题

### 日志保存在哪里？

日志保存在 `plugins/LiveRecorder/privacy.db` SQLite 数据库文件中。

### 如何查看日志？

使用命令查看：
```bash
/lr logs 20
```

或直接查询数据库。

### 如何清理旧日志？

插件会自动清理超出限制的日志。也可以手动减小 `keep-count` 配置。

### 日志会影响性能吗？

日志对性能影响很小：
- 只在事件发生时记录
- 不会持续消耗资源
- 使用 SQLite 高效存储

### 如何导出日志？

使用 SQLite 工具导出：
```bash
sqlite3 plugins/LiveRecorder/privacy.db "SELECT * FROM live_logs;" > logs.csv
```

## 最佳实践

### 保留足够的日志

根据活动规模设置合理的保留数量：
- 小型活动：50-100 条
- 中型活动：100-200 条
- 大型活动：200-500 条

### 定期检查日志

定期查看日志，了解直播情况：
- 检查是否有异常操作
- 了解玩家隐私设置
- 追溯问题根源

### 备份日志

重要活动前备份日志：
```bash
cp plugins/LiveRecorder/privacy.db plugins/LiveRecorder/privacy.db.backup
```

### 使用日志分析

使用日志分析工具：
- 统计直播时长
- 分析跟拍模式
- 了解玩家参与度