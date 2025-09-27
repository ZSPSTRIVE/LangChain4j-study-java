package com.bigbird.codertestbackend.model.dto.ai;

import lombok.Data;

import java.util.List;

/**
 * AI生成关卡响应
 */
@Data
public class LevelGenerationResponse {
    
    /**
     * 关卡名称
     */
    private String levelName;
    
    /**
     * 关卡需求描述
     */
    private String levelDesc;
    
    /**
     * 关卡选项列表
     */
    private List<LevelOption> options;
    
    /**
     * 难度等级（简单，中等，困难）
     */
    private String difficulty;
    
    /**
     * 目标薪资范围
     */
    private Integer targetSalary;
}
