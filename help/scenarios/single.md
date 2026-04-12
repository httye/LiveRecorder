# 单人直播跟拍

让一个录制者账号专门负责跟拍主播。

## 场景描述

- **适用模式：** AUTO
- **录制者数量：** 1
- **目标数量：** 1
- **推荐配置：** 标准镜头

## 操作步骤

### 1. 准备录制者账号

创建一个专用录制者账号（如 `CameraMan`），设置合适的游戏模式：

```bash
# 设置为旁观者模式（推荐）
/gamemode spectator CameraMan
```

### 2. 绑定录制者

```bash
/lr bind CameraMan 主播小明 auto
```

### 3. 开始录制

录制者会自动跟随主播，主播正常游戏即可。

## 效果预览

### 录制者视角
```
ActionBar: LiveRecorder | ● 跟随中 | 目标: 主播小明 | 模式: 自动
```

### 主播视角
```
ActionBar: 🔴 您正在被直播 | 1 位录制者跟拍中
发光效果: 黄色发光
```

### 其他玩家视角
```
看到录制者在主播身后自动跟随移动
```

## 推荐配置

### 镜头设置

```yaml
camera:
  pitch: 30.0              # 标准俯角
  distance: 5.0            # 标准距离
  height-offset: 0.0       # 无额外高度偏移
  follow-speed: 0.35       # 标准跟随速度
  arrival-threshold: 0.3   # 标准到达阈值
```

### 视觉反馈

```yaml
visual:
  target-glow: true        # 主播发光
  glow-color: YELLOW       # 黄色发光
  actionbar-enabled: true  # 显示状态
  target-actionbar: true   # 主播收到提示
```

### 自动切换

```yaml
auto-switch:
  enabled: false           # 单人跟拍无需自动切换
```

## 进阶技巧

### 切换为手动模式

如果需要精细控制，可以切换为手动模式：

```bash
/lr mode CameraMan manual
```

### 调整镜头位置

根据游戏类型调整镜头：

- **PVP 游戏：** 减小 distance，增大 follow-speed
- **建筑游戏：** 增大 pitch，获得鸟瞰视角
- **冒险游戏：** 使用标准配置即可

### 临时调整

可以使用 `/lr reload` 热重载配置，无需重启服务器。

## 常见问题

### 录制者跟丢了怎么办？

1. 检查主播是否使用了传送命令
2. 检查录制者是否还在线
3. 使用 `/lr list` 查看绑定状态

### 如何切换到另一个主播？

1. 解绑当前录制者：`/lr unbind CameraMan`
2. 绑定到新主播：`/lr bind CameraMan 新主播 auto`

### 录制者跟不上主播？

调整镜头配置：
```yaml
camera:
  follow-speed: 0.5       # 增大跟随速度
  distance: 4.0           # 减小镜头距离
```

## 最佳实践

### 录制者设置

1. 使用专用账号，不要使用正常游戏账号
2. 设置为旁观者模式，避免碰撞
3. 给录制者账号单独的权限节点

### 镜头配置

1. 根据游戏类型选择合适的镜头参数
2. 测试不同配置，找到最佳效果
3. 使用热重载快速调整配置

### 应急处理

1. 保留一个管理员账号，可以随时解绑录制者
2. 设置命令白名单，允许录制者使用基本命令
3. 准备备用录制者账号

## 示例脚本

### 快速绑定脚本

```bash
#!/bin/bash
# 快速绑定录制者到主播

RECORDER="CameraMan"
TARGET="主播小明"

# 检查录制者是否已绑定
/lr list

# 绑定录制者
/lr bind $RECORDER $TARGET auto

# 等待确认
sleep 5

# 查看绑定状态
/lr list
```

### 自动解绑脚本

```bash
#!/bin/bash
# 活动结束后自动解绑所有录制者

# 获取所有录制者
/lr list

# 逐个解绑
/lr unbind CameraMan
/lr unbind Camera02
/lr unbind Camera03

# 确认解绑
/lr list
```