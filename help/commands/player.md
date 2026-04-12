# 玩家命令

## /lr accept

同意直播请求。

**语法：**
```
/lr accept
```

**权限：** `liverecorder.use`

**示例：**
```bash
/lr accept
```

**效果：**
- 同意当前待确认的直播请求
- 隐私状态设置为 ACCEPTED
- 录制者开始跟随
- 记录同意日志

**使用场景：**
当管理员尝试绑定录制者到你时，你会收到请求：
```
§6[LiveRecorder] §e录制者 CameraMan 请求直播您的视角
§6[LiveRecorder] §a输入 /lr accept §7同意直播
§6[LiveRecorder] §c输入 /lr decline §7拒绝直播
```

输入 `/lr accept` 即可同意。

## /lr decline

拒绝直播请求。

**语法：**
```
/lr decline
```

**权限：** `liverecorder.use`

**示例：**
```bash
/lr decline
```

**效果：**
- 拒绝当前待确认的直播请求
- 隐私状态设置为 DECLINED
- 录制者收到拒绝通知
- 记录拒绝日志

**注意：**
- 拒绝后，录制者将无法绑定到你
- 以后也不会收到该录制者的请求
- 如果想重新允许被直播，需要使用 `/lr setprivacy accept`

## /lr privacy

查看个人隐私设置。

**语法：**
```
/lr privacy
```

**权限：** `liverecorder.use`

**示例：**
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

**字段说明：**
- `隐私状态` - 当前隐私设置（同意/拒绝/未设置）
- `隐身` - 是否隐身（针对录制者）
- `最后更新` - 最后一次更新时间

## /lr setprivacy

设置个人隐私状态。

**语法：**
```
/lr setprivacy <accept|decline|unset>
```

**参数：**
- `<accept|decline|unset>` - 隐私状态

**权限：** `liverecorder.use`

**示例：**
```bash
# 设置为同意直播
/lr setprivacy accept

# 设置为拒绝直播
/lr setprivacy decline

# 重置为未设置
/lr setprivacy unset
```

**状态说明：**

### accept（同意）
- 允许任何人绑定录制者跟拍你
- 不会收到确认请求
- 录制者可以立即开始跟随

### decline（拒绝）
- 禁止任何人绑定录制者跟拍你
- 录制者尝试绑定时会收到拒绝通知
- 适合注重隐私的玩家

### unset（未设置）
- 每次被直播时需要确认
- 会收到直播请求
- 选择会持久化保存

## 隐私设置建议

### 对于普通玩家

如果你想偶尔被直播，建议使用 `unset`：
```bash
/lr setprivacy unset
```

这样每次被直播时都会收到请求，可以自主决定。

### 对于主播/嘉宾

如果你经常被直播，建议设置为 `accept`：
```bash
/lr setprivacy accept
```

这样可以避免每次都收到请求。

### 对于注重隐私的玩家

如果你不想被直播，建议设置为 `decline`：
```bash
/lr setprivacy decline
```

这样可以完全拒绝被直播。

## 常见问题

### 我同意了直播，现在想取消怎么办？

使用以下命令：
```bash
/lr setprivacy decline
```

然后让管理员解绑录制者：
```bash
/lr unbind CameraMan
```

### 我设置了拒绝，现在想允许被直播怎么办？

使用以下命令：
```bash
/lr setprivacy accept
```

或者设置为未设置，每次确认：
```bash
/lr setprivacy unset
```

### 为什么我收到了直播请求？

因为你的隐私设置为 `unset`（未设置），每次被直播时需要确认。

如果不想收到请求，可以设置为 `accept` 或 `decline`。

### 录制者为什么看不到我？

这不是隐私设置的问题，而是录制者的隐身功能。录制者在直播时会对其他玩家隐身，这是正常行为。

### 如何查看我的隐私设置？

使用以下命令：
```bash
/lr privacy
```