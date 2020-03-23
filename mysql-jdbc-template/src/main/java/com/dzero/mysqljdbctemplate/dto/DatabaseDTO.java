package com.dzero.mysqljdbctemplate.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据库
 *
 * @author DZero
 * @date 2020/3/23 22:45
 */
@Data
public class DatabaseDTO {
    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 默认校对规则
     */
    private String defaultCollationName;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
