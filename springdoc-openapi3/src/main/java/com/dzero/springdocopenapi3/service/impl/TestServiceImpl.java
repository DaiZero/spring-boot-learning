package com.dzero.springdocopenapi3.service.impl;

import com.dzero.springdocopenapi3.service.HelloService;
import com.dzero.springdocopenapi3.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TestServiceImpl
 *
 * @author DaiZedong
 */
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private List<HelloService> multiServiceList;

    @Override
    public void sayHello() {
        for (HelloService multiService : multiServiceList) {
            multiService.sayHello();
        }
    }
}
