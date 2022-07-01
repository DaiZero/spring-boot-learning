package com.dzero.jpa.dynamic.repository;

import com.dzero.jpa.dynamic.common.BaseRepository;
import com.dzero.jpa.dynamic.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, Long> {
}
