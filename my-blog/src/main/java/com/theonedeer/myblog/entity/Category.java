package com.theonedeer.myblog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分类实体
 */
@Data
public class Category {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Long parentId;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
