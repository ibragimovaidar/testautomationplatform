package ru.itis.ibragimovaidar.jooqgenerator.jooq;

import org.jooq.meta.h2.H2Database;
import org.jooq.meta.jaxb.Jdbc;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractH2JooqGenerator extends AbstractJooqGenerator<H2Database> {

    @Override
    protected Jdbc getJdbc() {
        return new Jdbc()
                .withDriver("org.h2.Driver")
                .withUrl("jdbc:h2:mem:jooq;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS " + getInputSchema())
                .withUser(null)
                .withPassword(null);
    }

    @Override
    Class<H2Database> getDatabaseClass() {
        return H2Database.class;
    }

    @Override
    protected String getInputSchema() {
        return "PUBLIC";
    }
}
