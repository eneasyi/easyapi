package com.zxp.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxp.springboot.common.ErrorCode;
import com.zxp.springboot.exception.BusinessException;
import com.zxp.springboot.exception.ThrowUtils;
import com.zxp.springboot.mapper.InterfaceInfoMapper;
import com.zxp.springboot.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.zxp.springboot.model.entity.InterfaceInfo;
import com.zxp.springboot.model.entity.User;
import com.zxp.springboot.model.vo.InterfaceInfoVO;
import com.zxp.springboot.model.vo.UserVO;
import com.zxp.springboot.service.InterfaceInfoService;
import com.zxp.springboot.service.UserService;
import com.zxp.springboot.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口信息服务实现
 *
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Resource
    private UserService userService;

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, url, method), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        
        Long id = interfaceInfoQueryRequest.getId();
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        String method = interfaceInfoQueryRequest.getMethod();
        Integer status = interfaceInfoQueryRequest.getStatus();
        String category = interfaceInfoQueryRequest.getCategory();
        String version = interfaceInfoQueryRequest.getVersion();
        String visibility = interfaceInfoQueryRequest.getVisibility();
        Long userId = interfaceInfoQueryRequest.getUserId();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(url), "url", url);
        queryWrapper.eq(StringUtils.isNotBlank(method), "method", method);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq(StringUtils.isNotBlank(category), "category", category);
        queryWrapper.eq(StringUtils.isNotBlank(version), "version", version);
        queryWrapper.eq(StringUtils.isNotBlank(visibility), "visibility", visibility);
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals("ascend"),
                sortField);
        return queryWrapper;
    }

    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo) {
        if (interfaceInfo == null) {
            return null;
        }
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        
        // 复制基本属性
        BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
        
        // 关联查询用户信息
        Long userId = interfaceInfo.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        interfaceInfoVO.setUser(userVO);
        
        return interfaceInfoVO;
    }

    @Override
    public List<InterfaceInfoVO> getInterfaceInfoVO(List<InterfaceInfo> interfaceInfoList) {
        if (interfaceInfoList == null) {
            return new ArrayList<>();
        }
        
        // 关联查询用户信息
        Set<Long> userIds = interfaceInfoList.stream()
                .map(InterfaceInfo::getUserId)
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
        return interfaceInfoList.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            Long userId = interfaceInfo.getUserId();
            if (userId != null && userId > 0) {
                interfaceInfoVO.setUser(finalUserVOMap.get(userId));
            }
            return interfaceInfoVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean onlineInterfaceInfo(long id) {
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断接口是否可用
        // TODO: 实际需要请求接口进行测试是否可用
        
        // 仅本地测试
        if (interfaceInfo.getStatus() == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口已经处于上线状态");
        }
        
        // 修改接口状态为上线
        interfaceInfo.setStatus(1);
        return this.updateById(interfaceInfo);
    }

    @Override
    public boolean offlineInterfaceInfo(long id) {
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        if (interfaceInfo.getStatus() == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口已经处于下线状态");
        }
        
        // 修改接口状态为下线
        interfaceInfo.setStatus(0);
        return this.updateById(interfaceInfo);
    }

    @Override
    public Object invokeInterfaceInfo(long id, String userRequestParams) {
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        if (interfaceInfo.getStatus() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }
        
        // TODO: 实际需要调用第三方接口
        
        // 仅本地测试
        if (StrUtil.isBlank(userRequestParams)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        // 模拟调用
        // 需要根据实际情况编写调用逻辑
        String result = "模拟接口调用: " + userRequestParams;
        return result;
    }
} 