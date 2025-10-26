package com.theonedeer.myblog.controller;

import com.theonedeer.myblog.common.Result;
import com.theonedeer.myblog.dto.SearchResponse;
import com.theonedeer.myblog.service.DocumentService;
import com.theonedeer.myblog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final DocumentService documentService;
    private final JwtUtil jwtUtil;

    /**
     * 搜索文档
     */
    @GetMapping
    public Result<SearchResponse> search(
            @RequestParam String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return Result.success(documentService.searchDocuments(userId, keyword, page, pageSize));
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
