package com.bigbird.codertestbackend.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bigbird.codertestbackend.mapper.UserLevelMapper;
import com.bigbird.codertestbackend.model.dto.ai.LevelOption;
import com.bigbird.codertestbackend.model.dto.ai.ResultReportResponse;
import com.bigbird.codertestbackend.model.dto.level.SubmitAnswerRequest;
import com.bigbird.codertestbackend.model.entity.Level;
import com.bigbird.codertestbackend.model.entity.User;
import com.bigbird.codertestbackend.model.entity.UserLevel;
import com.bigbird.codertestbackend.service.LevelService;
import com.bigbird.codertestbackend.service.UserLevelService;
import com.bigbird.codertestbackend.service.UserService;
import com.bigbird.codertestbackend.service.ai.ResultReportAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户关卡服务实现
 */
@Service
@Slf4j
public class UserLevelServiceImpl extends ServiceImpl<UserLevelMapper, UserLevel> implements UserLevelService {

    @Resource
    private LevelService levelService;

    @Resource
    private UserService userService;
    
    @Resource
    private ResultReportAiService resultReportAiService;

    @Override
    public UserLevel submitAnswer(SubmitAnswerRequest submitAnswerRequest, String userId) {
        String levelId = submitAnswerRequest.getLevelId();
        List<String> userOptions = submitAnswerRequest.getUserOptions();

        if (StrUtil.isBlank(levelId) || StrUtil.isBlank(userId)) {
            throw new RuntimeException("参数不能为空");
        }

        // 获取关卡信息
        Level level = levelService.getById(levelId);
        if (level == null) {
            throw new RuntimeException("关卡不存在");
        }

        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        log.info("用户 {} 提交关卡 {} 的答案：{}", userId, levelId, userOptions);
        
        try {
            // 1. 解析关卡的正确答案
            List<String> trueOptions = extractTrueOptions(level.getOptions());
            
            // 2. 拼接用户消息并调用AI生成详细的结果报告
            String userMessage = String.format("""
                ### 关卡名称
                %s
                
                ## 关卡的需求描述
                %s
                
                ## 用户选择的选项
                %s
                
                ### 本关卡的正确选项
                %s
                
                ### 用户当前的薪资
                %d
                """, 
                level.getLevelName(),
                level.getLevelDesc(),
                JSONUtil.toJsonStr(userOptions),
                JSONUtil.toJsonStr(trueOptions),
                user.getSalary()
            );
            
            ResultReportResponse aiResponse = resultReportAiService.generateResultReport(userMessage);
            
            // 3. 将AI响应转换为UserLevel实体
            UserLevel userLevel = new UserLevel();
            userLevel.setUserId(userId);
            userLevel.setLevelId(levelId);
            userLevel.setUserOptions(JSONUtil.toJsonStr(userOptions));
            userLevel.setScore(aiResponse.getScore());
            userLevel.setComment(aiResponse.getComment());
            userLevel.setSalaryChange(aiResponse.getSalaryChange());
            userLevel.setSuggest(aiResponse.getSuggest());
            userLevel.setReason(aiResponse.getReason());
            userLevel.setTrueOptions(JSONUtil.toJsonStr(aiResponse.getTrueOptions()));
            userLevel.setStandardAnswer(aiResponse.getStandardAnswer());
            userLevel.setRecommendedQuestions(aiResponse.getRecommendedQuestions());

            // 4. 保存结果
            boolean saveResult = this.save(userLevel);
            if (!saveResult) {
                throw new RuntimeException("保存用户关卡结果失败");
            }

            // 5. 更新用户薪资
            boolean updateResult = userService.updateUserSalary(userId, userLevel.getSalaryChange());
            if (!updateResult) {
                log.warn("更新用户薪资失败，用户ID：{}，薪资变化：{}", userId, userLevel.getSalaryChange());
            }

            log.info("用户 {} 完成关卡 {}，得分：{}，薪资变化：{}", userId, levelId, 
                    userLevel.getScore(), userLevel.getSalaryChange());
            
            return userLevel;
            
        } catch (Exception e) {
            log.error("AI生成结果报告失败，用户ID：{}，关卡ID：{}，错误信息：{}", 
                    userId, levelId, e.getMessage(), e);
            
            // 降级处理：返回一个默认结果
            UserLevel fallbackUserLevel = createFallbackResult(userId, levelId, userOptions, level);
            this.save(fallbackUserLevel);
            
            // 不更新薪资（降级情况下保持原薪资）
            log.warn("使用降级结果报告，用户ID：{}，关卡ID：{}", userId, levelId);
            return fallbackUserLevel;
        }
    }
    
    /**
     * 从关卡选项中提取正确答案
     */
    private List<String> extractTrueOptions(String optionsJson) {
        try {
            List<LevelOption> options = JSONUtil.toList(optionsJson, LevelOption.class);
            return options.stream()
                    .filter(option -> Boolean.TRUE.equals(option.getTrueAnswer()))
                    .map(LevelOption::getOptionName)
                    .toList();
        } catch (Exception e) {
            log.error("解析关卡选项失败：{}", e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * 创建降级结果（当AI服务不可用时使用）
     */
    private UserLevel createFallbackResult(String userId, String levelId, 
                                         List<String> userOptions, Level level) {
        UserLevel userLevel = new UserLevel();
        userLevel.setUserId(userId);
        userLevel.setLevelId(levelId);
        userLevel.setUserOptions(JSONUtil.toJsonStr(userOptions));
        userLevel.setScore(60); // 默认及格分数
        userLevel.setComment("系统维护中，暂时无法生成详细评价");
        userLevel.setSalaryChange(0); // 不变化薪资
        userLevel.setSuggest("请稍后再试，我们会为您提供更详细的建议");
        userLevel.setReason("AI服务暂时不可用，无法生成详细分析");
        userLevel.setTrueOptions("[]");
        userLevel.setStandardAnswer("抱歉，系统维护中，暂时无法提供标准答案解析");
        return userLevel;
    }

    @Override
    public List<UserLevel> getUserLevelHistory(String userId) {
        if (StrUtil.isBlank(userId)) {
            throw new RuntimeException("用户ID不能为空");
        }

        QueryWrapper<UserLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.orderByDesc("createTime");

        return this.list(queryWrapper);
    }
}

