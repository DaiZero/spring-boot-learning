package com.dzero.jpa.dynamic.controller;

import com.dzero.jpa.dynamic.config.DynamicClassGenerator;
import com.dzero.jpa.dynamic.entity.UserEntity;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.query.Query;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

    @Resource
    private EntityManagerFactory entityManagerFactory;

    @GetMapping("/test")
    void testDynamicDdl2() {
        DynamicClassGenerator dynamicClassGenerator = new DynamicClassGenerator();
        String packageName = "com.dzero.jpa.dynamic";
        dynamicClassGenerator.createJpaEntity(packageName + ".entity.BookEntity", "book");

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        StandardServiceRegistry serviceRegistry =
                sessionFactory.getSessionFactoryOptions().getServiceRegistry();
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        sessionFactory.getSessionFactoryOptions();
        // 读取映射文件
        metadataSources.addAnnotatedClassName("com.dzero.jpa.dynamic.entity.BookEntity");
        Metadata metadata = metadataSources.buildMetadata();
        // 创建数据库Schema,如果不存在就创建表,存在就更新字段,不会影响已有数据
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.createOnly(EnumSet.of(TargetType.DATABASE), metadata);
        // 创建会话工厂
        SessionFactory newSessionFactory = metadata.buildSessionFactory();
        // 保存对象
        Session newSession = newSessionFactory.openSession();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> book = new HashMap<>();
            newSession.save("BookEntity", book);
        }
        // 查询所有对象
        Query query = newSession.createQuery("from BookEntity");
        List list = query.getResultList();
        System.out.println("resultList: " + list);
        // 关闭会话
        newSession.close();
    }

}
