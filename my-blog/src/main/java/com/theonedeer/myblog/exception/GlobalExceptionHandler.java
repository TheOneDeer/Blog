package com.theonedeer.myblog.exception;

import com.theonedeer.myblog.common.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResponseEntity.ok(Result.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getDefaultMessage())
            .reduce((a, b) -> a + ", " + b)
            .orElse("参数校验失败");
        log.error("参数校验异常: {}", message);
        return ResponseEntity.ok(Result.error(400, message));
    }

    /**
     * 处理JWT过期异常
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Result> handleExpiredJwtException(ExpiredJwtException e) {
        log.warn("JWT token已过期: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Result.error(401, "登录已过期，请重新登录"));
    }

    /**
     * 处理其他JWT相关异常（签名错误、格式错误等）
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Result> handleJwtException(JwtException e) {
        log.warn("JWT token验证失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Result.error(401, "Token无效，请重新登录"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.ok(Result.error(500, "系统异常，请稍后重试"));
    }
}
