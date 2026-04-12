# 更新日志

本项目所有重要变更都将记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [1.0.0] - 2026-04-12

### 🎉 首次发布

#### ✨ 新增

**核心功能**
- 🎬 智能镜头跟随系统
  - 使用 `setVelocity` 实现平滑跟随
  - 支持自定义俯角、距离、跟随速度
  - 距离过远时自动传送重定位
  - 弹性跟随算法，无瞬移感

- 📐 高精度镜头算法
  - 基于 `BigDecimal` 的高精度计算
  - 泰勒级数展开三角函数
  - 牛顿迭代法计算平方根
  - 12 位有效数字精度

- 👥 多模式录制管理
  - AUTO 模式：自动切换目标（随机/顺序）
  - MANUAL 模式：手动控制切换
  - 支持多录制者同时跟拍
  - 支持不同录制者不同模式

- 🔒 完善的录制者限制
  - 阻止打开背包、容器
  - 阻止交互、攻击、放置方块
  - 阻止使用命令（白名单除外）
  - 保持画面干净

- 🔐 隐私保护机制
  - 玩家同意/拒绝机制
  - 录制者自动隐身
  - SQLite 数据存储
  - 完整的直播日志记录

- 🎨 丰富的视觉反馈
  - 目标玩家发光效果
  - 镜头位置粒子标记
  - ActionBar 实时状态显示
  - 被直播提示

**命令系统**
- `/lr bind` - 绑定录制者到目标
- `/lr unbind` - 解除录制者绑定
- `/lr list` - 列出所有绑定
- `/lr mode` - 切换绑定模式
- `/lr switch` - 手动切换目标
- `/lr reload` - 重载配置
- `/lr logs` - 查看直播日志
- `/lr accept` - 同意被直播
- `/lr decline` - 拒绝被直播
- `/lr privacy` - 查看隐私设置
- `/lr setprivacy` - 设置隐私状态

**配置系统**
- 镜头设置（俯角、距离、高度偏移、跟随速度）
- 自动切换设置（启用/禁用、间隔、模式）
- 视觉反馈设置（发光、粒子、ActionBar）
- 录制者限制设置（背包、交互、攻击等）
- 隐私保护设置（隐身、日志、同意提示）

**数据库支持**
- SQLite 数据库存储
- 隐私设置持久化
- 直播日志记录
- 自动初始化表结构

**文档系统**
- 完整的 VitePress 文档
- 入门指南（快速开始、安装部署、基本概念）
- 命令参考（管理员命令、玩家命令）
- 配置详解（镜头、自动切换、视觉反馈、限制、隐私）
- 使用场景（单人直播、多机位、自动轮换、手动控制、隐私保护）
- 隐私保护（同意/拒绝、隐身、日志、存储）
- 常见问题（跟随、性能、隐私、配置）
- API 参考（LiveRecorder、LiveCore、DatabaseManager、CameraGeometry）

#### 🔧 技术特性

- 基于 Spigot 1.16+ API
- Java 8+ 兼容
- Maven 构建系统
- GitHub Actions 自动构建
- MIT 开源协议

#### 📦 包含组件

- **LiveRecorder.java** - 主插件类
- **LiveCore.java** - 核心管理器
- **DatabaseManager.java** - 数据库管理
- **CameraGeometry.java** - 镜头几何计算
- **RecorderBinding.java** - 录制者绑定模型
- **PrivacySetting.java** - 隐私设置模型
- **LiveLog.java** - 直播日志模型
- **CameraListener.java** - 镜头跟随监听
- **RecorderRestrictionListener.java** - 操作限制监听
- **VisualListener.java** - 视觉反馈监听
- **LiveRecorderCommand.java** - 命令处理器
- **LiveRecorderTabCompleter.java** - Tab 补全器

#### 🎯 设计目标

- 自动化直播录制过程
- 保护玩家隐私
- 高精度镜头计算
- 易于使用和配置
- 完善的文档支持

#### 📚 文档支持

- 简化的项目 README
- 完整的 VitePress 文档系统
- 详细的使用场景说明
- 常见问题解答
- API 参考文档
- 互动问答页面

#### 🚀 构建和部署

- Maven 编译构建
- GitHub Actions CI/CD
- 自动生成 Javadoc
- 支持本地编译和自动编译

#### 🔐 安全特性

- 命令权限控制
- 隐私同意机制
- 数据库安全
- 输入验证

#### 🎨 界面优化

- 美观的启动横幅
- 详细的初始化日志
- 系统自检功能
- 版权信息显示

#### 📊 性能优化

- 高精度计算优化
- 任务调度优化
- 数据库优化
- 事件监听优化

#### 🌟 亮点功能

1. **录制者自动隐身** - 对其他玩家完全不可见，不影响游戏体验
2. **隐私同意机制** - 玩家可以控制自己是否可以被直播
3. **高精度镜头计算** - 使用 BigDecimal 确保镜头位置精确
4. **弹性跟随算法** - 平滑移动，无瞬移感
5. **完整的日志系统** - 记录所有直播操作，便于审计

#### 📋 兼容性

- 最低要求：Spigot 1.16+
- 推荐版本：Paper 1.20+
- Java 要求：8+
- Maven 要求：3.6+

#### 🎉 未来计划

- [ ] 支持更多数据库（MySQL、PostgreSQL）
- [ ] 添加直播回放功能
- [ ] 支持录制路径记录
- [ ] 优化隐身机制
- [ ] 添加录制者 AI 行为
- [ ] 支持多世界直播
- [ ] 添加直播录制功能
- [ ] 优化性能和内存占用

---

## 版本说明

### 版本号格式

- **主版本号**：不兼容的 API 修改
- **次版本号**：向下兼容的功能性新增
- **修订号**：向下兼容的问题修正

### 变更类型

- ✨ **新增** (Added)：新功能
- 🔄 **变更** (Changed)：现有功能的变更
- 🗑️ **废弃** (Deprecated)：即将移除的功能
- ❌ **移除** (Removed)：已移除的功能
- 🐛 **修复** (Fixed)：问题修复
- 🔒 **安全** (Security)：安全相关的修复

---

## 贡献指南

欢迎提交 Issue 和 Pull Request！

查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解如何贡献。

## 开源协议

本项目基于 [MIT License](https://github.com/httye/LiveRecorder/blob/main/LICENSE) 开源。

---

**LiveRecorder** © 七月 | 七月个人制作组