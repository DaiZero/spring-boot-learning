package com.dzero.mysqljdbctemplate.dao;

import com.dzero.mysqljdbctemplate.dto.DatabaseDTO;

import java.util.List;

/**
 * DatabaseDao
 *
 * @author DZero
 * @date 2020/3/23 23:05
 */
public interface DatabaseDao {
    /**
     * 获取数据库信息
     * @return
     */
    List<DatabaseDTO> getDatabases();
}
