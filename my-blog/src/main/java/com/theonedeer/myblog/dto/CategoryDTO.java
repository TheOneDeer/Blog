package com.theonedeer.myblog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分类DTO
 */
@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
