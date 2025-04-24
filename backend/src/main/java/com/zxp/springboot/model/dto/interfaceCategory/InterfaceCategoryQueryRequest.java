package com.zxp.springboot.model.dto.interfaceCategory;

import com.zxp.springboot.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 接口分类查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceCategoryQueryRequest extends PageRequest implements Serializable {
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
     * 父分类ID
     */
    private Long parentId;

    /**
     * 创建人ID
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
} 