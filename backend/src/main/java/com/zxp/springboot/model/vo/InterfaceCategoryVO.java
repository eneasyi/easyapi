package com.zxp.springboot.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 接口分类视图
 *
 */
@Data
public class InterfaceCategoryVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类描述
     */
    private String categoryDescription;

    /**
     * 分类排序
     */
    private Integer categoryOrder;

    /**
     * 父分类ID
     */
    private Long parentId;

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
    
    /**
     * 子分类列表
     */
    private List<InterfaceCategoryVO> children;

    private static final long serialVersionUID = 1L;
} 