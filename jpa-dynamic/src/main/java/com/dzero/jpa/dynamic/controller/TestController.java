package com.dzero.jpa.dynamic.controller;

import com.dzero.jpa.dynamic.common.ObjectUtil;
import com.dzero.jpa.dynamic.config.DynamicClassGenerator;
import com.dzero.jpa.dynamic.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
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
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.named;

@Slf4j
@RestController
@RequestMapping("/v1/test")
public class TestController {
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private EntityManagerFactory entityManagerFactory;
    @Resource
    private DataSource dataSource;

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

    @GetMapping("/book/init")
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

    @GetMapping("/book/add")
    Object dynamicReposTestAdd() {
        try {
            Class<?> entityClass = Thread.currentThread().getContextClassLoader().loadClass("com.dzero.jpa.dynamic.entity.BookEntity");
            Class<?> repoClass = Thread.currentThread().getContextClassLoader().loadClass("com.dzero.jpa.dynamic.repository.BookRepository");
            for (Method method : repoClass.getMethods()) {
                log.info("方法：" + method.getName());
                Type[] paramTypeArr = method.getParameterTypes();
                for (int i = 0; i < paramTypeArr.length; i++) {
                    log.info("参数" + (i + 1) + "类型：" + paramTypeArr[i] + " ; ");
                }
            }

//            // 获取构造方法
//            for (Constructor<?> constructor : entityClass.getConstructors()) {
//                log.info("构造方法：" + constructor.getName());
//            }
//
//            Map<String, Object> bookMap = new HashMap<>();
//            bookMap.put("id",System.currentTimeMillis());
//            bookMap.put("name","book10");
//            bookMap.put("revision",1);
//            Object bookObj = null;
//            try {
//                bookObj = mapToObject(bookMap,entityClass);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }

            Method method = repoClass.getMethod("existsById", Object.class);
            Object repoClassObj = applicationContext.getBean(repoClass);
            Object result = method.invoke(repoClassObj, 1L);
            log.info("result: " + result);

            Method savemMethod = repoClass.getMethod("save", Object.class);
//            Class<?> clazz = Class.forName(className);
//            Object instance = clazz.newInstance();
//            set(instance, "salary", 15);
//            set(instance, "firstname", "John");

            Map<String, Object> bookMap = new HashMap<>();
//            bookMap.put("id", System.currentTimeMillis());
            bookMap.put("name", "book10");
            bookMap.put("revision", 1);
            bookMap.put("delFlag", 0);
            Object bookObj = null;
            try {
                bookObj = ObjectUtil.mapToObject(bookMap, entityClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Object result2 = savemMethod.invoke(repoClassObj, bookObj);
            log.info("result2: " + result2);
            return result2;
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                 InvocationTargetException e) {
            log.error("", e);
            return null;
        }
    }

    @GetMapping("/book/list")
    Object dynamicReposTestList() {
        try {
            Class<?> repoClass = Thread.currentThread().getContextClassLoader().loadClass("com.dzero.jpa.dynamic.repository.BookRepository");
            Object result = repoClass.getMethod("findAll")
                    .invoke(applicationContext.getBean(repoClass));
            log.info("result: " + result);
            return result;
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                 InvocationTargetException e) {
            log.error("", e);
            return null;
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
            log.info("No Such Bean Definition");
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
            log.info("No Such Bean Definition");
        }
        //create newBeanObj through GenericBeanDefinition
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(JpaRepositoryFactoryBean.class).addConstructorArgValue(jpaRepositoryClass);
        registry.registerBeanDefinition(jpaRepositoryClass.getSimpleName(), beanDefinitionBuilder.getBeanDefinition());
//        JpaRepositoryFactoryBean jpaRepositoryFactoryBean= applicationContext.getBean(jpaRepositoryClass.getSimpleName(), JpaRepositoryFactoryBean.class);
    }

    @GetMapping("/scheme")
    void getScheme() {
        final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
        final Catalog catalog;
        try {
            catalog = SchemaCrawlerUtility.getCatalog(dataSource.getConnection(), options);
            for (final Schema schema : catalog.getSchemas()) {
                if ("PUBLIC".equals(schema.getName())) {
                    for (final Table table : catalog.getTables(schema)) {
                        log.info("【表】" + table);
                        log.info("【表名】" + table.getName());
                        for (final Column column : table.getColumns()) {
                            Object type = column.getType();
                            log.info("    【字段名】" + column.getName());
                            log.info("    【字段类型】" + type);
                            log.info("    【字段默认值】" + column.getDefaultValue());
                            log.info("    【是否为空】" + column.getType().isNullable());
                        }
                    }
                }
            }
        } catch (SQLException e) {
           log.error("", e);
        }
    }
}
