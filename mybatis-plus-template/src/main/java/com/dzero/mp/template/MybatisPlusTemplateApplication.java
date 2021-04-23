package com.dzero.mp.template;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dzero.mp.template")
public class MybatisPlusTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisPlusTemplateApplication.class, args);
	}

}
