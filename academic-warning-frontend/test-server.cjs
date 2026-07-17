const express = require('express');
const app = express();
const port = 8070;

// 模拟管理员仪表板数据
app.get('/admin/dashboard', (req, res) => {
    res.json({
        totalUsers: 100,
        totalStudents: 1000,
        totalCourses: 200,
        totalWarnings: 50,
        recentWarnings: [],
        recentUsers: []
    });
});

// 模拟学院列表数据
app.get('/admin/colleges', (req, res) => {
    res.json([
        { id: 1, name: '计算机科学与技术学院', code: 'CS', createdAt: new Date().toISOString() },
        { id: 2, name: '电子信息工程学院', code: 'EE', createdAt: new Date().toISOString() },
        { id: 3, name: '数学与统计学院', code: 'MS', createdAt: new Date().toISOString() }
    ]);
});

// 模拟专业列表数据
app.get('/admin/majors', (req, res) => {
    res.json([
        { id: 1, name: '计算机科学与技术', code: 'CS', collegeId: 1, createdAt: new Date().toISOString() },
        { id: 2, name: '软件工程', code: 'SE', collegeId: 1, createdAt: new Date().toISOString() },
        { id: 3, name: '电子信息工程', code: 'EE', collegeId: 2, createdAt: new Date().toISOString() },
        { id: 4, name: '通信工程', code: 'CE', collegeId: 2, createdAt: new Date().toISOString() },
        { id: 5, name: '数学与应用数学', code: 'MA', collegeId: 3, createdAt: new Date().toISOString() },
        { id: 6, name: '统计学', code: 'ST', collegeId: 3, createdAt: new Date().toISOString() }
    ]);
});

// 模拟用户列表数据
app.get('/admin/users', (req, res) => {
    res.json({
        total: 10,
        items: [
            { id: 1, username: 'admin', name: '管理员', email: 'admin@example.com', role: 0, status: 1, createdAt: new Date().toISOString() },
            { id: 2, username: 'teacher1', name: '教师1', email: 'teacher1@example.com', role: 1, status: 1, createdAt: new Date().toISOString() },
            { id: 3, username: 'teacher2', name: '教师2', email: 'teacher2@example.com', role: 1, status: 1, createdAt: new Date().toISOString() },
            { id: 4, username: 'counselor1', name: '辅导员1', email: 'counselor1@example.com', role: 2, status: 1, createdAt: new Date().toISOString() },
            { id: 5, username: 'counselor2', name: '辅导员2', email: 'counselor2@example.com', role: 2, status: 1, createdAt: new Date().toISOString() }
        ]
    });
});

// 模拟课程列表数据
app.get('/admin/courses', (req, res) => {
    res.json([
        { id: 1, name: '高等数学', code: 'MATH101', credit: 4, createdAt: new Date().toISOString() },
        { id: 2, name: '大学物理', code: 'PHYS101', credit: 4, createdAt: new Date().toISOString() },
        { id: 3, name: '计算机基础', code: 'CS101', credit: 3, createdAt: new Date().toISOString() },
        { id: 4, name: '程序设计', code: 'CS102', credit: 4, createdAt: new Date().toISOString() }
    ]);
});

// 模拟统计数据
app.get('/admin/statistics', (req, res) => {
    res.json({
        userCount: 100,
        studentCount: 1000,
        courseCount: 200,
        warningCount: 50
    });
});

// 模拟规则列表数据
app.get('/admin/rules', (req, res) => {
    res.json([
        { id: 1, name: '旷课规则', description: '旷课次数超过课程总课时的1/3', threshold: 3, createdAt: new Date().toISOString() },
        { id: 2, name: '成绩规则', description: '课程成绩低于60分', threshold: 60, createdAt: new Date().toISOString() },
        { id: 3, name: '挂科规则', description: '挂科门数超过3门', threshold: 3, createdAt: new Date().toISOString() }
    ]);
});

// 启动服务
app.listen(port, () => {
    console.log(`Test server listening at http://localhost:${port}`);
});