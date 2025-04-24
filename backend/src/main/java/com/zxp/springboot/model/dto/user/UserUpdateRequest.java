package com.zxp.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新请求
 *
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 常驻城市
     */
    private String userCity;

    /**
     * 用户性别
     */
    private String userGender;

    /**
     * 用户等级
     */
    private Integer userLevel;

    /**
     * 用户经验值
     */
    private Long userExp;

    /**
     * 用户积分
     */
    private Long userPoints;

    private static final long serialVersionUID = 1L;
}