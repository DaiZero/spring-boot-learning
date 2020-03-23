package com.dzero.mysqljdbctemplate.mapper;

import com.dzero.mysqljdbctemplate.dto.DatabaseDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DataBaseMapper
 *
 * @author DZero
 * @date 2020/3/23 22:42
 */
public class DatabaseMapper implements RowMapper<DatabaseDTO> {
    @Override
    public DatabaseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        DatabaseDTO db = new DatabaseDTO();
        db.setDbName(rs.getString("Database"));
        return db;
    }
}
