package com.dzero.jpa.dynamic.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@Slf4j
@Component
public class SnowflakeGenerator implements IdentifierGenerator {
  @Override
  public Serializable generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o)
      throws HibernateException {
    return snowflakeId(1,2);
  }
  public synchronized long snowflakeId(long workerId, long datacenterId) {
    Snowflake snowflake2 = IdUtil.getSnowflake(workerId, datacenterId);
    return snowflake2.nextId();
  }
}
