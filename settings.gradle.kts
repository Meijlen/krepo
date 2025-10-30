rootProject.name = "ktor-repository"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("core")
include("app")
include("ktor-repository-exposed")