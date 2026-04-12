# 自动切换

自动切换设置控制录制者在不同目标玩家之间自动切换的行为。

## 配置项

```yaml
auto-switch:
  enabled: true      # 是否启用自动切换
  interval: 30       # 自动切换间隔（秒）
  mode: RANDOM       # 切换模式
```

## 参数详解

### enabled（启用状态）

是否启用自动切换功能。

- **类型：** boolean
- **默认值：** true
- **选项：** true / false

**效果：**
- `true` - 启用自动切换，录制者会自动切换目标
- `false` - 禁用自动切换，录制者不会自动切换

### interval（切换间隔）

自动切换的时间间隔。

- **类型：** int
- **单位：** 秒
- **默认值：** 30
- **范围：** 10 - 300

**调整建议：**
- 快速切换：10-20 秒（适合综艺节目）
- 标准切换：30-60 秒（适合日常直播）
- 慢速切换：90-180 秒（适合慢节奏活动）

### mode（切换模式）

自动切换的目标选择模式。

- **类型：** string
- **默认值：** RANDOM
- **选项：**
  - `RANDOM` - 随机选择
  - `SEQUENTIAL` - 顺序选择

**模式说明：**

#### RANDOM（随机模式）

随机选择下一个目标，适合综艺风格的直播。

**特点：**
- 不可预测性
- 更有趣味性
- 每个目标被选中的概率相同

#### SEQUENTIAL（顺序模式）

按顺序轮流选择目标，适合公平的轮播。

**特点：**
- 可预测性
- 每个目标轮流出现
- 适合多人活动展示

## 使用场景

### 综艺节目风格

```yaml
auto-switch:
  enabled: true
  interval: 15
  mode: RANDOM
```

快速随机切换，营造紧张刺激的氛围。

### 多人活动展示

```yaml
auto-switch:
  enabled: true
  interval: 45
  mode: SEQUENTIAL
```

按顺序轮流展示每个玩家，公平公正。

### 慢节奏活动

```yaml
auto-switch:
  enabled: true
  interval: 120
  mode: RANDOM
```

慢速随机切换，适合悠闲的活动。

## 注意事项

### 手动切换的影响

即使启用了自动切换，管理员也可以手动切换目标：

```bash
/lr switch CameraMan Alex
```

手动切换后，自动切换的计时器会重置。

### 在线玩家数量

自动切换需要至少 2 个在线玩家才能正常工作。如果只有 1 个在线玩家，自动切换会暂停。

### 目标玩家离线

如果当前目标玩家离线，录制者会立即切换到下一个在线目标。

## 常见问题

### 自动切换不工作？

检查以下配置：
1. `enabled` 是否为 `true`
2. 录制者是否为 AUTO 模式
3. 是否有至少 2 个在线玩家

### 如何调整切换速度？

修改 `interval` 值：
- 更快：减小 interval（如 15）
- 更慢：增大 interval（如 60）

### 如何让录制者不自动切换？

将 `enabled` 设置为 `false`，或将录制者模式改为 MANUAL：

```bash
/lr mode CameraMan manual
```

### RANDOM 和 SEQUENTIAL 有什么区别？

- **RANDOM** - 随机选择，不可预测
- **SEQUENTIAL** - 按顺序轮流，可预测