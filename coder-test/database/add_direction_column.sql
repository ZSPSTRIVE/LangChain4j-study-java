-- 给关卡表添加学习方向字段的迁移脚本
-- 执行时间：2025-09-16

USE `coder-test`;

-- 添加学习方向字段
ALTER TABLE `level` ADD COLUMN `direction` VARCHAR(100) DEFAULT '全栈开发' COMMENT '学习方向（前端开发、Java后端开发、软件测试等）' AFTER `targetSalary`;

-- 添加索引
ALTER TABLE `level` ADD INDEX `idx_direction` (`direction`);

-- 验证字段是否添加成功
DESC `level`;
