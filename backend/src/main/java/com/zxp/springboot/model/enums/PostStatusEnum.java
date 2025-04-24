package com.zxp.springboot.model.enums;

/**
 * 帖子状态枚举
 */
public enum PostStatusEnum {
    PUBLISHED("PUBLISHED", "已发布"),
    BAN("BAN", "封禁");

    private final String value;
    private final String desc;

    PostStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
} 