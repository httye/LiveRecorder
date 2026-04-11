<div align="center">

# 🎥 LiveRecorder

**无人机式自由录制者管理系统**

*面向直播/录屏场景的 Minecraft 服务器插件*

[![Java](https://img.shields.io/badge/Java-8%2B-orange?style=for-the-badge&logo=openjdk)](https://adoptium.net/)
[![Spigot](https://img.shields.io/badge/Spigot-1.16%2B-yellow?style=for-the-badge&logo=minecraft)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)
[![Build](https://img.shields.io/github/actions/workflow/status/httye/LiveRecorder/build.yml?style=for-the-badge&logo=github)](https://github.com/httye/LiveRecorder/actions)

---

插件会将指定的录制者玩家自动绑定到目标玩家，实时生成第三人称镜头，并在授权有效的前提下，按照预设的节奏自动切换跟拍对象。

</div>

---

## 🌟 核心亮点

| 🎬 跟随系统 | 📐 镜头算法 | 👥 多录制者 | 🔒 操作限制 | 🎨 视觉反馈 |
|:---:|:---:|:---:|:---:|:---:|
| `setVelocity` 实时跟随 | `BigDecimal` 高精度计算 | 自动/手动模式切换 | 阻止背包/GUI遮挡 | 发光 + ActionBar 提示 |
| 传送即时重定位 | 自定义俯角/距离 | 随机/顺序轮播 | 全面交互限制 | 粒子镜头标记 |
| 无瞬移平滑移动 | 弹性跟随算法 | 多机位同时跟拍 | 命令白名单机制 | 🔴 直播状态提示 |

---

## 📋 环境要求

| 项目 | 最低要求 | 推荐版本 |
|:----:|:--------:|:--------:|
| 服务端 | Spigot 1.16+ | Paper 1.20+ |
| Java | 8 | 17 |
| Maven | 3.6+ | 3.9+ |

---

## 🔨 编译安装

### 方式一：本地编译

```bash
# 克隆仓库
git clone https://github.com/httye/LiveRecorder.git
cd LiveRecorder

# 编译打包
mvn clean package

# 产物路径
# target/LiveRecorder-1.0.0.jar
```

### 方式二：GitHub Actions 自动编译

> 推送到 GitHub 后自动编译，无需本地环境

1. 将代码推送到 GitHub 仓库
2. 进入 **Actions** 标签页查看编译进度
3. 编译成功后，在 **Artifacts** 中下载 `LiveRecorder` 产物
4. 如需发布 Release，推送 tag 即可自动发布：
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

### 安装到服务器

1. 将 `LiveRecorder-1.0.0.jar` 放入服务器的 `plugins/` 目录
2. ⚠️ **首次安装必须重启服务器**（不可热加载）
3. 修改 `plugins/LiveRecorder/config.yml` 配置
4. 使用 `/lr reload` 热重载配置（无需重启）

---

## 🚀 快速上手

### 三步开始录制

```
步骤 1 ➜ 安装插件并重启服务器

步骤 2 ➜ 绑定录制者到目标玩家
         /lr bind CameraMan Steve auto

步骤 3 ➜ 开始录制！录制者画面自动跟随目标
```

### 效果预览

绑定成功后，各方看到的内容：

| 角色 | 看到的效果 |
|:----:|:----------|
| 🎥 **录制者** | ActionBar: `LiveRecorder | ● 跟随中 | 目标: Steve | 模式: 自动` |
| 🎯 **目标玩家** | ActionBar: `🔴 您正在被直播 | 1 位录制者跟拍中` + 黄色发光 |
| 👀 **其他玩家** | 看到录制者在目标身后自动跟随移动 |

---

## 📖 使用场景

### 🎯 场景一：单人直播跟拍

让一个录制者账号专门负责跟拍主播

```bash
/lr bind Recorder01 主播小明 auto
```

> 录制者 `Recorder01` 自动跟随主播，主播正常游戏即可

---

### 📺 场景二：多机位直播

多个录制者从不同角度跟拍同一目标

```bash
/lr bind Camera01 Steve auto
/lr bind Camera02 Steve auto
/lr bind Camera03 Steve auto
```

> 每个录制者都会自动跟随，导播可在不同录制者间切换画面

---

### 🎲 场景三：自动轮换跟拍

录制者自动在不同玩家之间切换（综艺节目风格）

```bash
# 1. 绑定录制者
/lr bind Camera01 Steve auto

# 2. 修改 config.yml 中自动切换间隔为 15 秒
#    auto-switch:
#      enabled: true
#      interval: 15
#      mode: RANDOM

# 3. 重载配置
/lr reload
```

> `Camera01` 每 15 秒随机切换到另一个在线目标

---

### 🎮 场景四：手动控制跟拍

导播手动决定录制者跟拍谁

```bash
# 绑定为手动模式
/lr bind Camera01 Steve manual

# 手动切换目标
/lr switch Camera01 Alex

# 切换回来
/lr switch Camera01 Steve
```

> 手动模式下不会自动切换，完全由导播控制

---

### 🏁 场景五：活动结束

```bash
# 查看所有绑定
/lr list

# 逐个解除
/lr unbind Camera01
/lr unbind Camera02
/lr unbind Camera03
```

---

## 💻 命令参考

> 所有命令需要 `liverecorder.admin` 权限（默认 OP）  
> 命令别名：`/liverecorder` 或 `/lr`

| 命令 | 说明 | 示例 |
|:-----|:-----|:-----|
| `/lr bind <录制者> <目标> [auto\|manual]` | 绑定录制者到目标 | `/lr bind Cam Steve auto` |
| `/lr unbind <录制者>` | 解除录制者绑定 | `/lr unbind Cam` |
| `/lr list` | 列出所有绑定 | `/lr list` |
| `/lr mode <录制者> <auto\|manual>` | 切换绑定模式 | `/lr mode Cam manual` |
| `/lr switch <录制者> <新目标>` | 手动切换跟拍目标 | `/lr switch Cam Alex` |
| `/lr reload` | 重载配置文件 | `/lr reload` |

---

## 🔒 录制者限制

当玩家被绑定为录制者后，插件会自动限制其操作，确保录制画面干净：

| 限制 | 说明 |
|:----:|:-----|
| 🚫 移动 | 位置由插件自动控制 |
| 🚫 打开背包 | 防止背包界面遮挡画面 |
| 🚫 打开容器 | 防止箱子/GUI 遮挡画面 |
| 🚫 丢弃物品 | 防止误操作 |
| 🚫 交互方块 | 防止误触拉杆/按钮等 |
| 🚫 攻击实体 | 防止影响游戏进程 |
| 🚫 放置/破坏方块 | 防止影响游戏世界 |
| 🚫 使用命令 | 仅允许白名单命令 |

> 💡 白名单默认包含 `lr` 和 `liverecorder`，可在配置中自定义

---

## ⚙️ 配置详解

配置文件路径：`plugins/LiveRecorder/config.yml`

<details>
<summary>📄 完整配置文件（点击展开）</summary>

```yaml
# ============================================
# LiveRecorder 配置文件
# 无人机式自由录制者管理系统
# ============================================

# 镜头设置
camera:
  # 俯角（度数），0为水平，90为正上方
  pitch: 30.0
  # 镜头与目标玩家的水平距离（格）
  distance: 5.0
  # 镜头高度偏移（格），在俯角计算之外额外增加的高度
  height-offset: 0.0
  # 跟随速度系数（0.0~1.0），值越大跟随越紧密
  follow-speed: 0.35
  # 镜头位置到达阈值（格），小于此距离视为已到达
  arrival-threshold: 0.3

# 自动切换设置
auto-switch:
  # 是否启用自动切换
  enabled: true
  # 自动切换间隔（秒）
  interval: 30
  # 切换模式：RANDOM(随机) | SEQUENTIAL(顺序)
  mode: RANDOM

# 视觉反馈
visual:
  # 是否为目标玩家添加发光效果
  target-glow: true
  # 发光颜色
  glow-color: YELLOW
  # 是否在镜头位置显示粒子
  camera-particle: false
  # 粒子类型
  particle-type: END_ROD
  # ActionBar 状态显示（录制者）
  actionbar-enabled: true
  # ActionBar 刷新间隔（tick）
  actionbar-interval: 20
  # 是否在被直播的目标玩家 ActionBar 显示提示
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

# 调试模式
debug: false
```

</details>

### 🎥 镜头参数调整

| 效果 | pitch | distance | follow-speed |
|:----:|:-----:|:--------:|:------------:|
| 🦅 鸟瞰视角 | 60° | 8.0 | 0.35 |
| 📹 标准跟拍 | 30° | 5.0 | 0.35 |
| 🎬 平视特写 | 10° | 3.0 | 0.40 |
| 🏃 快速跟随 | 25° | 4.0 | 0.50 |
| 🐢 慢速平滑 | 35° | 6.0 | 0.20 |

### 🎨 发光颜色

`WHITE` · `ORANGE` · `MAGENTA` · `LIGHT_BLUE` · `YELLOW` · `LIME` · `PINK` · `GRAY` · `CYAN` · `PURPLE` · `BLUE` · `GREEN` · `RED`

---

## 🏗️ 项目结构

```
LiveRecorder/
├── pom.xml                                            # Maven 构建配置
├── README.md                                          # 项目说明
├── LICENSE                                            # MIT 开源协议
├── .gitignore                                         # Git 忽略规则
├── .github/workflows/build.yml                        # GitHub Actions CI/CD
└── src/main/
    ├── java/com/liverecorder/
    │   ├── LiveRecorder.java                          # 🏠 主插件类
    │   ├── model/
    │   │   └── RecorderBinding.java                   # 📎 录制者绑定数据类
    │   ├── manager/
    │   │   └── LiveCore.java                          # 🧠 核心管理器
    │   ├── util/
    │   │   └── CameraGeometry.java                    # 📐 镜头几何算法
    │   ├── listener/
    │   │   ├── CameraListener.java                    # 🎥 镜头跟随监听
    │   │   ├── RecorderRestrictionListener.java       # 🔒 操作限制监听
    │   │   └── VisualListener.java                    # 🎨 视觉反馈监听
    │   └── command/
    │       ├── LiveRecorderCommand.java               # ⌨️ 命令处理器
    │       └── LiveRecorderTabCompleter.java          # 📝 Tab 补全
    └── resources/
        ├── plugin.yml                                 # 插件描述文件
        └── config.yml                                 # 默认配置文件
```

---

## 🔧 核心算法

### 镜头位置计算

```
镜头位置 = 目标玩家位置 + 偏移量

  dx = -distance × sin(yaw)                    ← 水平X偏移（身后）
  dz =  distance × cos(yaw)                    ← 水平Z偏移
  dy =  distance × tan(pitch) + heightOffset   ← 垂直偏移
```

> 所有三角函数运算使用 `BigDecimal` 泰勒级数展开，确保高精度

### 弹性跟随算法

```
速度 = 位置差值 × 跟随速度系数

  · 距离越大 → 速度越快 → 平滑追赶
  · 最大速度限制 2.0 格/tick → 避免瞬移感
  · 距离超过 30 格 → 自动传送 → 快速重定位
```

---

## ❓ 常见问题

<details>
<summary>录制者画面出现瞬移怎么办？</summary>

尝试调低 `follow-speed` 值（如 0.2），或增大 `distance` 让镜头更远。如果目标玩家频繁传送，这是正常行为——插件会自动传送录制者到新位置。

</details>

<details>
<summary>录制者跟不上目标玩家怎么办？</summary>

调高 `follow-speed` 值（如 0.5），或减小 `distance` 让镜头更近。

</details>

<details>
<summary>如何让录制者使用旁观者模式？</summary>

在绑定录制者之前，先将录制者设为旁观者模式：
```
/gamemode spectator CameraMan
/lr bind CameraMan Steve auto
```

</details>

<details>
<summary>目标玩家下线了怎么办？</summary>

插件会自动将录制者切换到其他在线目标。如果没有其他目标，跟随会暂停，录制者会收到通知。

</details>

<details>
<summary>可以同时跟拍多个目标吗？</summary>

一个录制者同一时间只能跟拍一个目标。但你可以绑定多个录制者到不同目标，实现多机位效果。

</details>

<details>
<summary>如何关闭录制者的操作限制？</summary>

在 `config.yml` 中将对应的限制项设为 `false`，然后执行 `/lr reload`。

</details>

---

## 📄 权限

| 权限 | 说明 | 默认 |
|:-----|:-----|:----:|
| `liverecorder.admin` | 使用所有 LiveRecorder 命令 | OP |
| `liverecorder.recorder` | 录制者权限 | false |

---

<div align="center">

## 📜 开源协议

本项目基于 [MIT License](LICENSE) 开源

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. 🍴 Fork 本仓库
2. 🌿 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 💾 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 📤 推送到分支 (`git push origin feature/AmazingFeature`)
5. 🎉 提交 Pull Request

---

**LiveRecorder** © 2026 - Present

</div>