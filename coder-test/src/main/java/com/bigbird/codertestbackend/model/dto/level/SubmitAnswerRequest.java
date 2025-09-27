package com.bigbird.codertestbackend.model.dto.level;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 提交答案请求体
 */
@Data
public class SubmitAnswerRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 关卡ID
     */
    private String levelId;

    /**
     * 用户选择的选项
     */
    private List<String> userOptions;
}

