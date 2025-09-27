package com.bigbird.codertestbackend.service.ai;

import com.bigbird.codertestbackend.model.dto.ai.LevelGenerationResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI生成关卡服务
 */
public interface LevelGenerationAiService {

    /**
     * 根据拼接好的用户消息生成关卡
     * 
     * @param userMessage 拼接好的用户消息内容（包含薪资和学习方向）
     * @return 生成的关卡信息
     */
    @SystemMessage(fromResource = "prompts/level-generation-system.txt")
    @UserMessage("{{userMessage}}")
    LevelGenerationResponse generateLevel(String userMessage);
}
