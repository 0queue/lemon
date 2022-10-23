import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

// NowInAndroid leaves some config in the build.gradle.kts of :app, specifically
// - namespace/appId/versionCode/versionName
// - buildTypes
// - some testing stuff
// - packaging options
// It also splits some things into a compose convention plugin.  Not sure why
class LemonApplicationConventionPlugin : Plugin<Project> {
    @Suppress("UnstableApiUsage")
    override fun apply(target: Project): Unit = target.run {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        extensions.configure<ApplicationExtension> {
            compileSdk = 33

            defaultConfig {
                minSdk = 23
                targetSdk = 33
            }

            compileOptions {
                isCoreLibraryDesugaringEnabled = true
            }

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
    }
}
