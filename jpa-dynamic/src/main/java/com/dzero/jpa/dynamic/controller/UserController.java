package com.dzero.jpa.dynamic.controller;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dzero.jpa.dynamic.config.DynamicClassGenerator;
import com.dzero.jpa.dynamic.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.utility.SchemaCrawlerUtility;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.named;

@Slf4j
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
            log.error("", e);
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
            book.put("id", i);
            book.put("name", "book" + i);
            newSession.save("BookEntity", book);
        }
        // 查询所有对象
        Query query = newSession.createQuery("from BookEntity");
        List list = query.getResultList();
        log.info("resultList: " + list);
        // 关闭会话
        newSession.close();
    }

    @Resource
    private ApplicationContext applicationContext;

    @GetMapping("/test2/list")
    void dynamicReposTestList() {
        try {
            Class<?> repoClass = Thread.currentThread().getContextClassLoader().loadClass("com.dzero.jpa.dynamic.repository.BookRepository");
            Object result = repoClass.getMethod("findAll")
                    .invoke(applicationContext.getBean(repoClass));
            log.info("result: " + result);
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                 InvocationTargetException e) {
            log.error("", e);
        }
    }

    @GetMapping("/test2/add")
    void dynamicReposTestAdd() {
        try {
            Class<?> entityClass = Thread.currentThread().getContextClassLoader().loadClass("com.dzero.jpa.dynamic.entity.BookEntity");
            Class<?> repoClass = Thread.currentThread().getContextClassLoader().loadClass("com.dzero.jpa.dynamic.repository.BookRepository");
            JSONObject book = JSONUtil.createObj();
            book.set("id", LocalDateTime.now().getNano());
            book.set("name", "book10");
            Method method = repoClass.getMethod("save", new Class[] { entityClass.getClass() });

            Object repoClassObj = applicationContext.getBean(repoClass);
            Object result = method.invoke(repoClassObj, book);
            log.info("result: " + result);
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                 InvocationTargetException e) {
            log.error("", e);
        }
    }

    @GetMapping("/test2")
    void dynamicReposTest() {
        DynamicClassGenerator dynamicClassGenerator = new DynamicClassGenerator();
        String packageName = "com.dzero.jpa.dynamic";
        Optional<Class<?>> entityClass = dynamicClassGenerator.createJpaEntity(packageName + ".entity.BookEntity", "book");
        Optional<Class<?>> repoClass =
                dynamicClassGenerator.createJpaRepository(entityClass.get(), packageName + ".repository.BookRepository");
        removeExistingAndAddNewEntityBean(entityClass.get());
        removeExistingAndAddNewRepoBean(repoClass.get());
    }

    public void removeExistingAndAddNewEntityBean(Class<?> entityClass) {
        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
        try {
            registry.removeBeanDefinition(entityClass.getSimpleName());
        } catch (NoSuchBeanDefinitionException e) {
            System.out.println("No Such Bean Definition");
        }
        //create newBeanObj through GenericBeanDefinition
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition().addConstructorArgValue(entityClass);
        registry.registerBeanDefinition(entityClass.getSimpleName(), beanDefinitionBuilder.getBeanDefinition());
//        JpaRepositoryFactoryBean jpaRepositoryFactoryBean= applicationContext.getBean(jpaRepositoryClass.getSimpleName(), JpaRepositoryFactoryBean.class);
    }

    public void removeExistingAndAddNewRepoBean(Class<?> jpaRepositoryClass) {
        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
        try {
            registry.removeBeanDefinition(jpaRepositoryClass.getSimpleName());
        } catch (NoSuchBeanDefinitionException e) {
            System.out.println("No Such Bean Definition");
        }
        //create newBeanObj through GenericBeanDefinition
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(JpaRepositoryFactoryBean.class).addConstructorArgValue(jpaRepositoryClass);
        registry.registerBeanDefinition(jpaRepositoryClass.getSimpleName(), beanDefinitionBuilder.getBeanDefinition());
//        JpaRepositoryFactoryBean jpaRepositoryFactoryBean= applicationContext.getBean(jpaRepositoryClass.getSimpleName(), JpaRepositoryFactoryBean.class);
    }

    @Resource
    private DataSource dataSource;

    @GetMapping("/scheme")
    void getScheme() {
        final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
        final Catalog catalog;
        try {
            catalog = SchemaCrawlerUtility.getCatalog(dataSource.getConnection(), options);
            for (final Schema schema : catalog.getSchemas()) {
                if ("PUBLIC".equals(schema.getName())) {
                    for (final Table table : catalog.getTables(schema)) {
                        System.out.println("【表】" + table);
                        System.out.println("【表名】" + table.getName());
                        for (final Column column : table.getColumns()) {
                            Object type = column.getType();
                            System.out.println("    【字段名】" + column.getName());
                            System.out.println("    【字段类型】" + type);
                            System.out.println("    【字段默认值】" + column.getDefaultValue());
                            System.out.println("    【是否为空】" + column.getType().isNullable());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
