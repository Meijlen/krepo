val logback_version: String by project

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

group = "io.github.meijlen.ktor-repository"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("com.google.dagger:dagger-compiler:2.51.1")
    ksp("com.google.dagger:dagger-compiler:2.51.1")
    testImplementation(kotlin("test"))
    implementation("ch.qos.logback:logback-classic:${logback_version}")
}

kotlin {
    jvmToolchain(17)
}