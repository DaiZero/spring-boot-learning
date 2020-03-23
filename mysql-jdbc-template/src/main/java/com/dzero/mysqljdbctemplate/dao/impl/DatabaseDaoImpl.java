package com.dzero.mysqljdbctemplate.dao.impl;

import com.dzero.mysqljdbctemplate.config.DataSourceConfig;
import com.dzero.mysqljdbctemplate.dao.DatabaseDao;
import com.dzero.mysqljdbctemplate.datasource.DataSourceManager;
import com.dzero.mysqljdbctemplate.dto.DatabaseDTO;
import com.dzero.mysqljdbctemplate.mapper.DatabaseMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * DatabaseDaoImpl
 *
 * @author DZero
 * @date 2020/3/23 23:07
 */
public class DatabaseDaoImpl implements DatabaseDao {

     DataSource dataSource;
     JdbcTemplate jdbcTemplate;

     DatabaseDaoImpl(DataSourceConfig config){
        dataSource= DataSourceManager.getDataSource(config);
        jdbcTemplate= DataSourceManager.getTemplate(dataSource);
    }

    @Override
    public List<DatabaseDTO> getDatabases() {
        return jdbcTemplate.query("show DataBases;",new DatabaseMapper());
    }
}
