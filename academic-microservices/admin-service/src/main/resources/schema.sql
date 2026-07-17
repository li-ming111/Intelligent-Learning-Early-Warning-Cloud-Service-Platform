-- 创建预警规则表
CREATE TABLE IF NOT EXISTS warning_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_code VARCHAR(50) UNIQUE COMMENT '规则编码',
    description TEXT COMMENT '规则描述',
    level INT DEFAULT 1 COMMENT '预警等级：1-低, 2-中, 3-高',
    rule_condition TEXT COMMENT '触发条件表达式',
    fail_definition TEXT COMMENT '挂科定义标准',
    statistic_cycle VARCHAR(50) COMMENT '统计周期：natural_year-自然学年, full_program-完整学制',
    trigger_type VARCHAR(50) COMMENT '触发机制：real_time-实时监测, periodic-定期评估',
    release_condition TEXT COMMENT '预警解除条件',
    intervention_plan TEXT COMMENT '学业干预方案',
    notification_flow TEXT COMMENT '通知流程',
    responsible_person VARCHAR(100) COMMENT '责任人',
    tracking_mechanism TEXT COMMENT '跟踪反馈机制',
    archive_spec TEXT COMMENT '归档规范',
    data_caliber TEXT COMMENT '数据统计口径',
    department_responsibility TEXT COMMENT '部门职责分工',
    appeal_process TEXT COMMENT '申诉复议流程',
    exception_handling TEXT COMMENT '例外情况处理办法',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_level (level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警规则表';

-- 创建预警记录表
CREATE TABLE IF NOT EXISTS academic_warnings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_name VARCHAR(100) COMMENT '学生姓名',
    student_no VARCHAR(50) COMMENT '学号',
    warning_level INT DEFAULT 1 COMMENT '预警等级：1-低, 2-中, 3-高',
    warning_type VARCHAR(50) COMMENT '预警类型',
    description TEXT COMMENT '预警描述',
    fail_count INT DEFAULT 0 COMMENT '挂科次数',
    fail_courses TEXT COMMENT '挂科课程列表',
    rule_id BIGINT COMMENT '关联规则ID',
    status INT DEFAULT 0 COMMENT '状态：0-待处理, 1-已处理, 2-已解除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    processed_by BIGINT COMMENT '处理人ID',
    process_time DATETIME COMMENT '处理时间',
    process_note TEXT COMMENT '处理备注',
    college_id BIGINT COMMENT '学院ID',
    major_id BIGINT COMMENT '专业ID',
    class_id BIGINT COMMENT '班级ID',
    INDEX idx_student_id (student_id),
    INDEX idx_warning_level (warning_level),
    INDEX idx_status (status),
    INDEX idx_rule_id (rule_id),
    INDEX idx_college_id (college_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警记录表';

-- 创建预警解除记录表
CREATE TABLE IF NOT EXISTS warning_release_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warning_id BIGINT NOT NULL COMMENT '关联预警记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    release_reason VARCHAR(50) COMMENT '解除原因：makeup_pass-补考通过, retake_pass-重修合格, other-其他',
    release_note TEXT COMMENT '解除备注',
    release_by BIGINT COMMENT '解除人ID',
    release_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '解除时间',
    INDEX idx_warning_id (warning_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警解除记录表';

-- 创建预警跟踪记录表
CREATE TABLE IF NOT EXISTS warning_tracking_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warning_id BIGINT NOT NULL COMMENT '关联预警记录ID',
    tracking_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '跟踪时间',
    tracking_content TEXT COMMENT '跟踪内容',
    tracking_by BIGINT COMMENT '跟踪人ID',
    next_follow_time DATETIME COMMENT '下次跟进时间',
    INDEX idx_warning_id (warning_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警跟踪记录表';

-- 创建挂科记录表
CREATE TABLE IF NOT EXISTS fail_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT COMMENT '课程ID',
    course_name VARCHAR(100) COMMENT '课程名称',
    exam_time DATETIME COMMENT '考试时间',
    score DECIMAL(5,2) COMMENT '成绩',
    fail_type VARCHAR(50) COMMENT '挂科类型：fail-不及格, absent-缺考, cheat-作弊',
    semester VARCHAR(20) COMMENT '学期',
    academic_year VARCHAR(20) COMMENT '学年',
    status INT DEFAULT 0 COMMENT '状态：0-挂科, 1-补考通过, 2-重修通过',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挂科记录表';

-- 插入预警规则测试数据
INSERT INTO warning_rules (name, rule_code, description, level, rule_condition, fail_definition, statistic_cycle, trigger_type, release_condition, intervention_plan, notification_flow, responsible_person, tracking_mechanism, archive_spec, data_caliber, department_responsibility, appeal_process, exception_handling, status) VALUES 
('低级预警', 'RULE001', '学生在所有学期内累计挂科次数为1-2科（含2科）', 1, 'fail_count >= 1 AND fail_count <= 2', '课程考核成绩低于60分（不含60分）视为不及格；未参加考试（缺考）视为挂科；考试作弊取消成绩视为挂科', 'full_program', 'periodic', '补考通过或重修合格后自动解除预警', '1. 教师谈话提醒；2. 学习计划调整建议；3. 每周学习进度跟踪', '通知对象：教师、学生。流程：系统自动生成预警，发送通知给教师和学生，教师在3个工作日内约谈学生', '教师', '教师每周跟踪一次，记录学生学习状态变化，每月汇总上报学院', '预警记录保存至学生毕业离校后5年，电子档案与纸质档案同步归档', '挂科次数统计范围：学生入学以来所有学期的不及格课程（不含已通过补考/重修的课程）', '1. 教务处：负责数据统计和规则维护；2. 学院：负责预警通知和学业干预；3. 教师：负责跟踪学生；4. 学生：配合干预措施', '学生对预警结果有异议的，可在收到通知后5个工作日内向学院提交申诉材料，学院在10个工作日内给出复议结果', '因不可抗力导致的挂科可申请特殊处理，需提供相关证明材料，经学院审核批准后可不计入挂科统计', 1),
('中级预警', 'RULE002', '学生在所有学期内累计挂科次数为3-5科（含3科和5科）', 2, 'fail_count >= 3 AND fail_count <= 5', '课程考核成绩低于60分（不含60分）视为不及格；未参加考试（缺考）视为挂科；考试作弊取消成绩视为挂科', 'full_program', 'periodic', '补考通过或重修合格后自动解除预警', '1. 辅导员约谈；2. 制定个性化学习方案；3. 家长沟通；4. 每周两次学习辅导', '通知对象：教师、学生、辅导员。流程：系统自动生成预警，发送通知给教师、学生和辅导员，辅导员在3个工作日内约谈学生并与家长沟通', '辅导员', '辅导员每3天跟踪一次，学院每周召开预警学生专题会议，每月向教务处上报进展', '预警记录保存至学生毕业离校后5年，电子档案与纸质档案同步归档', '挂科次数统计范围：学生入学以来所有学期的不及格课程（不含已通过补考/重修的课程）', '1. 教务处：负责数据统计和规则维护；2. 学院：负责预警通知和学业干预；3. 辅导员：负责跟踪学生；4. 学生：配合干预措施', '学生对预警结果有异议的，可在收到通知后5个工作日内向学院提交申诉材料，学院在10个工作日内给出复议结果', '因不可抗力导致的挂科可申请特殊处理，需提供相关证明材料，经学院审核批准后可不计入挂科统计', 1),
('高级预警', 'RULE003', '学生在所有学期内累计挂科次数超过5科（不含5科）', 3, 'fail_count > 5', '课程考核成绩低于60分（不含60分）视为不及格；未参加考试（缺考）视为挂科；考试作弊取消成绩视为挂科', 'full_program', 'real_time', '补考通过或重修合格后自动解除预警；累计挂科超过8科建议留级处理', '1. 学院院长约谈；2. 制定严格的学业改进计划；3. 家长到校沟通；4. 每日学习打卡；5. 可能建议留级', '通知对象：教师、学生、辅导员、管理员。流程：系统实时生成预警，立即通知教师、学生、辅导员和管理员，学院在3个工作日内组织专项会议', '学院院长', '辅导员每日跟踪，学院成立专项帮扶小组，每周向教务处上报进展', '预警记录保存至学生毕业离校后5年，电子档案与纸质档案同步归档', '挂科次数统计范围：学生入学以来所有学期的不及格课程（不含已通过补考/重修的课程）', '1. 教务处：负责数据统计和规则维护；2. 学院：负责预警通知和学业干预；3. 辅导员：负责跟踪学生；4. 学生：配合干预措施', '学生对预警结果有异议的，可在收到通知后5个工作日内向学院提交申诉材料，学院在10个工作日内给出复议结果', '因不可抗力导致的挂科可申请特殊处理，需提供相关证明材料，经学院审核批准后可不计入挂科统计', 1);

-- 插入挂科记录测试数据
INSERT INTO fail_records (student_id, course_id, course_name, exam_time, score, fail_type, semester, academic_year, status) VALUES 
(1, 1, '高等数学', '2024-01-15 09:00:00', 45.00, 'fail', '2023-2024-2', '2023-2024', 0),
(1, 2, '大学物理', '2024-01-16 10:00:00', 52.00, 'fail', '2023-2024-2', '2023-2024', 0),
(2, 1, '高等数学', '2024-01-15 09:00:00', 38.00, 'fail', '2023-2024-2', '2023-2024', 0),
(2, 2, '大学物理', '2024-01-16 10:00:00', 48.00, 'fail', '2023-2024-2', '2023-2024', 0),
(2, 3, '程序设计基础', '2024-01-17 14:00:00', 55.00, 'fail', '2023-2024-2', '2023-2024', 0),
(3, 1, '高等数学', '2024-01-15 09:00:00', 28.00, 'fail', '2023-2024-2', '2023-2024', 0),
(3, 2, '大学物理', '2024-01-16 10:00:00', 35.00, 'fail', '2023-2024-2', '2023-2024', 0),
(3, 3, '程序设计基础', '2024-01-17 14:00:00', 42.00, 'fail', '2023-2024-2', '2023-2024', 0),
(3, 4, '线性代数', '2024-01-18 09:00:00', 48.00, 'fail', '2023-2024-2', '2023-2024', 0),
(3, 5, '数据结构', '2024-01-19 10:00:00', 52.00, 'fail', '2023-2024-2', '2023-2024', 0),
(3, 6, '计算机网络', '2024-01-20 14:00:00', 39.00, 'fail', '2023-2024-2', '2023-2024', 0),
(4, 1, '高等数学', '2024-01-15 09:00:00', 0.00, 'absent', '2023-2024-2', '2023-2024', 0),
(4, 2, '大学物理', '2024-01-16 10:00:00', 0.00, 'absent', '2023-2024-2', '2023-2024', 0),
(4, 3, '程序设计基础', '2024-01-17 14:00:00', 0.00, 'absent', '2023-2024-2', '2023-2024', 0),
(4, 4, '线性代数', '2024-01-18 09:00:00', 0.00, 'absent', '2023-2024-2', '2023-2024', 0),
(4, 5, '数据结构', '2024-01-19 10:00:00', 0.00, 'absent', '2023-2024-2', '2023-2024', 0),
(4, 6, '计算机网络', '2024-01-20 14:00:00', 0.00, 'absent', '2023-2024-2', '2023-2024', 0),
(4, 7, '操作系统', '2024-01-21 09:00:00', 0.00, 'absent', '2023-2024-2', '2023-2024', 0);

-- 插入预警记录测试数据
INSERT INTO academic_warnings (student_id, student_name, student_no, warning_level, warning_type, description, fail_count, fail_courses, rule_id, status, college_id, major_id, class_id) VALUES 
(1, '张三', '2021001', 1, '学业预警', '累计挂科2科，触发低级预警', 2, '高等数学,大学物理', 1, 0, 1, 1, 1),
(2, '李四', '2021002', 2, '学业预警', '累计挂科3科，触发中级预警', 3, '高等数学,大学物理,程序设计基础', 2, 0, 1, 1, 1),
(3, '王五', '2021003', 3, '学业预警', '累计挂科6科，触发高级预警', 6, '高等数学,大学物理,程序设计基础,线性代数,数据结构,计算机网络', 3, 0, 1, 2, 3),
(4, '赵六', '2021004', 3, '学业预警', '累计挂科7科（全部缺考），触发高级预警', 7, '高等数学,大学物理,程序设计基础,线性代数,数据结构,计算机网络,操作系统', 3, 0, 1, 2, 3);