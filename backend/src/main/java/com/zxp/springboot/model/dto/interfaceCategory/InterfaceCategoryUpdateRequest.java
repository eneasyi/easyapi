package com.zxp.springboot.model.dto.interfaceCategory;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口分类更新请求
 *
 */
@Data
public class InterfaceCategoryUpdateRequest implements Serializable {
    
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

    private static final long serialVersionUID = 1L;
} 