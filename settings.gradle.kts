pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "AnixStudyAssist"
include(":app")
include(":core:ui", ":core:data-handler", ":core:aikit")

include(":feature:landing")
include(":feature:settings")
include(":feature:entry")
include(":feature:topic")
include(":feature:aichat")
include(":feature:data-store")
