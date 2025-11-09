package com.theonedeer.myblog.mapper;

import com.theonedeer.myblog.entity.Document;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档Mapper
 */
@Mapper
public interface DocumentMapper {
    
    Document findById(Long id);
    
    List<Document> findByUserId(@Param("userId") Long userId,
                                @Param("page") Integer page,
                                @Param("pageSize") Integer pageSize);
    
    int countByUserId(Long userId);
    
    List<Document> searchByKeyword(@Param("userId") Long userId,
                                    @Param("keyword") String keyword,
                                    @Param("page") Integer page,
                                    @Param("pageSize") Integer pageSize);
    
    int countSearchResults(@Param("userId") Long userId,
                           @Param("keyword") String keyword);
    
    int insert(Document document);
    
    int update(Document document);
    
    int delete(Long id);
    
    /**
     * 增加浏览次数
     */
    int incrementViewCount(Long id);
    
    /**
     * 增加下载次数
     */
    int incrementDownloadCount(Long id);
}
