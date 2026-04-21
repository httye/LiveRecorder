# 快速开始

欢迎使用 LiveRecorder！本指南将帮助你快速上手使用插件。

## 🎯 三步开始录制

### 步骤 1: 安装插件

将 `LiveRecorder-1.0.0.jar` 放入服务器的 `plugins/` 目录。

::: tip 重要
首次安装必须**重启服务器**，不能使用热加载。
:::

### 步骤 2: 准备玩家

确保有以下玩家：
- **录制者**: 负责跟随拍摄的玩家（如 `CameraMan`）
- **目标玩家**: 被跟拍的玩家（如 `Steve`）

### 步骤 3: 绑定录制者

在服务器控制台或游戏内执行：

```bash
/lr bind CameraMan Steve auto
```

**参数说明：**
- `CameraMan`: 录制者玩家名
- `Steve`: 目标玩家名
- `auto`: 自动模式（会自动切换目标）

## ✅ 验证安装

### 检查插件是否加载

在服务器控制台查看输出：

```
[LiveRecorder] ============================================
[LiveRecorder]   LiveRecorder 无人机式录制系统已启动
[LiveRecorder]   版本: 1.0.0
[LiveRecorder] ============================================
```

### 测试功能

绑定成功后，你应该能看到：

| 角色 | 看到的效果 |
|:----:|:----------|
| 🎥 **录制者** | ActionBar 显示 `● 跟随中 \| 目标: Steve \| 模式: 自动` |
| 🎯 **目标玩家** | ActionBar 显示 `🔴 您正在被直播 \| 1 位录制者跟拍中` |
| 👀 **其他玩家** | 看不到录制者（自动隐身） |

## 📝 常用命令

### 基本操作

```bash
# 查看所有绑定
/lr list

# 解除录制者绑定
/lr unbind CameraMan

# 手动切换目标
/lr switch CameraMan Alex

# 切换绑定模式
/lr mode CameraMan manual

# 重载配置
/lr reload
```

### 玩家隐私命令

```bash
# 查看隐私设置
/lr privacy

# 同意被直播
/lr accept

# 拒绝被直播
/lr decline

# 设置隐私状态
/lr setprivacy accept    # 同意
/lr setprivacy decline   # 拒绝
/lr setprivacy unset     # 清除设置
```

## 🎨 效果预览

### 标准跟拍模式

```bash
/lr bind CameraMan Steve auto
```

**效果：**
- 录制者自动跟随 `Steve`
- 镜头在目标身后 5 格位置
- 俯角 30°，高度与目标持平
- 使用平滑插值实现流畅跟随

### 多机位模式

```bash
/lr bind Camera01 Steve auto
/lr bind Camera02 Steve auto
/lr bind Camera03 Steve auto
```

**效果：**
- 三个录制者同时跟随 `Steve`
- 可以在不同录制者间切换画面
- 适合多角度直播

### 自动轮换模式

修改 `config.yml`：

```yaml
auto-switch:
  enabled: true
  interval: 15      # 每 15 秒切换一次
  mode: RANDOM      # 随机选择下一个目标
```

**效果：**
- 录制者每 15 秒自动切换到另一个在线玩家
- 适合综艺节目风格的轮播

## ⚙️ 基本配置

### 调整镜头参数

编辑 `plugins/LiveRecorder/config.yml`：

```yaml
camera:
  pitch: 30.0            # 俯角（度）
  distance: 5.0          # 水平距离（格）
  position-smooth: 0.12  # 位置平滑系数
  rotation-smooth: 0.1   # 视角平滑系数
```

### 启用视觉反馈

```yaml
visual:
  target-glow: true        # 目标发光
  glow-color: YELLOW      # 发光颜色
  actionbar-enabled: true # ActionBar 显示
```

### 配置隐私保护

```yaml
privacy:
  recorder-invisible:
    enabled: true         # 录制者自动隐身
  live-logs:
    enabled: true         # 记录直播日志
    keep-count: 100       # 保留最近 100 条
```

配置完成后，执行 `/lr reload` 重载配置。

## 🔧 常见问题

### 录制者没有跟随？

检查以下几点：
1. 录制者和目标是否都在线
2. 录制者是否被正确绑定（使用 `/lr list` 查看）
3. 目标玩家是否拒绝了直播（使用 `/lr privacy` 查看）

### 录制者卡住不动？

可能原因：
1. 目标玩家距离过远（超过 30 格），录制者正在传送
2. 平滑系数设置太低（调高 `position-smooth`）
3. 录制者被限制移动（检查是否有其他插件干扰）

### 如何停止录制？

```bash
# 解除所有录制者绑定
/lr unbind CameraMan
/lr unbind Camera01
# ...
```

## 📚 下一步

现在你已经掌握了基本用法，可以继续学习：

- [安装部署](/guide/installation) - 详细的安装步骤
- [基本概念](/guide/basics) - 了解核心概念
- [配置详解](/config/index) - 完整配置说明
- [使用场景](/scenarios/index) - 实际应用案例

## 🆘 获取帮助

遇到问题？

- 查看 [常见问题](/faq/index)
- 提交 [GitHub Issue](https://github.com/httye/LiveRecorder/issues)
- 加入我们的社区讨论

---

开始你的直播之旅吧！🎬