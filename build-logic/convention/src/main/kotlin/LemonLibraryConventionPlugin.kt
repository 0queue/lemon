import com.android.build.gradle.LibraryExtension
import dev.thomasharris.lemon.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class LemonLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        pluginManager.run {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
        }

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            // little weird how both have targetSdk but not in CommonExtension
            defaultConfig.targetSdk = 35
        }
    }
}
