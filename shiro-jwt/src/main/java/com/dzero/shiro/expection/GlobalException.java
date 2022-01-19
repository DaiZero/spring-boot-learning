package com.dzero.shiro.expection;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局的异常配置
 * 只能拦截RestController调用层里的异常信息
 */
@RestControllerAdvice
public class GlobalException {
}
