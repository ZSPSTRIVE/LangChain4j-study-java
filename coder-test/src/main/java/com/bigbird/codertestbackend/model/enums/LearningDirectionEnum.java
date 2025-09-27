package com.bigbird.codertestbackend.model.enums;

import lombok.Getter;

/**
 * 学习方向枚举
 */
@Getter
public enum LearningDirectionEnum {

    FULL_STACK("全栈开发", "全栈开发"),
    FRONTEND("前端开发", "前端开发"),
    JAVA_BACKEND("Java后端开发", "Java后端开发"),
    PYTHON_BACKEND("Python后端开发", "Python后端开发"),
    SOFTWARE_TESTING("软件测试", "软件测试"),
    AI_ALGORITHM("AI算法", "AI算法"),
    NETWORK_OPERATIONS("网络运维", "网络运维"),
    DATA_ANALYSIS("数据分析", "数据分析"),
    MOBILE_DEVELOPMENT("移动端开发", "移动端开发"),
    DEVOPS("DevOps运维", "DevOps运维"),
    CYBERSECURITY("网络安全", "网络安全"),
    GAME_DEVELOPMENT("游戏开发", "游戏开发");

    private final String value;
    private final String text;

    LearningDirectionEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static LearningDirectionEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (LearningDirectionEnum directionEnum : LearningDirectionEnum.values()) {
            if (directionEnum.value.equals(value)) {
                return directionEnum;
            }
        }
        return null;
    }

    /**
     * 获取所有方向的值数组
     */
    public static String[] getAllValues() {
        LearningDirectionEnum[] enums = LearningDirectionEnum.values();
        String[] values = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            values[i] = enums[i].getValue();
        }
        return values;
    }
}
