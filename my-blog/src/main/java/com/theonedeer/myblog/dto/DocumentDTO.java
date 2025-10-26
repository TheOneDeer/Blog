package com.theonedeer.myblog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文档DTO
 */
@Data
public class DocumentDTO {
    private Long id;
    private String title;
    private String description;
    private String fileName;
    private String fileOriginalName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String thumbnail;
    private Long categoryId;
    private String categoryName;
    private Integer viewCount;
    private Integer downloadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
