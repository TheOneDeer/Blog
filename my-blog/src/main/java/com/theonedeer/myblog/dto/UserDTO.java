package com.theonedeer.myblog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户DTO
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private LocalDateTime createdAt;
}
