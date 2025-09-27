package com.bigbird.codertestbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bigbird.codertestbackend.model.dto.level.SubmitAnswerRequest;
import com.bigbird.codertestbackend.model.entity.UserLevel;

import java.util.List;

/**
 * 用户关卡服务
 */
public interface UserLevelService extends IService<UserLevel> {

    /**
     * 提交答案并生成结果报告
     *
     * @param submitAnswerRequest 提交答案请求
     * @param userId              用户ID
     * @return 结果报告
     */
    UserLevel submitAnswer(SubmitAnswerRequest submitAnswerRequest, String userId);

    /**
     * 获取用户闯关历史
     *
     * @param userId 用户ID
     * @return 闯关历史列表
     */
    List<UserLevel> getUserLevelHistory(String userId);
}

