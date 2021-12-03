plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val exampleType: String? by project

val jarAgent by tasks.creating(Jar::class.java) {
    archiveFileName.set("agent.jar")

    val agentClass = when(exampleType) {
        "hello" -> "ru.meetup.agent.HelloAgent"
        "counter" -> "ru.meetup.agent.AgentCounter"
        else -> "ru.meetup.agent.HelloAgent"
    }
    manifest {
        attributes("PreMain-Class" to agentClass)
    }
    from(sourceSets.main.get().output)
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("agent"))
}
