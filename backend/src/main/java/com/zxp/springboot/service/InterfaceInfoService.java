package com.zxp.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxp.springboot.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.zxp.springboot.model.entity.InterfaceInfo;
import com.zxp.springboot.model.vo.InterfaceInfoVO;

import java.util.List;

/**
 * 接口信息服务
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 获取接口信息封装
     *
     * @param interfaceInfo
     * @return
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo);

    /**
     * 获取接口信息封装列表
     *
     * @param interfaceInfoList
     * @return
     */
    List<InterfaceInfoVO> getInterfaceInfoVO(List<InterfaceInfo> interfaceInfoList);

    /**
     * 上线接口
     *
     * @param id
     * @return
     */
    boolean onlineInterfaceInfo(long id);

    /**
     * 下线接口
     *
     * @param id
     * @return
     */
    boolean offlineInterfaceInfo(long id);

    /**
     * 调用接口
     *
     * @param id
     * @param userRequestParams
     * @return
     */
    Object invokeInterfaceInfo(long id, String userRequestParams);
} 