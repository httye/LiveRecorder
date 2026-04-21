# GitHub Actions 工作流说明

本项目使用 GitHub Actions 实现自动化构建、发布和文档部署。

## 📋 工作流列表

### 1. Build & Release (`build.yml`)

**触发条件:**
- 推送到 `main`, `master`, `dev` 分支
- 推送 tag (格式: `v*`)
- Pull Request 到 `main` 或 `master`
- 手动触发

**功能:**
- ✅ 在 Java 8, 11, 17 三个版本上编译
- ✅ 运行 Maven 打包
- ✅ 上传构建产物(仅 Java 17)
- ✅ 当推送 tag 时自动创建 GitHub Release

**Release 特性:**
- 自动生成更新日志
- 按提交类型分类显示
- 包含兼容性信息
- 附加编译好的 JAR 文件

---

### 2. Deploy VitePress (`deploy-pages.yml`)

**触发条件:**
- 推送到 `main` 或 `master` 分支且 `help/` 目录有变化
- 手动触发

**功能:**
- ✅ 安装 Node.js 依赖
- ✅ 构建 VitePress 文档
- ✅ 自动部署到 `gh-pages` 分支
- ✅ 配置自定义域名 (lr.hosh.dev)
- ✅ 保留现有文件

**访问地址:**
- https://lr.hosh.dev
- https://httye.github.io/LiveRecorder

---

### 3. Auto Version Bump (`auto-version.yml`)

**触发条件:**
- 仅手动触发

**功能:**
- ✅ 自动递增版本号 (major/minor/patch)
- ✅ 更新 `pom.xml` 中的版本号
- ✅ 更新 `CHANGELOG.md`
- ✅ 创建并推送新的 Git tag
- ✅ 自动触发 Release 工作流

**使用方法:**

1. 进入 GitHub 仓库页面
2. 点击 **Actions** 标签
3. 选择 **Auto Version Bump** 工作流
4. 点击 **Run workflow**
5. 选择版本类型:
   - `patch` - 补丁版本 (1.0.0 → 1.0.1)
   - `minor` - 次要版本 (1.0.0 → 1.1.0)
   - `major` - 主要版本 (1.0.0 → 2.0.0)
6. 输入提交消息(可选)
7. 点击 **Run workflow**

---

## 🚀 完整发布流程

### 方法一: 自动发布(推荐)

```bash
# 1. 完成代码开发和测试
git add .
git commit -m "feat: add new feature"
git push origin main

# 2. 使用 GitHub Actions 自动版本号管理
#    (在 GitHub UI 中触发 Auto Version Bump)

# 3. 系统自动执行:
#    - 更新版本号
#    - 创建 tag
#    - 触发 Build & Release
#    - 创建 GitHub Release
#    - 如果文档有更新，自动部署 Pages
```

### 方法二: 手动发布

```bash
# 1. 更新版本号
# 编辑 pom.xml, 修改 <version> 标签

# 2. 更新 CHANGELOG.md
# 添加新版本的更新说明

# 3. 提交更改
git add pom.xml help/CHANGELOG.md
git commit -m "chore: release v1.0.3"
git push origin main

# 4. 创建并推送 tag
git tag -a v1.0.3 -m "Release v1.0.3"
git push origin v1.0.3

# 5. GitHub Actions 自动:
#    - 编译项目
#    - 创建 Release
#    - 部署文档(如果有更新)
```

---

## 📊 工作流状态

查看工作流运行状态:
- GitHub 仓库 → **Actions** 标签
- 或直接访问: https://github.com/httye/LiveRecorder/actions

---

## 🔧 工作流配置说明

### 环境变量

所有工作流使用以下默认配置:
- **Node.js**: 20.x (文档构建)
- **Java**: 8, 11, 17 (多版本编译)
- **Maven**: 自动缓存加速

### 权限要求

工作流需要以下权限:
- `contents: write` - 推送代码、创建 Release
- `packages: write` - 发布包(未来使用)

### 缓存策略

- **Maven 依赖**: 跨工作流缓存,加速构建
- **npm 依赖**: 基于 `package-lock.json` 缓存
- **构建产物**: 保留 30 天

---

## ⚠️ 注意事项

### 1. Tag 命名规范

必须遵循语义化版本:
```bash
✅ 正确: v1.0.0, v1.2.3, v2.0.0-beta
❌ 错误: 1.0.0, version-1.0, v1
```

### 2. 分支保护

建议启用分支保护规则:
- 要求 PR 审查
- 要求 CI 通过
- 禁止强制推送

### 3. 并发控制

Pages 部署工作流启用了并发控制:
- 同一时间只运行一个部署
- 新的部署会等待当前完成
- 避免冲突和不一致

### 4. 失败处理

如果工作流失败:
1. 查看 **Actions** 标签中的详细日志
2. 检查错误信息
3. 修复问题后重新触发
4. 可以手动重试失败的 job

---

## 🎯 最佳实践

### 提交消息规范

遵循约定式提交 (Conventional Commits):

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式(不影响功能)
refactor: 重构代码
test: 添加测试
chore: 构建过程或辅助工具变动
```

### 版本发布频率

- **Patch**: 每周或按需发布( Bug 修复)
- **Minor**: 每 2-4 周发布(新功能)
- **Major**: 每季度或按需发布(重大变更)

### 文档更新

- 每次代码变更后同步更新文档
- 保持 CHANGELOG.md 最新
- 确保 API 文档与实际代码一致

---

## 🆘 常见问题

### Q: Release 没有自动创建?

A: 检查以下几点:
1. Tag 名称是否以 `v` 开头
2. Build 工作流是否成功完成
3. 查看 Actions 日志是否有错误

### Q: Pages 部署失败?

A: 可能原因:
1. VitePress 构建失败(检查死链接)
2. gh-pages 分支权限问题
3. 网络超时(重试即可)

### Q: 如何取消正在运行的工作流?

A: 
1. 进入 **Actions** 标签
2. 点击正在运行的工作流
3. 点击右上角 **Cancel workflow**

### Q: 如何重新运行失败的工作流?

A:
1. 进入失败的工作流
2. 点击右上角 **Re-run jobs**
3. 选择 **Re-run all jobs** 或特定 job

---

## 📚 相关资源

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [语义化版本 2.0.0](https://semver.org/lang/zh-CN/)
- [约定式提交](https://www.conventionalcommits.org/zh-hans/)
- [VitePress 文档](https://vitepress.dev/)

---

Made with ❤️ by 七月个人制作组
