package com.dzero.wf.camunda.demo.service.impl;

import com.dzero.wf.camunda.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户服务的实现
 *
 * @author DaiZedong
 */
@Service("UserService")
@Slf4j
public class UserServiceImpl implements UserService {
    @Override
    public String getLoginUser() {
        log.info("成功调用UserService.getLoginUser()");
        return "login-admin-001";
    }
}
