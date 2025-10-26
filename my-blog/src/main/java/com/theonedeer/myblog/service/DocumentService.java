package com.theonedeer.myblog.service;

import com.theonedeer.myblog.dto.DocumentDTO;
import com.theonedeer.myblog.dto.DocumentListResponse;
import com.theonedeer.myblog.dto.DocumentUploadRequest;
import com.theonedeer.myblog.dto.SearchResponse;
import com.theonedeer.myblog.entity.Document;
import com.theonedeer.myblog.exception.BusinessException;
import com.theonedeer.myblog.mapper.DocumentMapper;
import com.theonedeer.myblog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文档服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final JwtUtil jwtUtil;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    /**
     * 上传文档
     */
    public DocumentDTO upload(MultipartFile file, DocumentUploadRequest request, Long userId) {
        // 文件校验
        validateFile(file);

        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new BusinessException("创建上传目录失败");
            }
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
        String fileName = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(fileName);
        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new BusinessException("文件保存失败");
        }

        // 创建文档记录
        Document document = new Document();
        document.setUserId(userId);
        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setFileName(fileName);
        document.setFileOriginalName(originalFilename);
        document.setFilePath(filePath.toString());
        document.setFileUrl(urlPrefix + fileName);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setCategoryId(request.getCategoryId());
        document.setViewCount(0);
        document.setDownloadCount(0);
        document.setStatus(1);

        documentMapper.insert(document);

        log.info("文档上传成功: {}", document.getTitle());

        return convertToDTO(document);
    }

    /**
     * 获取文档详情
     */
    public DocumentDTO getDocumentById(Long id, Long userId) {
        Document document = documentMapper.findById(id);
        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }

        // 检查是否有权限访问
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问");
        }

        return convertToDTO(document);
    }

    /**
     * 获取文档列表
     */
    public DocumentListResponse getDocumentList(Long userId, Integer page, Integer pageSize) {
        page = page != null && page > 0 ? page : 1;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : 10;
        
        int offset = (page - 1) * pageSize;
        
        Long total = (long) documentMapper.countByUserId(userId);
        List<Document> documents = documentMapper.findByUserId(userId, offset, pageSize);
        
        DocumentListResponse response = new DocumentListResponse();
        response.setTotal(total);
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setDocuments(documents.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
        
        return response;
    }

    /**
     * 更新文档信息
     */
    public DocumentDTO updateDocument(Long id, DocumentUploadRequest request, Long userId) {
        Document document = documentMapper.findById(id);
        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }

        if (!document.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权修改");
        }

        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setCategoryId(request.getCategoryId());
        
        documentMapper.update(document);
        
        log.info("文档更新成功: {}", document.getTitle());
        
        return convertToDTO(document);
    }

    /**
     * 删除文档
     */
    public void deleteDocument(Long id, Long userId) {
        Document document = documentMapper.findById(id);
        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }

        if (!document.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除");
        }

        documentMapper.delete(id);
        
        log.info("文档删除成功: {}", document.getTitle());
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        // 文件大小限制（100MB）
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new BusinessException(400, "文件大小不能超过100MB");
        }

        // 文件类型限制
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!"pdf".equals(extension) && !"docx".equals(extension) && 
                !"doc".equals(extension) && !"txt".equals(extension)) {
                throw new BusinessException(400, "不支持的文件类型，仅支持PDF、DOCX、DOC、TXT");
            }
        }
    }

    /**
     * 搜索文档
     */
    public SearchResponse searchDocuments(Long userId, String keyword, Integer page, Integer pageSize) {
        page = page != null && page > 0 ? page : 1;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : 10;
        
        int offset = (page - 1) * pageSize;
        
        Long total = (long) documentMapper.countSearchResults(userId, keyword);
        List<Document> documents = documentMapper.searchByKeyword(userId, keyword, offset, pageSize);
        
        SearchResponse response = new SearchResponse();
        response.setTotal(total);
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setKeyword(keyword);
        response.setDocuments(documents.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
        
        return response;
    }

    /**
     * 转换Document为DocumentDTO
     */
    private DocumentDTO convertToDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setDescription(document.getDescription());
        dto.setFileName(document.getFileName());
        dto.setFileOriginalName(document.getFileOriginalName());
        dto.setFileUrl(document.getFileUrl());
        dto.setFileType(document.getFileType());
        dto.setFileSize(document.getFileSize());
        dto.setThumbnail(document.getThumbnail());
        dto.setCategoryId(document.getCategoryId());
        dto.setViewCount(document.getViewCount());
        dto.setDownloadCount(document.getDownloadCount());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        return dto;
    }
}
