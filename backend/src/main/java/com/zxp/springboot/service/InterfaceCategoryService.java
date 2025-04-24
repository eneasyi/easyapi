package com.zxp.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxp.springboot.model.dto.interfaceCategory.InterfaceCategoryQueryRequest;
import com.zxp.springboot.model.entity.InterfaceCategory;
import com.zxp.springboot.model.vo.InterfaceCategoryVO;

import java.util.List;

/**
 * 接口分类服务
 *
 */
public interface InterfaceCategoryService extends IService<InterfaceCategory> {

    /**
     * 校验
     *
     * @param interfaceCategory
     * @param add
     */
    void validInterfaceCategory(InterfaceCategory interfaceCategory, boolean add);

    /**
     * 获取查询条件
     *
     * @param interfaceCategoryQueryRequest
     * @return
     */
    QueryWrapper<InterfaceCategory> getQueryWrapper(InterfaceCategoryQueryRequest interfaceCategoryQueryRequest);

    /**
     * 获取接口分类封装
     *
     * @param interfaceCategory
     * @return
     */
    InterfaceCategoryVO getInterfaceCategoryVO(InterfaceCategory interfaceCategory);

    /**
     * 获取接口分类封装列表
     *
     * @param interfaceCategoryList
     * @return
     */
    List<InterfaceCategoryVO> getInterfaceCategoryVO(List<InterfaceCategory> interfaceCategoryList);

    /**
     * 获取树形结构的接口分类
     *
     * @return
     */
    List<InterfaceCategoryVO> listCategoryTree();
} 