plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val exampleType: String? by project

val jarApp by tasks.creating(Jar::class.java) {
    archiveFileName.set("app.jar")

    val appClass = when(exampleType) {
        "hello" -> "ru.meetup.app.HelloApp"
        "counter" -> "ru.meetup.app.CounterApp"
        else -> "ru.meetup.app.HelloApp"
    }
    manifest {
        attributes("Main-Class" to appClass)
    }
    from(sourceSets.main.get().output)
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("app"))
}
