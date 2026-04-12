# 管理员命令

## /lr bind

绑定录制者到目标玩家。

**语法：**
```
/lr bind <录制者> <目标> [auto|manual]
```

**参数：**
- `录制者` - 录制者玩家名称
- `目标` - 目标玩家名称
- `[auto|manual]` - 绑定模式（可选，默认为 auto）

**权限：** `liverecorder.admin`

**示例：**
```bash
# 绑定为自动模式
/lr bind CameraMan Steve auto

# 绑定为手动模式
/lr bind CameraMan Steve manual

# 使用默认模式（auto）
/lr bind CameraMan Steve
```

**返回值：**
- `0` - 绑定成功
- `1` - 录制者已绑定
- `2` - 目标玩家拒绝直播
- `3` - 等待目标玩家确认

## /lr unbind

解除录制者绑定。

**语法：**
```
/lr unbind <录制者>
```

**参数：**
- `录制者` - 录制者玩家名称

**权限：** `liverecorder.admin`

**示例：**
```bash
/lr unbind CameraMan
```

**效果：**
- 录制者停止跟随
- 录制者取消隐身
- 记录结束日志
- 目标玩家停止发光

## /lr list

列出所有活跃的录制者绑定。

**语法：**
```
/lr list
```

**权限：** `liverecorder.admin`

**示例：**
```bash
/lr list
```

**输出示例：**
```
========== LiveRecorder 绑定列表 ==========
共 2 个录制者
1. Camera01 → Steve | 模式: 自动 | 状态: 活跃
2. Camera02 → Alex | 模式: 手动 | 状态: 活跃
===========================================
```

## /lr mode

切换录制者的绑定模式。

**语法：**
```
/lr mode <录制者> <auto|manual>
```

**参数：**
- `录制者` - 录制者玩家名称
- `<auto|manual>` - 新的绑定模式

**权限：** `liverecorder.admin`

**示例：**
```bash
# 切换为自动模式
/lr mode CameraMan auto

# 切换为手动模式
/lr mode CameraMan manual
```

## /lr switch

手动切换录制者的跟拍目标。

**语法：**
```
/lr switch <录制者> <新目标>
```

**参数：**
- `录制者` - 录制者玩家名称
- `<新目标>` - 新的目标玩家名称

**权限：** `liverecorder.admin`

**示例：**
```bash
/lr switch CameraMan Alex
```

**注意：**
- 仅在手动模式下有效
- 自动模式下也可以手动切换，但会重置切换计时器

## /lr reload

重载配置文件。

**语法：**
```
/lr reload
```

**权限：** `liverecorder.admin`

**示例：**
```bash
/lr reload
```

**效果：**
- 重新加载 config.yml
- 重启所有任务
- 应用新的配置参数

**不会：**
- 不影响现有的绑定
- 不会停止录制
- 不会清除日志

## /lr logs

查看直播日志。

**语法：**
```
/lr logs [数量]
```

**参数：**
- `[数量]` - 日志数量（可选，默认 10，最大 50）

**权限：** `liverecorder.admin`

**示例：**
```bash
# 查看最近 10 条日志
/lr logs

# 查看最近 20 条日志
/lr logs 20
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

**日志类型：**
- `START` - 开始直播
- `END` - 结束直播
- `SWITCH` - 切换目标
- `ACCEPTED` - 同意直播
- `DECLINED` - 拒绝直播

## 常见问题

### 为什么无法绑定录制者？

可能的原因：
1. 录制者已经绑定到其他目标
2. 目标玩家拒绝被直播
3. 目标玩家离线
4. 录制者离线

### 如何查看录制者的当前目标？

使用 `/lr list` 命令查看所有绑定的详细信息。

### 可以同时绑定多个录制者吗？

可以，一个目标可以同时被多个录制者跟拍。

### 如何停止所有录制者？

```bash
# 查看所有录制者
/lr list

# 逐个解绑
/lr unbind Camera01
/lr unbind Camera02
/lr unbind Camera03
```