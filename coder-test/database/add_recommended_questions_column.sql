-- 为 user_level 表添加推荐面试题字段

USE
    `coder-test`;
ALTER TABLE user_level 
ADD COLUMN recommendedQuestions TEXT COMMENT '推荐面试题' AFTER standardAnswer;
