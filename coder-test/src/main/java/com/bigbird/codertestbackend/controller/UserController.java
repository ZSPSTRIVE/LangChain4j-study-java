package com.bigbird.codertestbackend.controller;

import com.bigbird.codertestbackend.model.dto.user.UserLoginRequest;
import com.bigbird.codertestbackend.model.dto.user.UserRegisterRequest;
import com.bigbird.codertestbackend.model.entity.User;
import com.bigbird.codertestbackend.model.vo.BaseResponse;
import com.bigbird.codertestbackend.model.vo.ResultUtils;
import com.bigbird.codertestbackend.model.vo.UserVO;
import com.bigbird.codertestbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户接口")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     * @return 新用户 id
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public BaseResponse<String> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        try {
            String userId = userService.userRegister(userRegisterRequest);
            return ResultUtils.success(userId);
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return ResultUtils.error(40000, e.getMessage());
        }
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @param request          请求对象
     * @return 脱敏后的用户信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        try {
            UserVO userVO = userService.userLogin(userLoginRequest, request);
            return ResultUtils.success(userVO);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ResultUtils.error(40000, e.getMessage());
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request 请求对象
     * @return 当前用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        try {
            User user = userService.getLoginUser(request);
            UserVO userVO = userService.getLoginUserVO(user);
            return ResultUtils.success(userVO);
        } catch (Exception e) {
            log.error("获取当前用户失败", e);
            return ResultUtils.error(40100, e.getMessage());
        }
    }

    /**
     * 用户注销
     *
     * @param request 请求对象
     * @return 是否成功
     */
    @PostMapping("/logout")
    @Operation(summary = "用户注销")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        try {
            boolean result = userService.userLogout(request);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("用户注销失败", e);
            return ResultUtils.error(40000, e.getMessage());
        }
    }
}

