package com.bigbird.codertestbackend.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigbird.codertestbackend.model.dto.level.LevelAddRequest;
import com.bigbird.codertestbackend.model.dto.level.LevelQueryRequest;
import com.bigbird.codertestbackend.model.dto.level.LevelUpdateRequest;
import com.bigbird.codertestbackend.model.entity.Level;
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
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员接口
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@Tag(name = "管理员接口")
public class AdminController {

    @Resource
    private LevelService levelService;

    @Resource
    private UserService userService;

    /**
     * 分页查询关卡列表
     */
    @PostMapping("/level/list")
    @Operation(summary = "分页查询关卡列表")
    public BaseResponse<Page<Level>> listLevels(@RequestBody LevelQueryRequest queryRequest,
                                              HttpServletRequest request) {
        try {
            // 校验管理员权限
            userService.checkAdminAuth(request);

            long current = queryRequest.getCurrent();
            long size = queryRequest.getPageSize();

            // 构建查询条件
            QueryWrapper<Level> queryWrapper = new QueryWrapper<>();
            if (StrUtil.isNotBlank(queryRequest.getLevelName())) {
                queryWrapper.like("levelName", queryRequest.getLevelName());
            }
            if (StrUtil.isNotBlank(queryRequest.getDifficulty())) {
                queryWrapper.eq("difficulty", queryRequest.getDifficulty());
            }
            if (queryRequest.getPriority() != null) {
                queryWrapper.eq("priority", queryRequest.getPriority());
            }
            if (StrUtil.isNotBlank(queryRequest.getDirection())) {
                queryWrapper.eq("direction", queryRequest.getDirection());
            }

            // 按优先级和创建时间排序
            queryWrapper.orderByDesc("priority", "createTime");

            Page<Level> levelPage = levelService.page(new Page<>(current, size), queryWrapper);
            return ResultUtils.success(levelPage);

        } catch (Exception e) {
            log.error("查询关卡列表失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 添加关卡
     */
    @PostMapping("/level/add")
    @Operation(summary = "添加关卡")
    public BaseResponse<String> addLevel(@RequestBody LevelAddRequest addRequest,
                                       HttpServletRequest request) {
        try {
            // 校验管理员权限
            userService.checkAdminAuth(request);

            // 参数校验
            if (StrUtil.hasBlank(addRequest.getLevelName(), addRequest.getLevelDesc(), 
                               addRequest.getOptions(), addRequest.getDifficulty())) {
                return ResultUtils.error(40000, "参数不能为空");
            }

            Level level = new Level();
            BeanUtils.copyProperties(addRequest, level);

            // 如果没有设置优先级，默认为普通
            if (level.getPriority() == null) {
                level.setPriority(LevelPriorityEnum.NORMAL.getValue());
            }

            // 如果没有设置目标薪资，默认为10000
            if (level.getTargetSalary() == null) {
                level.setTargetSalary(10000);
            }

            boolean result = levelService.save(level);
            if (!result) {
                return ResultUtils.error(50000, "添加关卡失败");
            }

            return ResultUtils.success(level.getId());

        } catch (Exception e) {
            log.error("添加关卡失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 更新关卡
     */
    @PostMapping("/level/update")
    @Operation(summary = "更新关卡")
    public BaseResponse<Boolean> updateLevel(@RequestBody LevelUpdateRequest updateRequest,
                                           HttpServletRequest request) {
        try {
            // 校验管理员权限
            userService.checkAdminAuth(request);

            if (StrUtil.isBlank(updateRequest.getId())) {
                return ResultUtils.error(40000, "关卡ID不能为空");
            }

            Level level = levelService.getById(updateRequest.getId());
            if (level == null) {
                return ResultUtils.error(40400, "关卡不存在");
            }

            // 更新字段
            BeanUtils.copyProperties(updateRequest, level);

            boolean result = levelService.updateById(level);
            return ResultUtils.success(result);

        } catch (Exception e) {
            log.error("更新关卡失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 删除关卡
     */
    @PostMapping("/level/delete/{id}")
    @Operation(summary = "删除关卡")
    public BaseResponse<Boolean> deleteLevel(@PathVariable String id,
                                           HttpServletRequest request) {
        try {
            // 校验管理员权限
            userService.checkAdminAuth(request);

            if (StrUtil.isBlank(id)) {
                return ResultUtils.error(40000, "关卡ID不能为空");
            }

            Level level = levelService.getById(id);
            if (level == null) {
                return ResultUtils.error(40400, "关卡不存在");
            }

            boolean result = levelService.removeById(id);
            return ResultUtils.success(result);

        } catch (Exception e) {
            log.error("删除关卡失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 设置关卡为精选
     */
    @PostMapping("/level/setFeatured/{id}")
    @Operation(summary = "设置关卡为精选")
    public BaseResponse<Boolean> setLevelFeatured(@PathVariable String id,
                                                HttpServletRequest request) {
        try {
            // 校验管理员权限
            userService.checkAdminAuth(request);

            if (StrUtil.isBlank(id)) {
                return ResultUtils.error(40000, "关卡ID不能为空");
            }

            Level level = levelService.getById(id);
            if (level == null) {
                return ResultUtils.error(40400, "关卡不存在");
            }

            // 设置为精选
            level.setPriority(LevelPriorityEnum.FEATURED.getValue());
            boolean result = levelService.updateById(level);

            return ResultUtils.success(result);

        } catch (Exception e) {
            log.error("设置关卡精选失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 取消关卡精选
     */
    @PostMapping("/level/cancelFeatured/{id}")
    @Operation(summary = "取消关卡精选")
    public BaseResponse<Boolean> cancelLevelFeatured(@PathVariable String id,
                                                   HttpServletRequest request) {
        try {
            // 校验管理员权限
            userService.checkAdminAuth(request);

            if (StrUtil.isBlank(id)) {
                return ResultUtils.error(40000, "关卡ID不能为空");
            }

            Level level = levelService.getById(id);
            if (level == null) {
                return ResultUtils.error(40400, "关卡不存在");
            }

            // 设置为普通
            level.setPriority(LevelPriorityEnum.NORMAL.getValue());
            boolean result = levelService.updateById(level);

            return ResultUtils.success(result);

        } catch (Exception e) {
            log.error("取消关卡精选失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 批量设置关卡优先级
     */
    @PostMapping("/level/setPriority")
    @Operation(summary = "批量设置关卡优先级")
    public BaseResponse<Boolean> setLevelPriority(@RequestParam String ids,
                                                @RequestParam Integer priority,
                                                HttpServletRequest request) {
        try {
            // 校验管理员权限
            userService.checkAdminAuth(request);

            if (StrUtil.isBlank(ids) || priority == null) {
                return ResultUtils.error(40000, "参数不能为空");
            }

            // 验证优先级是否有效
            if (LevelPriorityEnum.getEnumByValue(priority) == null) {
                return ResultUtils.error(40000, "无效的优先级值");
            }

            String[] idArray = ids.split(",");
            for (String id : idArray) {
                if (StrUtil.isNotBlank(id.trim())) {
                    Level level = levelService.getById(id.trim());
                    if (level != null) {
                        level.setPriority(priority);
                        levelService.updateById(level);
                    }
                }
            }

            return ResultUtils.success(true);

        } catch (Exception e) {
            log.error("批量设置关卡优先级失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }
}
