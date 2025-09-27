package com.bigbird.codertestbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigbird.codertestbackend.model.dto.level.GenerateLevelRequest;
import com.bigbird.codertestbackend.model.entity.Level;
import com.bigbird.codertestbackend.model.entity.User;
import com.bigbird.codertestbackend.model.enums.LevelPriorityEnum;
import com.bigbird.codertestbackend.model.vo.BaseResponse;
import com.bigbird.codertestbackend.model.vo.ResultUtils;
import com.bigbird.codertestbackend.service.LevelService;
import com.bigbird.codertestbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 关卡接口
 */
@RestController
@RequestMapping("/level")
@Slf4j
@Tag(name = "关卡接口")
public class LevelController {

    @Resource
    private LevelService levelService;

    @Resource
    private UserService userService;

    /**
     * 生成关卡
     *
     * @param generateLevelRequest 生成关卡请求体
     * @param request              请求对象
     * @return 生成的关卡
     */
    @PostMapping("/generate")
    @Operation(summary = "生成关卡")
    public BaseResponse<Level> generateLevel(@RequestBody GenerateLevelRequest generateLevelRequest, 
                                           HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User user = userService.getLoginUser(request);
            
            // 使用用户当前薪资或请求中的薪资
            Integer salary = generateLevelRequest.getSalary();
            if (salary == null) {
                salary = user.getSalary();
            }
            
            // 获取学习方向
            String direction = generateLevelRequest.getDirection();
            
            // 生成关卡（如果没有指定方向，服务层会设置默认值）
            Level level = levelService.generateLevel(salary, direction);
            
            return ResultUtils.success(level);
        } catch (Exception e) {
            log.error("生成关卡失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 获取关卡详情
     *
     * @param id 关卡ID
     * @return 关卡详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取关卡详情")
    public BaseResponse<Level> getLevelById(@PathVariable String id) {
        try {
            Level level = levelService.getById(id);
            if (level == null) {
                return ResultUtils.error(40400, "关卡不存在");
            }
            return ResultUtils.success(level);
        } catch (Exception e) {
            log.error("获取关卡详情失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 分页获取精选关卡列表（公开接口）
     *
     * @param current  当前页码
     * @param pageSize 页面大小
     * @return 精选关卡分页数据
     */
    @GetMapping("/featured")
    @Operation(summary = "分页获取精选关卡列表")
    public BaseResponse<Page<Level>> getFeaturedLevels(@RequestParam(defaultValue = "1") long current,
                                                      @RequestParam(defaultValue = "10") long pageSize) {
        try {
            // 构建查询条件：只查询精选关卡（priority >= 999）
            QueryWrapper<Level> queryWrapper = new QueryWrapper<>();
            queryWrapper.ge("priority", LevelPriorityEnum.FEATURED.getValue());
            
            // 按优先级和创建时间排序
            queryWrapper.orderByDesc("priority", "createTime");
            
            Page<Level> levelPage = levelService.page(new Page<>(current, pageSize), queryWrapper);
            return ResultUtils.success(levelPage);
            
        } catch (Exception e) {
            log.error("获取精选关卡列表失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }
}

