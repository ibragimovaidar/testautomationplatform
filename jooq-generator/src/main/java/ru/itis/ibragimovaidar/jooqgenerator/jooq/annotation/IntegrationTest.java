package ru.itis.ibragimovaidar.jooqgenerator.jooq.annotation;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Tag("integration")
public @interface IntegrationTest {
}
