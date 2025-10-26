package com.theonedeer.myblog.dto;

import lombok.Data;

/**
 * 文档上传请求DTO
 */
@Data
public class DocumentUploadRequest {
    private String title;
    private String description;
    private Long categoryId;
}
