package com.theonedeer.myblog.dto;

import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {
    private String token;
    private Long expiresIn;
}
