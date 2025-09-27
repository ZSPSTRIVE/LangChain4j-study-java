package com.bigbird.codertestbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bigbird.codertestbackend.model.entity.Level;

/**
 * 关卡服务
 */
public interface LevelService extends IService<Level> {

    /**
     * 根据用户薪资和学习方向生成关卡
     *
     * @param salary 用户当前薪资
     * @param direction 学习方向（可选，为空时默认为全栈开发）
     * @return 生成的关卡
     */
    Level generateLevel(Integer salary, String direction);
}

