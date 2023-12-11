import static ru.itis.ibragimovaidar.testautomationplatform.lang.DslBuilder.test

test {
    name = "Test1"
    preConditions = {
        println "preconditions"
    }
    body = {
        println "Body"
    }
    postConditions = {
        println "postConditions"
    }
}
