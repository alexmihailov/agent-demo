plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.12.3")
}

val exampleType: String? by project

tasks.jar {
    archiveFileName.set("agent.jar")

    val agentClass = when(exampleType) {
        "hello" -> "ru.meetup.agent.HelloAgent"
        "counter" -> "ru.meetup.agent.CounterAgent"
        "counterBroken" -> "ru.meetup.agent.CounterBrokenAgent"
        "changeMethod" -> "ru.meetup.agent.ChangeMethodAgent"
        "changeMethodAnnotation" -> "ru.meetup.agent.ChangeMethodAgentAnnotation"
        "changeMethodException" -> "ru.meetup.agent.ChangeMethodAgentException"
        else -> "ru.meetup.agent.HelloAgent"
    }
    manifest {
        attributes("PreMain-Class" to agentClass)
    }

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("agent"))
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}
