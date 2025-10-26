package com.theonedeer.myblog.dto;

import lombok.Data;
import java.util.List;

/**
 * 文档列表响应DTO
 */
@Data
public class DocumentListResponse {
    private Long total;
    private Integer page;
    private Integer pageSize;
    private List<DocumentDTO> documents;
}
