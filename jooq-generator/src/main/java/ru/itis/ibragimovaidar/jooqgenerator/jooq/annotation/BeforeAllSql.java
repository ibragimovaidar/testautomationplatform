package ru.itis.ibragimovaidar.jooqgenerator.jooq.annotation;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BeforeAllSql {
    @Language("SQL")
    String[] value() default {};
}
