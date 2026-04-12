# 安装部署

本指南将详细介绍如何安装和部署 LiveRecorder 插件。

## 📦 下载插件

### 方式一：下载编译版本

前往 [GitHub Releases](https://github.com/httye/LiveRecorder/releases) 下载最新版本的 `LiveRecorder-x.x.x.jar`。

### 方式二：本地编译

如果你需要从源代码编译：

::: code-group

```bash [使用 Maven]
git clone https://github.com/httye/LiveRecorder.git
cd LiveRecorder
mvn clean package
```

```bash [编译产物]
编译完成后，在 target/ 目录下找到：
- LiveRecorder-1.0.0.jar  (插件文件)
- LiveRecorder-1.0.0-javadoc.jar  (API 文档)
- LiveRecorder-1.0.0-sources.jar  (源代码)
```

:::

### 方式三：GitHub Actions 自动编译

1. 将代码推送到 GitHub 仓库
2. 进入 **Actions** 标签页
3. 等待编译完成
4. 在 **Artifacts** 中下载编译产物

## 🚀 安装步骤

### 步骤 1: 停止服务器

```bash
# 停止 Minecraft 服务器
stop
```

::: warning 重要
首次安装必须重启服务器，不能使用热加载（如 PlugMan）。
:::

### 步骤 2: 安装插件

将 `LiveRecorder-1.0.0.jar` 放入服务器的 `plugins/` 目录：

```bash
# Linux / macOS
cp LiveRecorder-1.0.0.jar /path/to/server/plugins/

# Windows
copy LiveRecorder-1.0.0.jar C:\path\to\server\plugins\
```

### 步骤 3: 启动服务器

```bash
# 启动 Minecraft 服务器
java -jar server.jar
```

### 步骤 4: 验证安装

检查控制台输出：

```
[LiveRecorder] ============================================
[LiveRecorder]   LiveRecorder 无人机式录制系统已启动
[LiveRecorder]   版本: 1.0.0
[LiveRecorder] ============================================
```

检查是否生成了配置文件：

```
plugins/
├── LiveRecorder/
│   ├── config.yml        # 配置文件
│   └── privacy.db        # 隐私数据库
```

## ⚙️ 初始配置

### 修改配置文件

编辑 `plugins/LiveRecorder/config.yml`：

```yaml
# 镜头设置
camera:
  pitch: 30.0              # 俯角（度）
  distance: 5.0            # 水平距离（格）
  follow-speed: 0.35       # 跟随速度

# 自动切换
auto-switch:
  enabled: true
  interval: 30            # 切换间隔（秒）
  mode: RANDOM            # 随机模式

# 视觉反馈
visual:
  target-glow: true        # 发光效果
  glow-color: YELLOW       # 发光颜色
  actionbar-enabled: true  # ActionBar 显示

# 隐私保护
privacy:
  recorder-invisible:
    enabled: true          # 录制者自动隐身
  live-logs:
    enabled: true          # 日志记录
    keep-count: 100        # 保留数量
```

### 重载配置

在游戏内或控制台执行：

```bash
/lr reload
```

## 🔐 权限配置

### 基本权限

在服务器的权限插件（如 LuckPerms、GroupManager）中配置：

```yaml
# OP 默认拥有所有权限
liverecorder.admin: true

# 普通玩家权限
liverecorder.use: true
```

### 权限说明

| 权限 | 说明 | 默认 |
|:-----|:-----|:----:|
| `liverecorder.admin` | 使用所有管理员命令 | OP |
| `liverecorder.use` | 使用玩家隐私命令 | false |

### LuckPerms 配置示例

```bash
# 给管理员权限
/lp user admin permission set liverecorder.admin true

# 给普通玩家权限
/lp user steve permission set liverecorder.use true
```

## 🗄️ 数据库配置

### SQLite 数据库

插件默认使用 SQLite 数据库，无需额外配置：

```
plugins/LiveRecorder/privacy.db
```

数据库包含两个表：

| 表名 | 用途 |
|:----:|:-----|
| `privacy_settings` | 存储玩家隐私设置 |
| `live_logs` | 存储直播操作日志 |

### 数据库结构

```sql
-- 隐私设置表
CREATE TABLE privacy_settings (
    player_uuid TEXT PRIMARY KEY,
    player_name TEXT NOT NULL,
    consent_status TEXT NOT NULL,
    invisible BOOLEAN DEFAULT 0,
    last_updated INTEGER NOT NULL
);

-- 直播日志表
CREATE TABLE live_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    log_type TEXT NOT NULL,
    recorder_uuid TEXT,
    recorder_name TEXT,
    target_uuid TEXT,
    target_name TEXT,
    timestamp INTEGER NOT NULL
);
```

## 🔧 高级配置

### 性能优化

如果服务器性能有限，可以调整以下配置：

```yaml
camera:
  follow-speed: 0.25      # 降低跟随速度，减少计算

visual:
  actionbar-interval: 40   # 降低 ActionBar 刷新频率

auto-switch:
  interval: 60            # 增加切换间隔
```

### 禁用功能

如果不需要某些功能，可以在配置中禁用：

```yaml
# 禁用视觉反馈
visual:
  target-glow: false
  camera-particle: false
  actionbar-enabled: false

# 禁用隐私保护
privacy:
  recorder-invisible:
    enabled: false
  live-logs:
    enabled: false
```

## 🐛 故障排除

### 插件未加载

**症状：** 控制台没有显示 LiveRecorder 启动信息

**解决方案：**
1. 检查 Java 版本（需要 Java 8+）
2. 检查 Spigot/Paper 版本（需要 1.16+）
3. 查看错误日志（logs/latest.log）
4. 确保没有其他插件冲突

### 数据库错误

**症状：** 提示数据库连接失败

**解决方案：**
1. 检查 `plugins/LiveRecorder/` 目录权限
2. 确保 SQLite 驱动已正确加载
3. 尝试删除 `privacy.db` 重新生成

### 命令无法使用

**症状：** 提示"未知命令"

**解决方案：**
1. 检查权限配置
2. 确认插件已正确加载
3. 尝试使用完整命令 `/liverecorder` 而不是 `/lr`

## 📋 卸载步骤

如果需要卸载插件：

```bash
# 1. 停止服务器
stop

# 2. 删除插件文件
rm plugins/LiveRecorder-1.0.0.jar

# 3. 删除数据文件（可选）
rm -rf plugins/LiveRecorder/

# 4. 启动服务器
java -jar server.jar
```

::: tip 数据备份
卸载前建议备份 `plugins/LiveRecorder/` 目录，以防需要恢复数据。
:::

## 📚 下一步

安装完成后，继续学习：

- [快速开始](/guide/getting-started) - 快速上手指南
- [基本概念](/guide/basics) - 了解核心概念
- [配置详解](/config/index) - 完整配置说明

## 🆘 获取帮助

遇到安装问题？

- 查看 [常见问题](/faq/index)
- 提交 [GitHub Issue](https://github.com/httye/LiveRecorder/issues)
- 查看 [GitHub Discussions](https://github.com/httye/LiveRecorder/discussions)

---

祝你安装顺利！🎉