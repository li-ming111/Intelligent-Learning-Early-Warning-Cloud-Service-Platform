import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import TeacherTabBar from './components/teacher-tab-bar/teacher-tab-bar.vue'
import CounselorTabBar from './components/counselor-tab-bar/counselor-tab-bar.vue'
import CollegeAdminTabBar from './components/collegeadmin-tab-bar/collegeadmin-tab-bar.vue'
import AdminTabBar from './components/admin-tab-bar/admin-tab-bar.vue'

export function createApp() {
  const app = createSSRApp(App)
  app.component('teacher-tab-bar', TeacherTabBar)
  app.component('counselor-tab-bar', CounselorTabBar)
  app.component('collegeadmin-tab-bar', CollegeAdminTabBar)
  app.component('admin-tab-bar', AdminTabBar)
  return {
    app
  }
}
// #endif