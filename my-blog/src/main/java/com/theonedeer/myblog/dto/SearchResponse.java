package com.theonedeer.myblog.dto;

import lombok.Data;
import java.util.List;

/**
 * 搜索结果响应DTO
 */
@Data
public class SearchResponse {
    private Long total;
    private Integer page;
    private Integer pageSize;
    private String keyword;
    private List<DocumentDTO> documents;
}
