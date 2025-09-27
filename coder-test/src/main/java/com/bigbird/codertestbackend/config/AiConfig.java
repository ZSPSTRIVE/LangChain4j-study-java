package com.bigbird.codertestbackend.config;

import com.bigbird.codertestbackend.service.ai.InterviewQuestionSearchTool;
import com.bigbird.codertestbackend.service.ai.LevelGenerationAiService;
import com.bigbird.codertestbackend.service.ai.ResultReportAiService;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类
 */
@Configuration
public class AiConfig {

    @Value("${langchain4j.dashscope.api-key}")
    private String apiKey;

    @Value("${langchain4j.dashscope.model-name}")
    private String modelName;

    @Value("${langchain4j.dashscope.temperature:0.7}")
    private Float temperature;
    
    @Resource
    private InterviewQuestionSearchTool interviewQuestionSearchTool;

    /**
     * 配置 ChatModel Bean
     */
    @Bean
    public ChatModel chatModel() {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .build();
    }

    /**
     * 配置关卡生成 AI 服务
     */
    @Bean
    public LevelGenerationAiService levelGenerationAiService(ChatModel chatModel) {
        return AiServices.builder(LevelGenerationAiService.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 配置结果报告生成 AI 服务
     */
    @Bean
    public ResultReportAiService resultReportAiService(ChatModel chatModel) {
        return AiServices.builder(ResultReportAiService.class)
                .chatModel(chatModel)
                .tools(interviewQuestionSearchTool)
                .build();
    }
}
