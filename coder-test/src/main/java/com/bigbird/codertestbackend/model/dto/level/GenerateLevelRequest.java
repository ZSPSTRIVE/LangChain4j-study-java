package com.bigbird.codertestbackend.model.dto.level;

import lombok.Data;

import java.io.Serializable;

/**
 * 生成关卡请求体
 */
@Data
public class GenerateLevelRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 当前薪资（用于匹配难度）
     */
    private Integer salary;

    /**
     * 学习方向
     */
    private String direction;
}

