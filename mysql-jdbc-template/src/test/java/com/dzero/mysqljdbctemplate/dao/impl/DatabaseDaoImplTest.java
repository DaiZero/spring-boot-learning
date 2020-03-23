package com.dzero.mysqljdbctemplate.dao.impl;

import com.dzero.mysqljdbctemplate.config.DataSourceConfig;
import com.dzero.mysqljdbctemplate.dao.DatabaseDao;
import com.dzero.mysqljdbctemplate.dto.DatabaseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseDaoImplTest {

    @Test
    void getDatabases() {
        DataSourceConfig  config=new DataSourceConfig();
        config.setUrl("jdbc:mysql://192.168.13.108:3306?useSSL=false&serverTimezone=UTC");
        config.setUserName("root");
        config.setPassword("123456");
        DatabaseDao databaseDao=new DatabaseDaoImpl(config);
        List<DatabaseDTO> databaseDaoList= databaseDao.getDatabases();
        assertTrue(databaseDaoList.size()>0);
    }
}