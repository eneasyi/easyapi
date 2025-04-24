package com.zxp.springboot.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 */
@Data
public class UserVO implements Serializable {

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
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户等级
     */
    private Integer userLevel;

    /**
     * 用户经验值
     */
    private Long userExp;

    /**
     * 常驻城市
     */
    private String userCity;

    /**
     * 用户性别
     */
    private String userGender;

    /**
     * 用户积分
     */
    private Long userPoints;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户邮箱
     */
    private String userEmail;

    private static final long serialVersionUID = 1L;
}