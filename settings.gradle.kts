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
include(":feature:landing")
include(":feature:settings")
include(":core:ui")
include(":feature:entry")
include(":core:data-handler")
include(":feature:topic")
include(":feature:aichat")
include(":core:aikit")
