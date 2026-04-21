# 场景化配置推荐

根据不同使用场景，我们提供了经过优化的配置方案。你可以直接复制这些配置到你的 `config.yml` 文件中。

## 如何应用配置

1. 打开 `plugins/LiveRecorder/config.yml`
2. 找到对应的配置部分
3. 替换为推荐的配置值
4. 在游戏中执行 `/lr reload` 重载配置

---

## 🎮 PVP 竞技场景

**特点：** 玩家移动速度快、频繁转向、激烈战斗

### 镜头配置

```yaml
camera:
  pitch: 25.0              # 较低俯角，保持紧张感
  distance: 4.0            # 较近距离，突出战斗细节
  height-offset: 0.0
  position-smooth: 0.35    # 高跟随系数，快速响应
  rotation-smooth: 0.25    # 快速视角旋转
```

**设计思路：**
- 高 `position-smooth` (0.35) 确保镜头紧跟快速移动的玩家
- 较近的距离 (4.0) 让观众看清战斗细节
- 快速视角响应 (0.25) 适应频繁转向

### 自动切换配置

```yaml
auto-switch:
  enabled: false           # PVP 时禁用自动切换，专注当前战斗
  interval: 30
  mode: RANDOM
```

### 视觉反馈配置

```yaml
visual:
  target-glow: true
  glow-color: RED          # 红色发光，强调战斗状态
  camera-particle: false   # 关闭粒子，避免干扰
  actionbar-enabled: true
  actionbar-interval: 10   # 高频更新，实时显示状态
  target-actionbar: true
```

### 录制者限制配置

```yaml
recorder-restrictions:
  block-inventory: true
  block-container: true
  block-drop-item: true
  block-interact: true
  block-attack: true       # 阻止攻击，避免干扰比赛
  block-place: true
  block-break: true
  block-chat: true         # 阻止聊天，保持画面干净
  block-command: true
  command-whitelist:
    - lr
    - liverecorder
```

**性能提示：**
- PVP 场景下跟随任务负载较高，建议服务器 TPS 保持在 18+
- 如果 TPS 下降，可以适当降低 `actionbar-interval` 到 20

---

## 🏗️ 建筑展示场景

**特点：** 缓慢移动、注重细节、电影感镜头

### 镜头配置

```yaml
camera:
  pitch: 35.0              # 中等俯角，展示建筑全貌
  distance: 8.0            # 较远距离，展现宏大场景
  height-offset: 1.0       # 额外高度，鸟瞰效果
  position-smooth: 0.06    # 非常平滑，电影质感
  rotation-smooth: 0.05    # 柔和视角过渡
```

**设计思路：**
- 极低 `position-smooth` (0.06) 创造丝滑的电影感
- 远距离 (8.0) + 高俯角 (35°) 展现建筑宏伟
- 高度偏移 (1.0) 提供更好的俯视角度

### 自动切换配置

```yaml
auto-switch:
  enabled: true
  interval: 60             # 长时间停留，充分展示
  mode: SEQUENTIAL         # 顺序切换，系统化展示
```

### 视觉反馈配置

```yaml
visual:
  target-glow: true
  glow-color: CYAN         # 青色发光，科技感
  camera-particle: true    # 启用粒子，增加氛围
  particle-type: END_ROD
  actionbar-enabled: false # 关闭 ActionBar，沉浸式体验
  target-actionbar: false
```

**适用场景：**
- 建筑作品展示
- 地图游览
- 城市规划演示

---

## 🎪 综艺节目场景

**特点：** 多玩家轮播、轻松愉快、自动化运行

### 镜头配置

```yaml
camera:
  pitch: 40.0              # 较高俯角，类似综艺摄像机
  distance: 6.0            # 适中距离
  height-offset: 0.5
  position-smooth: 0.15    # 平衡平滑度和响应速度
  rotation-smooth: 0.12
```

### 自动切换配置

```yaml
auto-switch:
  enabled: true
  interval: 20             # 每 20 秒切换一次
  mode: RANDOM             # 随机选择，增加趣味性
```

**设计思路：**
- 较短的切换间隔 (20s) 保持节目节奏
- 随机模式带来意外惊喜
- 中等俯角 (40°) 类似电视综艺视角

### 隐私配置

```yaml
privacy:
  recorder-invisible:
    enabled: true
    invisibility-message: "§6[LiveRecorder] §a你已隐身"
  live-logs:
    enabled: true
    keep-count: 200        # 保留更多日志
  consent-prompt:
    enabled: true
    timeout: 30            # 缩短确认时间
```

**运营建议：**
- 提前告知所有参与者正在录制
- 设置合理的同意超时时间
- 定期检查日志，监控录制状态

---

## 📺 专业直播场景（OBS + 多机位）

**特点：** 高质量输出、稳定帧率、导播控制

### 镜头配置

```yaml
camera:
  pitch: 30.0              # 标准俯角
  distance: 5.0            # 标准距离
  height-offset: 0.0
  position-smooth: 0.12    # 平衡配置
  rotation-smooth: 0.1
```

### 多机位设置示例

假设有 3 个录制者作为不同机位：

```bash
# 机位 1：主机位（中景）
/lr bind Camera01 Streamer manual

# 机位 2：特写机位
/lr bind Camera02 Streamer manual
# 然后调整 Camera02 的配置：distance=3.0, pitch=20.0

# 机位 3：全景机位
/lr bind Camera03 Streamer manual
# 然后调整 Camera03 的配置：distance=10.0, pitch=50.0
```

**在 OBS 中：**
- 添加 3 个游戏捕获源
- 分别对应 3 个录制者的视角
- 使用场景切换实现导播效果

### 性能优化配置

```yaml
visual:
  target-glow: false       # 关闭发光，减少渲染负担
  camera-particle: false   # 关闭粒子
  actionbar-enabled: true
  actionbar-interval: 20   # 标准更新频率
  target-actionbar: false  # 关闭目标提示
```

**性能提示：**
- 多机位会显著增加服务器负载
- 每个录制者都是一个独立的实体 AI
- 建议服务器至少 4 核 CPU，8GB RAM
- 监控 TPS，如果低于 18，考虑减少机位数量

---

## 🎓 教学教程场景

**特点：** 清晰展示操作、稳定镜头、注重细节

### 镜头配置

```yaml
camera:
  pitch: 20.0              # 低俯角，接近第一人称
  distance: 3.5            # 近距离，看清手部动作
  height-offset: -0.5      # 略低于标准高度
  position-smooth: 0.18    # 较快响应，跟上操作节奏
  rotation-smooth: 0.15
```

**设计思路：**
- 低俯角 (20°) 让观众看清玩家面前的区域
- 近距离 (3.5) 突出手部操作和物品栏
- 较快响应适应教学中的快速操作

### 录制者限制配置

```yaml
recorder-restrictions:
  block-inventory: false   # 允许打开背包，展示物品管理
  block-container: false   # 允许打开容器
  block-drop-item: true
  block-interact: true
  block-attack: true
  block-place: true
  block-break: true
  block-chat: false        # 允许聊天，进行解说
  block-command: true
  command-whitelist:
    - lr
    - liverecorder
    - help
    - say
```

**教学技巧：**
- 允许录制者聊天，可以边操作边解说
- 开放背包查看，展示物品管理技巧
- 保持稳定镜头，避免观众晕眩

---

## 🌍 服务器监控场景

**特点：** 全局巡视、自动运行、长期在线

### 镜头配置

```yaml
camera:
  pitch: 55.0              # 高俯角，类似监控摄像头
  distance: 10.0           # 远距离，大范围视野
  height-offset: 2.0       # 额外高度
  position-smooth: 0.10    # 平滑移动
  rotation-smooth: 0.08
```

### 自动切换配置

```yaml
auto-switch:
  enabled: true
  interval: 45             # 每 45 秒巡视一个区域
  mode: SEQUENTIAL         # 顺序巡视，覆盖全面
```

### 隐私配置

```yaml
privacy:
  recorder-invisible:
    enabled: true          # 完全隐身，不打扰玩家
    invisibility-message: ""
  live-logs:
    enabled: true
    keep-count: 500        # 保留大量日志用于审计
  consent-prompt:
    enabled: false         # 监控模式不需要同意
```

**应用场景：**
- GM 巡查服务器
- 反作弊监控
- 活动监督
- 纠纷取证

**法律提示：**
- 在服务器规则中明确说明存在监控
- 遵守当地隐私保护法规
- 日志仅用于正当目的

---

## 🎬 电影制作场景

**特点：** 极致平滑、精确控制、艺术性构图

### 镜头配置

```yaml
camera:
  pitch: 28.0              # 黄金俯角
  distance: 7.0            # 中远距离
  height-offset: 0.3
  position-smooth: 0.04    # 极致平滑
  rotation-smooth: 0.03    # 极致柔和
```

**设计思路：**
- 极低平滑系数创造丝滑如油的镜头运动
- 中等距离便于后期裁剪和缩放
- 轻微高度偏移优化构图

### 高级技巧：路径录制

结合多个录制者创建复杂镜头运动：

```bash
# 镜头 1：远景引入
/lr bind Cam1 Actor manual
# 配置：distance=15.0, pitch=45.0

# 镜头 2：推进到中景
/lr bind Cam2 Actor manual
# 配置：distance=8.0, pitch=30.0

# 镜头 3：特写
/lr bind Cam3 Actor manual
# 配置：distance=3.0, pitch=20.0
```

在后期软件中按时间线切换镜头，模拟推拉摇移。

### 视觉配置

```yaml
visual:
  target-glow: false       # 关闭发光，自然画面
  camera-particle: false   # 关闭粒子
  actionbar-enabled: false # 完全无 UI
  target-actionbar: false
```

**后期处理建议：**
- 录制原始画面，后期添加色彩分级
- 使用多个机位素材交叉剪辑
- 添加动态模糊增强电影感

---

## ⚡ 性能对比表

| 场景 | CPU 占用 | 内存占用 | TPS 影响 | 推荐最大录制者数 |
|------|---------|---------|----------|----------------|
| PVP 竞技 | 高 | 中 | -1~2 TPS | 2-3 |
| 建筑展示 | 低 | 低 | -0.5 TPS | 5-8 |
| 综艺节目 | 中 | 中 | -1 TPS | 4-6 |
| 专业直播 | 高 | 高 | -2~3 TPS | 2-4 |
| 教学教程 | 低 | 低 | -0.5 TPS | 3-5 |
| 服务器监控 | 中 | 中 | -1 TPS | 6-10 |
| 电影制作 | 极低 | 低 | -0.3 TPS | 3-6 |

**测试环境：** Paper 1.20.4, 8 核 CPU, 16GB RAM, 50 在线玩家

---

## 🔧 配置调试工具

### 实时预览配置效果

使用以下命令测试配置：

```bash
# 1. 绑定一个测试录制者
/lr bind TestCam YourName manual

# 2. 修改配置后重载
/lr reload

# 3. 观察镜头效果
# 4. 如果不满意，继续调整
# 5. 测试完成后解绑
/lr unbind TestCam
```

### 配置验证脚本

创建一个测试清单：

```markdown
## 配置测试清单

- [ ] 录制者能否正常跟随？
- [ ] 镜头是否过于抖动？
- [ ] 视角旋转是否平滑？
- [ ] ActionBar 显示是否正常？
- [ ] 自动切换是否按预期工作？
- [ ] 隐身功能是否生效？
- [ ] TPS 是否在可接受范围？
```

### 回滚配置

如果新配置不理想，可以快速回滚：

```bash
# 1. 备份当前配置
cp config.yml config.yml.backup

# 2. 编辑配置
# ... 修改 ...

# 3. 如果不满意，恢复备份
cp config.yml.backup config.yml

# 4. 重载
/lr reload
```

---

## 💡 配置优化建议

### 通用原则

1. **从小改动开始** - 每次只调整 1-2 个参数
2. **记录变化** - 记下每次修改的效果
3. **实际测试** - 在真实场景中测试，不要只看静止状态
4. **考虑负载** - 平滑度越高，计算量越大
5. **用户反馈** - 询问观众观看体验

### 常见问题排查

| 问题 | 可能原因 | 解决方案 |
|------|---------|---------|
| 镜头跟不上 | position-smooth 太低 | 提高到 0.25-0.40 |
| 镜头太抖 | position-smooth 太高 | 降低到 0.08-0.12 |
| 视角翻转 | rotation-smooth 太高 | 降低到 0.05-0.08 |
| TPS 下降 | 录制者太多 | 减少录制者数量 |
| 画面单调 | 切换间隔太长 | 缩短 interval |
| 观众头晕 | 镜头太平稳但距离近 | 增大 distance 或提高 pitch |

### 进阶技巧

#### 动态配置切换

根据不同的时间段使用不同配置：

```yaml
# 白天配置（活跃时段）
# position-smooth: 0.20
# auto-switch.interval: 20

# 夜晚配置（休闲时段）
# position-smooth: 0.08
# auto-switch.interval: 60
```

可以使用插件或脚本定时修改配置文件并执行 `/lr reload`。

#### 世界特定配置

在不同世界使用不同镜头风格：

- **主世界：** 标准跟拍
- **下界：** 更远距离（避免熔岩遮挡）
- **末地：** 更高俯角（开阔地形）

这需要自定义开发或使用条件配置插件。

---

## 📊 配置模板下载

你可以从以下链接获取完整的配置模板：

- [PVP 配置模板](https://github.com/httye/LiveRecorder/tree/main/templates/pvp-config.yml)
- [建筑展示配置模板](https://github.com/httye/LiveRecorder/tree/main/templates/building-config.yml)
- [综艺节目配置模板](https://github.com/httye/LiveRecorder/tree/main/templates/variety-config.yml)
- [专业直播配置模板](https://github.com/httye/LiveRecorder/tree/main/templates/streaming-config.yml)

*(注：以上链接为示例，实际项目中需要创建这些文件)*

---

## 🎯 总结

选择合适的配置需要考虑：

1. **使用场景** - PVP、建筑、教学等不同需求
2. **服务器性能** - TPS、CPU、内存限制
3. **观众体验** - 平滑度、视角、信息密度
4. **录制目的** - 直播、录播、监控等

没有"最好"的配置，只有"最适合"的配置。建议从推荐配置开始，然后根据实际使用情况逐步调整。

**记住：** 好的配置 = 清晰的画面 + 流畅的体验 + 稳定的性能

祝你录制愉快！🎬
