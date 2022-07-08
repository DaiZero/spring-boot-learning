package com.dzero.jpa.dynamic.config;

import com.dzero.jpa.dynamic.common.BaseEntity;
import com.dzero.jpa.dynamic.common.BaseRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.utility.CompoundList;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
public class DynamicClassGenerator {
    public Optional<Class<?>> createJpaEntity(String entityClassName, String tableName){
        if (classFileExists(entityClassName)) {
            log.info("【*******】【The Entity class " + entityClassName + " already exists, not creating a new one】");
            try {
                return Optional.of(Class.forName(entityClassName));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("【*******】【Creating new Entity class: {}...】", entityClassName);
        Unloaded<?> generatedClass =
                new ByteBuddy()
                        .subclass(BaseEntity.class)
                        .annotateType(
                                AnnotationDescription.Builder.ofType(Entity.class).build(),
                                AnnotationDescription.Builder.ofType(Data.class).build(),
                                AnnotationDescription.Builder.ofType(NoArgsConstructor.class).build(),
                                AnnotationDescription.Builder.ofType(AllArgsConstructor.class).build(),
                                AnnotationDescription.Builder.ofType(Accessors.class)
                                        .define("chain", true).build(),
                                AnnotationDescription.Builder.ofType(EqualsAndHashCode.class).define("callSuper", true).build(),
                                AnnotationDescription.Builder.ofType(Table.class).define("name", tableName).build())
                        .defineProperty("name", String.class)
                        .annotateField(AnnotationDescription.Builder.ofType(Column.class).define("name", "name").build())
                        .name(entityClassName)
                        .make();
        return Optional.of(saveGeneratedClassAsFile(generatedClass));
    }

    public Optional<Class<?>> createJpaRepository(Class<?> entityClass, String repositoryClassName) {
        if (classFileExists(repositoryClassName)) {
            log.info(
                    "【*******】【The Repository class "
                            + repositoryClassName
                            + " already exists, not creating a new one】");
            return Optional.empty();
        }
        log.info("【*******】【Creating new Repo class: {}...】", repositoryClassName);

        Generic crudRepo =
                Generic.Builder.parameterizedType(BaseRepository.class, entityClass, Long.class).build();
        Unloaded<?> generatedClass =
                new ByteBuddy()
                        .makeInterface(crudRepo)
                        .name(repositoryClassName)
                        .annotateType(
                                AnnotationDescription.Builder.ofType(Repository.class)
                                        .build())
                        .make();

        return Optional.of(saveGeneratedClassAsFile(generatedClass));
    }

    private boolean classFileExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private Class<?> saveGeneratedClassAsFile(Unloaded<?> unloadedClass) {

        try {
            String path = this.getClass().getClassLoader().getResource("").getPath();
            unloadedClass.saveIn(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //getClass().getClassLoader()
        Loaded<?> loadedClass =
                unloadedClass.load(
                        Thread.currentThread().getContextClassLoader(),
                        ClassLoadingStrategy.Default.INJECTION);
        return loadedClass.getLoaded();
    }
}
