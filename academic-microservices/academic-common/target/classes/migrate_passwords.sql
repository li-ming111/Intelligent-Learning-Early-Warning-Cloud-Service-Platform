-- ========================================
-- 密码迁移脚本：将明文密码改为 BCrypt 哈希
-- 用于更新已有数据库
-- 运行前请备份数据库
-- ========================================

-- BCrypt($2a$10$, "123456") = $2a$10$O1AmnqNe50/knConTInm6Owvk/BkiYTYAviRZk5mz0uQfpT99Oija

-- 1. 修复 admin 密码为 123456
UPDATE `users` SET `password` = '$2a$10$O1AmnqNe50/knConTInm6Owvk/BkiYTYAviRZk5mz0uQfpT99Oija' WHERE `username` = 'admin';

-- 2. 修复 adm001 明文密码
UPDATE `users` SET `password` = '$2a$10$O1AmnqNe50/knConTInm6Owvk/BkiYTYAviRZk5mz0uQfpT99Oija' WHERE `username` = 'adm001' AND `password` = '123456';

-- 3. 修复所有明文密码 '123456' 的学生用户
UPDATE `users` SET `password` = '$2a$10$O1AmnqNe50/knConTInm6Owvk/BkiYTYAviRZk5mz0uQfpT99Oija' WHERE `password` = '123456';

-- 4. 验证是否还有明文密码残留
SELECT `id`, `username`, `role`, 
  CASE 
    WHEN `password` NOT LIKE '$2a$%' THEN '明文密码'
    ELSE 'BCrypt加密'
  END AS password_status
FROM `users`
WHERE `password` NOT LIKE '$2a$%';
