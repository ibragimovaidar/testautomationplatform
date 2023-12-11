package ru.itis.ibragimovaidar.testautomationplatform.lang

class Main {
    static void main(String[] args) {
        def spec = new TestSpecification(name: "test1")
        Closure closure = {
            name = "test2"
        }
        closure.delegate = spec
        closure()
        println spec.name
    }
}