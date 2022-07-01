package com.dzero.jpa.dynamic.controller;

import com.dzero.jpa.dynamic.entity.UserEntity;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

import static net.bytebuddy.matcher.ElementMatchers.named;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    @GetMapping("/change")
    public void change() {
        ByteBuddyAgent.install();
        try {
            new ByteBuddy()
                    .redefine(UserEntity.class)
                    .method(named("sayHelloFoo"))
                    .intercept(FixedValue.value("Hello Foo Redefined"))
                    .make()
                    .load(
                            UserEntity.class.getClassLoader(),
                            ClassReloadingStrategy.fromInstalledAgent())
                    .saveIn(new File("target/classes"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/hello")
    public String hello() {
        UserEntity f = new UserEntity();
        return f.sayHelloFoo();
    }

}
