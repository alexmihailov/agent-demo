plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

tasks.register<JavaExec>("runAppWithAgent") {
    dependsOn("clean")
    dependsOn(":agent:generateAgent")
    dependsOn(":app:generateApp")
    jvmArgs = listOf("-javaagent:${layout.buildDirectory.dir("agent").get().file("agent.jar").asFile.absolutePath}")
    classpath = layout.buildDirectory.dir("app").get().asFileTree
}
