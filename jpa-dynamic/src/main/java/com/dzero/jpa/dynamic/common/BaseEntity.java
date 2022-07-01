package com.dzero.jpa.dynamic.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 基础实体类
 *
 * @author daizedong
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /** ID */
  @Id
  @GeneratedValue(generator = "snowflakeGenerator")
  @GenericGenerator(
      name = "snowflakeGenerator", strategy = "com.dzero.jpa.dynamic.config.SnowflakeGenerator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  /** 创建人 */
  @Column
  @CreatedBy
  private String createBy;

  /** 创建时间 */
  @Column(name = "create_time",columnDefinition = "DATETIME")
  @CreatedDate
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  /** 更新人 */
  @Column
  @LastModifiedBy
  private String updateBy;

  /** 更新时间 */
  @Column(name = "update_time",columnDefinition = "DATETIME")
  @LastModifiedDate
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;

  /** 所属部门 */
  @Column
  private String sysOrgCode;

  @Column
  @Version
  private Integer revision;

  /** 是否已删除 0：未删除，1：已删除 */
  @Column
  private Integer delFlag;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    BaseEntity that = (BaseEntity) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
