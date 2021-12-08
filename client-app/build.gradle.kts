plugins {
    application
    kotlin("jvm") version "1.6.0"
}

group = "ru.meetup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-cio:1.6.6")
    implementation("io.ktor:ktor-client-logging:1.6.6")
    implementation("ch.qos.logback:logback-classic:1.2.7")
    implementation("io.github.config4k:config4k:0.4.2")
    implementation("com.typesafe:config:1.4.1")
}

application {
    mainClass.set("ru.meetup.client.ClientApp")
}
