-- 删除旧表（如果存在）
DROP TABLE IF EXISTS counselor_class_application;

-- 创建辅导员班级管理申请表
CREATE TABLE counselor_class_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    counselor_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    reason TEXT,
    status INT DEFAULT 0 COMMENT '0-待审核, 1-通过, 2-拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    class_name VARCHAR(100),
    counselor_name VARCHAR(100),
    INDEX idx_counselor_id (counselor_id),
    INDEX idx_class_id (class_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='辅导员班级管理申请表';

-- 插入测试申请数据
INSERT INTO counselor_class_application (counselor_id, class_id, reason, status, class_name, counselor_name) VALUES
(1, 1, '希望负责计算机2101班的日常管理工作', 0, '计算机2101班', '张明'),
(1, 2, '希望同时管理计算机2102班', 0, '计算机2102班', '张明'),
(2, 3, '申请管理软件工程2101班', 1, '软件工程2101班', '李华'),
(2, 4, '申请管理软件工程2102班', 2, '软件工程2102班', '李华');

-- 创建班级表（如果不存在）
CREATE TABLE IF NOT EXISTS classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '班级名称',
    college_id BIGINT COMMENT '学院ID',
    major_id BIGINT COMMENT '专业ID',
    teacher_id BIGINT COMMENT '教师ID',
    counselor_id BIGINT COMMENT '辅导员ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_college_id (college_id),
    INDEX idx_major_id (major_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_counselor_id (counselor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS counselor_profiles;

-- 创建辅导员信息表
CREATE TABLE counselor_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL COMMENT '用户ID',
    college_id BIGINT COMMENT '学院ID',
    counselor_id VARCHAR(50) COMMENT '辅导员编号',
    name VARCHAR(100) COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    department VARCHAR(100) COMMENT '部门',
    title VARCHAR(50) COMMENT '职称',
    education VARCHAR(50) COMMENT '学历',
    introduction TEXT COMMENT '个人简介',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    college_name VARCHAR(100) COMMENT '学院名称',
    staff_id VARCHAR(50) COMMENT '员工编号',
    office_location VARCHAR(200) COMMENT '办公地点',
    duty_phone VARCHAR(20) COMMENT '值班电话',
    class_count INT DEFAULT 0 COMMENT '管理班级数',
    student_count INT DEFAULT 0 COMMENT '管理学生数',
    INDEX idx_user_id (user_id),
    INDEX idx_college_id (college_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='辅导员信息表';

-- 插入测试班级数据
INSERT INTO classes (name, college_id, major_id) VALUES 
('计算机2101班', 1, 1),
('计算机2102班', 1, 1),
('软件工程2101班', 1, 2),
('软件工程2102班', 1, 2),
('人工智能2101班', 1, 3),
('人工智能2102班', 1, 3);
