package com.dzero.redis.bloom.filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedisBloomFilterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisBloomFilterApplication.class, args);
	}

}
