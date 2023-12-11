package ru.itis.ibragimovaidar.testautomationplatform.lang

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder

@TupleConstructor
@Builder
class TestSpecification {

    String name
    Closure preConditions
    Closure body
    Closure postConditions
}
