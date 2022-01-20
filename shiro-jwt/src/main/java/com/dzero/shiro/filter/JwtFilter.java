package com.dzero.shiro.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 过滤器
 * @author dzero
 */
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 是否允许访问
     *
     * @param request 请求
     * @param response 回复
     * @param mappedValue
     * @return bool
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return super.isAccessAllowed(request, response, mappedValue);
    }

    /**
     * 执行登录
     *
     * @param request 请求
     * @param response 回复
     * @return 是否成功
     * @throws Exception 异常
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        return super.executeLogin(request, response);
    }

    /**
     * 是否接受登录
     *
     * @param authzHeader 验证头
     * @return
     */
    @Override
    protected boolean isLoginAttempt(String authzHeader) {
        return super.isLoginAttempt(authzHeader);
    }

    /**
     * 是否接受登录
     *
     * @param request 请求
     * @param response 回复
     * @return
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        return super.isLoginAttempt(request, response);
    }
}
