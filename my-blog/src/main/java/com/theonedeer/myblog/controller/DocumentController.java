package com.theonedeer.myblog.controller;

import com.theonedeer.myblog.common.Result;
import com.theonedeer.myblog.dto.DocumentDTO;
import com.theonedeer.myblog.dto.DocumentListResponse;
import com.theonedeer.myblog.dto.DocumentUploadRequest;
import com.theonedeer.myblog.service.DocumentService;
import com.theonedeer.myblog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档控制器
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final JwtUtil jwtUtil;

    /**
     * 上传文档
     */
    @PostMapping("/upload")
    public Result<DocumentDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            HttpServletRequest request) {
        
        Long userId = getUserIdFromToken(request);
        
        DocumentUploadRequest uploadRequest = new DocumentUploadRequest();
        uploadRequest.setTitle(title);
        uploadRequest.setDescription(description);
        uploadRequest.setCategoryId(categoryId);
        
        return Result.success("上传成功", documentService.upload(file, uploadRequest, userId));
    }

    /**
     * 获取文档列表
     */
    @GetMapping
    public Result<DocumentListResponse> getDocumentList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return Result.success(documentService.getDocumentList(userId, page, pageSize));
    }

    /**
     * 获取文档详情
     */
    @GetMapping("/{id}")
    public Result<DocumentDTO> getDocument(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return Result.success(documentService.getDocumentById(id, userId));
    }

    /**
     * 更新文档信息
     */
    @PutMapping("/{id}")
    public Result<DocumentDTO> updateDocument(
            @PathVariable Long id,
            @RequestBody DocumentUploadRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
        return Result.success("更新成功", documentService.updateDocument(id, request, userId));
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDocument(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        documentService.deleteDocument(id, userId);
        return Result.success("删除成功");
    }

    /**
     * 下载文档
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id, 
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return documentService.downloadDocument(id, userId);
    }

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未登录");
        }
        token = token.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}
