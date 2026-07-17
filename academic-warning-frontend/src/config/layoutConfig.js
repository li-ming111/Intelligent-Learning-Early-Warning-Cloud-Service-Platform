// 前端路由配置与界面布局规范
export const layoutConfig = {
  // 学生端布局
  student: {
    sidebar: true,
    header: true,
    headerHeight: 64,
    sidebarWidth: 250,
    menuItems: [
      { label: '仪表板', path: '/student/dashboard', icon: 'home' },
      { label: '成绩', path: '/student/scores', icon: 'file-earmark-text' },
      { label: '学业预警', path: '/student/warnings', icon: 'exclamation-triangle' },
      { label: '帮扶计划', path: '/student/assistance', icon: 'person-check' },
      { label: '成绩申诉', path: '/student/appeals', icon: 'chat-left-text' },
      { label: '对标分析', path: '/student/benchmark', icon: 'bar-chart' },
      { label: '通知', path: '/student/notifications', icon: 'bell' },
      { label: '反馈', path: '/student/feedback', icon: 'chat-dots' },
      { label: '选修课', path: '/student/electives', icon: 'book' },
    ],
    theme: 'light',
    primaryColor: '#4CAF50',
  },

  // 教师端布局
  teacher: {
    sidebar: true,
    header: true,
    headerHeight: 64,
    sidebarWidth: 250,
    menuItems: [
      { label: '仪表板', path: '/teacher/dashboard', icon: 'home' },
      { label: '我的课程', path: '/teacher/courses', icon: 'book' },
      { label: '学生成绩', path: '/teacher/scores', icon: 'file-earmark-text' },
      { label: '班级信息', path: '/teacher/classes', icon: 'people' },
      { label: '学业预警', path: '/teacher/warnings', icon: 'exclamation-triangle' },
      { label: '成绩申诉', path: '/teacher/appeals', icon: 'chat-left-text' },
      { label: '学生反馈', path: '/teacher/feedback', icon: 'chat-dots' },
      { label: '通知', path: '/teacher/notifications', icon: 'bell' },
      { label: '成绩分析', path: '/teacher/analytics', icon: 'bar-chart' },
    ],
    theme: 'light',
    primaryColor: '#2196F3',
  },

  // 辅导员端布局
  counselor: {
    sidebar: true,
    header: true,
    headerHeight: 64,
    sidebarWidth: 250,
    menuItems: [
      { label: '仪表板', path: '/counselor/dashboard', icon: 'home' },
      { label: '班级管理', path: '/counselor/classes', icon: 'people' },
      { label: '学生管理', path: '/counselor/students', icon: 'person-circle' },
      { label: '学业预警', path: '/counselor/warnings', icon: 'exclamation-triangle' },
      { label: '帮扶计划', path: '/counselor/assistance', icon: 'person-check' },
      { label: '学分监控', path: '/counselor/credit-monitor', icon: 'percent' },
      { label: '学分预测', path: '/counselor/credit-forecast', icon: 'graph-up' },
      { label: '成绩分析', path: '/counselor/analytics', icon: 'bar-chart' },
      { label: '通知中心', path: '/counselor/notifications', icon: 'bell' },
      { label: '反馈管理', path: '/counselor/feedback', icon: 'chat-dots' },
    ],
    theme: 'light',
    primaryColor: '#2563eb',
  },

  // 管理员端布局
  admin: {
    sidebar: true,
    header: true,
    headerHeight: 64,
    sidebarWidth: 250,
    menuItems: [
      { label: '仪表板', path: '/admin/dashboard', icon: 'home' },
      { label: '用户管理', path: '/admin/users', icon: 'person-circle' },
      { label: '课程管理', path: '/admin/courses', icon: 'book' },
      { label: '班级管理', path: '/admin/classes', icon: 'people' },
      { label: '专业管理', path: '/admin/majors', icon: 'briefcase' },
      { label: '学院管理', path: '/admin/colleges', icon: 'building' },
      { label: '权限管理', path: '/admin/permissions', icon: 'lock' },
      { label: '预警规则', path: '/admin/rules', icon: 'sliders' },
      { label: '选修模块', path: '/admin/modules', icon: 'collection' },
      { label: '数据导出', path: '/admin/export', icon: 'download' },
      { label: '系统日志', path: '/admin/logs', icon: 'file-text' },
      { label: '消息管理', path: '/admin/messages', icon: 'envelope' },
    ],
    theme: 'dark',
    primaryColor: '#9C27B0',
  },
};

// 响应式断点配置
export const breakpoints = {
  xs: 480,
  sm: 768,
  md: 992,
  lg: 1200,
  xl: 1600,
};

// 颜色方案配置
export const colorScheme = {
  primary: {
    50: '#E8F5E9',
    100: '#C8E6C9',
    200: '#A5D6A7',
    300: '#81C784',
    400: '#66BB6A',
    500: '#4CAF50',
    600: '#43A047',
    700: '#388E3C',
    800: '#2E7D32',
    900: '#1B5E20',
  },
  danger: {
    50: '#FFEBEE',
    100: '#FFCDD2',
    500: '#F44336',
    700: '#D32F2F',
    900: '#B71C1C',
  },
  warning: {
    50: '#FFF3E0',
    100: '#FFE0B2',
    500: '#2563eb',
    700: '#1d40af',
    900: '#E65100',
  },
  success: {
    50: '#E8F5E9',
    500: '#4CAF50',
    700: '#388E3C',
  },
  info: {
    50: '#E3F2FD',
    500: '#2196F3',
    700: '#1976D2',
  },
};

// 页面加载配置
export const pageLoadConfig = {
  // 所有页面初始化时自动加载数据
  autoLoad: true,
  // 数据刷新间隔（毫秒）
  refreshInterval: 30000,
  // 页面离开时清除数据
  clearOnUnmount: true,
  // 错误重试次数
  retryCount: 3,
  // 错误重试延迟（毫秒）
  retryDelay: 1000,
};
