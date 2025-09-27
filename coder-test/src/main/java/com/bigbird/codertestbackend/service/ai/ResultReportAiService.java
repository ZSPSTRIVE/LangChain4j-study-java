package com.bigbird.codertestbackend.service.ai;

import com.bigbird.codertestbackend.model.dto.ai.ResultReportResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI生成结果报告服务
 */
public interface ResultReportAiService {

    /**
     * 根据拼接好的用户消息生成结果报告
     * 
     * @param userMessage 拼接好的用户消息内容
     * @return 生成的结果报告
     */
    @SystemMessage(fromResource = "prompts/result-report-system.txt")
    @UserMessage("{{userMessage}}")
    ResultReportResponse generateResultReport(String userMessage);
}
