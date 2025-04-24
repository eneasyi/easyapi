package com.zxp.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户封禁请求
 */
@Data
public class UserBanRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
} 