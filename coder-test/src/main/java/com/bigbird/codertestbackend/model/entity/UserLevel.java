package com.bigbird.codertestbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关卡表
 * @TableName user_level
 */
@TableName(value = "user_level")
@Data
public class UserLevel implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 关卡ID
     */
    private String levelId;

    /**
     * 用户选择的选项（JSON格式存储）
     */
    private String userOptions;

    /**
     * 得分（0-100分）
     */
    private Integer score;

    /**
     * 评价
     */
    private String comment;

    /**
     * 薪资变化（正数为加薪，负数为减薪）
     */
    private Integer salaryChange;

    /**
     * 公司投递建议
     */
    private String suggest;

    /**
     * 评分原因
     */
    private String reason;

    /**
     * 正确选项（JSON格式存储）
     */
    private String trueOptions;

    /**
     * 标准答案解析
     */
    private String standardAnswer;

    /**
     * 推荐面试题
     */
    private String recommendedQuestions;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

