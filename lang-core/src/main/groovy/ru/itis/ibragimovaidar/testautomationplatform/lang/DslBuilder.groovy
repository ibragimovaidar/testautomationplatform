package ru.itis.ibragimovaidar.testautomationplatform.lang

class DslBuilder {

    static TestSpecification test(Closure closure) {
        def specification = new TestSpecification()
        closure.delegate = specification
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure()
        specification
    }
}
