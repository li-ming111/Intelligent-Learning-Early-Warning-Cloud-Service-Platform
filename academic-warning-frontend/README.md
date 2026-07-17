# 智能学习预警云服务平台前端

## 项目简介

智能学习预警云服务平台是一个基于Vue 3的现代化前端应用，旨在帮助学校和教师及时发现并干预学生的学业问题，提高教学质量和学生成功率。

## 技术栈

- **前端框架**: Vue 3 + Vite
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **UI组件库**: Element Plus
- **图表库**: ECharts
- **HTTP客户端**: Axios
- **样式预处理器**: Sass

## 项目结构

```
src/
├── api/            # API接口调用
├── assets/         # 静态资源文件
├── components/     # 通用组件
├── config/         # 配置文件
├── locales/        # 国际化资源
├── router/         # 路由配置
├── store/          # 状态管理
├── styles/         # 全局样式
├── utils/          # 工具函数
├── views/          # 页面组件
│   ├── student/    # 学生端页面
│   ├── Admin*.vue  # 管理员页面
│   ├── Counselor*.vue # 辅导员页面
│   ├── Teacher*.vue # 教师页面
│   └── *.vue       # 公共页面
├── App.vue         # 根组件
└── main.js         # 入口文件
```

## 安装与运行

### 环境要求

- Node.js >= 16.0.0
- npm >= 7.0.0

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 功能模块

### 1. 学生端
- **通知中心**: 接收学业预警、系统通知等信息
- **成绩管理**: 查看个人成绩、班级排名
- **申诉管理**: 对预警结果进行申诉
- **辅助反馈**: 提交学习困难反馈
- **基准分析**: 与班级平均水平对比分析

### 2. 教师端
- **预警管理**: 查看和处理学生预警
- **成绩管理**: 录入和管理学生成绩
- **课程管理**: 管理所教授的课程
- **反馈管理**: 处理学生的反馈信息
- **信用预测**: 预测学生的学业信用

### 3. 辅导员端
- **班级管理**: 管理所负责的班级
- **学生管理**: 查看和管理学生信息
- **预警管理**: 处理学生预警信息
- **辅助管理**: 管理学生的辅助请求
- **信用监控**: 监控学生的学业信用

### 4. 管理员端
- **用户管理**: 管理系统用户
- **班级管理**: 管理学校班级
- **课程管理**: 管理学校课程
- **规则管理**: 管理预警规则
- **数据导出**: 导出系统数据
- **统计分析**: 系统数据统计分析

## 开发指南

### 代码规范
- 使用ES6+语法
- 组件命名采用PascalCase
- 变量和函数命名采用camelCase
- 常量命名采用UPPER_SNAKE_CASE
- 缩进使用2个空格

### 提交规范
- 提交信息使用中文
- 提交信息格式: `类型: 描述`
- 类型包括: feat(新功能)、fix(修复)、docs(文档)、style(样式)、refactor(重构)、test(测试)、chore(构建/依赖)

## 部署说明

1. 构建生产版本
   ```bash
   npm run build
   ```

2. 将 `dist` 目录部署到Web服务器
   - Nginx配置示例:
     ```nginx
     server {
       listen 80;
       server_name example.com;
       
       root /path/to/dist;
       index index.html;
       
       location / {
         try_files $uri $uri/ /index.html;
       }
     }
     ```

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'feat: 添加令人惊叹的功能'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开 Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请联系项目维护者。