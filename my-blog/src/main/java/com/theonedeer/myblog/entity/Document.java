package com.theonedeer.myblog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文档实体
 */
@Data
public class Document {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String fileName;
    private String fileOriginalName;
    private String filePath;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String thumbnail;
    private Long categoryId;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
