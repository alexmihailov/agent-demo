plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

tasks.register<JavaExec>("runAppWithAgent") {
    dependsOn("clean")
    dependsOn(":agent:jarAgent")
    dependsOn(":app:jarApp")
    jvmArgs = listOf("-javaagent:${layout.buildDirectory.dir("agent").get().file("agent.jar").asFile.absolutePath}")
    classpath = layout.buildDirectory.dir("app").get().asFileTree
}
