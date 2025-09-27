package com.bigbird.codertestbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关卡表
 * @TableName level
 */
@TableName(value = "level")
@Data
public class Level implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 关卡名称
     */
    private String levelName;

    /**
     * 关卡需求描述
     */
    private String levelDesc;

    /**
     * 关卡选项（JSON格式存储）
     */
    private String options;

    /**
     * 难度等级（简单，中等，困难）
     */
    private String difficulty;

    /**
     * 目标薪资范围（用于难度匹配）
     */
    private Integer targetSalary;

    /**
     * 学习方向（前端开发、Java后端开发、软件测试等）
     */
    private String direction;

    /**
     * 关卡优先级（0-普通，99-推荐，999-精选，9999-置顶）
     */
    private Integer priority;

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

