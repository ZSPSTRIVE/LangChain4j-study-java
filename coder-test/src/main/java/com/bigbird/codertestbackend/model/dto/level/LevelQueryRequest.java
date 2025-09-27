package com.bigbird.codertestbackend.model.dto.level;

import lombok.Data;

import java.io.Serializable;

/**
 * 关卡查询请求
 */
@Data
public class LevelQueryRequest implements Serializable {

    /**
     * 关卡名称
     */
    private String levelName;

    /**
     * 难度等级
     */
    private String difficulty;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 学习方向
     */
    private String direction;

    /**
     * 当前页码
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    private static final long serialVersionUID = 1L;
}
