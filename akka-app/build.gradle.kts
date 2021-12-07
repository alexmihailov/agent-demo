plugins {
    java
    application
    id("com.bmuschko.docker-java-application") version "7.1.0"
    id ("com.avast.gradle.docker-compose") version "0.14.11"
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object Versions {
    const val akka = "2.6.8"
    const val scala = "2.13"
    const val kamon = "2.4.1"
}

dependencies {
    implementation(platform("com.typesafe.akka:akka-http-bom_${Versions.scala}:10.2.7"))
    implementation("com.typesafe.akka:akka-stream_${Versions.scala}:${Versions.akka}")
    implementation("com.typesafe.akka:akka-actor-typed_${Versions.scala}:${Versions.akka}")
    implementation("com.typesafe.akka:akka-http_${Versions.scala}:10.2.7")

    implementation("io.kamon:kamon-bundle_${Versions.scala}:${Versions.kamon}")
    implementation("io.kamon:kamon-prometheus_${Versions.scala}:${Versions.kamon}")

    implementation("commons-codec:commons-codec:1.15")
    implementation("org.freemarker:freemarker:2.3.31")
    implementation("org.slf4j:slf4j-simple:1.7.32")
}

application {
    mainClass.set("ru.meetup.akka.AkkaHttpServer")
}

docker {
    javaApplication {
        baseImage.set("bellsoft/liberica-openjdk-alpine-musl:11.0.13-8")
        maintainer.set("Alex Mihailov `av.miahilov.dev@gmail.com`")
    }
}

tasks.composeUp {
    dependsOn(tasks.dockerBuildImage)
}

dockerCompose {
    useComposeFiles.set(listOf("docker-compose.yml"))
}

