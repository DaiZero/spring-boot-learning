package com.dzero.wf.camunda.demo.service.impl;

import com.dzero.wf.camunda.demo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务的实现
 *
 * @author DaiZedong
 */
@Service("UserService")
public class UserServiceImpl implements UserService {
    @Override
    public String getLoginUser() {
        return "login-admin-001";
    }
}
