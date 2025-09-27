-- 添加管理员功能的数据库迁移脚本
-- 执行时间：2025-09-16

USE `coder-test`;

ALTER TABLE `user`
    MODIFY COLUMN `userRole` VARCHAR(20) DEFAULT 'user' COMMENT '用户角色（user/admin）';

-- 给关卡表添加优先级字段
ALTER TABLE `level` ADD COLUMN `priority` INT DEFAULT 0 COMMENT '关卡优先级（0-普通，99-推荐，999-精选，9999-置顶）' AFTER `targetSalary`;

-- 添加索引
ALTER TABLE `user` ADD INDEX `idx_userRole` (`userRole`);
ALTER TABLE `level` ADD INDEX `idx_priority` (`priority`);

-- 创建一个管理员用户（可选，用于测试）
-- 注意：密码是 'admin123' 经过MD5加盐加密后的结果
INSERT INTO `user` (`username`, `password`, `nickname`, `userRole`, `salary`) 
VALUES ('admin', 'c8837b23ff8aaa8a2dde915473ce0991', '系统管理员', 'admin', 50000)
ON DUPLICATE KEY UPDATE `userRole` = 'admin';

-- 验证字段是否添加成功
DESC `user`;
DESC `level`;

-- 查看管理员用户
SELECT id, username, nickname, userRole FROM `user` WHERE userRole = 'admin';
