package com.dzero.wf.camunda.demo;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class WfCamundaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WfCamundaDemoApplication.class, args);
    }
}
