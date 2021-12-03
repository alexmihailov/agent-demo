plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val exampleType: String? by project

tasks.jar {
    archiveFileName.set("app.jar")

    val appClass = when(exampleType) {
        "hello" -> "ru.meetup.app.HelloApp"
        "counter" -> "ru.meetup.app.CounterApp"
        "changeMethod" -> "ru.meetup.app.ChangeMethodApp"
        else -> "ru.meetup.app.HelloApp"
    }
    manifest {
        attributes("Main-Class" to appClass)
    }
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("app"))
}
