package ru.itis.ibragimovaidar.jooqgenerator.jooq;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Configuration;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.TableDefinition;

public class QualifiedJavaGenerator extends JavaGenerator {
    public static String DAO_CONSTRUCTOR_QUALIFIER = "";

    @Override
    protected void printDaoConstructorAnnotations(TableDefinition table, JavaWriter out) {
        if (StringUtils.isNotEmpty(DAO_CONSTRUCTOR_QUALIFIER)) {
            if (this.generateSpringAnnotations()) {
                super.printDaoConstructorAnnotations(table, out);
                String className = this.getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.DAO);

                out.print("public %s(@%s(\"%s\") %s configuration) {//",
                        className,
                        out.ref("org.springframework.beans.factory.annotation.Qualifier"),
                        DAO_CONSTRUCTOR_QUALIFIER,
                        Configuration.class);
            }
        } else {
            super.printDaoConstructorAnnotations(table, out);
        }
    }
}
