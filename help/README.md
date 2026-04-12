# LiveRecorder 文档

本目录包含 LiveRecorder 的详细文档，使用 VitePress 构建。

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run docs:dev
```

访问本地文档查看。

### 构建静态文件

```bash
npm run docs:build
```

构建产物位于 `.vitepress/dist/` 目录。

### 预览构建结果

```bash
npm run docs:preview
```

## 文档结构

```
help/
├── .vitepress/
│   └── config.ts          # VitePress 配置
├── guide/                 # 入门指南
├── commands/              # 命令参考
├── config/                # 配置详解
├── scenarios/             # 使用场景
├── privacy/               # 隐私保护
├── faq/                   # 常见问题
├── api/                   # API 参考
├── index.md               # 首页
├── package.json           # npm 配置
└── README.md              # 本文件
```

## 文档分类

### 入门指南

- 快速开始
- 安装部署
- 基本概念

### 命令参考

- 命令概览
- 管理员命令
- 玩家命令

### 配置详解

- 配置概览
- 镜头设置
- 自动切换
- 视觉反馈
- 录制者限制
- 隐私设置

### 使用场景

- 场景概览
- 单人直播跟拍
- 多机位直播
- 自动轮换跟拍
- 手动控制跟拍
- 隐私保护模式

### 隐私保护

- 隐私概览
- 同意/拒绝机制
- 录制者隐身
- 直播日志
- 数据存储

### 常见问题

- 问题概览
- 跟随问题
- 性能问题
- 隐私问题
- 配置问题

### API 参考

- API 概览
- LiveRecorder 类
- LiveCore 类
- DatabaseManager 类
- CameraGeometry 类

## 贡献文档

欢迎贡献文档改进！

1. Fork 本仓库
2. 修改或添加文档
3. 提交 Pull Request

## 部署

### 部署到 GitHub Pages

构建完成后，将 `.vitepress/dist/` 目录的内容推送到 `gh-pages` 分支：

```bash
npm run docs:build
git add .vitepress/dist
git commit -m "Update docs"
git subtree push --prefix help/.vitepress/dist origin gh-pages
```

### 部署到其他平台

`.vitepress/dist/` 目录包含所有静态文件，可以部署到任何静态网站托管服务。

## 许可证

文档遵循 MIT 许可证。