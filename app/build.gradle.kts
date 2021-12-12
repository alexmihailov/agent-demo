plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":agent"))
}

val exampleType: String? by project

tasks.jar {
    archiveFileName.set("app.jar")

    val appClass = when(exampleType) {
        "hello" -> "ru.meetup.app.HelloApp"
        "counter" -> "ru.meetup.app.CounterApp"
        "changeMethod", "changeMethodException" -> "ru.meetup.app.ChangeMethodApp"
        "changeMethodAnnotation" -> "ru.meetup.app.ChangeMethodAppAnnotation"
        else -> "ru.meetup.app.HelloApp"
    }
    manifest {
        attributes("Main-Class" to appClass)
    }
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("app"))
}
