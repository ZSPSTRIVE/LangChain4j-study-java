package com.bigbird.codertestbackend.controller;

import com.bigbird.codertestbackend.model.vo.BaseResponse;
import com.bigbird.codertestbackend.model.vo.ResultUtils;
import com.bigbird.codertestbackend.model.vo.UserRankingVO;
import com.bigbird.codertestbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 排行榜接口
 */
@RestController
@RequestMapping("/ranking")
@Slf4j
@Tag(name = "排行榜接口")
public class RankingController {

    @Resource
    private UserService userService;

    /**
     * 获取薪资排行榜
     *
     * @param limit 排行榜数量限制，默认30
     * @return 薪资排行榜
     */
    @GetMapping("/salary")
    @Operation(summary = "获取薪资排行榜")
    public BaseResponse<List<UserRankingVO>> getSalaryRanking(@RequestParam(defaultValue = "30") int limit) {
        try {
            // 限制最大查询数量，防止数据量过大
            if (limit > 100) {
                limit = 100;
            }
            if (limit < 1) {
                limit = 30;
            }
            
            List<UserRankingVO> rankingList = userService.getSalaryRanking(limit);
            return ResultUtils.success(rankingList);
            
        } catch (Exception e) {
            log.error("获取薪资排行榜失败", e);
            return ResultUtils.error(50000, "获取排行榜失败");
        }
    }
}
