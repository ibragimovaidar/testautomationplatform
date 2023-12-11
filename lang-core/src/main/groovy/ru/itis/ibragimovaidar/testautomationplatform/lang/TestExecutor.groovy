package ru.itis.ibragimovaidar.testautomationplatform.lang

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder

import static io.restassured.RestAssured.given
import static io.restassured.RestAssured.post

class TestExecutor {

    static {
        RestAssured.with()
                .baseUri("https://catfact.ninja/fact")
    }

    static void execute(TestSpecification spec) {
        given post() then() body()
        def builder = new RequestSpecBuilder()
        println String.format("Executing %s", spec.name)
        spec.preConditions()
        spec.body()
        spec.postConditions()
    }
}
