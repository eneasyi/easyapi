package com.zxp.springboot.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息视图
 *
 */
@Data
public class InterfaceInfoVO implements Serializable {

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
     * 接口调用次数
     */
    private Long callCount;

    /**
     * 接口成功次数
     */
    private Long successCount;

    /**
     * 接口失败次数
     */
    private Long failCount;

    /**
     * 接口限流（次/分钟）
     */
    private Integer rateLimit;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人ID
     */
    private Long userId;
    
    /**
     * 创建人信息
     */
    private UserVO user;

    private static final long serialVersionUID = 1L;
} 