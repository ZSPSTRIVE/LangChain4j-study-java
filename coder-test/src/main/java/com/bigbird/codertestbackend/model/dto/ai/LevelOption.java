package com.bigbird.codertestbackend.model.dto.ai;

import lombok.Data;

/**
 * 关卡选项
 */
@Data
public class LevelOption {
    
    /**
     * 选项名称
     */
    private String optionName;
    
    /**
     * 是否为正确答案
     */
    private Boolean trueAnswer;
}
