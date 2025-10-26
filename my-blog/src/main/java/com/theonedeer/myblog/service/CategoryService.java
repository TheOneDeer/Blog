package com.theonedeer.myblog.service;

import com.theonedeer.myblog.dto.CategoryDTO;
import com.theonedeer.myblog.dto.CategoryRequest;
import com.theonedeer.myblog.entity.Category;
import com.theonedeer.myblog.exception.BusinessException;
import com.theonedeer.myblog.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryMapper categoryMapper;

    /**
     * 创建分类
     */
    public CategoryDTO createCategory(CategoryRequest request, Long userId) {
        // 参数校验
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BusinessException("分类名称不能为空");
        }

        Category category = new Category();
        category.setUserId(userId);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus(1);

        categoryMapper.insert(category);

        log.info("分类创建成功: {}", request.getName());

        return convertToDTO(category);
    }

    /**
     * 获取分类列表
     */
    public List<CategoryDTO> getCategoryList(Long userId) {
        List<Category> categories = categoryMapper.findByUserId(userId);
        return categories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 更新分类
     */
    public CategoryDTO updateCategory(Long id, CategoryRequest request, Long userId) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        if (!category.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权修改");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder());

        categoryMapper.update(category);

        log.info("分类更新成功: {}", request.getName());

        return convertToDTO(category);
    }

    /**
     * 删除分类
     */
    public void deleteCategory(Long id, Long userId) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        if (!category.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除");
        }

        categoryMapper.delete(id);

        log.info("分类删除成功: {}", category.getName());
    }

    /**
     * 转换Category为CategoryDTO
     */
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParentId());
        dto.setSortOrder(category.getSortOrder());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
