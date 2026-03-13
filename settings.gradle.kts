pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AnixStudyAssist"
include(":app")
include(":core")
include(":feature:auth")
include(":feature:ai")
include(":feature:class-details")
include(":feature:landing")
include(":feature:settings")
include(":core:ui")
include(":feature:screens")
include(":feature:aiengine")
include(":core:designkit")
include(":core:support")
include(":core:infra")
