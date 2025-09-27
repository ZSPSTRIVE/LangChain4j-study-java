package com.bigbird.codertestbackend.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bigbird.codertestbackend.mapper.LevelMapper;
import com.bigbird.codertestbackend.model.dto.ai.LevelGenerationResponse;
import com.bigbird.codertestbackend.model.entity.Level;
import com.bigbird.codertestbackend.model.enums.LearningDirectionEnum;
import com.bigbird.codertestbackend.model.enums.LevelPriorityEnum;
import com.bigbird.codertestbackend.service.LevelService;
import com.bigbird.codertestbackend.service.ai.LevelGenerationAiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 关卡服务实现
 */
@Service
@Slf4j
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level> implements LevelService {

    @Resource
    private LevelGenerationAiService levelGenerationAiService;

    @Override
    public Level generateLevel(Integer salary, String direction) {
        // 如果没有指定学习方向，默认为全栈开发
        if (direction == null || direction.trim().isEmpty()) {
            direction = LearningDirectionEnum.FULL_STACK.getValue();
        }
        
        log.info("开始生成关卡，用户薪资：{}，学习方向：{}", salary, direction);
        
        try {
            // 拼接用户消息
            String userMessage = "当前薪资：" + salary + "，学习方向：" + direction;
            
            // 调用AI生成关卡
            LevelGenerationResponse aiResponse = levelGenerationAiService.generateLevel(userMessage);
            
            // 将AI响应转换为Level实体
            Level level = new Level();
            level.setLevelName(aiResponse.getLevelName());
            level.setLevelDesc(aiResponse.getLevelDesc());
            level.setOptions(JSONUtil.toJsonStr(aiResponse.getOptions())); // 将选项列表转换为JSON字符串
            level.setDifficulty(aiResponse.getDifficulty());
            level.setTargetSalary(aiResponse.getTargetSalary());
            level.setDirection(direction); // 设置学习方向
            level.setPriority(LevelPriorityEnum.NORMAL.getValue()); // 默认优先级为普通
            
            // 保存到数据库
            boolean saveResult = this.save(level);
            if (!saveResult) {
                throw new RuntimeException("保存关卡失败");
            }
            
            log.info("关卡生成成功，关卡ID：{}，关卡名称：{}，学习方向：{}", level.getId(), level.getLevelName(), direction);
            return level;
            
        } catch (Exception e) {
            log.error("AI生成关卡失败，用户薪资：{}，学习方向：{}，错误信息：{}", salary, direction, e.getMessage(), e);
            
            // 降级处理：返回一个默认关卡
            Level fallbackLevel = createFallbackLevel(salary, direction);
            this.save(fallbackLevel);
            
            log.warn("使用降级关卡，关卡ID：{}", fallbackLevel.getId());
            return fallbackLevel;
        }
    }
    
    /**
     * 创建降级关卡（当AI服务不可用时使用）
     */
    private Level createFallbackLevel(Integer salary, String direction) {
        Level level = new Level();
        level.setLevelName("系统维护中的临时关卡");
        level.setLevelDesc("抱歉，AI服务暂时不可用，这是一个临时关卡。请稍后再试。");
        level.setOptions("[]"); // 空选项
        level.setDifficulty(getDifficultyBySalary(salary));
        level.setTargetSalary(salary);
        level.setDirection(direction); // 保持用户选择的学习方向
        level.setPriority(LevelPriorityEnum.NORMAL.getValue()); // 默认优先级为普通
        return level;
    }
    
    /**
     * 根据薪资确定难度等级
     */
    private String getDifficultyBySalary(Integer salary) {
        if (salary <= 8000) {
            return "简单";
        } else if (salary <= 15000) {
            return "中等";
        } else {
            return "困难";
        }
    }
}

