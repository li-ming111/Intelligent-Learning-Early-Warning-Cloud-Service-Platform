// API服务模块 - 完整版（对齐PC端所有功能）
// 开发环境: http://localhost:8076/api
// 生产环境: 替换为实际域名

const API_BASE_URL = 'http://localhost:8076/api'

class ApiClient {
  constructor() { this.baseUrl = API_BASE_URL }

  // ========== 通用请求 ==========
  async request(endpoint, options = {}) {
    const url = `${this.baseUrl}${endpoint}`
    const token = uni.getStorageSync('token')
    const headers = { 'Content-Type': 'application/json', ...options.headers }
    if (token) headers['Authorization'] = `Bearer ${token}`
    // 构建 GET 参数到 URL 上
    let finalUrl = url
    if (options.params) {
      const qs = Object.keys(options.params)
        .filter(k => options.params[k] != null)
        .map(k => `${encodeURIComponent(k)}=${encodeURIComponent(options.params[k])}`)
        .join('&')
      if (qs) finalUrl += (url.includes('?') ? '&' : '?') + qs
    }
    try {
      const res = await uni.request({
        url: finalUrl,
        method: options.method || 'GET',
        data: options.data,
        header: headers
      })
      if (res.statusCode === 200) return res.data.data
      if (res.statusCode === 401) {
        uni.removeStorageSync('token')
        uni.reLaunch({ url: '/pages/auth/login' })
        throw new Error('登录已过期')
      }
      throw new Error(res.data?.message || `请求失败(${res.statusCode})`)
    } catch (e) {
      // 防止 reLaunch 抛出的错误被误当作网络错误
      if (e.message === '登录已过期') throw e
      console.error(`[API] ${options.method||'GET'} ${endpoint}:`, e)
      throw e
    }
  }

  _get = (url, params) => this.request(url, { params })
  _post = (url, data) => this.request(url, { method: 'POST', data })
  _put = (url, data) => this.request(url, { method: 'PUT', data })
  _del = (url) => this.request(url, { method: 'DELETE' })
  _patch = (url, data) => this.request(url, { method: 'PATCH', data })

  // ========== 认证 ==========
  login = (username, password) => this._post('/auth/login', { username, password })
  logout = (userId, role) => this._post('/auth/logout', { userId, role })
  changePassword = (data) => this._post('/auth/change-password', data)
  registerStudent = (data) => this._post('/auth/register/student', data)
  registerTeacher = (data) => this._post('/auth/register/teacher', data)
  registerCounselor = (data) => this._post('/auth/register/counselor', data)
  getColleges = () => this._get('/auth/colleges')
  getMajorsByCollege = (collegeId) => this._get(`/auth/majors?collegeId=${collegeId}`)
  getClasses = (collegeId) => {
    const u = `/auth/classes`;
    if (collegeId) u += `?collegeId=${collegeId}`;
    return this._get(u)
  }
  getAllCourses = () => this._get('/auth/courses')

  // ========== 学生端 ==========
  student = {
    getDashboard: (userId) => this._get(`/students/dashboard/${userId}`),
    getScores: (userId, semester) => this._get(`/students/scores/${userId}${semester ? '?semester=' + semester : ''}`),
    getWarnings: (userId) => this._get(`/students/warnings/${userId}`),
    getAssistancePlans: (userId) => this._get(`/students/assistance/${userId}`),
    getGPA: (id) => this._get(`/students/${id}/gpa`),
    getStudentInfo: (id) => this._get(`/students/${id}`),
    getStudentByUserId: (userId) => this._get(`/students/student-by-user/${userId}`),
    getSuggestions: (userId) => this._get(`/students/suggestions/${userId}`),
    getClassInfo: (id) => this._get(`/students/${id}/class-info`),
    getClassMembers: (userId) => this._get(`/students/class-members/${userId}`),
    getClassRanking: (userId) => this._get(`/students/class-ranking/${userId}`),
    // 通知
    getNotifications: (userId, page = 1, size = 10) => this._get(`/students/notification-center/${userId}/list?page=${page}&pageSize=${size}`),
    getUnreadNotifications: (userId) => this._get(`/students/notification-center/${userId}/unread`),
    getUnreadCount: (userId) => this._get(`/students/notification-center/${userId}/unread-count`),
    markRead: (id) => this._post(`/students/notification-center/${id}/mark-read`),
    markBatchRead: (userId, ids) => this._post(`/students/notification-center/${userId}/mark-batch-read`, ids),
    deleteNotification: (id) => this._post(`/students/notification-center/${id}/delete`),
    // 消息
    getMessages: (userId) => this._get(`/students/messages/${userId}`),
    getUnreadMsgCount: (userId) => this._get(`/students/messages/${userId}/unread-count`),
    sendMessage: (data) => this._post('/students/messages', data),
    markMessageRead: (id) => this._post(`/students/messages/${id}/mark-read`),
    // 申诉
    submitAppeal: (data) => this._post('/students/appeals/submit', data),
    getAppeals: (studentId) => this._get(`/students/appeals/${studentId}/list`),
    // 对标
    getLatestBenchmark: (studentId) => this._get(`/students/benchmark/${studentId}/latest`),
    // 设置
    getSettings: (userId) => this._get(`/students/settings/${userId}`),
    updateSettings: (userId, data) => this._put(`/students/settings/${userId}`, data),
    updatePlanProgress: (planId, progress) => this._patch(`/counselors/assistance-plans/${planId}/status`, { status: progress })
  }

  // ========== 教师端 ==========
  teacher = {
    getDashboard: (userId) => this._get(`/teachers/dashboard/${userId}`),
    getStudents: (teacherId) => this._get(`/teachers/students/${teacherId}`),
    getCourses: (teacherId) => this._get(`/teachers/courses${teacherId ? '?teacher_id=' + teacherId : ''}`),
    updateCourse: (id, data) => this._put(`/teachers/courses/${id}`, data),
    getScores: (courseId) => this._get(`/teachers/scores?course_id=${courseId}`),
    saveScores: (data) => this._post('/teachers/scores', data),
    updateScore: (id, data) => this._put(`/teachers/scores/${id}`, data),
    deleteScore: (id) => this._del(`/teachers/scores/${id}`),
    getWarnings: (teacherId, status) => this._get(`/teachers/warnings/${teacherId}${status ? '?status=' + status : ''}`),
    processWarning: (id, data) => this._post(`/teachers/warnings/${id}/process`, data),
    getEnrollments: (teacherId) => this._get(`/teachers/enrollments?teacherId=${teacherId}`),
    getFeedbacks: (teacherId) => this._get(`/teachers/feedbacks?teacherId=${teacherId}`),
    replyFeedback: (id, data) => this._put(`/teachers/feedbacks/${id}`, data),
    getCreditPrediction: (studentId) => this._get(`/teachers/students/${studentId}/credit-prediction`),
    getAuditLogs: (teacherId) => this._get(`/teachers/scores/audit/${teacherId}`),
    getMyClasses: (teacherId) => this._get(`/teachers/class-management/my-classes?teacherId=${teacherId}`),
    getClassStudents: (classId) => this._get(`/teachers/classes/${classId}/students`),
    getTodos: (teacherId) => this._get(`/teachers/todos/${teacherId}`),
    getClassScoreAnalysis: (classId) => this._get(`/teachers/classes/${classId}/analysis`),
    recommendCourse: (data) => this._post('/teachers/courses/recommend', data),
    getMessages: (userId) => this._get(`/teachers/messages/${userId}`),
    getAnalysis: (courseId) => this._get(`/teachers/analysis?course_id=${courseId}`),
    getTeachers: () => this._get('/teachers/list')
  }

  // ========== 辅导员端 ==========
  counselor = {
    getDashboard: (userId) => this._get(`/counselors/dashboard/${userId}`),
    getStudents: (counselorId) => this._get(`/counselors/students?counselor_id=${counselorId}`),
    getStudentDetail: (studentId) => this._get(`/counselors/students/${studentId}`),
    getWarnings: (counselorId, status) => this._get(`/counselors/warnings?counselor_id=${counselorId}${status ? '&status=' + status : ''}`),
    processWarning: (id, data) => this._post(`/counselors/warnings/${id}/process`, data),
    batchProcessWarnings: (ids) => this._post('/counselors/warnings/batch-process', ids),
    getCounselorPlans: (counselorId) => this._get(`/counselors/${counselorId}/assistance-plans`),
    createAssistancePlan: (data) => this._post('/counselors/assistance-plans', data),
    updateAssistancePlan: (id, data) => this._put(`/counselors/assistance-plans/${id}`, data),
    deleteAssistancePlan: (id) => this._del(`/counselors/assistance-plans/${id}`),
    getClassScores: (classId, counselorId) => this._get(`/counselors/scores/class/${classId}?counselor_id=${counselorId}`),
    getLowScoreStudents: (counselorId) => this._get(`/counselors/scores/low-score?counselor_id=${counselorId}`),
    getStudentScoreTrend: (studentId) => this._get(`/counselors/scores/student/${studentId}/trend`),
    getMyClasses: (counselorId) => this._get(`/counselors/class-management/my-classes?counselorId=${counselorId}`),
    getClassStudents: (classId) => this._get(`/counselors/classes/${classId}/students`),
    notifyStudents: (data) => this._post('/counselors/students/notify', data),
    // 数据分析
    getCreditInsufficientRate: (counselorId) => this._get(`/counselors/analytics/credit-insufficient?counselor_id=${counselorId}`),
    getWarningDistribution: (counselorId) => this._get(`/counselors/analytics/warning-distribution?counselor_id=${counselorId}`),
    getHandlingEfficiency: (counselorId) => this._get(`/counselors/analytics/handling-efficiency?counselor_id=${counselorId}`),
    getCreditAchievementRanking: (counselorId) => this._get(`/counselors/analytics/credit-achievement-ranking?counselor_id=${counselorId}`),
    getWarningTrend: (counselorId) => this._get(`/counselors/analytics/warning-trend?counselor_id=${counselorId}`),
    getAssistanceCompletion: (counselorId) => this._get(`/counselors/analytics/assistance-completion?counselor_id=${counselorId}`),
    getCreditMonitor: (counselorId) => this._get(`/counselors/credit-monitor?counselor_id=${counselorId}`)
  }

  // ========== 管理员端 ==========
  admin = {
    getDashboard: () => this._get('/admin/dashboard'),
    getStatistics: () => this._get('/admin/statistics'),
    getUsers: (page = 1, size = 20, collegeId, role) => this._get(`/admin/users/page?page=${page}&size=${size}${collegeId ? '&collegeId=' + collegeId : ''}${role ? '&role=' + role : ''}`),
    toggleUserStatus: (id) => this._post(`/admin/users/${id}/toggle-status`),
    resetPassword: (id) => this._post(`/admin/users/${id}/reset-password`),
    deleteUser: (id) => this._del(`/admin/users/${id}`),
    getColleges: () => this._get('/admin/colleges'),
    createCollege: (data) => this._post('/admin/colleges', data),
    updateCollege: (id, data) => this._put(`/admin/colleges/${id}`, data),
    deleteCollege: (id) => this._del(`/admin/colleges/${id}`),
    getMajors: () => this._get('/admin/majors'),
    createMajor: (data) => this._post('/admin/majors', data),
    updateMajor: (id, data) => this._put(`/admin/majors/${id}`, data),
    deleteMajor: (id) => this._del(`/admin/majors/${id}`),
    getCourses: () => this._get('/admin/courses'),
    createCourse: (data) => this._post('/admin/courses', data),
    updateCourse: (id, data) => this._put(`/admin/courses/${id}`, data),
    deleteCourse: (id) => this._del(`/admin/courses/${id}`),
    getRules: () => this._get('/admin/rules'),
    createRule: (data) => this._post('/admin/rules', data),
    updateRule: (id, data) => this._put(`/admin/rules/${id}`, data),
    deleteRule: (id) => this._del(`/admin/rules/${id}`),
    getWarnings: () => this._get('/admin/warnings'),
    processWarning: (id, data) => this._put(`/admin/warnings/${id}/handle`, data),
    getClasses: () => this._get('/admin/classes'),
    getPendingRequests: () => this._get('/admin/pending-requests'),
    approveRequest: (id, type = 'teacher') => this._post(`/admin/class-management/approve/${id}?type=${type}`),
    rejectRequest: (id, reason, type = 'teacher') => this._post(`/admin/class-management/reject/${id}?type=${type}`, { reason }),
    broadcastMessage: (data) => this._post('/admin/messages/broadcast', data)
  }
  // ========== 学院管理员端 ==========
  collegeAdmin = {
    getProfile: (uid) => this._get(`/college-admin/profile/${uid}`),
    getDashboard: (uid) => this._get(`/college-admin/dashboard/${uid}`),
    getClasses: (uid) => this._get(`/college-admin/classes/${uid}`),
    getStudents: (uid) => this._get(`/college-admin/students/${uid}`),
    getTeachers: (uid) => this._get(`/college-admin/teachers/${uid}`),
    getWarnings: (uid) => this._get(`/college-admin/warnings/${uid}`),
    getScoreStats: (uid) => this._get(`/college-admin/scores-stats/${uid}`),
    getScoreAnalysis: (uid) => this._get(`/college-admin/score-analysis/${uid}`),
    getClassComparison: (uid) => this._get(`/college-admin/class-comparison/${uid}`),
    getCounselors: (uid) => this._get(`/college-admin/counselors/${uid}`),
    getAssistance: (uid) => this._get(`/college-admin/assistance/${uid}`),
  }
}

export const apiClient = new ApiClient()
export const studentAPI = apiClient.student
export const teacherAPI = apiClient.teacher
export const counselorAPI = apiClient.counselor
export const adminAPI = apiClient.admin
export const collegeAdminAPI = apiClient.collegeAdmin
