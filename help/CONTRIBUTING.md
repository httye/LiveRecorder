# 贡献指南

感谢你考虑为 LiveRecorder 做出贡献！本文档将指导你如何参与项目开发。

## 📋 目录

- [行为准则](#行为准则)
- [如何贡献](#如何贡献)
- [开发流程](#开发流程)
- [代码规范](#代码规范)
- [提交规范](#提交规范)
- [问题报告](#问题报告)
- [功能请求](#功能请求)
- [文档贡献](#文档贡献)

## 🤝 行为准则

参与 LiveRecorder 项目，你需要遵守以下行为准则：

- **尊重他人** - 尊重所有贡献者，无论他们的经验水平
- **友善交流** - 使用友善、专业的语言进行交流
- **接受批评** - 建设性地接受批评，专注于项目的改进
- **保持耐心** - 理解社区成员有不同的时间表和优先级

## 🚀 如何贡献

有多种方式可以为 LiveRecorder 做出贡献：

### 报告问题

发现 Bug？请在 [GitHub Issues](https://github.com/httye/LiveRecorder/issues) 中报告。

### 提交代码

修复 Bug 或添加新功能？欢迎提交 Pull Request！

### 改进文档

发现文档有误或不完整？欢迎改进文档！

### 提出建议

有好的想法？在 [GitHub Discussions](https://github.com/httye/LiveRecorder/discussions) 中讨论！

## 💻 开发流程

### 1. Fork 仓库

在 GitHub 上 Fork 本仓库到你的账号。

### 2. 克隆仓库

```bash
git clone https://github.com/你的用户名/LiveRecorder.git
cd LiveRecorder
```

### 3. 添加上游仓库

```bash
git remote add upstream https://github.com/httye/LiveRecorder.git
```

### 4. 创建特性分支

```bash
git checkout -b feature/AmazingFeature
```

### 5. 进行开发

- 修改代码
- 编写测试（如果有）
- 更新文档（如果需要）

### 6. 提交更改

```bash
git add .
git commit -m "feat: add amazing feature"
```

### 7. 推送到你的 Fork

```bash
git push origin feature/AmazingFeature
```

### 8. 提交 Pull Request

在 GitHub 上提交 Pull Request 到主仓库的 `main` 分支。

## 📐 代码规范

### Java 代码规范

遵循以下 Java 代码规范：

- **缩进**：使用 4 个空格（不要使用 Tab）
- **命名**：使用驼峰命名法
  - 类名：`PascalCase`（如 `LiveRecorder`）
  - 方法名：`camelCase`（如 `getDatabaseManager`）
  - 变量名：`camelCase`（如 `cameraDistance`）
  - 常量名：`UPPER_SNAKE_CASE`（如 `MAX_DISTANCE`）
- **注释**：为公共方法和复杂逻辑添加注释
- **文档**：使用 Javadoc 格式添加文档注释

**示例：**

```java
/**
 * 获取数据库管理器
 *
 * @return 数据库管理器实例
 */
public DatabaseManager getDatabaseManager() {
    return databaseManager;
}
```

### 文档规范

遵循以下文档规范：

- **Markdown**：使用标准 Markdown 语法
- **标题**：使用层级标题（#、##、###）
- **代码**：使用 ``` 语言名 标记代码块
- **表格**：使用标准 Markdown 表格
- **链接**：使用相对链接（如 `/guide/getting-started`）

## 📝 提交规范

使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范提交信息。

### 提交信息格式

```
<类型>(<范围>): <描述>

[可选正文]

[可选脚注]
```

### 类型

- `feat`: 新功能
- `fix`: 问题修复
- `docs`: 文档更新
- `style`: 代码格式（不影响代码运行）
- `refactor`: 重构（既不是新功能也不是修复）
- `perf`: 性能优化
- `test`: 添加测试
- `chore`: 构建过程或辅助工具的变动

### 示例

```
feat(privacy): add consent confirmation mechanism

Players can now accept or decline being streamed through
the /lr accept and /lr decline commands.

Closes #123
```

```
fix(camera): correct pitch calculation in high precision mode

Fixed an issue where the camera pitch was not calculated
correctly when using BigDecimal for high precision calculations.
```

```
docs(readme): update installation instructions

Updated the installation section to include the new
database initialization step.
```

## 🐛 问题报告

### 报告 Bug

报告 Bug 时，请提供以下信息：

1. **问题描述** - 清晰描述问题
2. **复现步骤** - 如何复现问题
3. **预期行为** - 你期望发生什么
4. **实际行为** - 实际发生了什么
5. **环境信息**：
   - LiveRecorder 版本
   - Minecraft/Spigot 版本
   - Java 版本
   - 操作系统
6. **日志信息** - 相关的错误日志
7. **配置信息** - 相关的配置文件内容

### 问题报告模板

```markdown
**问题描述**
简要描述问题。

**复现步骤**
1. 步骤 1
2. 步骤 2
3. 步骤 3

**预期行为**
描述你期望发生什么。

**实际行为**
描述实际发生了什么。

**环境信息**
- LiveRecorder 版本: 1.0.0
- Spigot 版本: 1.20.4
- Java 版本: 17
- 操作系统: Ubuntu 22.04

**日志信息**
```
粘贴相关日志
```

**配置信息**
```yaml
# 粘贴相关配置
```
```

## 💡 功能请求

### 提出新功能

提出新功能时，请提供以下信息：

1. **功能描述** - 清晰描述新功能
2. **使用场景** - 这个功能有什么用
3. **实现建议** - 如何实现这个功能（如果有）
4. **替代方案** - 是否有其他替代方案

### 功能请求模板

```markdown
**功能描述**
简要描述新功能。

**使用场景**
描述这个功能的使用场景。

**实现建议**
如果有实现建议，请详细说明。

**替代方案**
是否有其他替代方案？
```

## 📚 文档贡献

### 改进文档

改进文档时，请遵循以下原则：

1. **准确性** - 确保信息准确无误
2. **清晰性** - 使用简洁明了的语言
3. **完整性** - 提供完整的信息
4. **示例** - 提供实际的使用示例
5. **更新** - 及时更新文档以反映代码变更

### 文档类型

- **用户文档** - 面向用户的使用指南
- **开发文档** - 面向开发者的技术文档
- **API 文档** - API 参考文档
- **贡献文档** - 贡献指南

## 🧪 测试

### 编写测试

如果你添加了新功能，请编写相应的测试：

```java
@Test
public void testNewFeature() {
    // 测试代码
}
```

### 运行测试

```bash
mvn test
```

## 🔄 同步上游仓库

定期同步上游仓库以获取最新更改：

```bash
git fetch upstream
git checkout main
git merge upstream/main
git push origin main
```

## 📧 联系方式

- GitHub: [httye/LiveRecorder](https://github.com/httye/LiveRecorder)
- Issues: [提交问题](https://github.com/httye/LiveRecorder/issues)
- Discussions: [参与讨论](https://github.com/httye/LiveRecorder/discussions)

## 🙏 致谢

感谢所有为 LiveRecorder 做出贡献的开发者！

### 特别感谢

- Spigot 社区提供的服务器 API
- 所有提交 Issue 和 PR 的贡献者
- 使用并支持 LiveRecorder 的用户

## 📜 开源协议

通过贡献代码，你同意你的贡献将根据 [MIT License](LICENSE) 进行授权。

## 🏆 贡献者

感谢所有为 LiveRecorder 做出贡献的开发者！

### 特别感谢

- Spigot 社区提供的服务器 API
- 所有提交 Issue 和 PR 的贡献者
- 使用并支持 LiveRecorder 的用户

---

再次感谢你的贡献！🎉

**Made with ❤️ by 七月个人制作组**