package com.dzero.jpa.dynamic.common;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Serializable>
    extends CrudRepository<T, ID> {
  @NotNull
  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.delFlag = 0")
  Iterable<T> findAll();

  @NotNull
  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.id in ?1 and e.delFlag = 0")
  Iterable<T> findAllById(@NotNull Iterable<ID> iterable);

  @NotNull
  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.id = ?1 and e.delFlag = 0")
  Optional<T> findById(@NotNull ID id);

  @Override
  @Transactional(readOnly = true)
  @Query("select count(e) from #{#entityName} e where e.delFlag = 0")
  long count();

  @Override
  @Transactional(readOnly = true)
  default boolean existsById(@NotNull ID id) {
    return findById(id).isPresent();
  }

  @Query("update #{#entityName} e set e.delFlag = 1 where e.id = ?1")
  @Transactional
  @Modifying
  void logicDelete(ID id);

  @Transactional
  default void logicDelete(T entity) {
    logicDelete((ID) entity.getId());
  }

  @Transactional
  default void logicDelete(Iterable<? extends T> entities) {
    entities.forEach(entity -> logicDelete((ID) entity.getId()));
  }

  @Query("update #{#entityName} e set e.delFlag = 1 ")
  @Transactional
  @Modifying
  void logicDeleteAll();
}
