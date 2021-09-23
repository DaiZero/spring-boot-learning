package com.dzero.springdocopenapi3.service.impl;

import com.dzero.springdocopenapi3.service.HelloService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * DogHelloService
 *
 * @author DaiZedong
 */
@Service
@Order(3)
public class DogHelloService implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("I'm a Dog,Hello world!");
    }
}
