package com.zxp.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户修改密码请求
 */
@Data
public class UserUpdatePasswordRequest implements Serializable {

    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

    private static final long serialVersionUID = 1L;
} 