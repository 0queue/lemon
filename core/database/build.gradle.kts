import app.cash.sqldelight.gradle.SqlDelightTask
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("dev.thomasharris.lemon.library")
    id("dev.thomasharris.lemon.hilt")
    alias(libs.plugins.app.cash.sqldelight)
}

android {
    namespace = "dev.thomasharris.lemon.core.database"
}

androidComponents {
    onVariants { variant ->
        afterEvaluate {
            project.tasks.getByName("ksp" + variant.name.capitalized() + "Kotlin") {
                // "closed" lol: https://github.com/google/dagger/issues/4097
                val sqlDelightTask =
                    project.tasks.getByName("generate${variant.name.capitalized()}LobstersDatabaseInterface") as SqlDelightTask
                (this as AbstractKotlinCompileTool<*>).setSource(sqlDelightTask.outputDirectory)
            }
        }
    }
}

dependencies {
    implementation(libs.kotlinx.datetime)

    implementation(libs.app.cash.sqldelight.android.driver)
    api(libs.app.cash.sqldelight.coroutines)
}

sqldelight {
    databases {
        create("LobstersDatabase") {
            packageName.set("dev.thomasharris.lemon.core.database")
        }
    }
}
