# 配置问题

关于配置文件的常见问题。

## 配置文件位置

### 配置文件路径

```
plugins/LiveRecorder/config.yml
```

### 默认配置

首次启动时，插件会自动生成默认配置文件。

## 如何修改配置？

### 方法一：直接编辑配置文件

1. 打开 `plugins/LiveRecorder/config.yml`
2. 修改需要调整的配置项
3. 保存文件
4. 在游戏内执行 `/lr reload` 热重载配置

### 方法二：使用命令热重载

修改配置后，直接在游戏内执行：
```bash
/lr reload
```

## 配置不生效？

### 检查配置语法

确保 YAML 语法正确：
- 使用空格缩进，不要使用 Tab
- 冒号后面要有空格
- 布尔值使用 `true` 或 `false`

### 重载配置

修改配置后，必须执行：
```bash
/lr reload
```

### 检查配置文件

确保修改的是正确的配置文件：
```
plugins/LiveRecorder/config.yml
```

不是服务器根目录的配置文件。

## 配置验证

### 自动验证

修改配置后，插件会自动验证配置的有效性。如果配置无效，插件会：

1. 在控制台输出警告信息
2. 使用默认值替代无效配置
3. 继续正常运行

### 手动验证

使用 YAML 验证工具验证配置文件：
- 在线 YAML 验证器
- VSCode YAML 插件
- IntelliJ IDEA YAML 插件

## 常见配置错误

### 缩进错误

**错误示例：**
```yaml
camera:
pitch: 30.0    # 错误：缺少缩进
```

**正确示例：**
```yaml
camera:
  pitch: 30.0  # 正确：使用两个空格缩进
```

### 布尔值错误

**错误示例：**
```yaml
enabled: yes    # 错误：yes 不是有效的布尔值
```

**正确示例：**
```yaml
enabled: true   # 正确：使用 true 或 false
```

### 字符串错误

**错误示例：**
```yaml
glow-color: 'YELLOW'    # 错误：不需要引号
```

**正确示例：**
```yaml
glow-color: YELLOW       # 正确：直接使用字符串
```

## 如何重置配置？

### 方法一：删除配置文件

```bash
rm plugins/LiveRecorder/config.yml
```

重启服务器，插件会重新生成默认配置。

### 方法二：备份后重置

```bash
# 备份当前配置
cp plugins/LiveRecorder/config.yml plugins/LiveRecorder/config.yml.backup

# 删除配置文件
rm plugins/LiveRecorder/config.yml

# 重启服务器
```

## 配置文件损坏？

### 症状

- 配置文件无法读取
- 插件无法正常加载
- 控制台出现 YAML 解析错误

### 解决方案

#### 方法一：修复配置

检查并修复 YAML 语法错误。

#### 方法二：重置配置

删除配置文件，重启服务器：
```bash
rm plugins/LiveRecorder/config.yml
```

#### 方法三：从备份恢复

```bash
cp plugins/LiveRecorder/config.yml.backup plugins/LiveRecorder/config.yml
```

## 配置文件过大？

### 原因

- 添加了过多的注释
- 添加了不必要的配置项

### 解决方案

#### 删除注释

删除不必要的注释，保持文件简洁。

#### 精简配置

只保留必要的配置项，删除默认值。

#### 使用默认值

如果配置项与默认值相同，可以删除该配置项。

## 配置文件权限？

### 正确权限

```bash
chmod 644 plugins/LiveRecorder/config.yml
```

### 权限问题

如果权限不正确，插件可能无法读取或写入配置文件。

## 配置文件版本？

### 向后兼容

插件会尽量保持配置文件的向后兼容性。

### 更新配置

更新插件版本时，建议：
1. 备份当前配置文件
2. 更新插件
3. 检查新配置项
4. 更新配置文件

## 如何获取默认配置？

### 方法一：删除配置文件

```bash
rm plugins/LiveRecorder/config.yml
```

重启服务器，插件会重新生成默认配置。

### 方法二：从 GitHub 获取

从 GitHub 仓库获取默认配置：
```
https://github.com/httye/LiveRecorder/blob/main/src/main/resources/config.yml
```

## 配置文件示例

### 最小配置

```yaml
camera:
  pitch: 30.0
  distance: 5.0
  follow-speed: 0.35

auto-switch:
  enabled: true
  interval: 30
  mode: RANDOM

visual:
  target-glow: true
  glow-color: YELLOW
  actionbar-enabled: true
  target-actionbar: true

privacy:
  recorder-invisible:
    enabled: true
  live-logs:
    enabled: true
    keep-count: 100
  consent-prompt:
    enabled: true
    timeout: 60
```

### 完整配置

查看完整配置文件：
```
plugins/LiveRecorder/config.yml
```

或从 GitHub 获取：
```
https://github.com/httye/LiveRecorder/blob/main/src/main/resources/config.yml
```