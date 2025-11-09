package com.theonedeer.myblog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件访问路径
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }

    /**
     * 配置CORS跨域支持
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置API接口的CORS
        registry.addMapping("/api/**")
                // 允许的前端地址
                .allowedOrigins("http://localhost:8080", "http://localhost:8081")
                // 允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许发送Cookie和认证信息
                .allowCredentials(true)
                // 预检请求的缓存时间（秒）
                .maxAge(3600);
        
        // 配置文件访问路径的CORS
        registry.addMapping("/files/**")
                // 允许的前端地址
                .allowedOrigins("http://localhost:8080", "http://localhost:8081")
                // 允许的HTTP方法（文件访问主要是GET）
                .allowedMethods("GET", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许发送Cookie和认证信息
                .allowCredentials(true)
                // 预检请求的缓存时间（秒）
                .maxAge(3600);
    }
}
