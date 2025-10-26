package com.theonedeer.myblog.service;

import com.theonedeer.myblog.dto.LoginRequest;
import com.theonedeer.myblog.dto.LoginResponse;
import com.theonedeer.myblog.dto.RegisterRequest;
import com.theonedeer.myblog.dto.UserDTO;
import com.theonedeer.myblog.entity.User;
import com.theonedeer.myblog.exception.BusinessException;
import com.theonedeer.myblog.mapper.UserMapper;
import com.theonedeer.myblog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     */
    public UserDTO register(RegisterRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().length() < 3 || request.getUsername().length() > 50) {
            throw new BusinessException("用户名长度必须在3-50字符之间");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new BusinessException("密码长度至少8位");
        }
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new BusinessException("邮箱格式不正确");
        }

        // 检查用户名是否存在
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException(1002, "用户名已存在");
        }

        // 检查邮箱是否存在
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException(1002, "邮箱已存在");
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(request.getEmail());
        user.setStatus(1);

        userMapper.insert(user);

        log.info("用户注册成功: {}", request.getUsername());

        return convertToDTO(user);
    }

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查找用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(1001, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(1001, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(1004, "用户已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId());

        log.info("用户登录成功: {}", request.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpiresIn(3600L);

        return response;
    }

    /**
     * 获取用户信息
     */
    public UserDTO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(1001, "用户不存在");
        }
        return convertToDTO(user);
    }

    /**
     * 转换User为UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
