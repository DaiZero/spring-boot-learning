package com.dzero.mysqljdbctemplate.config;

import lombok.Data;

/**
 * @author DZero
 * @date 2020/3/23 17:15
 */
@Data
public class DataSourceConfig {

    /**
     * 驱动类的名称
     */
    private String driverClassName;

    /**
     * 链接
     */
    private String url;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;
}
