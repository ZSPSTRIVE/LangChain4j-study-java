package com.bigbird.codertestbackend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户排行榜视图对象
 */
@Data
public class UserRankingVO implements Serializable {

    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 当前薪资（单位：元/月）
     */
    private Integer salary;

    /**
     * 排名
     */
    private Integer rank;

    private static final long serialVersionUID = 1L;
}
