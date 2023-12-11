package ru.itis.ibragimovaidar.jooqgenerator.jooq;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.AbstractDatabase;
import org.jooq.meta.jaxb.*;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import ru.itis.ibragimovaidar.jooqgenerator.jooq.annotation.JooqGenerateTest;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@Slf4j
@JooqGenerateTest
public abstract class AbstractJooqGenerator<DB extends AbstractDatabase> {

    protected void generateJooq(ForcedType... forcedTypes) throws Exception {
        generateJooq("classpath:config/liquibase/master.xml", forcedTypes);
    }

    protected void generateJooq(String changelog, ForcedType... forcedTypes) throws Exception {
        applyInitSql();
        applyLiquibase(changelog);

        String packagePath = this.getClass().getPackageName().replace(".", "/");
        Path targetDir = Paths.get(System.getProperty("user.dir") + "/src/main/java/" + packagePath).normalize();
        Path targetTempDir = TestUtils.createTempDirectory("jooq").normalize();

        try {
            Configuration configuration = new Configuration()
                    .withJdbc(getJdbc())
                    .withGenerator(new Generator()
                            .withName(QualifiedJavaGenerator.class.getCanonicalName())
                            .withDatabase(new Database()
                                            .withName(getDatabaseClass().getCanonicalName())
                                            .withIncludes(getIncludeExpression())
                                            .withExcludes(getExcludeExpression())
                                            .withInputSchema(getInputSchema())
                                            .withForcedTypes(forcedTypes)
                                            .withRecordVersionFields(getRecordVersionFields())
                            )
                            .withGenerate(getGenerate())
                            .withTarget(new Target()
                                    .withPackageName(this.getClass().getPackageName())
                                    .withDirectory(targetTempDir.toString())
                            )
                            .withStrategy(new Strategy()
                                    .withName(PrefixGeneratorStrategy.class.getCanonicalName())
                            )
                    );
            GenerationTool.generate(configuration);
        } catch (Exception ex) {
            log.error("Can't generate database", ex);
            throw ex;
        }
        if (!targetTempDir.toFile().exists()) {
            throw new IllegalStateException("Can't find generated jooq classes");
        }
        FileUtils.deleteDirectory(targetDir.toFile());
        targetTempDir = targetTempDir.resolve(packagePath);
        FileUtils.moveDirectory(targetTempDir.toFile(), targetDir.toFile());
    }

    protected String getExcludeExpression() {
        return "databasechangelog(lock)?|shedlock";
    }

    protected String getIncludeExpression() {
        return ".*";
    }

    protected String getRecordVersionFields() {
        return "";
    }

    protected Generate getGenerate() {
        return new Generate()
                .withNonnullAnnotation(false)
                .withNullableAnnotation(false)
                .withNewline(System.lineSeparator())
                .withPojos(true)
                .withDaos(true)
                .withFluentSetters(true)
                .withSpringAnnotations(true)
                .withGeneratedAnnotation(true)
                .withValidationAnnotations(true);
    }

    protected abstract Jdbc getJdbc();

    protected DataSource getDataSource() {
        var jdbc = getJdbc();
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(jdbc.getDriver());
        dataSourceBuilder.url(jdbc.getUrl());
        dataSourceBuilder.username(jdbc.getUser());
        dataSourceBuilder.password(jdbc.getPassword());
        return dataSourceBuilder.build();
    }

    abstract Class<DB> getDatabaseClass();

    abstract String getInputSchema();

    protected void applyLiquibase(String changelog) throws Exception {
        if (changelog == null) {
            return;
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(getDataSource());
        liquibase.setChangeLog(changelog);
        liquibase.setDefaultSchema(getInputSchema());
        liquibase.setResourceLoader(new DefaultResourceLoader(this.getClass().getClassLoader()));
        liquibase.afterPropertiesSet();
    }

    protected <T extends Enum<T>> ForcedType enumType(String table, String column, Class<T> converter) {
        return new ForcedType()
                .withUserType(converter.getCanonicalName())
                .withIncludeExpression(table + "." + column)
                .withEnumConverter(true);
    }

    private void applyInitSql() throws Exception {
        var queries = getInitSql();
        if (queries == null) {
            return;
        }
        try (Connection con = getDataSource().getConnection()) {
            try (Statement stmt = con.createStatement()) {
                for (String query : queries) {
                    stmt.execute(query);
                }
            }
        }
    }

    protected List<String> getInitSql() {
        return null;
    }
}
