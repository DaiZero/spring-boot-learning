package com.dzero.jpa.dynamic.entity;

import com.dzero.jpa.dynamic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_user")
public class UserEntity extends BaseEntity {

    @Column
    private String userName;

    @Column
    private int age;

    public String sayHelloFoo() {
        return "Hello in Foo!";
    }
}
