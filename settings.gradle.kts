@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "lemon"
include(":app")
include(":core:better-html")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:lobsters-api")
include(":core:material-color-utilities")
include(":core:model")
include(":core:theme")
include(":core:ui")
include(":feature:comments")
include(":feature:frontpage")
include(":feature:settings")
include(":feature:userprofile")
