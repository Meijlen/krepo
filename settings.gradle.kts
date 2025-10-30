pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version "2.0.20" apply false
        id("org.jetbrains.dokka") version "2.0.0" apply false
        id("org.gradle.maven-publish") version "0.26.0" apply false
        id("org.gradle.signing") version "8.10" apply false
    }
}

rootProject.name = "krepo"
include(":core")