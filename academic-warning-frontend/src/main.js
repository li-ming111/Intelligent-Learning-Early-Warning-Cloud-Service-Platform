import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import './styles/admin.css'
import { getElementPlusTheme, applyThemeMode } from './config/theme'

// 应用用户保存的深色/浅色模式设置
const applyUserThemeMode = () => {
  const savedPreferences = localStorage.getItem('adminPreferences')
  if (savedPreferences) {
    try {
      const prefs = JSON.parse(savedPreferences)
      if (prefs.theme) {
        applyThemeMode(prefs.theme)
        return
      }
    } catch (e) {
      console.error('解析主题设置失败:', e)
    }
  }
  // 默认使用浅色模式
  applyThemeMode('light')
}

// 获取当前用户角色对应的主题
const themeVars = getElementPlusTheme()

// 动态设置 Element Plus CSS 变量
const setElementPlusTheme = () => {
  for (const [key, value] of Object.entries(themeVars)) {
    document.documentElement.style.setProperty(key, value)
  }
}

const app = createApp(App)
const pinia = createPinia()

app.use(ElementPlus, { locale: zhCn })
app.use(pinia)
app.use(router)

// 先应用深色/浅色模式，再应用角色主题色
applyUserThemeMode()
setElementPlusTheme()

app.mount('#app')

console.log('Vue应用初始化成功 - 主题已配置')