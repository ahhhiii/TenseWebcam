import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("io.ktor.plugin") version "2.3.2"
}

group = "net.bruhitsalex.tensewebcam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.2")
    implementation("io.ktor:ktor-server-websockets-jvm:2.3.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("net.bruhitsalex.tensewebcam.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "net.bruhitsalex.tensewebcam.MainKt"
    }

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}