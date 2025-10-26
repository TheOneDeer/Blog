package com.theonedeer.myblog.controller;

import com.theonedeer.myblog.common.Result;
import com.theonedeer.myblog.dto.LoginRequest;
import com.theonedeer.myblog.dto.LoginResponse;
import com.theonedeer.myblog.dto.RegisterRequest;
import com.theonedeer.myblog.dto.UserDTO;
import com.theonedeer.myblog.service.UserService;
import com.theonedeer.myblog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody RegisterRequest request) {
        return Result.success("注册成功", userService.register(request));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        return Result.success("登录成功", userService.login(request));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserDTO> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            return Result.success(userService.getUserInfo(userId));
        }
        return Result.error(401, "未登录");
    }
}
