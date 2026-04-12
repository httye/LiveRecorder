# 配置详解

## 配置文件概览

配置文件路径：`plugins/LiveRecorder/config.yml`

配置文件采用 YAML 格式，分为以下几个部分：

1. **镜头设置** (camera) - 控制镜头位置和跟随行为
2. **自动切换** (auto-switch) - 控制自动切换功能
3. **视觉反馈** (visual) - 控制视觉效果
4. **录制者限制** (recorder-restrictions) - 控制录制者操作限制
5. **隐私设置** (privacy) - 控制隐私相关功能

## 修改配置

### 方式一：直接编辑配置文件

1. 打开 `plugins/LiveRecorder/config.yml`
2. 修改需要调整的配置项
3. 保存文件
4. 在游戏内执行 `/lr reload` 热重载配置

### 方式二：使用命令热重载

修改配置后，直接在游戏内执行：
```
/lr reload
```

**注意：**
- 不需要重启服务器
- 不会影响现有的绑定
- 配置修改立即生效

## 配置文件结构

```yaml
# 镜头设置
camera:
  pitch: 30.0
  distance: 5.0
  height-offset: 0.0
  follow-speed: 0.35
  arrival-threshold: 0.3

# 自动切换设置
auto-switch:
  enabled: true
  interval: 30
  mode: RANDOM

# 视觉反馈
visual:
  target-glow: true
  glow-color: YELLOW
  camera-particle: false
  particle-type: END_ROD
  actionbar-enabled: true
  actionbar-interval: 20
  target-actionbar: true

# 录制者限制
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

# 隐私设置
privacy:
  recorder-invisible:
    enabled: true
    invisibility-message: "§6[LiveRecorder] §a你已隐身，其他玩家无法看到你"
  live-logs:
    enabled: true
    keep-count: 100
  consent-prompt:
    enabled: true
    timeout: 60

# 调试模式
debug: false
```

## 常见配置项

### 启用/禁用自动切换

```yaml
auto-switch:
  enabled: true  # true 为启用，false 为禁用
```

### 调整镜头距离

```yaml
camera:
  distance: 5.0  # 距离越远，镜头越远
```

### 调整跟随速度

```yaml
camera:
  follow-speed: 0.35  # 值越大，跟随越快
```

### 关闭录制者隐身

```yaml
privacy:
  recorder-invisible:
    enabled: false  # false 为禁用隐身
```

### 调整发光颜色

```yaml
visual:
  glow-color: YELLOW  # 可选：WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, CYAN, PURPLE, BLUE, GREEN, RED
```

## 详细说明

- [镜头设置](./camera) - 镜头位置和跟随行为
- [自动切换](./autoswitch) - 自动切换功能
- [视觉反馈](./visual) - 视觉效果
- [录制者限制](./restrictions) - 录制者操作限制
- [隐私设置](./privacy) - 隐私相关功能

## 配置验证

修改配置后，插件会自动验证配置的有效性。如果配置无效，插件会：

1. 在控制台输出警告信息
2. 使用默认值替代无效配置
3. 继续正常运行

## 调试模式

启用调试模式可以查看详细的运行日志：

```yaml
debug: true
```

**调试信息包括：**
- 录制者位置更新
- 镜头计算过程
- 自动切换触发
- 数据库操作

**注意：** 调试模式会产生大量日志，建议仅在排查问题时启用。