val postgres_version: String by project
val exposed_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.2.20"
    id("io.ktor.plugin") version "3.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
}

group = "ru.fmsh"
version = "0.0.1"

application {
    mainClass = "ru.fmsh.DatabaseMigrateKt"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.postgresql:postgresql:${postgres_version}")
    implementation("org.jetbrains.exposed:exposed-core:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-json:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposed_version}")
    implementation("ch.qos.logback:logback-classic:${logback_version}")
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}