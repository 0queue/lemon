import com.android.build.api.dsl.ApplicationExtension
import dev.thomasharris.lemon.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// NowInAndroid leaves some config in the build.gradle.kts of :app, specifically
// - namespace/appId/versionCode/versionName
// - buildTypes
// - some testing stuff
// - packaging options
class LemonApplicationConventionPlugin : Plugin<Project> {
    @Suppress("UnstableApiUsage")
    override fun apply(target: Project): Unit = target.run {
        pluginManager.run {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = 33
        }
    }
}
