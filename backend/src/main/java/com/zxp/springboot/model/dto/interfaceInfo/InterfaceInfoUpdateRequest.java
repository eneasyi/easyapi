package com.zxp.springboot.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息更新请求
 *
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {
    
    /**
     * id
     */
    private Long id;
    
    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 返回参数
     */
    private String responseParams;

    /**
     * 请求示例
     */
    private String requestExample;

    /**
     * 返回示例
     */
    private String responseExample;

    /**
     * 接口状态：0-关闭，1-开启
     */
    private Integer status;

    /**
     * 接口分类
     */
    private String category;

    /**
     * 接口版本
     */
    private String version;

    /**
     * Mock数据
     */
    private String mock;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;

    /**
     * 接口可见性：public/private
     */
    private String visibility;

    /**
     * 接口限流（次/分钟）
     */
    private Integer rateLimit;

    private static final long serialVersionUID = 1L;
} 