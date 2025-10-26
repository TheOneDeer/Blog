package com.theonedeer.myblog.mapper;

import com.theonedeer.myblog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper {
    
    Category findById(Long id);
    
    List<Category> findByUserId(Long userId);
    
    int insert(Category category);
    
    int update(Category category);
    
    int delete(Long id);
}
