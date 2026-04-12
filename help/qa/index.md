# 问答

这里是 LiveRecorder 的互动问答区域，你可以找到常见问题的答案。

## 🔥 热门问题

<details>
<summary><b>Q1: 录制者为什么有时候会瞬移？</b></summary>

**A:** 录制者出现瞬移通常有以下几种原因：

1. **距离过远**: 当录制者距离目标超过 30 格时，插件会自动传送录制者到镜头位置，以快速恢复跟随。
2. **目标玩家传送**: 如果目标玩家使用 `/tp`、死亡重生或世界切换等传送操作，录制者会立即传送到新的目标位置。
3. **服务器延迟**: 在高延迟的情况下，录制者的位置计算可能出现误差，导致瞬移。

**解决方法：**
- 调低 `follow-speed` 值（如 0.2）让跟随更平滑
- 增大 `distance` 让镜头更远，减少需要瞬移的频率
- 检查服务器性能，确保延迟在可接受范围内

</details>

<details>
<summary><b>Q2: 如何让录制者使用旁观者模式？</b></summary>

**A:** 录制者可以使用旁观者模式，这样可以完全不受碰撞和限制影响。

**步骤：**
```bash
# 1. 先将录制者设为旁观者模式
/gamemode spectator CameraMan

# 2. 然后绑定录制者
/lr bind CameraMan Steve auto
```

**注意：** 旁观者模式下，录制者可以穿墙和飞行，但仍然会受到插件的位置控制。

</details>

<details>
<summary><b>Q3: 目标玩家下线了怎么办？</b></summary>

**A:** 当目标玩家下线时，插件会自动处理：

1. **AUTO 模式**: 录制者会自动切换到其他在线目标。如果没有其他目标，跟随会暂停。
2. **MANUAL 模式**: 录制者会停留在最后位置，等待管理员手动切换目标。

**录制者会收到通知：**
```
[LiveRecorder] 目标玩家 Steve 已下线，已暂停跟随
```

**目标玩家上线后：**
- AUTO 模式：录制者会自动重新跟随
- MANUAL 模式：需要管理员手动切换

</details>

<details>
<summary><b>Q4: 可以同时跟拍多个目标吗？</b></summary>

**A:** 一个录制者同一时间只能跟拍一个目标。但你可以绑定多个录制者到不同目标，实现多机位效果。

**示例：**
```bash
# 三个录制者跟拍不同目标
/lr bind Camera01 Steve auto
/lr bind Camera02 Alex auto
/lr bind Camera03 Bob auto
```

这样导播可以在三个录制者间切换画面，实现多机位直播。

</details>

<details>
<summary><b>Q5: 如何禁用录制者的操作限制？</b></summary>

**A:** 你可以通过配置文件禁用特定的操作限制。

**编辑 `config.yml`：**
```yaml
recorder-restrictions:
  block-inventory: false     # 允许打开背包
  block-container: false     # 允许打开容器
  block-interact: false      # 允许交互
  block-attack: false        # 允许攻击
  block-place: false         # 允许放置方块
  block-break: false         # 允许破坏方块
  block-chat: false          # 允许聊天
  block-command: false       # 允许使用命令
```

**重载配置：**
```bash
/lr reload
```

::: warning 注意
禁用操作限制可能会导致录制画面出现干扰，建议仅在特殊情况下使用。
:::

</details>

<details>
<summary><b>Q6: 录制者跟不上目标玩家怎么办？</b></summary>

**A:** 录制者跟不上通常是因为跟随速度设置太低或目标移动太快。

**解决方法：**

1. **调高跟随速度：**
```yaml
camera:
  follow-speed: 0.50  # 从 0.35 提高到 0.50
```

2. **减小镜头距离：**
```yaml
camera:
  distance: 3.0  # 从 5.0 减小到 3.0
```

3. **增大俯角：**
```yaml
camera:
  pitch: 15.0  # 从 30.0 减小到 15.0，更接近水平
```

4. **使用快速跟随预设：**
```yaml
camera:
  pitch: 25.0
  distance: 4.0
  follow-speed: 0.50
```

</details>

<details>
<summary><b>Q7: 如何查看直播历史记录？</b></summary>

**A:** 使用 `/lr logs` 命令查看直播日志。

**基本用法：**
```bash
/lr logs          # 查看最近 20 条日志
/lr logs 50       # 查看最近 50 条日志
/lr logs 100      # 查看最近 100 条日志
```

**日志格式：**
```
[2026-04-12 10:30:15] START | 录制者: CameraMan | 目标: Steve
[2026-04-12 10:35:22] SWITCH | 录制者: CameraMan | 目标: Alex
[2026-04-12 10:40:30] END | 录制者: CameraMan | 目标: Alex
```

**日志类型：**
- `START`: 开始跟拍
- `END`: 结束跟拍
- `SWITCH`: 切换目标
- `ACCEPT`: 玩家同意直播
- `DECLINE`: 玩家拒绝直播

</details>

<details>
<summary><b>Q8: 如何备份和恢复隐私数据？</b></summary>

**A:** 隐私数据存储在 SQLite 数据库中，可以直接复制文件备份。

**备份数据：**
```bash
# 停止服务器
stop

# 备份数据库文件
cp plugins/LiveRecorder/privacy.db plugins/LiveRecorder/privacy.db.backup
```

**恢复数据：**
```bash
# 停止服务器
stop

# 恢复数据库文件
cp plugins/LiveRecorder/privacy.db.backup plugins/LiveRecorder/privacy.db

# 启动服务器
java -jar server.jar
```

::: tip 定期备份
建议定期备份数据库文件，特别是在重要活动或直播之前。
:::

</details>

<details>
<summary><b>Q9: 录制者可以正常说话吗？</b></summary>

**A:** 默认情况下，录制者不能说话（被阻止聊天）。

**允许录制者聊天：**
```yaml
recorder-restrictions:
  block-chat: false  # 改为 false
```

**重载配置：**
```bash
/lr reload
```

::: info 注意
允许聊天可能会在直播画面中出现聊天气泡，建议根据实际需求决定是否开启。
:::

</details>

<details>
<summary><b>Q10: 如何自定义 ActionBar 显示的内容？</b></summary>

**A:** 目前 ActionBar 的显示内容由插件内部决定，不支持自定义。但你可以通过配置控制是否显示和刷新频率。

**配置 ActionBar：**
```yaml
visual:
  actionbar-enabled: true      # 是否显示 ActionBar
  actionbar-interval: 20       # 刷新间隔（tick）
  target-actionbar: true       # 目标玩家是否显示被直播提示
```

**刷新频率对照表：**
- `20` ticks = 1 秒
- `10` ticks = 0.5 秒
- `40` ticks = 2 秒

</details>

## 📊 功能对比

| 功能 | 说明 | 是否支持 |
|:-----|:-----|:--------:|
| 多录制者同时工作 | 多个录制者同时跟拍不同目标 | ✅ |
| 自动切换目标 | 按时间间隔自动切换 | ✅ |
| 手动切换目标 | 管理员手动控制切换 | ✅ |
| 录制者隐身 | 对其他玩家不可见 | ✅ |
| 隐私同意机制 | 玩家可以拒绝被直播 | ✅ |
| 直播日志记录 | 记录所有直播操作 | ✅ |
| 录制者限制 | 限制录制者操作 | ✅ |
| 视觉反馈 | 发光、粒子、ActionBar | ✅ |
| 高精度镜头计算 | BigDecimal 高精度计算 | ✅ |
| 弹性跟随算法 | 平滑跟随，无瞬移感 | ✅ |

## 🔗 相关资源

- [常见问题](/faq/index) - 更详细的常见问题解答
- [配置详解](/config/index) - 完整配置说明
- [使用场景](/scenarios/index) - 实际应用案例
- [GitHub Issues](https://github.com/httye/LiveRecorder/issues) - 提交问题

## 💬 没有找到答案？

如果你没有找到问题的答案，可以：

1. 查看 [常见问题](/faq/index) 获取更多解答
2. 提交 [GitHub Issue](https://github.com/httye/LiveRecorder/issues) 获取帮助
3. 加入社区讨论，与其他用户交流

---

希望这个问答页面能帮助你解决问题！🎉