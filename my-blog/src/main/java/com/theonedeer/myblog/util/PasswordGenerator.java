package com.theonedeer.myblog.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具（用于生成admin密码）
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("加密密码: " + hashedPassword);
        
        // 测试密码
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("密码匹配: " + matches);
    }
}
