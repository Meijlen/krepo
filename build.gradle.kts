val exposed_version: String by project
val h2_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgres_version: String by project

group = "io.github.meijlen"
version = "0.0.1"

plugins {
    kotlin("jvm") version "2.2.20" apply false
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
}

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}