package com.bigbird.codertestbackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户视图对象（脱敏）
 */
@Data
public class UserVO implements Serializable {

    /**
     * 主键
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 用户角色（user/admin）
     */
    private String userRole;

    /**
     * 当前薪资（单位：元/月）
     */
    private Integer salary;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}

