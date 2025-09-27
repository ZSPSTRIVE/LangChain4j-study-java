package com.bigbird.codertestbackend.utils;

import java.util.Random;

/**
 * 头像工具类
 */
public class AvatarUtils {
    
    /**
     * 默认头像列表（4个）
     */
    private static final String[] DEFAULT_AVATARS = {
        "https://img.icons8.com/color/96/000000/user-male-circle--v1.png",
        "https://img.icons8.com/color/96/000000/user-female-circle--v1.png", 
        "https://img.icons8.com/color/96/000000/administrator-male--v1.png",
        "https://img.icons8.com/color/96/000000/businesswoman--v1.png"
    };
    
    private static final Random random = new Random();
    
    /**
     * 获取随机默认头像
     * 
     * @return 默认头像URL
     */
    public static String getRandomDefaultAvatar() {
        int index = random.nextInt(DEFAULT_AVATARS.length);
        return DEFAULT_AVATARS[index];
    }
    
    /**
     * 根据用户ID生成固定的默认头像（确保同一用户总是获得相同的默认头像）
     * 
     * @param userId 用户ID
     * @return 默认头像URL
     */
    public static String getDefaultAvatarByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return getRandomDefaultAvatar();
        }
        
        // 使用用户ID的哈希值来确保相同用户ID总是得到相同的头像
        int hash = Math.abs(userId.hashCode());
        int index = hash % DEFAULT_AVATARS.length;
        return DEFAULT_AVATARS[index];
    }
}
