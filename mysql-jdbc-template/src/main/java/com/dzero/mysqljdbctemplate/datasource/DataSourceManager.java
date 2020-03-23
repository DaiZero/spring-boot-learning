package com.dzero.mysqljdbctemplate.datasource;

import com.dzero.mysqljdbctemplate.config.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


/**
 * 数据源管理
 * @author DZero
 * @date 2020/3/23 17:11
 */
public class DataSourceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceManager.class);

    /**
     * 获取数据源
     * @param dsConfig 数据源配置
     * @return 数据源
     */
    public static DataSource getDataSource(DataSourceConfig dsConfig){
        LOGGER.info("开始");
        HikariConfig config=new HikariConfig();
       String driveClassName= dsConfig.getDriverClassName();
        if(driveClassName==null||driveClassName.isEmpty()){
            driveClassName= "com.mysql.cj.jdbc.Driver";
        }
        config.setDriverClassName(driveClassName);
        config.setJdbcUrl(dsConfig.getUrl());
        config.setUsername(dsConfig.getUserName());
        config.setPassword(dsConfig.getPassword());
        DataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    public static JdbcTemplate getTemplate(DataSource ds){
        JdbcTemplate template=new JdbcTemplate(ds);
        return template;
    }

    public static JdbcTemplate getTemplate(DataSourceConfig dsConfig){
        DataSource ds =getDataSource(dsConfig);
        JdbcTemplate template=new JdbcTemplate(ds);
        return template;
    }

}

