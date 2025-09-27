-- 给用户表添加头像字段的迁移脚本
-- 执行时间：2025-09-16

USE `coder-test`;

-- 添加头像字段
ALTER TABLE `user` ADD COLUMN `avatar` VARCHAR(512) DEFAULT NULL COMMENT '用户头像URL' AFTER `nickname`;

-- 验证字段是否添加成功
DESC `user`;
