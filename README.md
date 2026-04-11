# 🎥 LiveRecorder - 无人机式自由录制者管理系统

一款面向直播/录屏场景的 Minecraft 服务器插件，定位于"无人机式"的自由录制者管理系统。插件会将指定的录制者玩家自动绑定到目标玩家，实时生成第三人称镜头，并在授权有效的前提下，按照预设的节奏自动切换跟拍对象。

## ✨ 特性

### 🎬 LiveRecorder 风格跟随系统
- 参考 LiveRecorder 实现方式，使用 `setVelocity` 实时跟随
- `PlayerMoveEvent` 触发，同步移动向量，画面无瞬移感
- 玩家传送时立即重新计算镜头位置并传送录制者

### 📐 镜头几何算法
- 使用 `BigDecimal` 实现高精度计算
- 镜头位置 = 目标玩家位置 + 俯角/距离偏移
- 支持自定义 `camera-pitch`（俯角）、`camera-distance`（距离）
- 弹性跟随算法，距离越大速度越快，避免瞬移感

### 👥 多录制者 / 多目标
- `LiveCore` 维护录制者绑定表
- `RecorderBinding` 支持自动模式/手动模式
- 支持随机切换 / 顺序切换活跃玩家

### 🔒 录制者操作限制
- 阻止录制者打开背包/容器、丢弃物品、交互
- 阻止攻击实体、放置/破坏方块
- 阻止使用非白名单命令
- 保证录制画面不会被背包/GUI 遮挡

### 🎨 视觉反馈
- 自动为目标玩家添加发光效果（支持自定义颜色）
- 可选粒子展示镜头位置
- 录制者 ActionBar 持续显示跟随状态
- 目标玩家 ActionBar 显示"🔴 您正在被直播"提示（含录制者数量）

---

## 📋 环境要求

| 项目 | 要求 |
|------|------|
| 服务端 | Spigot / Paper 1.16+ |
| Java | 8+ |

## 📦 安装

1. 将编译好的 `LiveRecorder-1.0.0.jar` 放入服务器的 `plugins/` 目录
2. 重启服务器（首次安装必须重启，不可热加载）
3. 编辑 `plugins/LiveRecorder/config.yml` 进行配置
4. 使用 `/lr reload` 热重载配置（无需重启）

---

## 🎮 如何使用

### 快速上手（3 步开始录制）

#### 第 1 步：启动服务器并进入游戏

确保插件已正确安装，服务器控制台会显示：
```
============================================
  LiveRecorder 无人机式录制系统已启动
  版本: 1.0.0
============================================
```

#### 第 2 步：绑定录制者到目标玩家

假设你要让玩家 `CameraMan` 跟拍玩家 `Steve`：

```
/lr bind CameraMan Steve auto
```

- `CameraMan` = 录制者（负责录屏/直播的玩家）
- `Steve` = 目标玩家（被跟拍的玩家）
- `auto` = 自动模式（会自动切换跟拍对象）

执行后：
- 录制者 `CameraMan` 会自动传送到 `Steve` 身后的镜头位置
- `CameraMan` 的屏幕上方会显示：`LiveRecorder | ● 跟随中 | 目标: Steve | 模式: 自动`
- `Steve` 的屏幕上方会显示：`🔴 您正在被直播 | 1 位录制者跟拍中`
- `Steve` 会获得黄色发光效果

#### 第 3 步：开始录制！

现在 `CameraMan` 只需使用 OBS 等录屏软件录制自己的 Minecraft 画面即可。插件会自动控制 `CameraMan` 的位置和朝向，始终保持第三人称跟拍视角。

---

### 使用场景详解

#### 场景一：单人直播跟拍

让一个录制者账号专门负责跟拍主播：

```
# 绑定录制者到主播
/lr bind Recorder01 主播小明 auto
```

录制者 `Recorder01` 会自动跟随主播，主播正常游戏即可。

#### 场景二：多机位直播

多个录制者从不同角度跟拍同一目标：

```
# 绑定多个录制者到同一目标
/lr bind Camera01 Steve auto
/lr bind Camera02 Steve auto
/lr bind Camera03 Steve auto
```

每个录制者都会自动跟随 `Steve`，导播可以在不同录制者之间切换画面。

#### 场景三：自动轮换跟拍（综艺节目风格）

录制者自动在不同玩家之间切换：

```
# 绑定录制者，自动模式
/lr bind Camera01 Steve auto

# 设置 15 秒自动切换
# 需要在 config.yml 中修改：
# auto-switch:
#   enabled: true
#   interval: 15
#   mode: RANDOM
/lr reload
```

`Camera01` 会每 15 秒随机切换到另一个在线目标玩家。

#### 场景四：手动控制跟拍目标

导播手动决定录制者跟拍谁：

```
# 绑定录制者，手动模式
/lr bind Camera01 Steve manual

# 手动切换到新目标
/lr switch Camera01 Alex

# 再切换回来
/lr switch Camera01 Steve
```

手动模式下，录制者不会自动切换目标，完全由导播控制。

#### 场景五：活动结束，解除绑定

```
# 解除单个录制者
/lr unbind Camera01

# 查看所有绑定，逐个解除
/lr list
/lr unbind Camera02
/lr unbind Camera03
```

---

### 命令参考

所有命令需要 `liverecorder.admin` 权限（默认 OP）。命令别名：`/lr`

| 命令 | 说明 | 示例 |
|------|------|------|
| `/lr bind <录制者> <目标> [auto\|manual]` | 绑定录制者到目标玩家 | `/lr bind Cam Steve auto` |
| `/lr unbind <录制者>` | 解除录制者绑定 | `/lr unbind Cam` |
| `/lr list` | 列出所有绑定 | `/lr list` |
| `/lr mode <录制者> <auto\|manual>` | 切换绑定模式 | `/lr mode Cam manual` |
| `/lr switch <录制者> <新目标>` | 手动切换跟拍目标 | `/lr switch Cam Alex` |
| `/lr reload` | 重载配置文件 | `/lr reload` |

---

### 录制者会怎样？

当玩家被绑定为录制者后，插件会自动限制其操作，确保录制画面干净：

| 限制项 | 说明 |
|--------|------|
| 🚫 不能移动 | 位置由插件自动控制 |
| 🚫 不能打开背包 | 防止背包界面遮挡画面 |
| 🚫 不能打开容器 | 防止箱子/GUI 遮挡画面 |
| 🚫 不能丢弃物品 | 防止误操作 |
| 🚫 不能交互方块 | 防止误触拉杆/按钮等 |
| 🚫 不能攻击实体 | 防止影响游戏进程 |
| 🚫 不能放置/破坏方块 | 防止影响游戏世界 |
| 🚫 不能使用非白名单命令 | 仅允许 `/lr` 相关命令 |

> 💡 录制者仍可使用白名单中的命令（默认为 `lr` 和 `liverecorder`），可在配置中自定义。

---

### 目标玩家会看到什么？

被跟拍的目标玩家会收到以下视觉提示：

1. **ActionBar 提示**：屏幕上方持续显示 `🔴 您正在被直播 | X 位录制者跟拍中`
2. **发光效果**：身体周围出现黄色发光轮廓（颜色可配置）
3. **聊天通知**：绑定/解绑/切换时会收到聊天消息通知

---

## ⚙️ 配置详解

配置文件路径：`plugins/LiveRecorder/config.yml`

```yaml
# ============================================
# 镜头设置
# ============================================
camera:
  pitch: 30.0           # 俯角（度数），0=水平，90=正上方
  distance: 5.0         # 镜头与目标玩家的水平距离（格）
  height-offset: 0.0    # 额外高度偏移（格）
  follow-speed: 0.35    # 跟随速度系数（0.0~1.0），值越大跟随越紧密
  arrival-threshold: 0.3 # 镜头到达阈值（格），小于此距离视为已到达

# ============================================
# 自动切换设置
# ============================================
auto-switch:
  enabled: true          # 是否启用自动切换
  interval: 30           # 自动切换间隔（秒）
  mode: RANDOM           # 切换模式：RANDOM(随机) | SEQUENTIAL(顺序)

# ============================================
# 视觉反馈
# ============================================
visual:
  target-glow: true      # 是否为目标玩家添加发光效果
  glow-color: YELLOW     # 发光颜色
  camera-particle: false # 是否在镜头位置显示粒子（调试用）
  particle-type: END_ROD # 粒子类型
  actionbar-enabled: true # ActionBar 状态显示（录制者）
  actionbar-interval: 20  # ActionBar 刷新间隔（tick，20tick=1秒）
  target-actionbar: true  # 是否在被直播的目标玩家 ActionBar 显示提示

# ============================================
# 录制者限制
# ============================================
recorder-restrictions:
  block-inventory: true   # 阻止打开背包
  block-container: true   # 阻止打开容器
  block-drop-item: true   # 阻止丢弃物品
  block-interact: true    # 阻止交互
  block-attack: true      # 阻止攻击实体
  block-place: true       # 阻止放置方块
  block-break: true       # 阻止破坏方块
  block-chat: true        # 阻止聊天
  block-command: true     # 阻止使用命令（白名单除外）
  command-whitelist:      # 命令白名单
    - lr
    - liverecorder

# 调试模式
debug: false
```

### 常用配置调整

#### 调整镜头角度和距离

```yaml
camera:
  pitch: 45.0     # 更高的俯角，鸟瞰视角
  distance: 8.0   # 更远的距离，视野更广
```

```yaml
camera:
  pitch: 15.0     # 较低的俯角，平视视角
  distance: 3.0   # 更近的距离，特写镜头
```

#### 调整跟随灵敏度

```yaml
camera:
  follow-speed: 0.5   # 更快的跟随，适合快节奏游戏
```

```yaml
camera:
  follow-speed: 0.2   # 更慢的跟随，画面更平滑
```

#### 关闭目标玩家的直播提示

```yaml
visual:
  target-actionbar: false  # 关闭"您正在被直播"提示
  target-glow: false       # 关闭发光效果
```

### 发光颜色列表

`WHITE`, `ORANGE`, `MAGENTA`, `LIGHT_BLUE`, `YELLOW`, `LIME`, `PINK`, `GRAY`, `CYAN`, `PURPLE`, `BLUE`, `GREEN`, `RED`

---

## 🏗️ 项目结构

```
LiveRecorder/
├── pom.xml                                        # Maven 构建配置
├── README.md                                      # 项目说明
├── LICENSE                                        # MIT 开源协议
├── .gitignore                                     # Git 忽略规则
├── .github/workflows/build.yml                    # GitHub Actions 自动编译
└── src/
    └── main/
        ├── java/com/liverecorder/
        │   ├── LiveRecorder.java                  # 主插件类
        │   ├── model/
        │   │   └── RecorderBinding.java           # 录制者绑定数据类
        │   ├── manager/
        │   │   └── LiveCore.java                  # 核心管理器（绑定表/跟随/切换）
        │   ├── util/
        │   │   └── CameraGeometry.java            # 镜头几何算法（BigDecimal高精度）
        │   ├── listener/
        │   │   ├── CameraListener.java            # 镜头跟随事件监听
        │   │   ├── RecorderRestrictionListener.java # 录制者操作限制
        │   │   └── VisualListener.java            # 视觉反馈（发光/粒子）
        │   └── command/
        │       ├── LiveRecorderCommand.java        # 命令处理器
        │       └── LiveRecorderTabCompleter.java   # Tab 补全
        └── resources/
            ├── plugin.yml                          # 插件描述文件
            └── config.yml                          # 默认配置文件
```

---

## 🔧 核心算法说明

### 镜头位置计算

```
镜头位置 = 目标玩家位置 + 偏移量

偏移量计算：
  dx = -distance × sin(yaw)       // 水平X偏移（在玩家身后）
  dz =  distance × cos(yaw)       // 水平Z偏移
  dy =  distance × tan(pitch) + heightOffset  // 垂直偏移
```

所有三角函数运算使用 `BigDecimal` 泰勒级数展开实现，确保高精度。

### 跟随算法

采用弹性跟随策略：
- 速度 = 位置差值 × 跟随速度系数
- 距离越大，速度越快，实现平滑追赶
- 最大速度限制为 2.0 格/tick，避免瞬移感
- 距离超过 30 格时自动传送

---

## ❓ 常见问题

### Q: 录制者画面出现瞬移怎么办？
A: 尝试调低 `follow-speed` 值（如 0.2），或增大 `distance` 让镜头更远。如果目标玩家频繁传送，这是正常行为——插件会自动传送录制者到新位置。

### Q: 录制者跟不上目标玩家怎么办？
A: 调高 `follow-speed` 值（如 0.5），或减小 `distance` 让镜头更近。

### Q: 如何让录制者使用旁观者模式？
A: 在绑定录制者之前，先将录制者设为旁观者模式：`/gamemode spectator CameraMan`，然后再执行 `/lr bind`。

### Q: 目标玩家下线了怎么办？
A: 插件会自动将录制者切换到其他在线目标。如果没有其他目标，跟随会暂停，录制者会收到通知。

### Q: 可以同时跟拍多个目标吗？
A: 一个录制者同一时间只能跟拍一个目标。但你可以绑定多个录制者到不同目标，实现多机位效果。

### Q: 如何关闭录制者的操作限制？
A: 在 `config.yml` 中将对应的限制项设为 `false`，然后执行 `/lr reload`。

---

## 📄 权限

| 权限 | 说明 | 默认 |
|------|------|------|
| `liverecorder.admin` | 允许使用所有 LiveRecorder 命令 | OP |
| `liverecorder.recorder` | 录制者权限 | false |

---

## 📜 开源协议

本项目基于 [MIT License](LICENSE) 开源。

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request