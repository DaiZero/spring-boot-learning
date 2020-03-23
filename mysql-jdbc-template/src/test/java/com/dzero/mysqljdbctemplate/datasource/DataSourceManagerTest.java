package com.dzero.mysqljdbctemplate.datasource;

import com.dzero.mysqljdbctemplate.config.DataSourceConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSourceManagerTest {

    static DataSourceConfig  config;
    static DataSource dataSource;
    static JdbcTemplate jdbcTemplate;

     DataSourceManagerTest() {
    }

    @BeforeAll
    static void initAll() {
        config=new DataSourceConfig();
        config.setUrl("jdbc:mysql://192.168.13.108:3306?useSSL=false&serverTimezone=UTC");

        config.setUserName("root");
        config.setPassword("123456");
        dataSource= DataSourceManager.getDataSource(config);
        jdbcTemplate= DataSourceManager.getTemplate(dataSource);
    }


    @Test
    void getDataSourceIsHikariDataSource() {
        DataSource dataSource2= DataSourceManager.getDataSource(config);
        assertTrue(dataSource2 instanceof HikariDataSource);
    }

    @Test
    void getTemplateData() {
        JdbcTemplate jdbcTemplate2= DataSourceManager.getTemplate(dataSource);
        List<Map<String, Object>> mapList= jdbcTemplate2.queryForList("select * from test.user ");
        System.out.println(mapList.size());
    }

    @Test
    void getDataBaseStatus(){
        List<Map<String, Object>> mapList= jdbcTemplate.queryForList("show status");
        assertTrue(mapList.size()>0);
    }

    @Test
    void getDataBaseVariables(){
        List<Map<String, Object>> mapList= jdbcTemplate.queryForList("show VARIABLES");
        assertTrue(mapList.size()>0);
    }

    @Test
    void getDataBases(){
        List<Map<String, Object>> mapList= jdbcTemplate.queryForList("show DataBases");
        assertTrue(mapList.size()>0);
    }
}