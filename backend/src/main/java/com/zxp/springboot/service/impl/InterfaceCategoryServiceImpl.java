package com.zxp.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxp.springboot.common.ErrorCode;
import com.zxp.springboot.exception.BusinessException;
import com.zxp.springboot.exception.ThrowUtils;
import com.zxp.springboot.mapper.InterfaceCategoryMapper;
import com.zxp.springboot.model.dto.interfaceCategory.InterfaceCategoryQueryRequest;
import com.zxp.springboot.model.entity.InterfaceCategory;
import com.zxp.springboot.model.entity.User;
import com.zxp.springboot.model.vo.InterfaceCategoryVO;
import com.zxp.springboot.model.vo.UserVO;
import com.zxp.springboot.service.InterfaceCategoryService;
import com.zxp.springboot.service.UserService;
import com.zxp.springboot.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口分类服务实现
 *
 */
@Service
public class InterfaceCategoryServiceImpl extends ServiceImpl<InterfaceCategoryMapper, InterfaceCategory> implements InterfaceCategoryService {

    @Resource
    private UserService userService;

    @Override
    public void validInterfaceCategory(InterfaceCategory interfaceCategory, boolean add) {
        if (interfaceCategory == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        String categoryName = interfaceCategory.getCategoryName();
        String categoryDescription = interfaceCategory.getCategoryDescription();
        
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(categoryName), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(categoryName) && categoryName.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类名称过长");
        }
        if (StringUtils.isNotBlank(categoryDescription) && categoryDescription.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类描述过长");
        }
    }

    @Override
    public QueryWrapper<InterfaceCategory> getQueryWrapper(InterfaceCategoryQueryRequest interfaceCategoryQueryRequest) {
        QueryWrapper<InterfaceCategory> queryWrapper = new QueryWrapper<>();
        if (interfaceCategoryQueryRequest == null) {
            return queryWrapper;
        }
        
        Long id = interfaceCategoryQueryRequest.getId();
        String categoryName = interfaceCategoryQueryRequest.getCategoryName();
        String categoryDescription = interfaceCategoryQueryRequest.getCategoryDescription();
        Long parentId = interfaceCategoryQueryRequest.getParentId();
        Long userId = interfaceCategoryQueryRequest.getUserId();
        String sortField = interfaceCategoryQueryRequest.getSortField();
        String sortOrder = interfaceCategoryQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(categoryName)) {
            queryWrapper.like("categoryName", categoryName);
        }
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(categoryDescription), "categoryDescription", categoryDescription);
        queryWrapper.eq(parentId != null, "parentId", parentId);
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals("ascend"),
                sortField);
        return queryWrapper;
    }

    @Override
    public InterfaceCategoryVO getInterfaceCategoryVO(InterfaceCategory interfaceCategory) {
        if (interfaceCategory == null) {
            return null;
        }
        InterfaceCategoryVO interfaceCategoryVO = new InterfaceCategoryVO();
        
        // 复制基本属性
        BeanUtils.copyProperties(interfaceCategory, interfaceCategoryVO);
        
        // 关联查询用户信息
        Long userId = interfaceCategory.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        interfaceCategoryVO.setUser(userVO);
        
        return interfaceCategoryVO;
    }

    @Override
    public List<InterfaceCategoryVO> getInterfaceCategoryVO(List<InterfaceCategory> interfaceCategoryList) {
        if (interfaceCategoryList == null) {
            return new ArrayList<>();
        }
        
        // 关联查询用户信息
        Set<Long> userIds = interfaceCategoryList.stream()
                .map(InterfaceCategory::getUserId)
                .filter(userId -> userId != null && userId > 0)
                .collect(Collectors.toSet());
        
        Map<Long, UserVO> userVOMap = new HashMap<>();
        if (userIds.size() > 0) {
            List<User> users = userService.listByIds(userIds);
            userVOMap = users.stream()
                    .map(userService::getUserVO)
                    .collect(Collectors.toMap(UserVO::getId, userVO -> userVO));
        }
        
        // 填充信息
        Map<Long, UserVO> finalUserVOMap = userVOMap;
        return interfaceCategoryList.stream().map(interfaceCategory -> {
            InterfaceCategoryVO interfaceCategoryVO = new InterfaceCategoryVO();
            BeanUtils.copyProperties(interfaceCategory, interfaceCategoryVO);
            Long userId = interfaceCategory.getUserId();
            if (userId != null && userId > 0) {
                interfaceCategoryVO.setUser(finalUserVOMap.get(userId));
            }
            return interfaceCategoryVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<InterfaceCategoryVO> listCategoryTree() {
        // 获取所有分类
        List<InterfaceCategory> allCategories = this.list();
        List<InterfaceCategoryVO> allCategoryVOs = this.getInterfaceCategoryVO(allCategories);
        
        // 根据parentId分组
        Map<Long, List<InterfaceCategoryVO>> parentMap = new HashMap<>();
        for (InterfaceCategoryVO categoryVO : allCategoryVOs) {
            Long parentId = categoryVO.getParentId();
            if (parentId == null) {
                parentId = 0L;
            }
            List<InterfaceCategoryVO> subList = parentMap.getOrDefault(parentId, new ArrayList<>());
            subList.add(categoryVO);
            parentMap.put(parentId, subList);
        }
        
        // 递归构建树
        List<InterfaceCategoryVO> rootCategories = parentMap.getOrDefault(0L, new ArrayList<>());
        rootCategories.forEach(root -> buildSubTree(root, parentMap));
        
        return rootCategories;
    }
    
    /**
     * 递归构建分类树
     * @param current 当前节点
     * @param parentMap 父子节点映射
     */
    private void buildSubTree(InterfaceCategoryVO current, Map<Long, List<InterfaceCategoryVO>> parentMap) {
        Long id = current.getId();
        List<InterfaceCategoryVO> children = parentMap.getOrDefault(id, new ArrayList<>());
        
        // 子分类按排序顺序排序
        children.sort(Comparator.comparing(InterfaceCategoryVO::getCategoryOrder, 
                Comparator.nullsLast(Comparator.naturalOrder())));
        
        if (children.size() > 0) {
            current.setChildren(children);
            children.forEach(child -> buildSubTree(child, parentMap));
        }
    }
} 