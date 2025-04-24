package com.zxp.springboot.model.dto.interfaceInfo;

import com.zxp.springboot.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 接口信息查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
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
     * 接口可见性：public/private
     */
    private String visibility;

    /**
     * 创建人ID
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
} 