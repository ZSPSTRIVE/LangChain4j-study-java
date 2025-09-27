package com.bigbird.codertestbackend.model.enums;

import lombok.Getter;

/**
 * 关卡优先级枚举
 */
@Getter
public enum LevelPriorityEnum {

    NORMAL(0, "普通"),
    RECOMMEND(99, "推荐"),
    FEATURED(999, "精选"),
    TOP(9999, "置顶");

    private final Integer value;
    private final String text;

    LevelPriorityEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static LevelPriorityEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (LevelPriorityEnum priorityEnum : LevelPriorityEnum.values()) {
            if (priorityEnum.value.equals(value)) {
                return priorityEnum;
            }
        }
        return null;
    }
}
