<div align="center">

# 🎥 LiveRecorder

**无人机式自由录制者管理系统**

*面向直播/录屏场景的 Minecraft 服务器插件*

[![Java](https://img.shields.io/badge/Java-8%2B-orange?style=for-the-badge&logo=openjdk)](https://adoptium.net/)
[![Spigot](https://img.shields.io/badge/Spigot-1.16%2B-yellow?style=for-the-badge&logo=minecraft)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)
[![Build](https://img.shields.io/github/actions/workflow/status/httye/LiveRecorder/build.yml?style=for-the-badge&logo=github)](https://github.com/httye/LiveRecorder/actions)
[![Issues](https://img.shields.io/github/issues/httye/LiveRecorder?style=for-the-badge&logo=github)](https://github.com/httye/LiveRecorder/issues)
[![PRs](https://img.shields.io/github/issues-pr/httye/LiveRecorder?style=for-the-badge&logo=github)](https://github.com/httye/LiveRecorder/pulls)

---

插件会将指定的录制者玩家自动绑定到目标玩家，实时生成第三人称镜头，并在授权有效的前提下，按照预设的节奏自动切换跟拍对象。

[查看完整文档 →](https://lr.hosh.dev/)

</div>

---

## ✨ 核心特性

- 🎬 **智能跟随系统** - 使用 `setVelocity` 实现平滑跟随，无瞬移感
- 📐 **高精度镜头算法** - 基于 `BigDecimal` 的镜头位置计算
- 👥 **多录制者支持** - 自动/手动模式切换，随机/顺序轮播
- 🔐 **隐私保护** - 同意/拒绝机制，录制者自动隐身
- 🎨 **视觉反馈** - 发光效果 + ActionBar 状态提示
- 📝 **直播日志** - SQLite 数据存储，完整操作记录

---

## 🚀 快速开始

### 三步开始录制

```
步骤 1 ➜ 安装插件并重启服务器

步骤 2 ➜ 绑定录制者到目标玩家
         /lr bind CameraMan Steve auto

步骤 3 ➜ 开始录制！录制者画面自动跟随目标
```

### 编译安装

```bash
# 克隆仓库
git clone https://github.com/httye/LiveRecorder.git
cd LiveRecorder

# 编译打包
mvn clean package

# 产物路径: target/LiveRecorder-1.0.0.jar
```

### 安装到服务器

1. 将 `LiveRecorder-1.0.0.jar` 放入 `plugins/` 目录
2. 重启服务器
3. 修改 `plugins/LiveRecorder/config.yml` 配置
4. 使用 `/lr reload` 热重载配置

---

## 📖 文档

| 文档 | 说明 |
|:-----|:-----|
| [入门指南](https://lr.hosh.dev/guide/getting-started) | 快速上手插件 |
| [命令参考](https://lr.hosh.dev/commands/index) | 所有命令说明 |
| [配置详解](https://lr.hosh.dev/config/index) | 配置文件说明 |
| [使用场景](https://lr.hosh.dev/scenarios/index) | 实际使用案例 |
| [隐私保护](https://lr.hosh.dev/privacy/index) | 隐私功能说明 |
| [常见问题](https://lr.hosh.dev/faq/index) | 问题解答 |
| [API 参考](https://lr.hosh.dev/api/index) | 开发者 API |
| [更新日志](help/CHANGELOG.md) | 版本更新记录 |

---

## 🏗️ 项目结构

```
LiveRecorder/
├── pom.xml                          # Maven 构建配置
├── README.md                        # 项目说明（主文档）
├── CHANGELOG.md                     # 更新日志
├── CONTRIBUTING.md                  # 贡献指南
├── LICENSE                          # MIT 开源协议
├── help/                            # 📚 详细文档（VitePress）
│   ├── .vitepress/                  # VitePress 配置
│   ├── guide/                       # 入门指南
│   │   ├── getting-started.md       # 快速开始
│   │   ├── installation.md          # 安装部署
│   │   └── basics.md                # 基本概念
│   ├── commands/                    # 命令参考
│   │   ├── index.md                 # 命令概览
│   │   ├── admin.md                 # 管理员命令
│   │   └── player.md                # 玩家命令
│   ├── config/                      # 配置详解
│   │   ├── index.md                 # 配置概览
│   │   ├── camera.md                # 镜头设置
│   │   ├── autoswitch.md            # 自动切换
│   │   ├── visual.md                # 视觉反馈
│   │   ├── restrictions.md          # 录制者限制
│   │   └── privacy.md               # 隐私设置
│   ├── scenarios/                   # 使用场景
│   │   ├── index.md                 # 场景概览
│   │   ├── single.md                # 单人直播跟拍
│   │   ├── multi.md                 # 多机位直播
│   │   ├── rotate.md                # 自动轮换跟拍
│   │   ├── manual.md                # 手动控制跟拍
│   │   └── privacy.md               # 隐私保护模式
│   ├── privacy/                     # 隐私保护
│   │   ├── index.md                 # 隐私概览
│   │   ├── consent.md               # 同意/拒绝机制
│   │   ├── invisibility.md          # 录制者隐身
│   │   ├── logs.md                  # 直播日志
│   │   └── storage.md               # 数据存储
│   ├── faq/                         # 常见问题
│   │   ├── index.md                 # 问题概览
│   │   ├── follow.md                # 跟随问题
│   │   ├── performance.md           # 性能问题
│   │   ├── privacy.md               # 隐私问题
│   │   └── config.md                # 配置问题
│   ├── api/                         # API 参考
│   │   ├── index.md                 # API 概览
│   │   ├── liverecorder.md          # LiveRecorder 类
│   │   ├── livecore.md              # LiveCore 类
│   │   ├── databasemanager.md       # DatabaseManager 类
│   │   └── camerageometry.md        # CameraGeometry 类
│   ├── qa/                          # 互动问答
│   │   └── index.md                 # 热门问题
│   ├── index.md                     # 文档首页
│   ├── about.md                     # 项目介绍
│   ├── package.json                 # npm 配置
│   └── README.md                    # 文档说明
└── src/main/
    ├── java/com/liverecorder/
    │   ├── LiveRecorder.java        # 主插件类
    │   ├── model/                   # 数据模型
    │   │   ├── RecorderBinding.java  # 录制者绑定模型
    │   │   ├── PrivacySetting.java   # 隐私设置模型
    │   │   └── LiveLog.java          # 直播日志模型
    │   ├── manager/                 # 核心管理器
    │   │   └── LiveCore.java        # 核心管理器
    │   ├── database/                # 数据库管理
    │   │   └── DatabaseManager.java # 数据库管理器
    │   ├── util/                    # 工具类
    │   │   └── CameraGeometry.java  # 镜头几何计算
    │   ├── listener/                # 事件监听器
    │   │   ├── CameraListener.java  # 镜头跟随监听
    │   │   ├── RecorderRestrictionListener.java  # 操作限制监听
    │   │   └── VisualListener.java  # 视觉反馈监听
    │   └── command/                 # 命令处理器
    │       ├── LiveRecorderCommand.java  # 命令处理器
    │       └── LiveRecorderTabCompleter.java  # Tab 补全器
    └── resources/
        ├── plugin.yml               # 插件描述
        └── config.yml               # 默认配置
```

---

## 📄 权限

| 权限 | 说明 | 默认 |
|:-----|:-----|:----:|
| `liverecorder.admin` | 使用所有管理命令 | OP |
| `liverecorder.use` | 使用基本命令 | true |
| `liverecorder.recorder` | 录制者权限 | false |

---

## 🌟 核心功能演示

| 功能 | 效果 | 使用场景 |
|:-----|:-----|:---------|
| 🎥 跟随系统 | 录制者自动跟随目标，位置由插件控制 | 直播跟拍、游戏录制 |
| 👻 自动隐身 | 录制者对其他玩家完全隐身 | 不干扰其他玩家 |
| 🔐 隐私保护 | 玩家可以拒绝被直播 | 保护玩家隐私 |
| 🎨 视觉反馈 | 目标玩家发光 + ActionBar 提示 | 实时状态显示 |
| 📝 直播日志 | 记录所有直播操作 | 审计和回溯 |

## 📊 功能对比

| 功能 | LiveRecorder | 传统方法 |
|:-----|:------------:|:--------:|
| 自动跟随 | ✅ | ❌ |
| 隐私保护 | ✅ | ❌ |
| 多机位支持 | ✅ | ❌ |
| 高精度计算 | ✅ | ❌ |
| 日志记录 | ✅ | ❌ |
| 需要手动控制 | ❌ | ✅ |
| 需要插件配合 | ❌ | ✅ |

## 💡 使用示例

### 单人直播跟拍

```bash
# 绑定录制者到主播
/lr bind CameraMan 主播小明 auto

# 录制者自动跟随，主播正常游戏
```

### 多机位直播

```bash
# 三个录制者跟拍同一目标
/lr bind Camera01 Steve auto
/lr bind Camera02 Steve auto
/lr bind Camera03 Steve auto

# 导播可在三个录制者间切换画面
```

### 自动轮换跟拍

```yaml
# 配置文件
auto-switch:
  enabled: true
  interval: 15
  mode: RANDOM
```

```bash
# 录制者每 15 秒自动切换到另一个玩家
/lr bind CameraMan Steve auto
```

### 隐私保护

```bash
# 玩家查看隐私设置
/lr privacy

# 玩家拒绝被直播
/lr decline

# 玩家同意被直播
/lr accept
```

---

## 🔗 更多链接

- 📄 [更新日志](help/CHANGELOG.md) - 查看版本更新记录
- 🤝 [贡献指南](help/CONTRIBUTING.md) - 了解如何贡献
- 🆘 [获取支持](help/SUPPORT.md) - 获取帮助和支持
- 🛡️ [安全策略](help/SECURITY.md) - 报告安全漏洞
- 📜 [行为准则](help/CODE_OF_CONDUCT.md) - 社区行为准则
- 📜 [开源协议](./LICENSE) - MIT License
- 💬 [GitHub Discussions](https://github.com/httye/LiveRecorder/discussions) - 参与讨论
- 🐛 [GitHub Issues](https://github.com/httye/LiveRecorder/issues) - 报告问题
- 📧 [联系我们](mailto:contact@example.com) - 联系作者

---

## 📊 项目统计

- **总代码行数**: ~3000+
- **文档文件数**: 40+
- **支持版本**: Spigot 1.16+
- **数据库支持**: SQLite
- **权限节点**: 3 个
- **命令数量**: 11 个

---

## 🎯 适用场景

### 🎬 直播录制
- 游戏直播跟拍
- 多机位直播
- 综艺节目录制

### 📱 内容创作
- 游戏视频录制
- 教程视频制作
- 展示视频拍摄

### 🎮 游戏管理
- 服务器监控
- 游戏录制
- 活动记录

---

## 🌟 核心优势

### 1. 自动化
- 无需人工控制录制者
- 自动切换目标
- 自动隐藏录制者

### 2. 隐私保护
- 玩家同意/拒绝机制
- 录制者自动隐身
- 完整的日志记录

### 3. 高精度
- BigDecimal 高精度计算
- 平滑移动算法
- 准确的镜头位置

### 4. 易用性
- 简单的命令系统
- 直观的配置文件
- 完善的文档支持

---

## 📜 开源协议

本项目基于 [MIT License](LICENSE) 开源

### MIT License 摘要

- ✅ 可以自由使用
- ✅ 可以修改和分发
- ✅ 可以用于商业项目
- ✅ 无需公开源代码
- ⚠️ 需要保留版权声明

---

<div align="center">

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. 🍴 Fork 本仓库
2. 🌿 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 💾 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 📤 推送到分支 (`git push origin feature/AmazingFeature`)
5. 🎉 提交 Pull Request

查看 [贡献指南](help/CONTRIBUTING.md) 了解更多详情。

---

## ⭐ Star History

如果这个项目对你有帮助，请给我们一个 Star！

[![Star History Chart](https://api.star-history.com/svg?repos=httye/LiveRecorder&type=Date)](https://star-history.com/#httye/LiveRecorder&Date)

---

## 🙏 致谢

感谢所有为 LiveRecorder 做出贡献的开发者和用户！

### 特别感谢

- Spigot 社区提供的服务器 API
- 所有提交 Issue 和 PR 的贡献者
- 使用并支持 LiveRecorder 的用户

---

## 📢 联系我们

- GitHub: [httye/LiveRecorder](https://github.com/httye/LiveRecorder)
- Email: 1280773070@qq.com

---

**LiveRecorder** © 七月 | 七月个人制作组

Made with ❤️ by 七月个人制作组

</div>
