package com.theonedeer.myblog.mapper;

import com.theonedeer.myblog.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {
    
    User findById(Long id);
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    int insert(User user);
    
    int update(User user);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
