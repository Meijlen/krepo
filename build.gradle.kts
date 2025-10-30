val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.2.20" apply false
    id("org.jetbrains.dokka") apply false
    id("signing")
}

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")

    version = "0.0.1"
    group = "io.github.meijlen"
}