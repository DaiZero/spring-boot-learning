package com.dzero.mysqljdbctemplate.datasource;

import com.dzero.mysqljdbctemplate.config.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


/**
 * @author DZero
 * @date 2020/3/23 17:11
 */
public class DataSourceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceManager.class);

    /**
     * 获取数据源
     * @param dsConfig 数据源配置
     * @return
     */
    public DataSource getDataSource(DataSourceConfig dsConfig){
        LOGGER.info("开始");
        HikariConfig config=new HikariConfig();
        config.setJdbcUrl(dsConfig.getUrl());
        config.setUsername(dsConfig.getUserName());
        config.setPassword(dsConfig.getPassword());
        DataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    public JdbcTemplate getTemplateData(DataSource ds){
        JdbcTemplate template=new JdbcTemplate(ds);
        return template;
    }

}

