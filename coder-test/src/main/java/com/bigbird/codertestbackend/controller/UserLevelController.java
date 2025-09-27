package com.bigbird.codertestbackend.controller;

import com.bigbird.codertestbackend.model.dto.level.SubmitAnswerRequest;
import com.bigbird.codertestbackend.model.entity.User;
import com.bigbird.codertestbackend.model.entity.UserLevel;
import com.bigbird.codertestbackend.model.vo.BaseResponse;
import com.bigbird.codertestbackend.model.vo.ResultUtils;
import com.bigbird.codertestbackend.service.UserLevelService;
import com.bigbird.codertestbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户关卡接口
 */
@RestController
@RequestMapping("/user-level")
@Slf4j
@Tag(name = "用户关卡接口")
public class UserLevelController {

    @Resource
    private UserLevelService userLevelService;

    @Resource
    private UserService userService;

    /**
     * 提交答案
     *
     * @param submitAnswerRequest 提交答案请求体
     * @param request             请求对象
     * @return 结果报告
     */
    @PostMapping("/submit")
    @Operation(summary = "提交答案")
    public BaseResponse<UserLevel> submitAnswer(@RequestBody SubmitAnswerRequest submitAnswerRequest,
                                              HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User user = userService.getLoginUser(request);
            
            UserLevel userLevel = userLevelService.submitAnswer(submitAnswerRequest, user.getId());
            return ResultUtils.success(userLevel);
        } catch (Exception e) {
            log.error("提交答案失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 获取闯关历史
     *
     * @param request 请求对象
     * @return 闯关历史列表
     */
    @GetMapping("/history")
    @Operation(summary = "获取闯关历史")
    public BaseResponse<List<UserLevel>> getUserLevelHistory(HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User user = userService.getLoginUser(request);
            
            List<UserLevel> historyList = userLevelService.getUserLevelHistory(user.getId());
            return ResultUtils.success(historyList);
        } catch (Exception e) {
            log.error("获取闯关历史失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 获取闯关详情
     *
     * @param id 闯关记录ID
     * @return 闯关详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取闯关详情")
    public BaseResponse<UserLevel> getUserLevelById(@PathVariable String id) {
        try {
            UserLevel userLevel = userLevelService.getById(id);
            if (userLevel == null) {
                return ResultUtils.error(40400, "闯关记录不存在");
            }
            return ResultUtils.success(userLevel);
        } catch (Exception e) {
            log.error("获取闯关详情失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }
}

