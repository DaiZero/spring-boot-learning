package com.dzero.jpa.dynamic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

import java.util.Optional;

@Configuration
public class DynamicJpaBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicJpaBeanFactoryPostProcessor.class);

    private static final String ENTITY_CLASS_NAME = "BookEntity";
    private static final String REPO_CLASS_NAME = "BookJpaRepository";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        DynamicClassGenerator dynamicClassGenerator = new DynamicClassGenerator();
        String packageName = "com.dzero.jpa.dynamic";
        Optional<Class<?>> entityClass = dynamicClassGenerator.createJpaEntity(packageName + ".entity." + ENTITY_CLASS_NAME, "book");
        if (!entityClass.isPresent()) {
            return;
        }
        Optional<Class<?>> repoClass =
                dynamicClassGenerator.createJpaRepository(entityClass.get(), packageName + ".repository." + REPO_CLASS_NAME);

        if (!repoClass.isPresent()) {
            return;
        }

        LOG.info("【*******】【Created the Entity class {} and Repository class {} successfully】", ENTITY_CLASS_NAME,
                REPO_CLASS_NAME);
        registerJpaRepositoryFactoryBean(repoClass.get(), (DefaultListableBeanFactory) beanFactory);
    }


    private void registerJpaRepositoryFactoryBean(Class<?> jpaRepositoryClass,
                                                  DefaultListableBeanFactory defaultListableBeanFactory) {
        String beanName = jpaRepositoryClass.getSimpleName();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(JpaRepositoryFactoryBean.class).addConstructorArgValue(jpaRepositoryClass);
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());

        LOG.info("【*******】Registered the {} bean for {} successfully", JpaRepositoryFactoryBean.class.getSimpleName(),
                beanName);
    }

}