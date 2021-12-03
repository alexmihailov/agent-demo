plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.jar {
    archiveFileName.set("agent.jar")
    manifest {
        attributes("PreMain-Class" to "ru.meetup.agent.SimpleAgent")
    }
}

tasks.register<Copy>("generateAgent") {
    from(tasks.jar.get().outputs)
    into(rootProject.layout.buildDirectory.dir("agent").get())
}
