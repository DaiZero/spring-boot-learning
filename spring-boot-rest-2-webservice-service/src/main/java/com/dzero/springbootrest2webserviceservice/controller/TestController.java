package com.dzero.springbootrest2webserviceservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jws.WebMethod;

/**
 * @author dzero
 * @date 2020/7/20 16:50
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * hello方法
     *
     * @param name 名称
     * @return hello加名称
     */
    @WebMethod
    @GetMapping("/hello")
    String hello(@RequestParam String name) {
        return "Hello " + name;
    }

}
