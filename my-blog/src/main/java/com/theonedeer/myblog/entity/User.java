package com.theonedeer.myblog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
