package com.bigbird.codertestbackend.model.dto.ai;

import lombok.Data;

import java.util.List;

/**
 * AI生成结果报告响应
 */
@Data
public class ResultReportResponse {
    
    /**
     * 本关卡用户作答的分数（满分 100 分）
     */
    private Integer score;
    
    /**
     * 给本次用户的作答一个评价
     */
    private String comment;
    
    /**
     * 调整用户当前的薪资（一个具体的加减数字）
     */
    private Integer salaryChange;
    
    /**
     * 基于用户当前的薪资给出一些投递公司的建议
     */
    private String suggest;
    
    /**
     * 解释给出当前分数和评价的原因
     */
    private String reason;
    
    /**
     * 本关卡的正确选项
     */
    private List<String> trueOptions;
    
    /**
     * 标准的、详细的关卡分析和解读
     */
    private String standardAnswer;
    
    /**
     * 推荐的面试题列表
     */
    private String recommendedQuestions;
}
