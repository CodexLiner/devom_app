rootProject.name = "PandijtApp"

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        mavenLocal()
        mavenCentral()
    }
}
include(":composeApp")
include(":devom")
include(":network")
include((":core:models"))
include((":domain"))
include((":core:utils"))
include((":data:cache"))
include((":data:server"))
include((":data:repository"))
