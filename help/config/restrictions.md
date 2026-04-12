# 录制者限制

录制者限制设置控制录制者可以进行的操作，确保录制画面干净。

## 配置项

```yaml
recorder-restrictions:
  block-inventory: true   # 阻止打开背包
  block-container: true   # 阻止打开容器
  block-drop-item: true   # 阻止丢弃物品
  block-interact: true    # 阻止交互方块
  block-attack: true      # 阻止攻击实体
  block-place: true       # 阻止放置方块
  block-break: true       # 阻止破坏方块
  block-chat: true        # 阻止聊天
  block-command: true     # 阻止使用命令
  command-whitelist:      # 命令白名单
    - lr
    - liverecorder
```

## 参数详解

### block-inventory（阻止背包）

是否阻止录制者打开背包。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 背包界面会遮挡录制画面，影响观看体验。

### block-container（阻止容器）

是否阻止录制者打开容器（箱子、熔炉等）。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 容器界面会遮挡录制画面，影响观看体验。

### block-drop-item（阻止丢弃）

是否阻止录制者丢弃物品。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 防止录制者误操作，避免影响游戏进程。

### block-interact（阻止交互）

是否阻止录制者交互方块（拉杆、按钮、门等）。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 防止录制者误触机关，避免影响游戏进程。

### block-attack（阻止攻击）

是否阻止录制者攻击实体。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 防止录制者攻击其他玩家或生物，避免影响游戏进程。

### block-place（阻止放置）

是否阻止录制者放置方块。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 防止录制者改变游戏世界，避免影响游戏进程。

### block-break（阻止破坏）

是否阻止录制者破坏方块。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 防止录制者破坏游戏世界，避免影响游戏进程。

### block-chat（阻止聊天）

是否阻止录制者聊天。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 录制者不应参与聊天，保持纯粹的角色。

### block-command（阻止命令）

是否阻止录制者使用命令（白名单除外）。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**原因：** 防止录制者使用命令影响游戏。

### command-whitelist（命令白名单）

录制者可以使用的命令列表。

- **类型：** list
- **默认值：** [lr, liverecorder]
- **格式：** 命令名称（不带斜杠）

**添加命令：**
```yaml
command-whitelist:
  - lr
  - liverecorder
  - tp
  - gamemode
```

## 预设配置

### 严格限制（推荐）

```yaml
recorder-restrictions:
  block-inventory: true
  block-container: true
  block-drop-item: true
  block-interact: true
  block-attack: true
  block-place: true
  block-break: true
  block-chat: true
  block-command: true
  command-whitelist:
    - lr
    - liverecorder
```

完全限制录制者的操作，确保纯粹的角色。

### 宽松限制

```yaml
recorder-restrictions:
  block-inventory: false
  block-container: false
  block-drop-item: true
  block-interact: true
  block-attack: true
  block-place: true
  block-break: true
  block-chat: false
  block-command: true
  command-whitelist:
    - lr
    - liverecorder
    - tp
    - gamemode
    - msg
```

允许录制者打开背包和聊天，但仍然限制大部分操作。

### 无限制

```yaml
recorder-restrictions:
  block-inventory: false
  block-container: false
  block-drop-item: false
  block-interact: false
  block-attack: false
  block-place: false
  block-break: false
  block-chat: false
  block-command: false
  command-whitelist: []
```

完全取消限制，录制者可以自由操作（不推荐）。

## 常见问题

### 录制者为什么不能打开背包？

因为 `block-inventory` 设置为 `true`，这是为了防止背包界面遮挡画面。

### 录制者为什么不能聊天？

因为 `block-chat` 设置为 `true`，这是为了保持录制者的纯粹角色。

### 如何允许录制者使用特定命令？

在 `command-whitelist` 中添加命令名称：

```yaml
command-whitelist:
  - lr
  - liverecorder
  - tp
  - gamemode
```

### 如何取消所有限制？

将所有 `block-*` 项设置为 `false`：

```yaml
recorder-restrictions:
  block-inventory: false
  block-container: false
  block-drop-item: false
  block-interact: false
  block-attack: false
  block-place: false
  block-break: false
  block-chat: false
  block-command: false
```

## 注意事项

### 限制的必要性

录制者限制是为了确保：
1. 录制画面干净，没有 GUI 遮挡
2. 录制者不会影响游戏进程
3. 录制者保持纯粹的角色

### 灵活配置

可以根据实际需求调整限制：
- 正常直播：使用严格限制
- 特殊场景：适当放宽限制
- 测试环境：可以使用无限制配置

### 权限管理

建议给录制者账号单独的权限节点：
```yaml
permissions:
  liverecorder.recorder:
    default: false
```

然后只给录制者账号授予权限，方便管理。