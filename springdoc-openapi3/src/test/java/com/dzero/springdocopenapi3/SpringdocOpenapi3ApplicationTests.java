package com.dzero.springdocopenapi3;

import com.dzero.springdocopenapi3.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringdocOpenapi3ApplicationTests {

    @Autowired
    private TestService testService;

    @Test
    void contextLoads() {
        testService.sayHello();
    }

}
