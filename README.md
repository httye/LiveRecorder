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
- ActionBar 持续显示当前状态

---

## 📋 环境要求

| 项目 | 要求 |
|------|------|
| 服务端 | Spigot / Paper 1.16+ |
| Java | 8+ |
| 构建工具 | Maven 3.6+ |

---

## 🔨 编译

```bash
# 克隆仓库
git clone https://github.com/yourname/LiveRecorder.git
cd LiveRecorder

# 编译
mvn clean package

# 编译产物位于 target/LiveRecorder-1.0.0.jar
```

---

## 📦 安装

1. 将编译好的 `LiveRecorder-1.0.0.jar` 放入服务器的 `plugins/` 目录
2. 重启服务器
3. 编辑 `plugins/LiveRecorder/config.yml` 进行配置
4. 使用 `/lr reload` 热重载配置

---

## 🎮 命令

所有命令需要 `liverecorder.admin` 权限（默认 OP）。

| 命令 | 说明 |
|------|------|
| `/lr bind <录制者> <目标> [auto\|manual]` | 绑定录制者到目标玩家 |
| `/lr unbind <录制者>` | 解除录制者绑定 |
| `/lr list` | 列出所有绑定 |
| `/lr mode <录制者> <auto\|manual>` | 切换绑定模式 |
| `/lr switch <录制者> <新目标>` | 手动切换跟拍目标 |
| `/lr reload` | 重载配置文件 |

### 命令示例

```
# 将玩家 CameraMan 绑定到玩家 Steve，自动模式
/lr bind CameraMan Steve auto

# 将玩家 CameraMan 切换为手动模式
/lr mode CameraMan manual

# 手动切换 CameraMan 的跟拍目标为 Alex
/lr switch CameraMan Alex

# 解除 CameraMan 的录制者绑定
/lr unbind CameraMan

# 查看所有绑定
/lr list

# 重载配置
/lr reload
```

---

## ⚙️ 配置

配置文件路径：`plugins/LiveRecorder/config.yml`

```yaml
# 镜头设置
camera:
  pitch: 30.0           # 俯角（度数），0=水平，90=正上方
  distance: 5.0         # 镜头与目标玩家的水平距离（格）
  height-offset: 0.0    # 额外高度偏移（格）
  follow-speed: 0.35    # 跟随速度系数（0.0~1.0），值越大跟随越紧密
  arrival-threshold: 0.3 # 镜头到达阈值（格），小于此距离视为已到达

# 自动切换设置
auto-switch:
  enabled: true          # 是否启用自动切换
  interval: 30           # 自动切换间隔（秒）
  mode: RANDOM           # 切换模式：RANDOM(随机) | SEQUENTIAL(顺序)

# 视觉反馈
visual:
  target-glow: true      # 是否为目标玩家添加发光效果
  glow-color: YELLOW     # 发光颜色
  camera-particle: false # 是否在镜头位置显示粒子
  particle-type: END_ROD # 粒子类型
  actionbar-enabled: true # ActionBar 状态显示
  actionbar-interval: 20  # ActionBar 刷新间隔（tick）

# 录制者限制
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

### 发光颜色列表

`WHITE`, `ORANGE`, `MAGENTA`, `LIGHT_BLUE`, `YELLOW`, `LIME`, `PINK`, `GRAY`, `CYAN`, `PURPLE`, `BLUE`, `GREEN`, `RED`

---

## 🏗️ 项目结构

```
LiveRecorder/
├── pom.xml                                        # Maven 构建配置
├── README.md                                      # 项目说明
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