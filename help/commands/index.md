# 命令概览

LiveRecorder 提供了丰富的命令来管理录制者和隐私设置。

## 📋 命令列表

| 命令 | 类型 | 权限 | 说明 |
|:-----|:-----:|:-----:|:-----|
| `/lr bind` | 管理员 | admin | 绑定录制者到目标 |
| `/lr unbind` | 管理员 | admin | 解除录制者绑定 |
| `/lr list` | 管理员 | admin | 列出所有绑定 |
| `/lr mode` | 管理员 | admin | 切换绑定模式 |
| `/lr switch` | 管理员 | admin | 手动切换目标 |
| `/lr reload` | 管理员 | admin | 重载配置 |
| `/lr logs` | 管理员 | admin | 查看直播日志 |
| `/lr accept` | 玩家 | use | 同意被直播 |
| `/lr decline` | 玩家 | use | 拒绝被直播 |
| `/lr privacy` | 玩家 | use | 查看隐私设置 |
| `/lr setprivacy` | 玩家 | use | 设置隐私状态 |

## 🔑 权限说明

### 管理员权限

`liverecorder.admin` - 使用所有管理员命令

**默认分配：** OP

**配置示例（LuckPerms）：**
```bash
/lp group admin permission set liverecorder.admin true
/lp user steve permission set liverecorder.admin true
```

### 玩家权限

`liverecorder.use` - 使用玩家隐私命令

**默认分配：** 无

**配置示例（LuckPerms）：**
```bash
/lp group default permission set liverecorder.use true
/lp user steve permission set liverecorder.use true
```

## 🎯 命令分类

### 管理员命令

用于管理录制者和系统配置：

- [`/lr bind`](/commands/admin#bind) - 绑定录制者
- [`/lr unbind`](/commands/admin#unbind) - 解除绑定
- [`/lr list`](/commands/admin#list) - 查看绑定列表
- [`/lr mode`](/commands/admin#mode) - 切换模式
- [`/lr switch`](/commands/admin#switch) - 切换目标
- [`/lr reload`](/commands/admin#reload) - 重载配置
- [`/lr logs`](/commands/admin#logs) - 查看日志

### 玩家命令

用于管理隐私设置：

- [`/lr accept`](/commands/player#accept) - 同意直播
- [`/lr decline`](/commands/player#decline) - 拒绝直播
- [`/lr privacy`](/commands/player#privacy) - 查看隐私
- [`/lr setprivacy`](/commands/player#setprivacy) - 设置隐私

## 💡 使用提示

### Tab 补全

所有命令都支持 Tab 补全：

```bash
/lr bind C<Tab>  # 补全玩家名
/lr setprivacy a<Tab>  # 补全选项
```

### 命令别名

可以使用 `lr` 代替 `liverecorder`：

```bash
/lr bind CameraMan Steve
# 等同于
/liverecorder bind CameraMan Steve
```

### 权限检查

如果提示"没有权限"，检查以下内容：

1. 是否有对应的权限节点
2. 权限插件是否正确配置
3. 权限是否已刷新（重启服务器或使用重载命令）

## 📖 详细说明

点击下方链接查看每个命令的详细说明：

### 管理员命令

- [/lr bind](/commands/admin#bind) - 绑定录制者到目标
- [/lr unbind](/commands/admin#unbind) - 解除录制者绑定
- [/lr list](/commands/admin#list) - 列出所有绑定
- [/lr mode](/commands/admin#mode) - 切换绑定模式
- [/lr switch](/commands/admin#switch) - 手动切换目标
- [/lr reload](/commands/admin#reload) - 重载配置文件
- [/lr logs](/commands/admin#logs) - 查看直播日志

### 玩家命令

- [/lr accept](/commands/player#accept) - 同意被直播
- [/lr decline](/commands/player#decline) - 拒绝被直播
- [/lr privacy](/commands/player#privacy) - 查看隐私设置
- [/lr setprivacy](/commands/player#setprivacy) - 设置隐私状态

## 🔍 常见问题

### 命令提示"未知命令"

**可能原因：**
1. 插件未正确加载
2. 命令拼写错误
3. 权限不足

**解决方案：**
1. 检查插件是否已加载
2. 检查命令拼写
3. 确认有相应权限

### 权限提示"没有权限"

**可能原因：**
1. 没有配置权限节点
2. 权限插件未正确配置
3. 权限未刷新

**解决方案：**
1. 检查权限配置
2. 确认权限插件正确配置
3. 重启服务器或使用重载命令

### Tab 补全不工作

**可能原因：**
1. 插件版本过旧
2. Tab 补全功能未启用

**解决方案：**
1. 更新插件到最新版本
2. 检查配置文件中的 Tab 补全设置

## 📚 相关文档

- [快速开始](/guide/getting-started) - 快速上手指南
- [配置详解](/config/index) - 完整配置说明
- [使用场景](/scenarios/index) - 实际应用案例

---

继续学习命令的详细用法！🚀