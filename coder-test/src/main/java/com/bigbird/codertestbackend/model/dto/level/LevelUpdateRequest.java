package com.bigbird.codertestbackend.model.dto.level;

import lombok.Data;

import java.io.Serializable;

/**
 * 关卡更新请求
 */
@Data
public class LevelUpdateRequest implements Serializable {

    /**
     * 关卡ID
     */
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
     * 学习方向
     */
    private String direction;

    /**
     * 关卡优先级（0-普通，99-推荐，999-精选，9999-置顶）
     */
    private Integer priority;

    private static final long serialVersionUID = 1L;
}
