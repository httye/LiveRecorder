# 项目介绍

欢迎了解 LiveRecorder 项目！

## 📖 项目简介

LiveRecorder 是一个面向直播/录屏场景的 Minecraft Spigot 插件，实现了"无人机式自由录制者管理系统"。

### 核心功能

插件会将指定的录制者玩家自动绑定到目标玩家，实时生成第三人称镜头，并在授权有效的前提下，按照预设的节奏自动切换跟拍对象。

### 适用场景

- **单人直播**: 让一个录制者账号专门跟拍主播
- **多机位直播**: 多个录制者从不同角度跟拍同一目标
- **综艺节目**: 录制者自动在不同玩家间切换
- **手动导播**: 导播手动控制录制者跟拍目标

## 🎯 设计理念

### 1. 自动化

插件的核心目标是自动化直播录制过程：

- **自动跟随**: 录制者自动跟随目标玩家，无需人工控制
- **自动切换**: 可配置自动切换目标，适合轮播场景
- **自动隐身**: 录制者自动隐身，不影响其他玩家

### 2. 隐私保护

重视玩家隐私，提供完善的隐私保护机制：

- **同意/拒绝**: 玩家可以同意或拒绝被直播
- **隐私设置**: 隐私设置持久化存储
- **日志记录**: 所有直播操作都有记录

### 3. 高精度

使用高精度算法确保镜头位置准确：

- **BigDecimal 运算**: 使用高精度数学库
- **泰勒级数展开**: 自实现三角函数
- **弹性跟随**: 平滑移动，无瞬移感

### 4. 易用性

简单易用，快速上手：

- **直观命令**: 命令简单明了，支持 Tab 补全
- **丰富反馈**: ActionBar、发光效果等视觉反馈
- **灵活配置**: 配置项丰富，适应不同需求

## ✨ 核心特性

### 🎬 智能镜头跟随

- 使用 `setVelocity` 实现平滑跟随
- 支持自定义俯角、距离、速度
- 距离过远时自动传送重定位
- 弹性跟随算法，无瞬移感

### 📐 高精度镜头算法

- 基于 BigDecimal 高精度计算
- 泰勒级数展开三角函数
- 牛顿迭代法计算平方根
- 12 位有效数字精度

### 👥 多模式录制管理

- AUTO 模式：自动切换目标（随机/顺序）
- MANUAL 模式：手动控制切换
- 支持多录制者同时跟拍
- 支持不同录制者不同模式

### 🔒 完善的录制者限制

- 阻止打开背包、容器
- 阻止交互、攻击、放置方块
- 阻止使用命令（白名单除外）
- 保持画面干净

### 🔐 隐私保护机制

- 玩家同意/拒绝机制
- 录制者自动隐身
- SQLite 数据存储
- 完整的直播日志记录

### 🎨 丰富的视觉反馈

- 目标玩家发光效果
- 镜头位置粒子标记
- ActionBar 实时状态显示
- 被直播提示

## 🏗️ 技术架构

### 核心组件

```
LiveRecorder
├── DatabaseManager    # 数据库管理
│   ├── 隐私设置管理
│   └── 直播日志管理
├── LiveCore           # 核心管理器
│   ├── 绑定管理
│   ├── 跟随任务
│   ├── 自动切换
│   └── 隐私检查
├── CameraGeometry     # 镜头计算
│   ├── 位置计算
│   ├── 速度计算
│   └── 高精度数学
├── Command            # 命令处理
│   ├── 管理员命令
│   └── 玩家命令
└── Listener           # 事件监听
    ├── 镜头跟随
    ├── 操作限制
    └── 视觉反馈
```

### 数据流

```
用户命令
    ↓
命令处理器
    ↓
核心管理器
    ↓
┌─────────┬─────────┬─────────┐
│ 镜头计算 │ 隐私检查 │ 日志记录 │
└─────────┴─────────┴─────────┘
    ↓
事件监听器
    ↓
视觉反馈
```

## 📊 性能优化

### 优化策略

1. **高精度计算优化**
   - 使用 MathContext 控制精度
   - 缓存常用计算结果
   - 避免重复计算

2. **任务调度优化**
   - 使用 BukkitTask 高效调度
   - 合理设置任务间隔
   - 避免不必要的计算

3. **数据库优化**
   - 使用 SQLite 连接池
   - 批量操作减少 IO
   - 定期清理旧日志

4. **事件监听优化**
   - 优先级合理设置
   - 快速判断，减少处理
   - 避免阻塞主线程

### 性能指标

| 指标 | 数值 |
|:----:|:-----:|
| 内存占用 | ~50MB |
| CPU 占用 | < 1% |
| 网络开销 | 极低 |
| 支持录制者数量 | 建议 < 10 |

## 🎓 技术亮点

### 1. BigDecimal 高精度计算

```java
// 泰勒级数展开 sin(x)
private BigDecimal sin(BigDecimal x) {
    BigDecimal result = BigDecimal.ZERO;
    BigDecimal term = x;
    for (int i = 1; i <= 15; i += 2) {
        if (i % 4 == 1) result = result.add(term, MC);
        else result = result.subtract(term, MC);
        term = term.multiply(x, MC).multiply(x, MC);
        term = term.divide(new BigDecimal((long)(i + 1) * (i + 2)), MC);
    }
    return result;
}
```

### 2. 弹性跟随算法

```java
// 速度 = 位置差值 × 跟随系数
double vx = (cameraTarget.getX() - recorder.getX()) * followSpeed;
double vy = (cameraTarget.getY() - recorder.getY()) * followSpeed;
double vz = (cameraTarget.getZ() - recorder.getZ()) * followSpeed;

// 限制最大速度
double speed = Math.sqrt(vx * vx + vy * vy + vz * vz);
if (speed > 2.0) {
    double scale = 2.0 / speed;
    vx *= scale;
    vy *= scale;
    vz *= scale;
}

recorder.setVelocity(new Vector(vx, vy, vz));
```

### 3. 隐私同意机制

```java
// 绑定前检查隐私
PrivacySetting privacy = databaseManager.getPrivacySetting(target.getUuid());
if (privacy != null && privacy.hasDeclined()) {
    return PrivacyStatus.DECLINED;  // 玩家拒绝
}

if (privacy == null || privacy.getConsentStatus() == ConsentStatus.UNSET) {
    sendConsentRequest(target);  // 发送确认请求
    return PrivacyStatus.PENDING;
}

return PrivacyStatus.ACCEPTED;  // 玩家同意
```

## 🌟 未来计划

### 短期计划

- [ ] 支持更多数据库（MySQL、PostgreSQL）
- [ ] 添加直播回放功能
- [ ] 支持录制路径记录
- [ ] 优化隐身机制

### 中期计划

- [ ] 添加录制者 AI 行为
- [ ] 支持多世界直播
- [ ] 添加直播录制功能
- [ ] 优化性能和内存占用

### 长期计划

- [ ] 支持跨服务器直播
- [ ] 添加直播统计分析
- [ ] 支持 VR 模式
- [ ] 开发配套的直播客户端

## 🤝 贡献指南

我们欢迎任何形式的贡献！

### 如何贡献

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 贡献类型

- 🐛 Bug 修复
- ✨ 新功能
- 📝 文档改进
- 🎨 UI/UX 改进
- ⚡ 性能优化
- ✅ 测试

## 📜 开源协议

本项目基于 [MIT License](https://github.com/httye/LiveRecorder/blob/main/LICENSE) 开源。

### MIT License 摘要

- ✅ 可以自由使用
- ✅ 可以修改和分发
- ✅ 可以用于商业项目
- ✅ 无需公开源代码
- ⚠️ 需要保留版权声明

## 📞 联系我们

- GitHub: [httye/LiveRecorder](https://github.com/httye/LiveRecorder)
- Issues: [提交问题](https://github.com/httye/LiveRecorder/issues)
- Discussions: [参与讨论](https://github.com/httye/LiveRecorder/discussions)

## 🙏 致谢

感谢所有为 LiveRecorder 做出贡献的开发者和用户！

### 特别感谢

- Spigot 社区提供的服务器 API
- 所有提交 Issue 和 PR 的贡献者
- 使用并支持 LiveRecorder 的用户

---

感谢你了解 LiveRecorder！🎉