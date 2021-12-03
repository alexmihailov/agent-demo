plugins {
    java
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
//
//application {
//    mainClass.set("ru.meetup.app.App")
//    executableDir = rootProject.buildDir.absolutePath
//    this.ext
//    applicationDefaultJvmArgs = listOf("-javaagent:./agent.jar")
//}

dependencies {
}

tasks.jar {
    archiveFileName.set("app.jar")
    manifest {
        attributes("Main-Class" to "ru.meetup.app.App")
    }
}

tasks.register<Copy>("generateApp") {
    from(tasks.jar.get().outputs)
    into(rootProject.layout.buildDirectory.dir("app").get())
}
