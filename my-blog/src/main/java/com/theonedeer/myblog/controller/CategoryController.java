package com.theonedeer.myblog.controller;

import com.theonedeer.myblog.common.Result;
import com.theonedeer.myblog.dto.CategoryDTO;
import com.theonedeer.myblog.dto.CategoryRequest;
import com.theonedeer.myblog.service.CategoryService;
import com.theonedeer.myblog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtUtil jwtUtil;

    /**
     * 创建分类
     */
    @PostMapping
    public Result<CategoryDTO> createCategory(@RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
        return Result.success("创建成功", categoryService.createCategory(request, userId));
    }

    /**
     * 获取分类列表
     */
    @GetMapping
    public Result<List<CategoryDTO>> getCategoryList(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return Result.success(categoryService.getCategoryList(userId));
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
        return Result.success("更新成功", categoryService.updateCategory(id, request, userId));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        categoryService.deleteCategory(id, userId);
        return Result.success("删除成功");
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
