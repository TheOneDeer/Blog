package com.theonedeer.myblog.dto;

import lombok.Data;

/**
 * 分类请求DTO
 */
@Data
public class CategoryRequest {
    private String name;
    private String description;
    private Long parentId;
    private Integer sortOrder;
}
