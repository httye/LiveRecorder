import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'LiveRecorder',
  description: '无人机式自由录制者管理系统',
  lang: 'zh-CN',
  base: '/',

  // 主题配置
  themeConfig: {
    // Logo
    logo: '/logo.svg',
    
    // 顶部导航 - 简化版
    nav: [
      { text: '首页', link: '/' },
      { text: '关于', link: '/about' },
      { text: '指南', link: '/guide/getting-started' },
      { text: '命令', link: '/commands/index' },
      { text: '配置', link: '/config/index' },
      { text: '场景', link: '/scenarios/index' },
      { text: '隐私', link: '/privacy/index' },
      { text: '问答', link: '/qa/index' },
      { text: 'API', link: '/api/index' },
      {
        text: '更多',
        items: [
          { text: '更新日志', link: '/CHANGELOG.md' },
          { text: '贡献指南', link: '/CONTRIBUTING.md' },
          { text: '获取支持', link: '/SUPPORT.md' },
          { text: '安全策略', link: '/SECURITY.md' },
          { text: '行为准则', link: '/CODE_OF_CONDUCT.md' },
          { text: 'GitHub', link: 'https://github.com/httye/LiveRecorder' },
          { text: '发布日志', link: 'https://github.com/httye/LiveRecorder/releases' },
        ]
      }
    ],

    // 侧边栏 - 主要导航方式
    sidebar: {
      '/guide/': [
        {
          text: '📚 入门指南',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '快速开始', link: '/guide/getting-started' },
            { text: '安装部署', link: '/guide/installation' },
            { text: '基本概念', link: '/guide/basics' },
          ]
        }
      ],
      
      '/commands/': [
        {
          text: '⌨️ 命令参考',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '命令概览', link: '/commands/index' },
            {
              text: '管理员命令',
              collapsible: true,
              collapsed: false,
              items: [
                { text: '/lr bind', link: '/commands/admin#bind' },
                { text: '/lr unbind', link: '/commands/admin#unbind' },
                { text: '/lr list', link: '/commands/admin#list' },
                { text: '/lr mode', link: '/commands/admin#mode' },
                { text: '/lr switch', link: '/commands/admin#switch' },
                { text: '/lr reload', link: '/commands/admin#reload' },
                { text: '/lr logs', link: '/commands/admin#logs' },
              ]
            },
            {
              text: '玩家命令',
              collapsible: true,
              collapsed: false,
              items: [
                { text: '/lr accept', link: '/commands/player#accept' },
                { text: '/lr decline', link: '/commands/player#decline' },
                { text: '/lr privacy', link: '/commands/player#privacy' },
                { text: '/lr setprivacy', link: '/commands/player#setprivacy' },
              ]
            }
          ]
        }
      ],
      
      '/config/': [
        {
          text: '⚙️ 配置详解',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '配置概览', link: '/config/index' },
            {
              text: '镜头设置',
              collapsible: true,
              collapsed: true,
              items: [
                { text: '俯角 (pitch)', link: '/config/camera#pitch' },
                { text: '距离 (distance)', link: '/config/camera#distance' },
                { text: '高度偏移', link: '/config/camera#height-offset' },
                { text: '跟随速度', link: '/config/camera#follow-speed' },
              ]
            },
            {
              text: '自动切换',
              collapsible: true,
              collapsed: true,
              items: [
                { text: '启用/禁用', link: '/config/autoswitch#enabled' },
                { text: '切换间隔', link: '/config/autoswitch#interval' },
                { text: '切换模式', link: '/config/autoswitch#mode' },
              ]
            },
            {
              text: '视觉反馈',
              collapsible: true,
              collapsed: true,
              items: [
                { text: '发光效果', link: '/config/visual#glow' },
                { text: '粒子效果', link: '/config/visual#particle' },
                { text: 'ActionBar', link: '/config/visual#actionbar' },
              ]
            },
            {
              text: '录制者限制',
              collapsible: true,
              collapsed: true,
              items: [
                { text: '背包限制', link: '/config/restrictions#inventory' },
                { text: '交互限制', link: '/config/restrictions#interact' },
                { text: '命令限制', link: '/config/restrictions#command' },
              ]
            },
            {
              text: '隐私设置',
              collapsible: true,
              collapsed: true,
              items: [
                { text: '隐身功能', link: '/config/privacy#invisible' },
                { text: '日志记录', link: '/config/privacy#logs' },
                { text: '同意提示', link: '/config/privacy#consent' },
              ]
            }
          ]
        }
      ],
      
      '/scenarios/': [
        {
          text: '🎬 使用场景',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '场景概览', link: '/scenarios/index' },
            { text: '🎯 单人直播跟拍', link: '/scenarios/single' },
            { text: '📺 多机位直播', link: '/scenarios/multi' },
            { text: '🎲 自动轮换跟拍', link: '/scenarios/rotate' },
            { text: '🎮 手动控制跟拍', link: '/scenarios/manual' },
            { text: '🔒 隐私保护模式', link: '/scenarios/privacy' },
          ]
        }
      ],
      
      '/privacy/': [
        {
          text: '🔐 隐私保护',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '隐私概览', link: '/privacy/index' },
            { text: '隐私概述', link: '/privacy/overview' },
            { text: '同意/拒绝机制', link: '/privacy/consent' },
            { text: '录制者隐身', link: '/privacy/invisibility' },
            { text: '直播日志', link: '/privacy/logs' },
            { text: '数据存储', link: '/privacy/storage' },
            { text: '最佳实践', link: '/privacy/best-practices' },
            { text: '常见问题', link: '/privacy/faq' },
          ]
        }
      ],
      
      '/faq/': [
        {
          text: '❓ 常见问题',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '问题概览', link: '/faq/index' },
            { text: '🎥 跟随问题', link: '/faq/follow' },
            { text: '⚡ 性能问题', link: '/faq/performance' },
            { text: '🔐 隐私问题', link: '/faq/privacy' },
            { text: '⚙️ 配置问题', link: '/faq/config' },
          ]
        }
      ],
      
      '/api/': [
        {
          text: '🔧 API 参考',
          collapsible: true,
          collapsed: false,
          items: [
            { text: 'API 概览', link: '/api/index' },
            { text: 'LiveRecorder 类', link: '/api/liverecorder' },
            { text: 'LiveCore 类', link: '/api/livecore' },
            { text: 'DatabaseManager 类', link: '/api/databasemanager' },
            { text: 'CameraGeometry 类', link: '/api/camerageometry' },
          ]
        }
      ],
      
      '/qa/': [
        {
          text: '❓ 互动问答',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '热门问题', link: '/qa/index' },
          ]
        }
      ]
    },

    // 社交链接
    socialLinks: [
      { icon: 'github', link: 'https://github.com/httye/LiveRecorder' }
    ],

    // 页脚
    footer: {
      message: '基于 MIT 协议开源',
      copyright: '© 2026 LiveRecorder Team'
    },

    // 搜索配置
    search: {
      provider: 'local',
      options: {
        locales: {
          zh: {
            translations: {
              button: {
                buttonText: '搜索文档',
                buttonAriaLabel: '搜索文档'
              },
              modal: {
                noResultsText: '无法找到相关结果',
                resetButtonTitle: '清除查询条件',
                footer: {
                  selectText: '选择',
                  navigateText: '切换'
                }
              }
            }
          }
        }
      }
    },

    // 编辑链接
    editLink: {
      pattern: 'https://github.com/httye/LiveRecorder/edit/main/help/:path',
      text: '在 GitHub 上编辑此页'
    },

    // 上一个/下一个导航
    docFooter: {
      prev: '上一页',
      next: '下一页'
    },

    // 外部链接图标
    externalLinkIcon: true,

    // 最后更新时间
    lastUpdated: {
      text: '最后更新于',
      formatOptions: {
        dateStyle: 'short',
        timeStyle: 'medium'
      }
    },

    // 返回顶部
    backToTop: {
      text: '返回顶部'
    }
  },

  // Markdown 配置
  markdown: {
    lineNumbers: true
  },

  // 构建配置
  vite: {
    build: {
      chunkSizeWarningLimit: 1000
    }
  }
})