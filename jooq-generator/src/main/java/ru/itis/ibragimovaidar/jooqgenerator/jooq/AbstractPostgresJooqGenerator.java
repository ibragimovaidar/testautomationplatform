package ru.itis.ibragimovaidar.jooqgenerator.jooq;

import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.postgres.PostgresDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractPostgresJooqGenerator extends AbstractJooqGenerator<PostgresDatabase> {

    protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13")
            .withCommand("postgres -c shared_preload_libraries=pg_stat_statements");

    @BeforeAll
    public void beforeAll() {
        setup();
        POSTGRES.start();
    }

    protected void setup() {
    }

    @Override
    protected Jdbc getJdbc() {
        return new Jdbc()
                .withDriver(POSTGRES.getDriverClassName())
                .withUrl(POSTGRES.getJdbcUrl())
                .withUser(POSTGRES.getUsername())
                .withPassword(POSTGRES.getPassword());
    }

    @Override
    Class<PostgresDatabase> getDatabaseClass() {
        return PostgresDatabase.class;
    }

    @Override
    protected String getInputSchema() {
        return "public";
    }
}
