package com.zxp.springboot.model.enums;

/**
 * 模块类型枚举
 */
public enum ModuleTypeEnum {
    POST("POST", "帖子"),
    NEWS("NEWS", "资讯");

    private final String value;
    private final String desc;

    ModuleTypeEnum(String value, String desc) {
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