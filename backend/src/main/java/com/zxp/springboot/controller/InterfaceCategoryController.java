package com.zxp.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxp.springboot.annotation.AuthCheck;
import com.zxp.springboot.common.BaseResponse;
import com.zxp.springboot.common.DeleteRequest;
import com.zxp.springboot.common.ErrorCode;
import com.zxp.springboot.common.ResultUtils;
import com.zxp.springboot.constant.UserConstant;
import com.zxp.springboot.exception.BusinessException;
import com.zxp.springboot.exception.ThrowUtils;
import com.zxp.springboot.model.dto.interfaceCategory.InterfaceCategoryAddRequest;
import com.zxp.springboot.model.dto.interfaceCategory.InterfaceCategoryQueryRequest;
import com.zxp.springboot.model.dto.interfaceCategory.InterfaceCategoryUpdateRequest;
import com.zxp.springboot.model.entity.InterfaceCategory;
import com.zxp.springboot.model.entity.User;
import com.zxp.springboot.model.vo.InterfaceCategoryVO;
import com.zxp.springboot.service.InterfaceCategoryService;
import com.zxp.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口分类接口
 *
 */
@RestController
@RequestMapping("/interfaceCategory")
@Slf4j
public class InterfaceCategoryController {

    @Resource
    private InterfaceCategoryService interfaceCategoryService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceCategoryAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceCategory(@RequestBody InterfaceCategoryAddRequest interfaceCategoryAddRequest, HttpServletRequest request) {
        if (interfaceCategoryAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceCategory interfaceCategory = new InterfaceCategory();
        BeanUtils.copyProperties(interfaceCategoryAddRequest, interfaceCategory);
        // 校验
        interfaceCategoryService.validInterfaceCategory(interfaceCategory, true);
        User loginUser = userService.getLoginUser(request);
        interfaceCategory.setUserId(loginUser.getId());
        boolean result = interfaceCategoryService.save(interfaceCategory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceCategoryId = interfaceCategory.getId();
        return ResultUtils.success(newInterfaceCategoryId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceCategory(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceCategory oldInterfaceCategory = interfaceCategoryService.getById(id);
        ThrowUtils.throwIf(oldInterfaceCategory == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceCategory.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceCategoryService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceCategoryUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceCategory(@RequestBody InterfaceCategoryUpdateRequest interfaceCategoryUpdateRequest) {
        if (interfaceCategoryUpdateRequest == null || interfaceCategoryUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceCategory interfaceCategory = new InterfaceCategory();
        BeanUtils.copyProperties(interfaceCategoryUpdateRequest, interfaceCategory);
        // 参数校验
        interfaceCategoryService.validInterfaceCategory(interfaceCategory, false);
        long id = interfaceCategoryUpdateRequest.getId();
        // 判断是否存在
        InterfaceCategory oldInterfaceCategory = interfaceCategoryService.getById(id);
        ThrowUtils.throwIf(oldInterfaceCategory == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceCategoryService.updateById(interfaceCategory);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceCategoryVO> getInterfaceCategoryById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceCategory interfaceCategory = interfaceCategoryService.getById(id);
        if (interfaceCategory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceCategoryService.getInterfaceCategoryVO(interfaceCategory));
    }

    /**
     * 分页获取列表
     *
     * @param interfaceCategoryQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<InterfaceCategoryVO>> listInterfaceCategoryVOByPage(@RequestBody InterfaceCategoryQueryRequest interfaceCategoryQueryRequest,
            HttpServletRequest request) {
        long current = interfaceCategoryQueryRequest.getCurrent();
        long size = interfaceCategoryQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<InterfaceCategory> interfaceCategoryPage = interfaceCategoryService.page(new Page<>(current, size),
                interfaceCategoryService.getQueryWrapper(interfaceCategoryQueryRequest));
        
        Page<InterfaceCategoryVO> interfaceCategoryVOPage = new Page<>(current, size, interfaceCategoryPage.getTotal());
        List<InterfaceCategoryVO> interfaceCategoryVO = interfaceCategoryService.getInterfaceCategoryVO(interfaceCategoryPage.getRecords());
        interfaceCategoryVOPage.setRecords(interfaceCategoryVO);
        return ResultUtils.success(interfaceCategoryVOPage);
    }
    
    /**
     * 获取树形接口分类
     *
     * @return
     */
    @GetMapping("/tree")
    public BaseResponse<List<InterfaceCategoryVO>> listCategoryTree() {
        List<InterfaceCategoryVO> categoryTree = interfaceCategoryService.listCategoryTree();
        return ResultUtils.success(categoryTree);
    }

    // endregion
} 