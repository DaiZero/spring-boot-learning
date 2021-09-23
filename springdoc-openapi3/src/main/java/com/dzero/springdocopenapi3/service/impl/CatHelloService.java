package com.dzero.springdocopenapi3.service.impl;

import com.dzero.springdocopenapi3.service.HelloService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * CatHelloService
 *
 * @author DaiZedong
 */
@Service
@Order(10)
public class CatHelloService implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("I'm a Cat,Hello world!");
    }
}
