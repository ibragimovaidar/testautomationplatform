package ru.itis.ibragimovaidar.jooqgenerator.jooq;

import org.apache.commons.lang3.StringUtils;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;
import org.jooq.meta.SchemaDefinition;

import java.util.HashMap;
import java.util.Map;

public class PrefixGeneratorStrategy extends DefaultGeneratorStrategy {
    public static String GLOBAL_REFERENCES_PREFIX = "";
    public static Map<String, String> SCHEMA_MAPPING = new HashMap<>();

    @Override
    public String getGlobalReferencesJavaClassName(Definition container, Class<? extends Definition> objectType) {
        if (StringUtils.isNotEmpty(GLOBAL_REFERENCES_PREFIX)) {
            return GLOBAL_REFERENCES_PREFIX + super.getGlobalReferencesJavaClassName(container, objectType);
        } else {
            return super.getGlobalReferencesJavaClassName(container, objectType);
        }
    }

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        if (mode.equals(Mode.POJO)) {
            return super.getJavaClassName(definition, mode) + "Pojo";
        }
        if (!SCHEMA_MAPPING.isEmpty() && definition instanceof SchemaDefinition) {
            String original = super.getJavaClassName(definition, mode);
            if (SCHEMA_MAPPING.containsKey(original)) {
                return SCHEMA_MAPPING.get(original);
            }
            return original;
        } else {
            return super.getJavaClassName(definition, mode);
        }
    }
}
