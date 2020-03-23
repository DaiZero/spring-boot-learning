package com.dzero.mysqljdbctemplate.config;

import lombok.Data;

/**
 * @author daiZedong
 * @date 2020/3/23 17:15
 */
@Data
public class DataSourceConfig {

    private String driverClassName;

    private String url;

    private String userName;

    private String password;
}
