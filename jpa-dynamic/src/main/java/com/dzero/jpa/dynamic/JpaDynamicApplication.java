package com.dzero.jpa.dynamic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.dzero.jpa.dynamic.repository")
@EntityScan("com.dzero.jpa.dynamic.entity")
@SpringBootApplication
public class JpaDynamicApplication {
    public static void main(String[] args) {
        SpringApplication.run(JpaDynamicApplication.class, args);
    }
}
