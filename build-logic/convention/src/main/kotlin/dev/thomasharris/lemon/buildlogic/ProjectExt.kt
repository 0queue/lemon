@file:Suppress("UnstableApiUsage")

package dev.thomasharris.lemon.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *>,
) {
    commonExtension.run {
        compileSdk = 33

        defaultConfig {
            minSdk = 23
        }

        compileOptions {
            isCoreLibraryDesugaringEnabled = true
        }
    }

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    dependencies.add("coreLibraryDesugaring", libs.findLibrary("desugar").get())
}

fun Project.configureCompose(
    commonExtension: CommonExtension<*, *, *, *>,
) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.run {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs
                .findVersion("compose-compiler")
                .get()
                .toString()
        }
    }

    dependencies {
        add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
        add("debugImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())
        add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
    }}

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
